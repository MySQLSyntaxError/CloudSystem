package de.steuer.cloud.lib.networking;

import de.steuer.cloud.lib.networking.packet.Packet;
import de.steuer.cloud.lib.networking.packet.defaults.DisconnectPacket;
import de.steuer.cloud.lib.networking.packet.defaults.SetNamePacket;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public abstract class AbstractPacketHandler {

    public static final List<Class<? extends Packet>> PACKETS = new ArrayList<>();
    public static final List<Packet> PACKETS_TO_SEND = new ArrayList<>();

    public AbstractPacketHandler() {
        PACKETS.add(DisconnectPacket.class);
        PACKETS.add(SetNamePacket.class);



        this.registerPackets();
    }

    public static void sendPacketInstant(final Packet packet, final Channel channel) {
        if(channel == null)
            return;

        channel.writeAndFlush(packet, channel.voidPromise());
    }

    public void sendPacket(final Packet packet) {
        if(NetworkingHandler.getInstance().getType().equals(NetworkingHandler.Type.CLIENT)) {
            if(NetworkingHandler.getInstance().getNetworkingClient().getChannel() == null) {
                AbstractPacketHandler.PACKETS_TO_SEND.add(packet);
                return;
            }

            this.sendPacket(packet, NetworkingHandler.getInstance().getNetworkingClient().getChannel());
            return;
        }

        if(NetworkingHandler.getClients().size() == 0) {
            AbstractPacketHandler.PACKETS_TO_SEND.add(packet);
            return;
        }

        for(final Channel channel : NetworkingHandler.getClients().values())
            this.sendPacket(packet, channel);
    }

    public void sendPacket(final Packet packet, final Channel channel) {
        if(channel == null)
            return;

        channel.writeAndFlush(packet, channel.voidPromise());
    }

    public void registerPacket(final Class<? extends Packet> packetClass) {
        if(AbstractPacketHandler.PACKETS.contains(packetClass))
            return;

        AbstractPacketHandler.PACKETS.add(packetClass);
    }

    public abstract void handlePacket(final Packet packet, final Channel channel);
    public abstract void registerPackets();
}
