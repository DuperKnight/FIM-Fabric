package fish.crafting.fimfabric.util.render;

/**
 * Acts as a FadeTracker, but instead of applying fadeIn-stay-fadeOut when ran,
 * separates it into fadeIn and fadeOut, with saving the state ON or OFF
 */
public class CombinedFadeTracker {

    private boolean stay = false;
    private final FadeTracker fadeIn, fadeOut;

    public CombinedFadeTracker(double fade){
        this(fade, fade);
    }

    public CombinedFadeTracker(double fadeIn, double fadeOut){
        this.fadeIn = new FadeTracker(fadeIn, 0, 0);
        this.fadeOut = new FadeTracker(0, 0, fadeOut);
    }

    public int alpha(){
        if(stay){
            return fadeIn.alpha(255);
        }else{
            return fadeOut.alpha();
        }
    }

    public void fadeIn(){
        fadeIn.begin();
        stay = true;
    }

    public void fadeOut(){
        fadeOut.begin();
        stay = false;
    }

    public void forceStay() {
        this.stay = true;
    }
}
