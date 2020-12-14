package de.steuer.cloud.api.bungee.listener;

import de.steuer.cloud.api.bungee.BungeeCloudAPI;
import de.steuer.cloud.lib.packets.VerifyPlayerPacket;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

public class ServerConnectListener implements Listener {

    private final BungeeCloudAPI bungeeCloudAPI;

    public ServerConnectListener(final BungeeCloudAPI bungeeCloudAPI) {
        this.bungeeCloudAPI = bungeeCloudAPI;
    }

    @EventHandler
    public void onServerConnect(final ServerConnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final ServerInfo serverInfo = event.getTarget();

        final VerifyPlayerPacket verifyPlayerPacket = new VerifyPlayerPacket(player.getUniqueId(), serverInfo.getName());
        this.bungeeCloudAPI.getNetworking().getPacketHandler().sendPacket(verifyPlayerPacket);
    }
}
