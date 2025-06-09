package fish.crafting.fimfabric.connection.packetsystem;

import fish.crafting.fimfabric.connection.ConnectionManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class OutPacket {

    protected abstract PacketId getId();
    protected abstract void write(ByteBufOutputStream stream) throws IOException;

    public void send() {
        ByteBuf buffer = Unpooled.buffer();
        try(var stream = new ByteBufOutputStream(buffer)) {
            stream.writeUTF(getId().compile()); //Identifier

            write(stream);
            ConnectionManager.get().send(stream.buffer());
        }catch (Exception ignored){}
    }

    /*fun send(audience: MinecraftAudience = MinecraftAudience.LATEST) {
        val buffer = Unpooled.buffer()
        val stream = ByteBufOutputStream(buffer)

        stream.use {
            it.writeUTF(getId().compile()) //Identifier

            write(it)

            if(audience == MinecraftAudience.LATEST) {
                MinecraftManager.sendToLatestInstance(buffer)
            }else if(audience == MinecraftAudience.ALL) {
                MinecraftManager.sendToAllInstances(buffer)
            }
        }
    }*/

    protected final void writeUUID(@NotNull ByteBufOutputStream stream) throws IOException{
        stream.writeUTF(ConnectionManager.uuid().toString());
    }

}
