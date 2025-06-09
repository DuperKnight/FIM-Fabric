package fish.crafting.fimfabric.rendering.world;

import com.mojang.blaze3d.systems.RenderSystem;
import fish.crafting.fimfabric.client.FIMModClient;
import fish.crafting.fimfabric.editor.EditorReference;
import fish.crafting.fimfabric.editor.Referenced;
import fish.crafting.fimfabric.editor.vector.EditorLocation;
import fish.crafting.fimfabric.editor.vector.EditorVector;
import fish.crafting.fimfabric.rendering.InformationFeedManager;
import fish.crafting.fimfabric.settings.VectorSettings;
import fish.crafting.fimfabric.tools.Positioned;
import fish.crafting.fimfabric.tools.ToolManager;
import fish.crafting.fimfabric.tools.worldselector.WorldSelector;
import fish.crafting.fimfabric.tools.worldselector.WorldSelectorManager;
import fish.crafting.fimfabric.ui.InterfaceManager;
import fish.crafting.fimfabric.ui.UIComponent;
import fish.crafting.fimfabric.ui.actions.BlockPosActionElement;
import fish.crafting.fimfabric.ui.custom.blockactions.UIBlockActions;
import fish.crafting.fimfabric.util.CursorPicking;
import fish.crafting.fimfabric.util.DebugSettings;
import fish.crafting.fimfabric.util.KeyUtil;
import fish.crafting.fimfabric.util.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import static fish.crafting.fimfabric.util.DebugSettings.RENDER_SELECTOR_HITBOXES;

public class WorldRenderingManager {

    public static int RENDER_FRAME = 0;
    private static final Identifier WORLD_DRAW = Identifier.of(FIMModClient.NAMESPACE, "world_draw");
    private static WorldRenderingManager instance;
    private boolean renderedCursorPickingLastFrame = false;

    private WorldRenderingManager(){
        instance = this;
        WorldRenderEvents.AFTER_TRANSLUCENT.addPhaseOrdering(Event.DEFAULT_PHASE, WORLD_DRAW);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(WORLD_DRAW, get()::render);
    }

    public static WorldRenderingManager get(){
        return instance == null ? new WorldRenderingManager() : instance;
    }

    private void render(@NotNull WorldRenderContext context){
        RENDER_FRAME++;


        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

        MatrixStack matrices = context.matrixStack();
        Vec3d camera = context.camera().getPos();

        RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);
        RenderSystem.setShaderColor(1, 1, 1, 1f);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);

        Positioned editing = ToolManager.get().getEditing();
        if(editing != null){
            switch (editing) {
                case EditorVector vector -> renderVector(context, matrices, camera, vector);
                case EditorLocation location -> renderLocation(context, matrices, camera, location);
                default -> {}
            }
        }

        boolean isF3B = MinecraftClient.getInstance().getEntityRenderDispatcher().shouldRenderHitboxes() && DebugSettings.APPLY_MINECRAFT_DEBUG_TOOLS;
        if(RENDER_SELECTOR_HITBOXES || isF3B){
            WorldSelectorManager.get().renderSelectors(context, matrices, camera);
        }

        UIComponent currentlyHovering = InterfaceManager.get().getCurrentlyHovering();
        if(currentlyHovering instanceof BlockPosActionElement blockPosElement){
            blockPosElement.renderWorldSpace(context, matrices, camera);
        }

        renderCursorPickingBlock(context, matrices, camera);

        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        WorldSelectorManager.get().updateHover();
    }

    private void renderCursorPickingBlock(@NotNull WorldRenderContext context,
                                          @NotNull MatrixStack matrices,
                                          @NotNull Vec3d camera){
        boolean renderNoListActive = CursorPicking.areBlockPickingPrerequisitesMet();
        boolean renderListActive = InterfaceManager.get().isBlockActionListActive();

        if(renderListActive){
            CursorPicking.renderPickedPos(context, matrices, camera,
                    UIBlockActions.storedRayBlockPos, 0.8f, 1f, 0.68f, 1f);
        }

        if(renderNoListActive){
            renderedCursorPickingLastFrame = true;

            BlockHitResult raycast = CursorPicking.raycast();
            if(raycast == null) return;

            BlockPos blockPos = raycast.getBlockPos();
            if(blockPos == null) return;

            //Don't render twice if the list is already rendering
            if(renderListActive && blockPos.equals(UIBlockActions.storedRayBlockPos)) return;

            //If block-action list is already visible, render the new pick with slightly less alpha
            float alpha = renderListActive ? 0.5f : 1f;

            CursorPicking.renderPickedPos(context, matrices, camera,
                    blockPos, 1f, 1f, 1f, alpha);
        }else{
            renderedCursorPickingLastFrame = false;
        }

        if(renderedCursorPickingLastFrame){
            InterfaceManager.get().updateCursor(); //yup
        }
    }

    public void renderLocation(@NotNull WorldRenderContext context,
                               @NotNull MatrixStack matrices,
                               @NotNull Vec3d camera,
                               @NotNull EditorLocation location){
        if(location.lastRenderFrame == RENDER_FRAME) return;
        location.lastRenderFrame = RENDER_FRAME;

        matrices.push();
        matrices.translate(-camera.getX(), -camera.getY(), -camera.getZ());

        double d = VectorSettings.renderSize();

        Vec3d pos = location.vector;

        float r = 1f;
        float g = 173f / 255f;
        float b = 58f / 255f;

        WorldSelector activeSelector = WorldSelectorManager.get().getMainActiveSelector();
        if(activeSelector != null && activeSelector == location.selector()){
            g = b = 1f;
        }

        VertexConsumer vertexConsumer = context.consumers().getBuffer(RenderLayer.getDebugFilledBox());
        VertexRendering.drawFilledBox(matrices, vertexConsumer,
                pos.x - d, pos.y - d, pos.z - d,
                pos.x + d, pos.y + d, pos.z + d,
                r, g, b, 0.3f);

        vertexConsumer = context.consumers().getBuffer(RenderUtils.LINE_WIDTH_2);
        VertexRendering.drawBox(matrices, vertexConsumer,
                pos.x - d, pos.y - d, pos.z - d,
                pos.x + d, pos.y + d, pos.z + d,
                r, g, b, 1f);

        vertexConsumer = context.consumers().getBuffer(RenderUtils.LINE_WIDTH_2);
        Vec3d direction = location.getDirection();
        RenderUtils.renderLine(vertexConsumer,
                matrices.peek(),
                (float) pos.x,
                (float) pos.y,
                (float) pos.z,
                (float) (pos.x + direction.x),
                (float) (pos.y + direction.y),
                (float) (pos.z + direction.z),
                (int) (r * 255),
                (int) (g * 255),
                (int) (b * 255));

        matrices.pop();
    }

    public void renderVector(@NotNull WorldRenderContext context,
                             @NotNull MatrixStack matrices,
                             @NotNull Vec3d camera,
                             @NotNull EditorVector vector){
        if(vector.lastRenderFrame == RENDER_FRAME) return;
        vector.lastRenderFrame = RENDER_FRAME;

        matrices.push();
        matrices.translate(-camera.getX(), -camera.getY(), -camera.getZ());

        double d = VectorSettings.renderSize();

        Vec3d pos = vector.vector;

        float red = 0f;

        WorldSelector activeSelector = WorldSelectorManager.get().getMainActiveSelector();
        if(activeSelector != null && activeSelector == vector.selector()){
            red = 1f;
        }

        VertexConsumer vertexConsumer = context.consumers().getBuffer(RenderLayer.getDebugFilledBox());
        VertexRendering.drawFilledBox(matrices, vertexConsumer,
                pos.x - d, pos.y - d, pos.z - d,
                pos.x + d, pos.y + d, pos.z + d,
                red, 1f, 1f, 0.3f);

        vertexConsumer = context.consumers().getBuffer(RenderUtils.LINE_WIDTH_2);
        VertexRendering.drawBox(matrices, vertexConsumer,
                pos.x - d, pos.y - d, pos.z - d,
                pos.x + d, pos.y + d, pos.z + d,
                red, 1f, 1f, 1f);

        matrices.pop();
    }

    private void renderReference(@NotNull Referenced referenced){
        renderReference(referenced.reference());
    }

    private void renderReference(@NotNull EditorReference reference){

    }

}
