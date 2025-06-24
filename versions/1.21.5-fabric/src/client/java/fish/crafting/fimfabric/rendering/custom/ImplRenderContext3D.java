package fish.crafting.fimfabric.rendering.custom;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;

import static net.minecraft.client.render.RenderPhase.*;

//1.21.5
public class ImplRenderContext3D implements RenderContext3D {

    private static final RenderPipeline PIPELINE_LINES = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
                    .withLocation("pipeline/lines")
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .build());

    private static final RenderPipeline PIPELINE_BOX = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                    .withLocation("debug_filled_box")
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .build());

    private static final Map<Float, RenderLayer.MultiPhase> LINE_WIDTH_TRACKER = new HashMap<>();
    private static final RenderLayer.MultiPhase BOX_LAYER = RenderLayer.of(
                    "fim:box",
                    1536,
                    false,
                    true,
                    PIPELINE_BOX,
                    RenderLayer.MultiPhaseParameters.builder()
                        .layering(NO_LAYERING)
                        .target(ITEM_ENTITY_TARGET)
                        .build(false));

    private final WorldRenderContext mcContext;
    private RenderLayer.MultiPhase lineLayer = null;
    private Float currentLineWidth = null;

    public ImplRenderContext3D(WorldRenderContext context) {
        this.mcContext = context;
        setLineWidth(2f);
    }

    @Override
    public void beginRender() {
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
                1536,
                PIPELINE_LINES,
                RenderLayer.MultiPhaseParameters.builder()
                        .lineWidth(new LineWidth(OptionalDouble.of(lineWidth)))
                        .layering(VIEW_OFFSET_Z_LAYERING)
                        .target(ITEM_ENTITY_TARGET)
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
                .color(r1, g1, b1, 1f)
                .normal(entry, dX, dY, dZ);

        vertexConsumer.vertex(entry, x2, y2, z2)
                .color(r2, g2, b2, 1f)
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
        return mcContext.tickCounter().getTickProgress(true);
    }

    @Override
    public VertexHelper renderVertices() {
        VertexConsumer boxConsumer = boxConsumer();
        MatrixStack.Entry peek = mcContext.matrixStack().peek();

        return (x, y, z, color) -> {
            boxConsumer.vertex(peek, x, y, z).color(color);
        };
    }

    private VertexConsumer boxConsumer(){
        return mcContext.consumers().getBuffer(BOX_LAYER);
    }

    private VertexConsumer lineConsumer(){
        return mcContext.consumers().getBuffer(lineLayer);
    }
}
