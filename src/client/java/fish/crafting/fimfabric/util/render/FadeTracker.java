package fish.crafting.fimfabric.util.render;

import fish.crafting.fimfabric.util.ColorUtil;

public class FadeTracker {

    private final long fadeIn;
    private final long stay;
    private final long fadeOut;
    private final long total;
    private long enabledSince = Long.MIN_VALUE;

    /**
     *
     * @param fadeIn Fade in, in seconds
     * @param stay Stay, in seconds
     * @param fadeOut Fade out, in seconds
     */
    public FadeTracker(double fadeIn, double stay, double fadeOut){
        this.fadeIn = (long) (fadeIn * 1_000_000_000L);
        this.stay = (long) (stay * 1_000_000_000L);
        this.fadeOut = (long) (fadeOut * 1_000_000_000L);

        this.total = (long) (this.fadeIn + this.stay + this.fadeOut);
    }

    public void begin(){
        begin(System.nanoTime());
    }

    public void begin(long now){
        enabledSince = now;
    }

    public boolean isActive(){
        return System.nanoTime() < (total + enabledSince);
    }

    public int alpha(){
        return alpha(0);
    }

    public int alpha(int defaultReturnValue){
        long now = System.nanoTime();
        long active = now - enabledSince;

        if(active < fadeIn) {
            int alpha = (int) ((active / (double) fadeIn) * 255d);
            return ColorUtil.alphaCutoff(alpha);
        }

        active -= fadeIn;
        if(active < stay) {
            return 255;
        }

        active -= stay;
        if(active < fadeOut) {
            int alpha = 255 - (int) ((active / (double) fadeOut) * 255d);
            return ColorUtil.alphaCutoff(alpha);
        }

        return defaultReturnValue;
    }

    public int color(int color){
        return ColorUtil.alpha(color, alpha());
    }

}
