package fish.crafting.fimfabric.connection;

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
    }
}
