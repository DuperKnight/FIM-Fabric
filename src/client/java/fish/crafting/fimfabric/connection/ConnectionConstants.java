package fish.crafting.fimfabric.connection;

public class ConnectionConstants {

    private ConnectionConstants() {

    }

    public static final long KEEP_ALIVE_MS = 10_000L;
    public static final long ALIVE_TICK_CHECK_MS = 2_000L;
    public static final long RECONNECT_MS = 5_000L;
    public static final int PORT = 1101;

    //If the plugin and the mod mismatch these, they can't work together.
    public static final int COMPATIBILITY_VERSION = 3;

}
