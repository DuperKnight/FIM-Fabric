package fish.crafting.fimfabric.rendering.world;

import fish.crafting.fimfabric.client.FIMModClient;
import fish.crafting.fimfabric.editor.EditorReference;
import fish.crafting.fimfabric.editor.Referenced;
import fish.crafting.fimfabric.editor.vector.EditorLocation;
import fish.crafting.fimfabric.editor.vector.EditorVector;
import fish.crafting.fimfabric.rendering.custom.ImplRenderContext3D;
import fish.crafting.fimfabric.rendering.custom.RenderContext3D;
import fish.crafting.fimfabric.settings.VectorSettings;
import fish.crafting.fimfabric.tools.Positioned;
import fish.crafting.fimfabric.tools.ToolManager;
import fish.crafting.fimfabric.tools.render.ToolRenderingManager;
import fish.crafting.fimfabric.tools.worldselector.WorldSelector;
import fish.crafting.fimfabric.tools.worldselector.WorldSelectorManager;
import fish.crafting.fimfabric.ui.InterfaceManager;
import fish.crafting.fimfabric.ui.UIComponent;
import fish.crafting.fimfabric.ui.actions.BlockPosActionElement;
import fish.crafting.fimfabric.ui.custom.blockactions.UIBlockActions;
import fish.crafting.fimfabric.util.CursorPicking;
import fish.crafting.fimfabric.util.DebugSettings;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import static fish.crafting.fimfabric.util.DebugSettings.RENDER_SELECTOR_HITBOXES;

public class WorldRenderingManager {

    public static int RENDER_FRAME = 0;
    private static final Identifier WORLD_DRAW = Identifier.of(FIMModClient.NAMESPACE, "world_draw");
    private static WorldRenderingManager instance;
    private boolean renderedCursorPickingLastFrame = false;

    private WorldRenderingManager(){
        instance = this;
        WorldRenderEvents.AFTER_TRANSLUCENT.addPhaseOrdering(Event.DEFAULT_PHASE, WORLD_DRAW);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(WORLD_DRAW, get()::handleRenderEvent);
    }

    public static WorldRenderingManager get(){
        return instance == null ? new WorldRenderingManager() : instance;
    }

    private void handleRenderEvent(@NotNull WorldRenderContext worldCtx){
        RENDER_FRAME++;

        RenderContext3D context;
        //#if MC==12104
        context = new ImplRenderContext3D(worldCtx);
        //#endif

        //#if MC==12105
        //$$ context = new ImplRenderContext3D();
        //#endif

        context.beginRender();
        render(context);
        context.endRender();

        WorldSelectorManager.get().updateHover();
    }

    private void render(RenderContext3D context){
        //Render tool first, because tool updates vectors & locations
        ToolRenderingManager.get().render(context);

        Positioned editing = ToolManager.get().getEditing();
        if(editing != null){
            switch (editing) {
                case EditorVector vector -> renderVector(context, vector);
                case EditorLocation location -> renderLocation(context, location);
                default -> {}
            }
        }

        boolean isF3B = MinecraftClient.getInstance().getEntityRenderDispatcher().shouldRenderHitboxes() && DebugSettings.APPLY_MINECRAFT_DEBUG_TOOLS;
        if(RENDER_SELECTOR_HITBOXES || isF3B){
            WorldSelectorManager.get().renderSelectors(context);
        }

        UIComponent currentlyHovering = InterfaceManager.get().getCurrentlyHovering();
        if(currentlyHovering instanceof BlockPosActionElement blockPosElement){
            blockPosElement.renderWorldSpace(context);
        }

        renderCursorPickingBlock(context);
    }

    private void renderCursorPickingBlock(@NotNull RenderContext3D context){
        boolean renderNoListActive = CursorPicking.areBlockPickingPrerequisitesMet();
        boolean renderListActive = InterfaceManager.get().isBlockActionListActive();

        if(renderListActive){
            CursorPicking.renderPickedPos(context,
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

            CursorPicking.renderPickedPos(context,
                    blockPos, 1f, 1f, 1f, alpha);
        }else{
            renderedCursorPickingLastFrame = false;
        }

        if(renderedCursorPickingLastFrame){
            InterfaceManager.get().updateCursor(); //yup
        }
    }

    public void renderLocation(@NotNull RenderContext3D context,
                               @NotNull EditorLocation location){
        if(location.lastRenderFrame == RENDER_FRAME) return;
        location.lastRenderFrame = RENDER_FRAME;

        context.push();
        context.translateCamera();
        context.setLineWidth(2f);

        double d = VectorSettings.renderSize();

        Vec3d pos = location.vector;

        float r = 1f;
        float g = 173f / 255f;
        float b = 58f / 255f;

        WorldSelector activeSelector = WorldSelectorManager.get().getMainActiveSelector();
        if(activeSelector != null && activeSelector == location.selector()){
            g = b = 1f;
        }

        context.renderFilledBox(
                pos.x - d, pos.y - d, pos.z - d,
                pos.x + d, pos.y + d, pos.z + d,
                r, g, b, 0.3f);

        context.renderBoxOutline(
                pos.x - d, pos.y - d, pos.z - d,
                pos.x + d, pos.y + d, pos.z + d,
                r, g, b, 1f);

        Vec3d direction = location.getDirection();
        context.renderLine(
                (float) pos.x,
                (float) pos.y,
                (float) pos.z,
                (float) (pos.x + direction.x),
                (float) (pos.y + direction.y),
                (float) (pos.z + direction.z),
                (int) (r * 255),
                (int) (g * 255),
                (int) (b * 255));

        context.pop();
    }

    public void renderVector(@NotNull RenderContext3D context,
                             @NotNull EditorVector vector){
        if(vector.lastRenderFrame == RENDER_FRAME) return;
        vector.lastRenderFrame = RENDER_FRAME;

        context.push();
        context.translateCamera();

        double d = VectorSettings.renderSize();

        Vec3d pos = vector.vector;

        float red = 0f;

        WorldSelector activeSelector = WorldSelectorManager.get().getMainActiveSelector();
        if(activeSelector != null && activeSelector == vector.selector()){
            red = 1f;
        }

        context.renderFilledBox(pos.x - d, pos.y - d, pos.z - d,
                pos.x + d, pos.y + d, pos.z + d,
                red, 1f, 1f, 0.3f);

        context.renderBoxOutline(
                pos.x - d, pos.y - d, pos.z - d,
                pos.x + d, pos.y + d, pos.z + d,
                red, 1f, 1f, 1f);

        context.pop();
    }

    private void renderReference(@NotNull Referenced referenced){
        renderReference(referenced.reference());
    }

    private void renderReference(@NotNull EditorReference reference){

    }

}
