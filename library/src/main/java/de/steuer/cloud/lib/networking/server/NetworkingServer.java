package de.steuer.cloud.lib.networking.server;

import de.steuer.cloud.lib.logging.Logger;
import de.steuer.cloud.lib.networking.NetworkingHandler;
import de.steuer.cloud.lib.networking.packet.PacketDecoder;
import de.steuer.cloud.lib.networking.packet.PacketEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class NetworkingServer {

    private static final boolean EPOLL = Epoll.isAvailable();
    private static final ExecutorService POOL = Executors.newCachedThreadPool();

    private EventLoopGroup eventLoopGroup;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture channelFuture;

    private int port = 8000;

    public void startServer(final int port) {
        this.port = port;

        if(this.channelFuture != null)
            this.stopServer();

        POOL.execute(() -> {
            this.eventLoopGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
            try {
                this.serverBootstrap = new ServerBootstrap()
                        .group(this.eventLoopGroup)
                        .channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(new PacketEncoder());
                                socketChannel.pipeline().addLast(new PacketDecoder());
                                socketChannel.pipeline().addLast(new NetworkingServerHandler(NetworkingServer.this));
                            }
                        });

                this.channelFuture = this.serverBootstrap.bind(this.port);
                this.channelFuture.sync();

                this.channelFuture.sync().channel().closeFuture().syncUninterruptibly();
            } catch (Exception e) {
                if(NetworkingHandler.DEBUG)
                    Logger.getGlobal().error(e.getMessage(), e);
            } finally {
                this.eventLoopGroup.shutdownGracefully();
            }
        });
    }

    public void stopServer() {

    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static ExecutorService getPool() {
        return POOL;
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    public ServerBootstrap getServerBootstrap() {
        return serverBootstrap;
    }
}
