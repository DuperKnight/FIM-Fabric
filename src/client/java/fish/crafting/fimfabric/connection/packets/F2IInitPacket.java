package fish.crafting.fimfabric.connection.packets;

import fish.crafting.fimfabric.connection.ConnectionConstants;
import fish.crafting.fimfabric.connection.focuser.FocuserType;
import fish.crafting.fimfabric.connection.packetsystem.OutPacket;
import fish.crafting.fimfabric.connection.packetsystem.PacketId;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

public class F2IInitPacket extends OutPacket {

    private static final PacketId ID = new PacketId("f2i_init");

    @Override
    protected PacketId getId() {
        return ID;
    }

    @Override
    protected void write(ByteBufOutputStream stream) throws IOException {
        writeUUID(stream);
        stream.writeInt(ConnectionConstants.COMPATIBILITY_VERSION);

        FocuserType.writeToStream(stream);
    }
}
