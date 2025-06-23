package fish.crafting.fimfabric.connection;

import fish.crafting.fimfabric.editor.vector.EditorLocation;
import fish.crafting.fimfabric.rendering.InformationFeedManager;
import fish.crafting.fimfabric.tools.Positioned;
import fish.crafting.fimfabric.tools.ToolManager;
import fish.crafting.fimfabric.tools.worldselector.WorldSelectorManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class GenericTickListener implements ClientTickEvents.EndTick {

    private int totalTicks = 0;

    @Override
    public void onEndTick(MinecraftClient client) {
        totalTicks++;
        WorldSelectorManager.get().updateSelectors();
        ConnectionManager.get().tick();

        //#if MC>=12105
        //$$ InformationFeedManager.success("ha", false);
        //#endif
    }
}
