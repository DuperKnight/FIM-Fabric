package fish.crafting.fimfabric.connection.packets;

import fish.crafting.fimfabric.connection.packetsystem.OutPacket;
import fish.crafting.fimfabric.connection.packetsystem.PacketId;
import io.netty.buffer.ByteBufOutputStream;

public class F2IMinecraftFocusedPacket extends OutPacket {

    private static final PacketId ID = new PacketId("f2i_mc_focused");

    @Override
    protected PacketId getId() {
        return ID;
    }

    @Override
    protected void write(ByteBufOutputStream stream) {

    }
}
