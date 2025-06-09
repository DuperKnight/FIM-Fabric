package fish.crafting.fimfabric.tools.worldselector;

import fish.crafting.fimfabric.editor.vector.EditorLocation;
import fish.crafting.fimfabric.editor.vector.EditorVector;
import fish.crafting.fimfabric.rendering.world.WorldRenderingManager;
import fish.crafting.fimfabric.tools.CustomTool;
import fish.crafting.fimfabric.tools.Positioned;
import fish.crafting.fimfabric.tools.ToolManager;
import fish.crafting.fimfabric.util.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static fish.crafting.fimfabric.util.DebugSettings.RENDER_SELECTOR_HITBOXES;

/**
 * Handles all the Boxes in the world that the player can interact with.
 * Example: Bounding Boxes for Editor Tools
 */
public class WorldSelectorManager {

    public static int LAST_RENDER_FRAME = 0;
    private static WorldSelectorManager instance;
    private final Set<WorldSelector> activeSelectors = new HashSet<>();
    private WorldSelector hovered = null;
    private WorldSelector holding = null;

    private WorldSelectorManager(){
        instance = this;
    }

    public static WorldSelectorManager get(){
        return instance == null ? new WorldSelectorManager() : instance;
    }

    private void refreshActiveSelectors(){
        CustomTool<?> selectedTool = ToolManager.get().getSelectedTool();
        if(selectedTool != null) {
            List<WorldSelector> selectors = selectedTool.getSelectors();
            if(selectors != null) {
                selectors.forEach(WorldSelector::update);
            }
        }

        Positioned editing = ToolManager.get().getEditing();
        if(editing != null) {
            editing.selector().update();
        }
    }

    public void updateHover(){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return;

        float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
        Vec3d camera = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();

        Vec3d vec3d = player.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = vec3d.add(player.getRotationVec(tickDelta).multiply(100));

        Double lowestDistance = null;
        WorldSelector hovered = null;

        for (WorldSelector selector : activeSelectors) {
            Vec3d raycast = selector.raycast(vec3d, vec3d2);
            if(raycast == null) continue;

            double d = raycast.squaredDistanceTo(camera);
            if(lowestDistance == null || d < lowestDistance) {
                lowestDistance = d;
                hovered = selector;
            }
        }

        if(this.hovered == hovered) return;

        if(this.hovered != null){
            this.hovered.onHoverStatus(false);
        }

        if(hovered != null){
            hovered.onHoverStatus(true);
        }

        this.hovered = hovered;
    }

    public void renderSelectors(@NotNull WorldRenderContext context, MatrixStack matrices, Vec3d camera){
        matrices.push();
        matrices.translate(-camera.getX(), -camera.getY(), -camera.getZ());

        VertexConsumer vertexConsumer = context.consumers().getBuffer(RenderUtils.LINE_WIDTH_4);

        for (var selector : activeSelectors) {
            VertexRendering.drawBox(
                    matrices,
                    vertexConsumer,
                    selector.getBox(),
                    1,
                    1,
                    1,
                    RENDER_SELECTOR_HITBOXES ? 1f : 0.3f
            );
        }

        matrices.pop();
    }

    public void updateSelectors() {
        refreshActiveSelectors();

        //-------------------------

        int now = WorldRenderingManager.RENDER_FRAME;

        Iterator<WorldSelector> iterator = activeSelectors.iterator();
        while(iterator.hasNext()) {
            WorldSelector next = iterator.next();
            if(next.lastRenderFrame != now){
                remove(next);
                next.onDisabled();
                iterator.remove();
            }
        }

        LAST_RENDER_FRAME = now;
    }

    public @Nullable WorldSelector getMainActiveSelector(){
        if(holding != null) return holding;

        return hovered;
    }

    /**
     * @return True, if the click should be cancelled.
     */
    public boolean handlePress(int button, int action, int mods){
        if(action == 0){ //STOP PRESS
            if(holding != null){
                holding.handleDepress();
            }

            holding = null;
        }else if(action == 1 && hovered != null){ //START PRESS
            if (holding != null) {
                holding.handleDepress();
            }

            holding = hovered;
            holding.handlePress(button, mods);
            return true;
        }

        return false;
    }

    private void remove(WorldSelector selector){
        if(hovered == selector){
            hovered = null;
        }

        if(holding == selector){
            holding = null;
        }
    }


    public void add(WorldSelector worldSelector) {
        boolean add = this.activeSelectors.add(worldSelector);
        if(add) {
            worldSelector.onEnabled();
        }
    }
}
