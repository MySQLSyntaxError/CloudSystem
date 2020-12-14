package de.steuer.cloud.lib.networking.packet;

import de.steuer.cloud.lib.logging.Logger;
import de.steuer.cloud.lib.networking.AbstractPacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext context, Packet packet, ByteBuf byteBuf) throws Exception {
        final int id = AbstractPacketHandler.PACKETS.indexOf(packet.getClass());

        if(id == -1)
            Logger.getGlobal().warning("Couldnt find id of packet (" + packet.getClass().getSimpleName() + ")");

        byteBuf.writeInt(id);

        final UUID uuid = packet.getUniqueId();

        final byte[] bytes = uuid.toString().getBytes(StandardCharsets.UTF_8);

        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        packet.write(byteBuf);
    }
}
