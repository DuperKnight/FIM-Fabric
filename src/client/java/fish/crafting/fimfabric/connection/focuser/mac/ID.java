package fish.crafting.fimfabric.connection.focuser.mac;

import com.sun.jna.NativeLong;

public final class ID extends NativeLong {

    public ID() {
    }

    public ID(long peer) {
        super(peer);
    }

    public static final ID NIL = new ID(0L);

    public boolean booleanValue() {
        return intValue() != 0;
    }
}

