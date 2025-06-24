package fish.crafting.fimfabric.connection.packets;

import fish.crafting.fimfabric.connection.packetsystem.InPacket;
import fish.crafting.fimfabric.util.PlayerUtil;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.io.IOException;

public class I2FTeleportPacket implements InPacket {
    /*

    DOUBLE - x
    DOUBLE - y
    DOUBLE - z
    BOOLEAN - Has Rotation
     IF TRUE:
       | FLOAT - Pitch
       | FLOAT - Yaw
    STRING - world, empty string if null

     */

    @Override
    public void readAndExecute(ByteBufInputStream stream) throws IOException {
        double x = stream.readDouble();
        double y = stream.readDouble();
        double z = stream.readDouble();

        float pitch = 0f, yaw = 0f;
        boolean hasRot = stream.readBoolean();
        if(hasRot) {
            pitch = stream.readFloat();
            yaw = stream.readFloat();
        }

        String world = stream.readUTF();

        MinecraftClient client = MinecraftClient.getInstance();
        if(client.player == null) return;

        ClientPlayNetworkHandler networkHandler = client.player.networkHandler;
        if(networkHandler == null) return;

        //Setup teleport command
        String teleportCommand = "teleport @s " + x + " " + y + " " + z;
        if(hasRot) {
            teleportCommand += " " + (double) yaw + " " + (double) pitch;
        }

        if(!world.isEmpty()){
            teleportCommand = "execute in " + world + " run " + teleportCommand;
        }

        //If the packet has a rotation, or the player is just far away, teleport.
        if(hasRot || client.player.squaredDistanceTo(x, y, z) > (6 * 6)) {
            PlayerUtil.sendCommand(teleportCommand);
        }

        //If the packet doesn't have a rotation, make the player face the specified location
        if(!hasRot) {
            double facingY = y - client.player.getEyeHeight(client.player.getPose());
            PlayerUtil.sendCommand("rotate @s facing " + x + " " + facingY + " " + z);
        }

    }
}
