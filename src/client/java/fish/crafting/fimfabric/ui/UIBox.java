package fish.crafting.fimfabric.ui;

import com.mojang.datafixers.kinds.Const;
import fish.crafting.fimfabric.util.ColorUtil;
import fish.crafting.fimfabric.util.Constants;
import net.minecraft.client.gui.DrawContext;

public class UIBox extends UIComponent {

    public UIBox(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    protected void renderHover(DrawContext context){
        long now = System.nanoTime();
        if(this.startedHoveringAt > now || this.endedHoveringAt > now) return;

        long endDiff = now - this.endedHoveringAt;

        //Ending fade has finished, and this isn't hovered, don't render
        if(endDiff >= Constants.HOVER_FADE_NS && !isHovered()) return;

        double endProgress = endDiff / (double) Constants.HOVER_FADE_NS;

        long startDiff = now - this.startedHoveringAt;
        double startProgress = startDiff / (double) Constants.HOVER_FADE_NS;


        int alpha = Constants.HOVER_ALPHA;
        if(startProgress < 1.0) {
            alpha = (int) (alpha * startProgress);
        }else if(endProgress < 1.0){
            alpha = (int) (alpha * (1.0 - endProgress));
        }

        context.fill(renderX, renderY, renderX + renderWidth, renderY + renderHeight, ColorUtil.alpha(0xFFFFFFFF, alpha));
    }

}
