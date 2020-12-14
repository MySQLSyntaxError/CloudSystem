package de.steuer.cloud.lib.networking.packet.defaults;

import de.steuer.cloud.lib.networking.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class SetNamePacket extends Packet {

    private String name;

    public SetNamePacket() {
        this.name = "";
    }

    public SetNamePacket(final String name) {
        this.name = name;
    }

    @Override
    public void read(ByteBuf byteBuf) throws Exception {
        final int length = byteBuf.readInt();
        final byte[] bytes = new byte[length];

        for(int i = 0; i < length; i++)
            bytes[i] = byteBuf.readByte();

        this.setName(new String(bytes));
    }

    @Override
    public void write(ByteBuf byteBuf) throws Exception {
        final byte[] bytes = this.name.getBytes(StandardCharsets.UTF_8);

        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SetNamePacket{" +
                "name='" + name + '\'' +
                ", uniqueId=" + this.getUniqueId() +
                '}';
    }
}
