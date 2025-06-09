package fish.crafting.fimfabric.ui;

import fish.crafting.fimfabric.util.ColorUtil;
import net.minecraft.client.gui.DrawContext;

import static fish.crafting.fimfabric.util.Constants.ELEMENT_ALPHA;

public class UIBaseButton extends UIBox {

    protected boolean toggled = false;
    protected int color = ColorUtil.alpha(0xFFFFFFFF, ELEMENT_ALPHA);

    public UIBaseButton(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public UIComponent color(int color){
        this.color = color;
        return this;
    }

    public UIComponent toggle(boolean state){
        toggled = state;
        return this;
    }
}
