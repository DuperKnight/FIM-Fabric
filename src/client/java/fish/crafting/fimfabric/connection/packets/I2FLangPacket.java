package fish.crafting.fimfabric.connection.packets;

import fish.crafting.fimfabric.connection.ConnectionManager;
import fish.crafting.fimfabric.connection.packetsystem.InPacket;
import fish.crafting.fimfabric.tools.CustomTool;
import fish.crafting.fimfabric.tools.EditorTools;
import fish.crafting.fimfabric.tools.ToolManager;
import fish.crafting.fimfabric.util.ConnectionUtils;
import io.netty.buffer.ByteBufInputStream;

import java.io.IOException;

public class I2FLangPacket implements InPacket {

    @Override
    public void readAndExecute(ByteBufInputStream stream) throws IOException {
        ConnectionManager.kotlin = stream.readBoolean();
    }
}
