package de.steuer.cloud.api.bungee.networking.handler;

import de.steuer.cloud.api.bungee.networking.Networking;
import de.steuer.cloud.lib.networking.AbstractPacketHandler;
import de.steuer.cloud.lib.networking.packet.Packet;
import de.steuer.cloud.lib.packets.DispatchCommandPacket;
import de.steuer.cloud.lib.packets.RegisterServerPacket;
import de.steuer.cloud.lib.packets.UnregisterServerPacket;
import io.netty.channel.Channel;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

public class BungeePacketHandler extends AbstractPacketHandler {

    private final Networking networking;

    public BungeePacketHandler(final Networking networking) {
        this.networking = networking;
    }

    @Override
    public void handlePacket(Packet packet, Channel channel) {
        if(packet instanceof RegisterServerPacket) {
            final RegisterServerPacket registerServerPacket = (RegisterServerPacket) packet;

            if(ProxyServer.getInstance().getServers().containsKey(registerServerPacket.getCloudServer().getName()))
                return;

            final ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(
                    registerServerPacket.getCloudServer().getName(),
                    InetSocketAddress.createUnresolved(registerServerPacket.getCloudServer().getHost(), registerServerPacket.getCloudServer().getPort()),
                    registerServerPacket.getCloudServer().getMOTD(),
                    false
            );

            ProxyServer.getInstance().getServers().put(registerServerPacket.getCloudServer().getName(), serverInfo);

            if(this.networking.getBungeeCloudAPI().getProxyFallbacks().contains(registerServerPacket.getCloudServer().getCloudServerGroup().getGroupName()))
                ProxyServer.getInstance().getConfig().getListeners().iterator().next().getServerPriority().add(registerServerPacket.getCloudServer().getName());

            return;
        }

        if(packet instanceof UnregisterServerPacket) {
            final UnregisterServerPacket unregisterServerPacket = (UnregisterServerPacket) packet;

            if(!ProxyServer.getInstance().getServers().containsKey(unregisterServerPacket.getCloudServer().getName()))
                return;

            ProxyServer.getInstance().getServers().remove(unregisterServerPacket.getCloudServer().getName());

            if(this.networking.getBungeeCloudAPI().getProxyFallbacks().contains(unregisterServerPacket.getCloudServer().getCloudServerGroup().getGroupName()))
                ProxyServer.getInstance().getConfig().getListeners().iterator().next().getServerPriority().remove(unregisterServerPacket.getCloudServer().getName());

            return;
        }

        if(packet instanceof DispatchCommandPacket) {
            final DispatchCommandPacket dispatchCommandPacket = (DispatchCommandPacket) packet;
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), dispatchCommandPacket.getCommandLine());
            return;
        }
    }

    @Override
    public void registerPackets() {

    }
}
