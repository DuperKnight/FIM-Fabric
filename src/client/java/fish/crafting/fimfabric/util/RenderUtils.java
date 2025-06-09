package fish.crafting.fimfabric.util;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalDouble;

import static net.minecraft.client.render.RenderPhase.*;
import static net.minecraft.client.render.RenderPhase.ALL_MASK;
import static net.minecraft.client.render.RenderPhase.DISABLE_CULLING;
import static net.minecraft.client.render.RenderPhase.ITEM_ENTITY_TARGET;

public class RenderUtils {

    public static final RenderLayer.MultiPhase
            LINE_WIDTH_2 = line(2),
            LINE_WIDTH_4 = line(4),
            LINE_WIDTH_6 = line(6),
            LINE_WIDTH_8 = line(8);

    private static RenderLayer.MultiPhase line(double width){
        return RenderLayer.of(
                "line_wide_" + width,
                VertexFormats.LINES,
                VertexFormat.DrawMode.LINES,
                1536,
                RenderLayer.MultiPhaseParameters.builder()
                        .program(LINES_PROGRAM)
                        .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(width)))
                        .layering(VIEW_OFFSET_Z_LAYERING)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .target(ITEM_ENTITY_TARGET)
                        .writeMaskState(ALL_MASK)
                        .cull(DISABLE_CULLING)
                        .build(false)
        );
    }

    public static boolean shouldRenderScreenText(){
        MinecraftClient instance = MinecraftClient.getInstance();
        Screen screen = instance.currentScreen;

        return screen == null || screen instanceof ChatScreen;
    }

    public static void renderLine(@NotNull VertexConsumer consumer,
                                  @NotNull MatrixStack.Entry entry,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2,
                                  int r,
                                  int g,
                                  int b){
        renderLineGradient(consumer, entry, x1, y1, z1, x2, y2, z2, r, g, b, r, g, b);
    }

    public static void renderLineGradient(@NotNull VertexConsumer consumer,
                                          @NotNull MatrixStack.Entry entry,
                                          float x1, float y1, float z1,
                                          float x2, float y2, float z2,
                                          int r1,
                                          int g1,
                                          int b1,
                                          int r2,
                                          int g2,
                                          int b2){

        float dX = x2 - x1;
        float dY = y2 - y1;
        float dZ = z2 - z1;

        consumer.vertex(entry, x1, y1, z1)
                .color(r1, g1, b1, 255)
                .normal(entry, dX, dY, dZ);

        consumer.vertex(entry, x2, y2, z2)
                .color(r2, g2, b2, 255)
                .normal(entry, dX, dY, dZ);
    }

}
