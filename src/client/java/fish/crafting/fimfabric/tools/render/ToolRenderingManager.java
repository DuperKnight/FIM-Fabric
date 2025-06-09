package fish.crafting.fimfabric.tools.render;

import fish.crafting.fimfabric.client.FIMModClient;
import fish.crafting.fimfabric.editor.vector.EditorVector;
import fish.crafting.fimfabric.tools.CustomTool;
import fish.crafting.fimfabric.tools.MoveTool;
import fish.crafting.fimfabric.tools.Positioned;
import fish.crafting.fimfabric.tools.ToolManager;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class ToolRenderingManager {

    private static final Identifier TOOL_DRAW = Identifier.of(FIMModClient.NAMESPACE, "tool_draw");
    private static ToolRenderingManager instance;

    private ToolRenderingManager() {
        instance = this;
        WorldRenderEvents.AFTER_TRANSLUCENT.addPhaseOrdering(Event.DEFAULT_PHASE, TOOL_DRAW);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(TOOL_DRAW, get()::render);
    }

    public static ToolRenderingManager get(){
        return instance == null ? new ToolRenderingManager() : instance;
    }

    private void render(@NotNull WorldRenderContext context){
        ToolManager toolManager = ToolManager.get();

        Positioned editing = toolManager.getEditing();
        if(editing == null) return;

        CustomTool<?> selectedTool = toolManager.getSelectedTool();
        if(selectedTool == null) return;

        selectedTool.handleRender(context, editing);
    }
}
