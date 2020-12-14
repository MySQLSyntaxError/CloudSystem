package de.steuer.cloud.lib.packets;

import de.steuer.cloud.lib.networking.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

@NoArgsConstructor
public class ServerStoppedPacket extends Packet {

    private String name;

    public ServerStoppedPacket(final String name) {
        this.name = name;
    }

    @Override
    public void read(ByteBuf byteBuf) throws Exception {
        final int length = byteBuf.readInt();
        final byte[] bytes = new byte[length];

        for(int i = 0; i < length; i++)
            bytes[i] = byteBuf.readByte();

        this.name = new String(bytes);
    }

    @Override
    public void write(ByteBuf byteBuf) throws Exception {
        final byte[] bytes = this.name.getBytes(StandardCharsets.UTF_8);

        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    @Override
    public String toString() {
        return "ServerStoppedPacket{" +
                "name='" + name + '\'' +
                ", uniqueId=" + getUniqueId() +
                '}';
    }

    public String getName() {
        return name;
    }
}
