package fish.crafting.fimfabric.connection.packets;

import fish.crafting.fimfabric.connection.ConnectionManager;
import fish.crafting.fimfabric.connection.packetsystem.InPacket;
import fish.crafting.fimfabric.rendering.InformationFeedManager;
import fish.crafting.fimfabric.util.FocuserUtils;
import io.netty.buffer.ByteBufInputStream;

import java.io.IOException;

public class I2FConfirmPacket implements InPacket {
    @Override
    public void readAndExecute(ByteBufInputStream stream) throws IOException {
        boolean compatible = stream.readBoolean();
        int ver = stream.readInt(); //Plugin compatibility version
        long ijPID = stream.readLong();

        ConnectionManager.get().handleCompatibility(compatible, ver);

        if(!compatible){
            //TODO write the error to the instance or something
        }else{
            InformationFeedManager.success("Successfully connected to IntelliJ!", true);
            ConnectionManager.get().attachIJPID((int) ijPID);
        }
    }
}
