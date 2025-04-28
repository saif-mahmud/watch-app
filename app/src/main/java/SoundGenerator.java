import android.content.Context;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
public class SoundGenerator {

    private static final String TAG = "SoundGenerator";

    /*
     * Computes the discrete Fourier transform (DFT) of the given complex vector. Bsaed on:
     * https://www.nayuki.io/res/how-to-implement-the-discrete-fourier-transform/Dft.java
     * https://stackoverflow.com/questions/7604011/how-can-i-get-frequency-data-from-pcm-using-fft
     */

    static ComplexDT[] computeDft(ComplexDT[] in) {
        int n = in.length;
        ComplexDT[] out = new ComplexDT[n];

        for (int k = 0; k < n; k++) {  // For each output element
            double sumreal = 0;
            double sumimag = 0;
            for (int t = 0; t < n; t++) {  // For each input element
                double angle = 2 * Math.PI * t * k / n;
                sumreal +=  in[t].re() * Math.cos(angle) + in[t].im() * Math.sin(angle);
                sumimag += -in[t].re() * Math.sin(angle) + in[t].im() * Math.cos(angle);
            }
            out[k] = new ComplexDT(sumreal, sumimag);
        }

        return out;
    }

    static ComplexDT[] zcsequence(int u, int seqLen) {
        ComplexDT[] ret = new ComplexDT[seqLen];
        for (int i = 0; i < seqLen; i++)
            ret[i] = (new ComplexDT(0, -1)).scale(Math.PI * (double) u * (double) i * (double) (i + 1)).scale(1 / (double) seqLen).exp();
        return ret;
    }

    static ComplexDT[] generateZCSeq(int u, int len, int paddedSize)
    {
        if (paddedSize<len) {
            Log.i(TAG, "Padded size too short");
            return null;
        };

        try {
            // generate a zc sequence
            ComplexDT[] zcOrgTrunc = zcsequence(u, len); // checked and matches python

            // compute its fft. Apache only includes a DFT, so needed to use a custom imp. checked and matches python
            ComplexDT[] zcFFT = computeDft(zcOrgTrunc);

            // pad the center up to a size of paddedSize samples
            ComplexDT[] zcFFTPadded = new ComplexDT[paddedSize];
            int half = (int) Math.ceil((float)len/2.0);    // Do we have an odd/even problem? CHECK THIS. ONLY CHECKED FOR ODD SEQ LENS
            for (int i=0; i<half; i++) zcFFTPadded[i] = zcFFT[i];                                                   // works
            for (int i=half; i<=zcFFTPadded.length-half; i++) zcFFTPadded[i] = new ComplexDT(0, 0);                   // works
            for (int i=half;i<zcFFT.length;i++) zcFFTPadded[i+(zcFFTPadded.length-len)] = zcFFT[i];                 // works

            // compute ifft - this works if paddedSize is a power of 2.
            FastFourierTransform transformer = new FastFourierTransform();
            ComplexDT[] zcseqUpscaled = transformer.ifft(zcFFTPadded);
            // return ifft data
            return zcseqUpscaled;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static double[] generateCarrier(double fileFreq, double carrierFreq, double sampleDur, double volumeConst, ComplexDT[] data)
    {
        if (sampleDur<1) sampleDur = 1;
        sampleDur = sampleDur*data.length;
        double[] y  = new double[(int)sampleDur];

        for (int i=0;i<sampleDur;i++)
            y[i] = volumeConst * ((Math.cos(2 * Math.PI * carrierFreq * i / fileFreq) * data[i%data.length].re())
                    - (Math.sin(2 * Math.PI * carrierFreq * i / fileFreq) * data[i%data.length].im()));
        return y;
    }

    static public double[] generateChirpStepSound(double fs_Hz, double rep_Hz, int f0_Hz, int f1_Hz, int phase_rad, int Rep)
    {
        if (Rep<1) Rep = 1;
        int n = (int)(fs_Hz/rep_Hz);
        int sampleDur = Rep*n;
        double[] y  = new double[(int)sampleDur];
        double[] chirp = generateChirp(fs_Hz, rep_Hz, f0_Hz, f1_Hz, phase_rad);
        for(int i=0; i<sampleDur; i++)
        {
            y[i] = chirp[i%n];
        }
        return y;
    }

    static public double[] generateChirpZigzagSound(double fs_Hz, double rep_Hz, int f0_Hz, int f1_Hz, int phase_rad, int Rep20ms)
    {
        if (Rep20ms<1) Rep20ms = 1;
        int n = (int)(fs_Hz/rep_Hz);
        int sampleDur = Rep20ms*(n)*2;
        double[] y  = new double[(int)sampleDur];
        double[] chirp = generateChirp(fs_Hz, rep_Hz, f0_Hz, f1_Hz, phase_rad);
        double[] chirp2 = generateChirp(fs_Hz, rep_Hz, f1_Hz, f0_Hz, phase_rad);
        for(int i=0; i<sampleDur; i++)
        {
            if (((int)(i/n))%2 == 0)
            {
                y[i] = chirp[i%n];
            }
            else
            {
                y[i] = chirp2[i%n];
            }
        }
        return y;
    }

    static double[] generateChirp(double fs_Hz, double rep_Hz, int f0_Hz, int f1_Hz, int phase_rad)
    {
        double T_s = 1/rep_Hz; // Period of chirp in seconds.
        double chirpRate = (f1_Hz-f0_Hz)/T_s;
        int n = (int)(fs_Hz/rep_Hz);

        double[] result = new double[n];
        double[] tukey_window = generateTukey(n, 0.1);

        for (int i=0; i<n; i++)
        {
            double t_s = T_s/(double)n*i;
            double phi_Hz = (chirpRate*Math.pow(t_s,2))/2.0 + (f0_Hz*t_s);
            double phi_rad = 2*Math.PI*phi_Hz;
            phi_rad += phase_rad;
            result[i] = Math.cos(phi_rad);
            result[i] = Math.cos(phi_rad)*tukey_window[i];
        }

        return result;
    }


    /*
     * Generates a pure tone, mainly for testing purposes.
     */
    static double[] generateTone(double fileFreq, double toneFreq, double sampleDur)
    {
        sampleDur *= fileFreq;

        double[] y  = new double[(int)sampleDur];
        for (int i=0;i<sampleDur;i++)
//            y[i] = volumeConst * ((Math.cos(2 * Math.PI * toneFreq * i / fileFreq))); // carrier only
            y[i] = Math.cos(2 * Math.PI * i / (fileFreq/toneFreq)); // for continuous wave

        return y;
    }

    static public double[] generateCombTone(int fileFreq, int startFreq, int interFreq, int numFreq, int sampleDur)
    {
        sampleDur *= fileFreq;

        double[] y  = new double[sampleDur];
        for (double currentFreq = startFreq; currentFreq < startFreq + interFreq*numFreq; currentFreq += interFreq){
            if (currentFreq == startFreq){
                for (int i=0;i<sampleDur;i++){
                    y[i] = Math.cos(2 * Math.PI * i / (fileFreq/currentFreq)) / numFreq; // for continuous wave
                }
            }
            else {
                for (int i = 0; i < sampleDur; i++) {
                    y[i] += Math.cos(2 * Math.PI * i / (fileFreq / currentFreq)) / numFreq; // for continuous wave
                }
            }
        }
        return y;
    }

    static double[] generateTukey(int length, double alpha) {
        double[] window = new double[length];

        double[] n = Utilities.arange(0.0, length, 1.0);
        int width = (int)Math.floor(alpha*(length-1)/2.0);

        double[] n1 = Utilities.splitByIndex(n, 0, width+1);
        double[] n2 = Utilities.splitByIndex(n, width+1, length-width-1);
        double[] n3 = Utilities.splitByIndex(n, length-width-1, length);

        for (int i=0; i<n1.length; i++) {
            n1[i] = 0.5 * (1 + Math.cos(Math.PI * (-1 + 2.0*n1[i]/alpha/(length-1))));
        }
        Arrays.fill(n2, 1.0);
        for (int i=0; i<n3.length; i++) {
            n3[i] = 0.5 * (1 + Math.cos(Math.PI * (-2.0/alpha + 1 + 2.0*n3[i]/alpha/(length-1))));
        }

        window = Utilities.concatenateArray(n1, n2);
        window = Utilities.concatenateArray(window, n3);
        return window;
    }

    public static double[] addArrays(double[] array1, double[] array2) {
        int length = array1.length;

        // Check if the arrays have the same length
        if (length != array2.length) {
            throw new IllegalArgumentException("Input arrays must have the same length");
        }

        // Create a new array to store the result
        double[] resultArray = new double[length];

        // Add corresponding elements of the two arrays
        for (int i = 0; i < length; i++) {
            double mixed = array1[i] + array2[i];
            resultArray[i] = mixed;
        }

        return resultArray;
    }


    public static double[] loadWavAsDoubleArray(Context context, int rawResId) {
        try {
            // Open the WAV file from res/raw
            InputStream inputStream = context.getResources().openRawResource(rawResId);
            byte[] wavBytes = new byte[inputStream.available()];
            inputStream.read(wavBytes);
            inputStream.close();

            // Skip the WAV header (typically first 44 bytes)
            int headerSize = 44;
            int audioDataSize = wavBytes.length - headerSize;

            if (audioDataSize <= 0) {
                throw new IOException("Invalid WAV file or empty audio data.");
            }

            // Convert 16-bit PCM to double array
            double[] audioData = new double[audioDataSize / 2]; // Each sample is 2 bytes (16-bit)
            ByteBuffer buffer = ByteBuffer.wrap(wavBytes, headerSize, audioDataSize);
            buffer.order(ByteOrder.LITTLE_ENDIAN); // WAV files use little-endian format

            for (int i = 0; i < audioData.length; i++) {
                short sample = buffer.getShort(); // Read 16-bit sample
                audioData[i] = sample / 32768.0;  // Normalize to [-1, 1] range
            }

            return audioData;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double[] tileWaveArrays(double[] loadedArray, int length, int duration){
        double[] audioData = new double[ (int) duration*48000];
        for (int i=0; i < (int) (duration*48000) ; i++){
            audioData[i] = loadedArray[i%length];
        }
        return audioData;
    }


//    static double[] generateSineWave(double fileFreq, double toneFreq, double sampleDur, double volumeConst)
//    {
//        // Speicify playback time from SAMPLE_RATE
//        int sizePerMs = (int)(fileFreq / 1000);
//        for(int i = 0; i < sampleDur * sizePerMs; i++) {
//            double angle = i / (fileFreq / toneFreq) * 2.0 * Math.PI;
//            buf[0] = (byte) (Math.sin(angle) * 127.0 * vol);
//            sdl.write(buf, 0, 1);
//        }
//    }

}