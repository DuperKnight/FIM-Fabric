package fish.crafting.fimfabric.ui.scroll;

import fish.crafting.fimfabric.util.NanoUtils;
import lombok.Getter;

public abstract class Scroller {

    @Getter
    private float scroll = 0f;
    private float animScrollBegin = 0f, animScrollEnd = 0f;
    private long animTimeBegin = 0L, animTimeEnd = 0L;

    /**
     * Most likely will be called very often, so try to optimize.
     */
    public abstract float maxScroll();

    public void addAnimatedScroll(float scroll){
        checkScroll();

        boolean runningCurrently = animScrollEnd != 0L;
        long now = System.nanoTime();

        //Setup time
        animTimeBegin = now;
        animTimeEnd = now + NanoUtils.secondsToNano(0.1);

        //Start animating from this current rendered scroll
        animScrollBegin = this.scroll;

        //End at desired scroll
        if(runningCurrently){
            animScrollEnd += scroll;
        }else{
            animScrollEnd = this.scroll + scroll;
        }
    }

    /**
     * Fixes the scroll value if it isn't within bounds.
     * This should be called on render.
     */
    public void checkScroll(){
        calculateScroll();
        float max = maxScroll();

        if(scroll < 0f) scroll = 0f;
        else if(scroll > max) scroll = max;
    }

    private void calculateScroll(){
        if(animTimeEnd == 0L) return;

        long now = System.nanoTime();
        if(animTimeEnd < now){ //Animation ended
            animTimeEnd = 0L;
            scroll = animScrollEnd;
            return;
        }

        double progress = (double) (now - animTimeBegin) / (animTimeEnd - animTimeBegin);
        progress = Math.sin(progress * Math.PI / 2.0); //Maybe will make it smoother

        scroll = (float) (animScrollBegin + (animScrollEnd - animScrollBegin) * progress);
    }
}
