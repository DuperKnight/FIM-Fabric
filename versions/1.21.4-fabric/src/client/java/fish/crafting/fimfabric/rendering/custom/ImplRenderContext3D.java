package fish.crafting.fimfabric.rendering.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;

import static net.minecraft.client.render.RenderPhase.*;

//1.21.4
public class ImplRenderContext3D implements RenderContext3D {

    private static Map<Float, RenderLayer.MultiPhase> LINE_WIDTH_TRACKER = new HashMap<>();

    private final WorldRenderContext mcContext;
    private RenderLayer.MultiPhase lineLayer = null;
    private Float currentLineWidth = null;

    public ImplRenderContext3D(WorldRenderContext context) {
        this.mcContext = context;
        setLineWidth(2f);
    }

    @Override
    public void beginRender() {
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

        RenderSystem.setShaderColor(1, 1, 1, 1f);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
    }

    @Override
    public void endRender() {

    }

    @Override
    public void setLineWidth(float lineWidth) {
        if(currentLineWidth != null && currentLineWidth == lineWidth) {
            return;
        }

        currentLineWidth = lineWidth;

        lineLayer = LINE_WIDTH_TRACKER.computeIfAbsent(lineWidth, f -> RenderLayer.of(
                "line_wide_" + lineWidth,
                VertexFormats.LINES,
                VertexFormat.DrawMode.LINES,
                1536,
                RenderLayer.MultiPhaseParameters.builder()
                        .program(LINES_PROGRAM)
                        .lineWidth(new LineWidth(OptionalDouble.of(lineWidth)))
                        .layering(VIEW_OFFSET_Z_LAYERING)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .target(ITEM_ENTITY_TARGET)
                        .writeMaskState(ALL_MASK)
                        .cull(DISABLE_CULLING)
                        .build(false)
        ));
    }

    @Override
    public void renderLineGradient(float x1, float y1, float z1, float x2, float y2, float z2, float r1, float g1, float b1, float r2, float g2, float b2) {
        float dX = x2 - x1;
        float dY = y2 - y1;
        float dZ = z2 - z1;

        VertexConsumer vertexConsumer = mcContext.consumers().getBuffer(lineLayer);
        MatrixStack.Entry entry = mcContext.matrixStack().peek();

        vertexConsumer.vertex(entry, x1, y1, z1)
                .color(r1, g1, b1, 255)
                .normal(entry, dX, dY, dZ);

        vertexConsumer.vertex(entry, x2, y2, z2)
                .color(r2, g2, b2, 255)
                .normal(entry, dX, dY, dZ);
    }

    @Override
    public void renderFilledBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        VertexRendering.drawFilledBox(
                mcContext.matrixStack(),
                boxConsumer(),
                minX, minY, minZ,
                maxX, maxY, maxZ,
                red, green, blue, alpha
        );
    }

    @Override
    public void renderBoxOutline(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        VertexRendering.drawBox(mcContext.matrixStack(), lineConsumer(),
                minX, minY, minZ,
                maxX, maxY, maxZ,
                red, green, blue, alpha);
    }

    @Override
    public Vec3d camera() {
        return mcContext.camera().getPos();
    }

    @Override
    public void translate(double x, double y, double z) {
        mcContext.matrixStack().translate(x, y, z);
    }

    @Override
    public void push() {
        mcContext.matrixStack().push();
    }

    @Override
    public void pop() {
        mcContext.matrixStack().pop();
    }

    @Override
    public float tickDelta() {
        return mcContext.tickCounter().getTickDelta(true);
    }

    private VertexConsumer boxConsumer(){
        return mcContext.consumers().getBuffer(RenderLayer.getDebugFilledBox());
    }

    private VertexConsumer lineConsumer(){
        return mcContext.consumers().getBuffer(lineLayer);
    }

    @Override
    public VertexHelper renderVertices() {
        VertexConsumer boxConsumer = boxConsumer();
        MatrixStack.Entry peek = mcContext.matrixStack().peek();

        return (x, y, z, color) -> {
            boxConsumer.vertex(peek, x, y, z).color(color);
        };
    }
}
