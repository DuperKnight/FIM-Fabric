package fish.crafting.fimfabric.ui.custom;

import fish.crafting.fimfabric.rendering.InformationFeedManager;
import fish.crafting.fimfabric.rendering.custom.ScreenRenderContext;
import fish.crafting.fimfabric.ui.InterfaceManager;
import fish.crafting.fimfabric.ui.TexRegistry;
import fish.crafting.fimfabric.ui.UIBox;
import fish.crafting.fimfabric.ui.UIComponent;
import fish.crafting.fimfabric.util.ClickContext;
import fish.crafting.fimfabric.util.ColorUtil;
import fish.crafting.fimfabric.util.Cursors;
import fish.crafting.fimfabric.util.SoundUtil;
import fish.crafting.fimfabric.util.render.FadeTracker;

public class UIClearFeedButton extends UIBox {

    private final FadeTracker FADE_IN = new FadeTracker(0.2, 0, 0);
    private boolean isIconVisible = false;
    private final FadeTracker FADE_OUT = new FadeTracker(0, 0, 0.2);

    public UIClearFeedButton() {
        super(1, 10, 8, 8);
    }

    @Override
    protected void render(ScreenRenderContext context) {
        if(!isIconVisible && hasFeed()){
            isIconVisible = true;
            FADE_IN.begin();

            InterfaceManager.get().updateCursor();
        }else if(isIconVisible && !hasFeed()){
            isIconVisible = false;
            FADE_OUT.begin();

            InterfaceManager.get().updateCursor();
        }

        int alpha;

        if(isIconVisible){
            alpha = FADE_IN.alpha(255);
        }else{
            alpha = FADE_OUT.alpha(0);
        }

        renderIcon(context, TexRegistry.SMALL_X, ColorUtil.alpha(0xFFFFFFFF, alpha / 2));
    }


    @Override
    protected void onDisable(boolean wasEnabled) {
        isIconVisible = false;
    }

    @Override
    protected void onEnable(boolean wasDisabled) {
        isIconVisible = false;
    }

    @Override
    public void onClick(ClickContext context) {
        if(isIconVisible){
            InformationFeedManager.get().clear();
            SoundUtil.clickSound();
        }
    }

    @Override
    public long getHoverCursor() {
        if(isIconVisible) return Cursors.POINTING;
        else return super.getHoverCursor();
    }

    public static boolean hasFeed(){
        return InformationFeedManager.get().hasFeed();
    }
}
