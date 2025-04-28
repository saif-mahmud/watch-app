import android.os.Build;

import androidx.annotation.RequiresApi;

public class Utilities {
    static public String IP = "192.168.0.27";
    static public String SUB_ID = "0";

    static public int RecordingTime = 45;
    static public boolean IsRecordingIMU = false;
    static public int NofBlocksReps = 30;
    static public int NofBlocks = NofBlocksReps; // 3 conditions
    static public int BlockCounter = 0;
    static public int TrialCounter = 0;
    static public int TrialEndCounter = 0;

    static public int TargetPose = 1;
    static public int TargetReps = 1;
    static public int NofTrials = TargetPose*TargetReps;

    // 40: 3.1mm, 80: 6.2mm, 120: 9.3mm, 37.3mm, 480 pixel
    static public int[] TargetPoses = {0};


    @RequiresApi(api = Build.VERSION_CODES.O)
    static public String getDateTS()
    {
        String spacer = "";
        String ret = java.time.LocalDate.now().toString();
        ret = ret.substring(ret.indexOf("-")+1);
        ret = ret.replace("-", spacer);

        String ret2 = java.time.LocalTime.now().toString();
        ret2 = ret2.replace(":", spacer);
        ret2 = ret2.substring(0, ret2.indexOf("."));

        return ret+"_"+ret2;
    }

    // adds dots to a string of numbers to make an ipaddress.
    String addDots(String in)
    {
        if (in.length()>12)
            in = in.substring(0, 12);

        char[] fin = {'_', '_', '_', '.', '_', '_', '_', '.', '_', '_', '_', '.', '_', '_', '_'};
        for (int i=0; i<in.length(); i++)
            fin[i + i/3] = in.charAt(i);

        return new String(fin);
    }

    static public String leftPad(String result, int padNum)
    {
        StringBuilder sb = new StringBuilder();
        int rest = padNum - result.length();
        for (int i = 0; i < rest; i++)
        {
            sb.append("0");
        }
        sb.append(result);
        return sb.toString();
    }


    static public String stripDots(String in)
    {
        String out = "";
        for (char c : in.toCharArray())
            if (c>=(int)'0' && c<=(int)'9')
                out+=c;
        return out;
    }

    public static double[] linspace(int start, int stop, int samples, boolean includeEnd) {
        double[] time = new double[samples];
        double T;

        double span = stop - start;

        double stopVal = stop;
        double i = start;

        if (includeEnd) {
            T = span/(samples-1);
        }
        else {
            T = span/samples;
            stopVal = stopVal - T;
        }

        int index = 0;
        time[index] = i;

        for (index=1; index<time.length; index++) {
            i = i + T;
            time[index] = i;
        }
        if (includeEnd) {
            time[time.length-1] = stop;
        }
        return time;
    }

    public static double[] arange(double start, double stop, double step) {
        if (start > stop) {
            throw new IllegalArgumentException("start cannot be greater than stop");
        }
        int size = (int)((stop-start)/step);
        double[] arr = new double[size];

        double temp = start;
        for (int i=0; i<size; i++){
            arr[i] = temp;
            temp = temp + step;
        }
        return arr;
    }

    public static double[] splitByIndex(double[] arr, int start, int end) {
        double[] out = new double[end-start];
        System.arraycopy(arr, start, out, 0, out.length);
        return out;
    }

    public static double[] concatenateArray(double[] arr1, double[] arr2) {
        double[] out = new double[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, out, 0, arr1.length);
        System.arraycopy(arr2, 0, out, arr1.length, arr2.length);
        return out;
    }

}
