package fish.crafting.fimfabric.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.jetbrains.annotations.NotNull;

public class PlayerUtil {

    public static void sendCommand(@NotNull String command){
        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if(networkHandler != null) {
            //#if MC<=12105
            //$$ networkHandler.sendCommand(command);
            //#else
            networkHandler.sendChatCommand(command);
            //#endif
        }
    }

}
