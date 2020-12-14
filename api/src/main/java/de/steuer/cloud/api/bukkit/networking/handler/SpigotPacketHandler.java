package de.steuer.cloud.api.bukkit.networking.handler;

import de.steuer.cloud.api.bukkit.networking.Networking;
import de.steuer.cloud.lib.logging.Logger;
import de.steuer.cloud.lib.networking.AbstractPacketHandler;
import de.steuer.cloud.lib.networking.packet.Packet;
import de.steuer.cloud.lib.packets.DispatchCommandPacket;
import de.steuer.cloud.lib.packets.VerifyPlayerPacket;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

public class SpigotPacketHandler extends AbstractPacketHandler {

    private final Networking networking;

    public SpigotPacketHandler(final Networking networking) {
        this.networking = networking;
    }

    @Override
    public void handlePacket(final Packet packet, final Channel channel) {
        if(packet instanceof DispatchCommandPacket) {
            final DispatchCommandPacket dispatchCommandPacket = (DispatchCommandPacket) packet;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), dispatchCommandPacket.getCommandLine());
            return;
        }

        if(packet instanceof VerifyPlayerPacket) {
            final VerifyPlayerPacket verifyPlayerPacket = (VerifyPlayerPacket) packet;

            if(this.networking.getSpigotCloudAPI().getAllowedPlayers().contains(verifyPlayerPacket.getUniqueId()))
                return;

            this.networking.getSpigotCloudAPI().getAllowedPlayers().add(verifyPlayerPacket.getUniqueId());
            Logger.getGlobal().info("Verifying " + verifyPlayerPacket.getUniqueId() + " at " + System.currentTimeMillis());

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!networking.getSpigotCloudAPI().getAllowedPlayers().contains(verifyPlayerPacket.getUniqueId()))
                        return;

                    networking.getSpigotCloudAPI().getAllowedPlayers().add(verifyPlayerPacket.getUniqueId());
                }
            }.runTaskLater(this.networking.getSpigotCloudAPI(), 5 * 20);
        }
    }

    @Override
    public void registerPackets() {

    }
}
