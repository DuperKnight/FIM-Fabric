package fish.crafting.fimfabric.listener;

import fish.crafting.fimfabric.connection.ConnectionManager;
import fish.crafting.fimfabric.editor.vector.EditorLocation;
import fish.crafting.fimfabric.tools.Positioned;
import fish.crafting.fimfabric.tools.ToolManager;
import fish.crafting.fimfabric.tools.worldselector.WorldSelectorManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class GenericTickListener implements ClientTickEvents.EndTick {

    private int totalTicks = 0;

    @Override
    public void onEndTick(MinecraftClient client) {
        totalTicks++;
        WorldSelectorManager.get().updateSelectors();
        ConnectionManager.get().tick();

        Positioned editing = ToolManager.get().getEditing();
        if(editing == null){
            ToolManager.get().setEditing(new EditorLocation(43, 115, -114, 0f, 0f, ""));
        }

    }
}
