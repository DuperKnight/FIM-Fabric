package fish.crafting.fimfabric.util;

import java.text.NumberFormat;
import java.util.Locale;

public class NumUtil {

    /**
     * Lerps [a, b] based on the difference of startNS and current time, relative to duration
     * (nanoTime() - startMS) / duration
     *
     * @param startNS Beginning of the tracked time
     * @param duration Duration of lerp
     */
    public static double lerpCurrentTime(double a, double b, long startNS, long duration) {
        double p = (System.nanoTime() - startNS) / (double) duration;
        return clamp(p, 0.0, 1.0) * (b - a) + a;
    }

    /**
     * Lerps [a, b] based on the difference of startNS and current time, relative to duration
     * Uses a sine wave tho
     *
     * @param startNS Beginning of the tracked time
     * @param duration Duration of lerp
     */
    public static double sinLerpCurrentTime(double a, double b, long startNS, long duration) {
        double p = (System.nanoTime() - startNS) / (double) duration;
        return Math.sin(clamp(p, 0.0, 1.0) * Math.PI / 2.0) * (b - a) + a;
    }

    /**
     * Clamps N to [A, B]
     * Inclusive on both ends.
     */
    public static double clamp(double n, double a, double b) {
        if(a > n) return a;
        if(b < n) return b;
        return n;
    }

    public static String betterNumber(Number number) {
        return betterNumber(number, false);
    }

    /**
     * Makes the number look nice
     */
    public static String betterNumber(Number number, boolean accountForKotlin) {
        if(!accountForKotlin){
            return NumberFormat.getInstance(Locale.US).format(number);
        }

        String format = NumberFormat.getInstance(Locale.US).format(number);
        if(!format.contains(".")) return format + ".0"; //Kotlin yay
        return format;
    }

}
