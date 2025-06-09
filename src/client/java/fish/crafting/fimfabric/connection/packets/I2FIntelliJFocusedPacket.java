package fish.crafting.fimfabric.connection.packets;

import fish.crafting.fimfabric.connection.packetsystem.InPacket;
import fish.crafting.fimfabric.tools.ToolManager;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.io.IOException;

public class I2FIntelliJFocusedPacket implements InPacket {
    @Override
    public void readAndExecute(ByteBufInputStream stream) throws IOException {
        ToolManager.get().ijFocused();
    }
}
