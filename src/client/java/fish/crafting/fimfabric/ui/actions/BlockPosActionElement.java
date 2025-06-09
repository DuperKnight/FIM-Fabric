package fish.crafting.fimfabric.ui.actions;

import fish.crafting.fimfabric.rendering.InformationFeedManager;
import fish.crafting.fimfabric.ui.FancyText;
import fish.crafting.fimfabric.ui.actions.ActionElement;
import fish.crafting.fimfabric.util.BlockUtils;
import fish.crafting.fimfabric.util.ClickContext;
import fish.crafting.fimfabric.util.KeyUtil;
import fish.crafting.fimfabric.util.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

/**
 * Lets the user select a block to use this action element on.
 * Normally, this will select the block the player is looking at.
 */
public abstract class BlockPosActionElement extends ActionElement {
    public BlockPosActionElement(FancyText text) {
        super(text);
    }

    @Override
    protected final void activate(ClickContext context) {
        activate(context, getPos());
    }

    public static BlockPos getPos(){
        return BlockUtils.getTargetBlockPos();
    }

    @Override
    protected void render(DrawContext context) {
        super.render(context);
    }

    protected abstract void activate(ClickContext context, BlockPos pos);

    /**
     * This gets called when this action is hovered.
     */
    public void renderWorldSpace(@NotNull WorldRenderContext context, MatrixStack matrices, Vec3d camera) {
        BlockPos pos = getPos();

        matrices.push();
        matrices.translate(-camera.getX(), -camera.getY(), -camera.getZ());

        var vertexConsumer = context.consumers().getBuffer(RenderUtils.LINE_WIDTH_2);
        VertexRendering.drawBox(
                matrices, vertexConsumer,
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1,
                1f, 1f, 1f, 1f);

        matrices.pop();
    }
}
