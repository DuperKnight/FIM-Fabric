package fish.crafting.fimfabric.connection.packets;

import fish.crafting.fimfabric.connection.ConnectionManager;
import fish.crafting.fimfabric.connection.packetsystem.InPacket;
import fish.crafting.fimfabric.rendering.InformationFeedManager;
import fish.crafting.fimfabric.tools.CustomTool;
import fish.crafting.fimfabric.tools.EditorTools;
import fish.crafting.fimfabric.tools.ToolManager;
import fish.crafting.fimfabric.util.ConnectionUtils;
import io.netty.buffer.ByteBufInputStream;

import javax.swing.*;
import java.io.IOException;

public class I2FEditPacket implements InPacket {

    @Override
    public void readAndExecute(ByteBufInputStream stream) throws IOException {
        Object wild = ConnectionUtils.readWildcard(stream);
        if(wild == null) return;

        ToolManager.get().setEditing(wild);

        CustomTool<?> tool = EditorTools.getToolFromID(stream.readInt());
        if(tool != null) {
            ToolManager.get().setSelectedTool(tool);
        }
    }
}
