package de.steuer.cloud.lib.networking.packet.defaults;

import de.steuer.cloud.lib.networking.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class DisconnectPacket extends Packet {

    @Override
    public void read(ByteBuf byteBuf) throws Exception {

    }

    @Override
    public void write(ByteBuf byteBuf) throws Exception {

    }

    @Override
    public String toString() {
        return "DisconnectPacket{" +
                "uniqueId=" + this.getUniqueId() +
                '}';
    }
}
