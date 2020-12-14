package de.steuer.cloud.api.bungee.networking.listener;

import de.steuer.cloud.api.bungee.networking.Networking;
import de.steuer.cloud.lib.logging.Logger;
import de.steuer.cloud.lib.networking.AbstractConnectionListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

public class BungeeConnectionListener extends AbstractConnectionListener {

    private final Networking networking;
    private final String host;
    private final int port;

    public BungeeConnectionListener(final Networking networking, final String host, final int port) {
        this.networking = networking;
        this.host = host;
        this.port = port;
    }

    @Override
    public void channelConnected(ChannelHandlerContext context) {

    }

    @Override
    public void channelDisconnected(ChannelHandlerContext context) {
        Logger.getGlobal().info("Client lost connection to master (" + this.host + ":" + this.port + ")");
        Logger.getGlobal().info("Reconnecting to master in 3 seconds...");
        networking.getNetworkingHandler().clientReconnect(3);
    }
}
