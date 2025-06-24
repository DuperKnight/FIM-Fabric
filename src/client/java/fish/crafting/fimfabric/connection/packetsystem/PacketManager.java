package fish.crafting.fimfabric.connection.packetsystem;

import fish.crafting.fimfabric.connection.packets.*;
import fish.crafting.fimfabric.rendering.InformationFeedManager;
import fish.crafting.fimfabric.rendering.custom.ScreenRenderContext;
import fish.crafting.fimfabric.util.CooldownTracker;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.util.HashMap;
import java.util.Map;

public class PacketManager {

    private static PacketManager instance;
    private final Map<String, InPacket> inPacketMap = new HashMap<>();
    private final CooldownTracker UNKNOWN_MESSAGE_COOLDOWN = CooldownTracker.seconds(1800); //30min

    private PacketManager() {
        instance = this;

        register(new PacketId("i2f_tp"), new I2FTeleportPacket());
        register(new PacketId("i2f_confirm"), new I2FConfirmPacket());
        register(new PacketId("i2f_focus"), new I2FFocusPacket());
        register(new PacketId("i2f_edit"), new I2FEditPacket());
        register(new PacketId("i2f_ij_focused"), new I2FIntelliJFocusedPacket());
        register(new PacketId("i2f_lang"), new I2FLangPacket());
    }

    public void handleReceivedPacket(ByteBuf buf){
        try(var stream = new ByteBufInputStream(buf)) {
            String packetID = stream.readUTF();

            InPacket inPacket = inPacketMap.get(packetID);

            if(inPacket != null) {
                inPacket.readAndExecute(stream);
            }else{
                handleUnknownPacket(packetID);
            }

        }catch (Exception ignored){}
    }

    private void handleUnknownPacket(String packetID){
        if(UNKNOWN_MESSAGE_COOLDOWN.getAndStart()) return;

        InformationFeedManager.FeedLine warn = InformationFeedManager.warn("Received an unknown packet! Try updating your mod! (" + packetID + ")", true);
        warn.durationSeconds(40);
    }

    private InPacket register(PacketId id, InPacket packet) {
        return inPacketMap.put(id.compile(), packet);
    }

    public static PacketManager get(){
        return instance == null ? new PacketManager() : instance;
    }

}
