import java.util.Objects;

public class ComplexDT {
    private final double re;   // the real part
    private final double im;   // the imaginary part

    // create a new object with the given real and imaginary parts
    public ComplexDT(double real, double imag) {
        re = real;
        im = imag;
    }

    // return a string representation of the invoking Complex object
    public String toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        if (im <  0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    // return abs/modulus/magnitude
    public double abs() {
        return Math.hypot(re, im);
    }

    // return angle/phase/argument, normalized to be between -pi and pi
    public double phase() {
        return Math.atan2(im, re);
    }

    // return a new Complex object whose value is (this + b)
    public ComplexDT plus(ComplexDT b) {
        ComplexDT a = this;             // invoking object
        double real = a.re + b.re;
        double imag = a.im + b.im;
        return new ComplexDT(real, imag);
    }

    // return a new Complex object whose value is (this - b)
    public ComplexDT minus(ComplexDT b) {
        ComplexDT a = this;
        double real = a.re - b.re;
        double imag = a.im - b.im;
        return new ComplexDT(real, imag);
    }

    // return a new Complex object whose value is (this * b)
    public ComplexDT times(ComplexDT b) {
        ComplexDT a = this;
        double real = a.re * b.re - a.im * b.im;
        double imag = a.re * b.im + a.im * b.re;
        return new ComplexDT(real, imag);
    }

    // return a new object whose value is (this * alpha)
    public ComplexDT scale(double alpha) {
        return new ComplexDT(alpha * re, alpha * im);
    }

    // return a new Complex object whose value is the conjugate of this
    public ComplexDT conjugate() {
        return new ComplexDT(re, -im);
    }

    // return a new Complex object whose value is the reciprocal of this
    public ComplexDT reciprocal() {
        double scale = re*re + im*im;
        return new ComplexDT(re / scale, -im / scale);
    }

    // return the real or imaginary part
    public double re() { return re; }
    public double im() { return im; }

    // return a / b
    public ComplexDT divides(ComplexDT b) {
        ComplexDT a = this;
        return a.times(b.reciprocal());
    }

    // return a new Complex object whose value is the complex exponential of this
    public ComplexDT exp() {
        return new ComplexDT(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
    }

    // return a new Complex object whose value is the complex sine of this
    public ComplexDT sin() {
        return new ComplexDT(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex cosine of this
    public ComplexDT cos() {
        return new ComplexDT(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex tangent of this
    public ComplexDT tan() {
        return sin().divides(cos());
    }



    // a static version of plus
    public static ComplexDT plus(ComplexDT a, ComplexDT b) {
        double real = a.re + b.re;
        double imag = a.im + b.im;
        ComplexDT sum = new ComplexDT(real, imag);
        return sum;
    }

    // See Section 3.3.
    public boolean equals(Object x) {
        if (x == null) return false;
        if (this.getClass() != x.getClass()) return false;
        ComplexDT that = (ComplexDT) x;
        return (this.re == that.re) && (this.im == that.im);
    }

    // See Section 3.3.
    public int hashCode() {
        return Objects.hash(re, im);
    }

}

