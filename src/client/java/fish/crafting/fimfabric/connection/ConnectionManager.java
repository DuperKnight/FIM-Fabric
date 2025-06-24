package fish.crafting.fimfabric.connection;

import fish.crafting.fimfabric.client.FIMModClient;
import fish.crafting.fimfabric.connection.focuser.FocuserType;
import fish.crafting.fimfabric.connection.focuser.MacProgramFocuser;
import fish.crafting.fimfabric.connection.packets.F2IFocusPacket;
import fish.crafting.fimfabric.connection.packets.F2IInitPacket;
import fish.crafting.fimfabric.connection.packets.F2IKeepAlivePacket;
import fish.crafting.fimfabric.rendering.InformationFeedManager;
import fish.crafting.fimfabric.util.FocuserUtils;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.UUID;

public class ConnectionManager {

    /**
     * Tracks whether the last used language is kotlin (True) or java (False)
     */
    public static boolean kotlin;

    private boolean sendFeed = true;
    private long reconnectMsOverride = 0;
    private long lastDeadCheckMS = 0L, lastReconnectMS = 0L, lastKeepAlive = 0L;
    private static ConnectionManager instance;
    private boolean shutdown = false;
    private final UUID uuid;
    private @Nullable ServerConnectionHandler connection = null;
    private Integer intelliJpid = null;

    private ConnectionManager() {
        instance = this;
        uuid = UUID.randomUUID();
    }

    public static ConnectionManager get(){
        return instance == null ? new ConnectionManager() : instance;
    }

    public static UUID uuid(){
        return get().uuid;
    }

    /**
     * A check whether the client CAN open a connection
     */
    public boolean isWaitingForConnection() {
        if(shutdown) return false;

        return connection == null;
    }

    public boolean isConnected(){
        return !shutdown && connection != null;
    }

    /**
     * Start a connection to a specified address
     * @param address Address to connect to
     */
    public void startConnection(InetSocketAddress address) {
        if(shutdown || connection != null) return;

        connection = ServerConnectionHandler.connect(address);

        if(connection != null) { // Connected successfully
            new F2IInitPacket().send(); //Send init packet
        }
    }

    /**
     * End the connection (if one exists), but don't shut down the Connection Manager.
     * This means that new connections are still up for opening.
     */
    public void endConnection() {
        if(shutdown || connection == null) return;
        connection.shutdown();
        connection = null;
        intelliJpid = null;
    }

    /**
     * Tick to check if the current connection died in the meantime
     */
    public void tick() {
        if(shutdown) return;

        tickNewConnection();
        tickDeadCheck();
        tickKeepAlive();
    }

    private void tickKeepAlive() {
        if(connection == null) return;

        long now = System.currentTimeMillis();
        if(lastKeepAlive >= now - ConnectionConstants.KEEP_ALIVE_MS) return;

        lastKeepAlive = now;
        new F2IKeepAlivePacket().send();
    }

    private void tickNewConnection(){
        if(!isWaitingForConnection()) return; //Doesn't need new connection
        //Connection is null.

        long now = System.currentTimeMillis();
        if(lastReconnectMS >= now - Math.max(reconnectMsOverride, ConnectionConstants.RECONNECT_MS)) return;

        lastReconnectMS = now;

        InetSocketAddress address = new InetSocketAddress("localhost", ConnectionConstants.PORT);
        startConnection(address);
    }

    private void tickDeadCheck(){
        if(connection == null) return;
        if(!connection.isChannelReady()) return; //Channel is still initializing

        long now = System.currentTimeMillis();
        if(lastDeadCheckMS >= now - ConnectionConstants.ALIVE_TICK_CHECK_MS) return;

        lastDeadCheckMS = now;

        if(connection.isChannelOver()) { //RIP
            FIMModClient.LOGGER.info("Connection to IntelliJ was terminated!");

            if(sendFeed){
                InformationFeedManager.info("Connection to IntelliJ was terminated.", true);
            }

            endConnection();
        }
    }

    /**
     * Completely shut down the Connection Manager.
     * This will close the current connections, and no more connections can be opened.
     */
    public void shutdown() {
        shutdown = true;

        if(connection != null) {
            connection.shutdown();
            connection = null;
            intelliJpid = null;
        }
    }

    public void attachIJPID(int ijpid){
        this.intelliJpid = ijpid;
    }

    public void focusIntelliJ(){
        if(intelliJpid == null) return;

        FocuserType current = FocuserType.getCurrent();
        if(current == FocuserType.WINDOWS || current == FocuserType.LINUX){
            FocuserUtils.allowForegroundSetting(intelliJpid);
            new F2IFocusPacket().send();
        }else if(current == FocuserType.MAC){
            MacProgramFocuser.focus(intelliJpid);
        }

    }

    public void send(ByteBuf buffer) {
        if(shutdown || connection == null) return;
        connection.send(buffer);
    }

    /**
     * Client and IntelliJ have different compatibility versions.
     * For now, just slow down how often it will try to re-connect. Just in case they update IntelliJ or something.
     */
    public void handleCompatibility(boolean compatible, int intelliJver) {
        if(!compatible){
            reconnectMsOverride = 20_000; //20s
            if(sendFeed){
                InformationFeedManager.FeedLine error = InformationFeedManager.error("Incompatible FIM versions! Please update your mods! (IJ: " + intelliJver + ", MC: " + ConnectionConstants.COMPATIBILITY_VERSION + ")", true);
                error.durationSeconds(30);
            }
            sendFeed = false;
        }else{
            reconnectMsOverride = 0;
            sendFeed = true;
        }
    }
}
