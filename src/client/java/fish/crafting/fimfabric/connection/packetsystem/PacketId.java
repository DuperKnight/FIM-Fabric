package fish.crafting.fimfabric.connection.packetsystem;

import org.jetbrains.annotations.NotNull;

public record PacketId(@NotNull String id) {
    public String compile() {
        return id;
    }
}
