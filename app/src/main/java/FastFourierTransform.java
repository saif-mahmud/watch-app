public class FastFourierTransform {

    // compute the FFT of x[], assuming its length n is a power of 2
    public static ComplexDT[] fft(ComplexDT[] x) {
        int n = x.length;

        // base case
        if (n == 1) return new ComplexDT[]{x[0]};

        // radix 2 Cooley-Tukey FFT
        if (n % 2 != 0) {
            throw new IllegalArgumentException("n is not a power of 2");
        }

        // compute FFT of even terms
        ComplexDT[] even = new ComplexDT[n / 2];
        for (int k = 0; k < n / 2; k++) {
            even[k] = x[2 * k];
        }
        ComplexDT[] evenFFT = fft(even);

        // compute FFT of odd terms
        ComplexDT[] odd = even;  // reuse the array (to avoid n log n space)
        for (int k = 0; k < n / 2; k++) {
            odd[k] = x[2 * k + 1];
        }
        ComplexDT[] oddFFT = fft(odd);

        // combine
        ComplexDT[] y = new ComplexDT[n];
        for (int k = 0; k < n / 2; k++) {
            double kth = -2 * k * Math.PI / n;
            ComplexDT wk = new ComplexDT(Math.cos(kth), Math.sin(kth));
            y[k] = evenFFT[k].plus(wk.times(oddFFT[k]));
            y[k + n / 2] = evenFFT[k].minus(wk.times(oddFFT[k]));
        }
        return y;
    }


    // compute the inverse FFT of x[], assuming its length n is a power of 2
    public static ComplexDT[] ifft(ComplexDT[] x) {
        int n = x.length;
        ComplexDT[] y = new ComplexDT[n];

        // take conjugate
        for (int i = 0; i < n; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < n; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by n
        for (int i = 0; i < n; i++) {
            y[i] = y[i].scale(1.0 / n);
        }

        return y;

    }

    // compute the circular convolution of x and y
    public static ComplexDT[] cconvolve(ComplexDT[] x, ComplexDT[] y) {

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        if (x.length != y.length) {
            throw new IllegalArgumentException("Dimensions don't agree");
        }

        int n = x.length;

        // compute FFT of each sequence
        ComplexDT[] a = fft(x);
        ComplexDT[] b = fft(y);

        // point-wise multiply
        ComplexDT[] c = new ComplexDT[n];
        for (int i = 0; i < n; i++) {
            c[i] = a[i].times(b[i]);
        }

        // compute inverse FFT
        return ifft(c);
    }


    // compute the linear convolution of x and y
    public static ComplexDT[] convolve(ComplexDT[] x, ComplexDT[] y) {
        ComplexDT ZERO = new ComplexDT(0, 0);

        ComplexDT[] a = new ComplexDT[2 * x.length];
        for (int i = 0; i < x.length; i++) a[i] = x[i];
        for (int i = x.length; i < 2 * x.length; i++) a[i] = ZERO;

        ComplexDT[] b = new ComplexDT[2 * y.length];
        for (int i = 0; i < y.length; i++) b[i] = y[i];
        for (int i = y.length; i < 2 * y.length; i++) b[i] = ZERO;

        return cconvolve(a, b);
    }

    // compute the DFT of x[] via brute force (n^2 time)
    public static ComplexDT[] dft(ComplexDT[] x) {
        int n = x.length;
        ComplexDT ZERO = new ComplexDT(0, 0);
        ComplexDT[] y = new ComplexDT[n];
        for (int k = 0; k < n; k++) {
            y[k] = ZERO;
            for (int j = 0; j < n; j++) {
                int power = (k * j) % n;
                double kth = -2 * power * Math.PI / n;
                ComplexDT wkj = new ComplexDT(Math.cos(kth), Math.sin(kth));
                y[k] = y[k].plus(x[j].times(wkj));
            }
        }
        return y;
    }
}