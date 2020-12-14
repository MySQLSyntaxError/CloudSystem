package de.steuer.cloud.api.bungee.networking;

import de.steuer.cloud.api.bungee.BungeeCloudAPI;
import de.steuer.cloud.api.bungee.networking.handler.BungeePacketHandler;
import de.steuer.cloud.api.bungee.networking.listener.BungeeConnectionListener;
import de.steuer.cloud.lib.networking.AbstractConnectionListener;
import de.steuer.cloud.lib.networking.AbstractPacketHandler;
import de.steuer.cloud.lib.networking.NetworkingHandler;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

public class Networking {

    private String host = "localhost";

    private int port = 19132;

    private BungeeCloudAPI bungeeCloudAPI;

    private NetworkingHandler networkingHandler;

    private AbstractConnectionListener connectionListener;

    private AbstractPacketHandler packetHandler;

    public Networking(final BungeeCloudAPI bungeeCloudAPI, final String host, final int port) {
        this.bungeeCloudAPI = bungeeCloudAPI;
        this.host = host;
        this.port = port;

        this.networkingHandler = new NetworkingHandler();

        this.networkingHandler.clientConnect(this.host, this.port);
        this.networkingHandler.registerConnectionListener(new BungeeConnectionListener(this, this.host, this.port));
        this.networkingHandler.registerPacketHandler(new BungeePacketHandler(this));
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public BungeeCloudAPI getBungeeCloudAPI() {
        return bungeeCloudAPI;
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
