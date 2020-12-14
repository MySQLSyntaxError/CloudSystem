package de.steuer.cloud.lib.networking.packet;

import de.steuer.cloud.lib.logging.Logger;
import de.steuer.cloud.lib.networking.AbstractPacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.UUID;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        final int id = byteBuf.readInt();
        final Class<? extends Packet> packetClass = AbstractPacketHandler.PACKETS.get(id);

        if(packetClass == null)
            Logger.getGlobal().warning("Could not find packet with id (" + id + ")");

        final Packet packet = packetClass.newInstance();
        final int length = byteBuf.readInt();
        final byte[] bytes = new byte[length];

        for(int i = 0; i < length; i++)
            bytes[i] = byteBuf.readByte();

        packet.setUniqueId(UUID.fromString(new String(bytes)));

        packet.read(byteBuf);
        list.add(packet);
    }
}
