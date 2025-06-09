package fish.crafting.fimfabric.ui.custom.mainscreen;

import fish.crafting.fimfabric.ui.TexRegistry;
import fish.crafting.fimfabric.ui.UIBaseButton;
import fish.crafting.fimfabric.util.ColorUtil;
import fish.crafting.fimfabric.util.NumUtil;
import net.minecraft.client.gui.DrawContext;

public class UIMainActionButton extends UIBaseButton {

    private static final int PADDING_Y = 4, HEIGHT = 24;

    public UIMainActionButton() {
        super(0, 0, HEIGHT, HEIGHT);
        hoverCursorClick();
    }

    @Override
    protected void render(DrawContext context) {
        float move = (float) NumUtil.sinLerpCurrentTime(-(PADDING_Y + HEIGHT + 1.0), 0.0, lastEnableSwitchTime, 100_000_000L);

        context.getMatrices().translate(0f, move, 0f);

        renderHover(context);

        fill(context, ColorUtil.elementAlpha(0xFFFFFFFF));
        renderIcon(context, TexRegistry.UI_BURGER);
    }

    @Override
    public void onWindowResized(int width, int height) {
        positionRelativeToParent(0.5, 0.0, 0, PADDING_Y);
    }
}
