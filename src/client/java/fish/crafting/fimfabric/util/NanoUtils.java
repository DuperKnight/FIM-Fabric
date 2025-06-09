package fish.crafting.fimfabric.util;

public class NanoUtils {

    public static long secondsToNano(double s){
        return (long) (s * 1000_000_000L);
    }

    public static double nanoToSeconds(double ns){
        return ns / 1000_000_000D;
    }

    public static double secondsSince(long time) {
        return nanoToSeconds(System.nanoTime() - time);
    }
}
