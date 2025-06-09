package fish.crafting.fimfabric.ui.custom;

import fish.crafting.fimfabric.tools.CustomTool;
import fish.crafting.fimfabric.tools.Positioned;
import fish.crafting.fimfabric.tools.ToolManager;
import fish.crafting.fimfabric.ui.TexRegistry;
import fish.crafting.fimfabric.ui.UIBaseButton;
import fish.crafting.fimfabric.ui.UIBox;
import fish.crafting.fimfabric.util.*;
import net.minecraft.client.gui.DrawContext;

public class UIToolButton extends UIBox {

    private static final int WIDTH = 16, HEIGHT = 16, PADDING = 2;
    private final int index;
    private final CustomTool<?> tool;

    public UIToolButton(int index, CustomTool<?> tool) {
        super(0, 0, WIDTH, HEIGHT);
        this.index = index;
        this.tool = tool;
    }

    @Override
    protected void render(DrawContext context) {
        float move = (float) NumUtil.sinLerpCurrentTime(-(PADDING + HEIGHT + 1.0), 0.0, lastEnableSwitchTime, 100_000_000L);

        context.getMatrices().translate(0f, move, 0f);

        boolean enabled = enabled();

        if(enabled) {
            renderHover(context);
        }

        int color = enabled ? 0xFFFFFFFF : 0x44FFFFFF;
        fill(context, 0x66000000);
        context.drawBorder(renderX, renderY, renderWidth, renderHeight, color);
        renderIcon(context, tool.getTexture(), color);
    }

    @Override
    public void onClick(ClickContext clickContext) {
        if(clickContext.isLeftClick() && clickContext.isPress() && enabled()) {
            ToolManager.get().setSelectedTool(this.tool);
            SoundUtil.clickSound();
        }
    }

    @Override
    public void onWindowResized(int width, int height) {
        positionRelativeToParent(1.0, 0.0, PADDING, PADDING);
        x -= (PADDING + WIDTH) * index;
    }

    private boolean enabled(){
        Positioned editing = ToolManager.get().getEditing();
        return editing != null && tool.isAccessibleFor(editing);
    }

    @Override
    public long getHoverCursor() {
        Positioned editing = ToolManager.get().getEditing();
        if(editing == null) return super.getHoverCursor();

        if(tool.isAccessibleFor(editing)) return Cursors.POINTING;
        else return super.getHoverCursor();
    }
}
