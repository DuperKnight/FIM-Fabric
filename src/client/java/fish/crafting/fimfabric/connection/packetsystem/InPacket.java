package fish.crafting.fimfabric.connection.packetsystem;

import io.netty.buffer.ByteBufInputStream;

import java.io.IOException;

public interface InPacket {

    void readAndExecute(ByteBufInputStream stream) throws IOException;

}
