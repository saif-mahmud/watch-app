
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class DataPlayer {
    double[] sample = null;    // sound as double vals
    private static AudioTrack audioTrack = null;
    byte generatedSnd[]   = null;    // sound as PCM data
    boolean dataLoaded    = false;   // whether or not we have any contents.
    int sampleRate        = 48000;   // sample rate of signal - default is 48000

    private static final String TAG = "DataPlayer";


    public DataPlayer(double[] in)
    {
        setData(in);
    }
    public DataPlayer(double[] in, int repetition)
    {
        setData(in, repetition);
    }

    void setData(double[] in)
    {
        sample = new double[in.length];
        for (int i=0; i<in.length; i++)
            sample[i] = in[i];
        genTone();

        Log.i(TAG, "Generated a PCM file of " + in.length);

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                2 * in.length,
                AudioTrack.MODE_STATIC);

        audioTrack.setVolume(AudioTrack.getMaxVolume());
        Log.i(TAG, "Log/ Volume: " + AudioTrack.getMaxVolume());

        dataLoaded = true;
    }

    void setData(double[] in, int repetition)
    {
        sample = new double[in.length*repetition];
        for (int i=0; i<in.length; i++)
            for (int r=0; r<repetition; r++)
                sample[i + in.length*r] = in[i];
        genTone();

        Log.i(TAG, "Generated a PCM file of " + in.length);

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                2 * in.length * repetition,
                AudioTrack.MODE_STATIC);

        audioTrack.setVolume(AudioTrack.getMaxVolume());
        Log.i(TAG, "Log/ Volume: " + AudioTrack.getMaxVolume());


        dataLoaded = true;
    }

    // try to stop the sound playback manually.
    synchronized public void stop() {
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.setPlaybackHeadPosition(0); // back to the start
            //audioTrack.release();
            //audioTrack = null;
        }
    }

    // play for a fixed time - NOT IMPLEMENTED.
//    void play(int playLenMS) {
//        if (dataLoaded)
//        {
//            play();
//        } else
//            Log.i(TAG, "Warning: no sound data to play");
//    }

    // start the sound playback.
    synchronized public void play() {
        if (!dataLoaded) {
            Log.i(TAG, "DataPlayer: no data loaded");
            return;
        }

        try {
            audioTrack.write(generatedSnd, 0, generatedSnd.length);
            audioTrack.play();
        }
        catch (Exception e) {
            Log.i(TAG, "DataPlayer: " + e.toString());
        }
        catch (OutOfMemoryError e) {
            Log.i(TAG, "DataPlayer: " + e.toString());
        }
    }

    // start the sound playback.
    synchronized public void play_repeat() {
        if (!dataLoaded) {
            Log.i(TAG, "DataPlayer: no data loaded");
            return;
        }

        try {
            audioTrack.write(generatedSnd, 0, generatedSnd.length);
            audioTrack.play();
            // Set a listener to loop the sound when playback completes
            audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                @Override
                public void onMarkerReached(AudioTrack track) {
                    // Reload the static data to loop playback
                    audioTrack.stop();
                    audioTrack.reloadStaticData();
                    audioTrack.play();
                }

                @Override
                public void onPeriodicNotification(AudioTrack track) {
                    // Not used in this case
                }
            });
            // Set a marker to trigger looping at the end of the buffer
            audioTrack.setNotificationMarkerPosition(generatedSnd.length/2);
        }
        catch (Exception e) {
            Log.i(TAG, "DataPlayer: " + e.toString());
        }
        catch (OutOfMemoryError e) {
            Log.i(TAG, "DataPlayer: " + e.toString());
        }
    }

    // start the sound playback.
    synchronized public void play(int repetition) {
        if (!dataLoaded) {
            Log.i(TAG, "DataPlayer: no data loaded");
            return;
        }

        try {
//            for(int i=0; i<repetition; i++)
//                audioTrack.write(generatedSnd, generatedSnd.length*i, generatedSnd.length);
//            audioTrack.setPlaybackHeadPosition(0);
            audioTrack.write(generatedSnd, 0, generatedSnd.length);
            audioTrack.play();
        }
        catch (Exception e) {
            Log.i(TAG, "DataPlayer: " + e.toString());
        }
        catch (OutOfMemoryError e) {
            Log.i(TAG, "DataPlayer: " + e.toString());
        }
    }



    // Generate tone data for ~1 seconds - we round up to the length of the underlying data
    // why don't we just calculate this in the first place....
    synchronized void genTone() {
        // convert to 16 bit pcm sound array
        generatedSnd = new byte[2 * sample.length];
        Log.i(TAG, "GeneratedSnd: " + generatedSnd.length);
        int idx = 0;
        for (final double dVal : sample)
        {
//            final short val = (short) ((dVal * 25.0));
            final short val = (short) ((dVal * 32767));
            // scale here if you want to. Not clear what is best to do here.  TODO - ADJUST GAIN
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) ( val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
    }

    void genTone(double sampleDur, double freqOfTone){
        // fill out the array
        sampleDur *= sampleRate;
        sample = new double[(int)sampleDur];
        generatedSnd = new byte[(int) (2 * sampleDur)];

        for (int i = 0; i < sampleDur; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.

        int idx = 0;
        for (double dVal : sample) {
            short val = (short) (dVal * 32767);
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
    }


}
