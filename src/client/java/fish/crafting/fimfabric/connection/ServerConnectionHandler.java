package fish.crafting.fimfabric.connection;

import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import fish.crafting.fimfabric.client.FIMModClient;
import fish.crafting.fimfabric.connection.packetsystem.PacketManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.util.function.Supplier;

public class ServerConnectionHandler extends SimpleChannelInboundHandler<ByteBuf> {
    public static final Supplier<NioEventLoopGroup> CLIENT_IO_GROUP = Suppliers.memoize(() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build()));

    private @Nullable Channel channel = null;
    private @Nullable ChannelFuture tempFuture = null;
    private boolean shutdown = false;


    /**
     * Channel may have not been initialized yet, due to them being async.
     * This method returns whether the channel has been initialized.
     *
     * @return Whether the channel for this connection is ready.
     */
    public boolean isChannelReady(){
        return channel != null;
    }

    public boolean isChannelOver() {
        return shutdown || (channel != null && !channel.isActive());
    }

    public void shutdown() {
        this.shutdown = true;

        if(this.tempFuture != null) {
            try{
                this.tempFuture.cancel(true);
            }catch (Exception ignored) {

            }

            this.tempFuture = null;
        }

        if(this.channel != null){
            this.channel.flush();
            this.channel.close(); //I think this is correct
            this.channel = null;
        }
    }

    public void send(ByteBuf buffer) {
        if(!isChannelReady() || isChannelOver()) return;
        ChannelFuture channelFuture = channel.writeAndFlush(buffer);

        //TODO fix this eventually, maybe add an actual queue system??
        try {
            channelFuture.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        PacketManager.get().handleReceivedPacket(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        FIMModClient.LOGGER.info("FIM Connection died unexpectedly!");
        try{
            ConnectionManager.get().endConnection();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static @Nullable ServerConnectionHandler connect(InetSocketAddress address) {
        ServerConnectionHandler handler = new ServerConnectionHandler();

        try{
            ChannelFuture future = connect(address, handler);
            future.syncUninterruptibly();
        }catch (Exception e) { //Couldn't connect ig
            //ConnectionManager.get().markUnsuccessful(handler);
            return null;
        }

        FIMModClient.LOGGER.info("Successfully connected to IntelliJ!");

        return handler;
    }

    private static ChannelFuture connect(InetSocketAddress address, ServerConnectionHandler handler) {
        EventLoopGroup eventLoopGroup = CLIENT_IO_GROUP.get();
        ChannelFuture future = new Bootstrap().group(eventLoopGroup).handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException channelException) {
                    //empty
                }

                handler.setupChannel(channel);

                ChannelPipeline channelPipeline = channel.pipeline();
                //channelPipeline.addLast("timeout", new ReadTimeoutHandler(30));

                handler.addPipelineHandlers(channelPipeline);
            }
        }).channel(NioSocketChannel.class).connect(address.getAddress(), address.getPort());

        if(!future.isDone()) {
            handler.tempFuture = future;
        }

        /*future.addListener(f -> {
            if(!f.isSuccess()) {
                ConnectionManager.get().markUnsuccessful(handler);
            }
        });*/

        return future;
    }

    private void setupChannel(@NotNull Channel channel) {
        if(this.channel != null) {
            throw new IllegalStateException("Server Connection Handler tried to setup a channel, but a channel was already set up!");
        }

        this.tempFuture = null;
        this.channel = channel;
    }

    private void addPipelineHandlers(ChannelPipeline pipeline) {
        pipeline.addLast(this);
    }
}