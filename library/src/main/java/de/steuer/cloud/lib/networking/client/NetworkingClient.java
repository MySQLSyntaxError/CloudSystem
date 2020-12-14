package de.steuer.cloud.lib.networking.client;

import de.steuer.cloud.lib.logging.Logger;
import de.steuer.cloud.lib.networking.NetworkingHandler;
import de.steuer.cloud.lib.networking.packet.PacketDecoder;
import de.steuer.cloud.lib.networking.packet.PacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class NetworkingClient {

    private static final boolean EPOLL = Epoll.isAvailable();
    private static ExecutorService POOL = Executors.newCachedThreadPool();

    private EventLoopGroup eventLoopGroup;

    private Bootstrap bootstrap;

    private ChannelFuture channelFuture;
    private Channel channel;

    private String host = "localhost";
    private int port = 8000;

    public void connect(final String host, final int port) {
        this.host = host;
        this.port = port;

        POOL.execute(() -> {
            this.eventLoopGroup = EPOLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();

            try {
                this.bootstrap = new Bootstrap()
                        .group(this.eventLoopGroup)
                        .channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel.pipeline().addLast(new PacketEncoder());
                                channel.pipeline().addLast(new PacketDecoder());
                                channel.pipeline().addLast(new NetworkingClientHandler(NetworkingClient.this));
                            }
                        });

                this.channelFuture = this.bootstrap.connect(this.host, this.port);
                Logger.getGlobal().info("Connected to master (" + this.host + ":" + this.port + ")");
                this.channelFuture.sync();

                this.channelFuture.sync().channel().closeFuture().syncUninterruptibly();
            } catch (Exception e) {
                if(NetworkingHandler.DEBUG)
                    Logger.getGlobal().error(e.getMessage(), e);
                Logger.getGlobal().info("Cannot connect to master (" + this.host + ":" + this.port + ")");
                NetworkingHandler.getInstance().clientReconnect(3);
            } finally {
                this.eventLoopGroup.shutdownGracefully();
            }
        });
    }

    public void scheduledConnect(final int time) {
        if(this.connected())
            return;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                connect(host, port);
            }
        }, time);
    }

    public void disconnect() {
        try {
            if(this.channelFuture == null && this.channel == null)
                return;

            if(!this.channel.isActive())
                return;

            this.channel.close();
        } catch (Exception e) { }
    }

    public void reconnect() {
        this.scheduledConnect(0);
    }

    public boolean connected() {
        return this.channel != null && this.channel.isActive();
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    public static ExecutorService getPool() {
        return POOL;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
