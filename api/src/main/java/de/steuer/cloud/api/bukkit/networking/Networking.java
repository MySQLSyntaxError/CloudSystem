package de.steuer.cloud.api.bukkit.networking;

import de.steuer.cloud.api.bukkit.SpigotCloudAPI;
import de.steuer.cloud.api.bukkit.networking.handler.SpigotPacketHandler;
import de.steuer.cloud.api.bukkit.networking.listener.SpigotConnectionListener;
import de.steuer.cloud.lib.networking.AbstractConnectionListener;
import de.steuer.cloud.lib.networking.AbstractPacketHandler;
import de.steuer.cloud.lib.networking.NetworkingHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

public class Networking {

    private String host = "localhost";
    private int port = 19132;

    private SpigotCloudAPI spigotCloudAPI;

    private NetworkingHandler networkingHandler;

    private AbstractConnectionListener connectionListener;
    private AbstractPacketHandler packetHandler;

    public Networking(final SpigotCloudAPI spigotCloudAPI, final String host, final int port) {
        this.spigotCloudAPI = spigotCloudAPI;
        this.host = host;
        this.port = port;

        this.networkingHandler = new NetworkingHandler();

        this.networkingHandler.clientConnect(this.host, this.port);

        this.networkingHandler.registerConnectionListener(this.connectionListener = new SpigotConnectionListener(this, this.host, this.port));
        this.networkingHandler.registerPacketHandler(new SpigotPacketHandler(this));
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public SpigotCloudAPI getSpigotCloudAPI() {
        return spigotCloudAPI;
    }

    public NetworkingHandler getNetworkingHandler() {
        return networkingHandler;
    }

    public AbstractConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public AbstractPacketHandler getPacketHandler() {
        return packetHandler;
    }
}
