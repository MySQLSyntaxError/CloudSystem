package de.steuer.cloud.lib.packets;

import de.steuer.cloud.lib.networking.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

@NoArgsConstructor
public class VerifyPlayerPacket extends Packet {

    private UUID uuid;
    private String serverName;

    public VerifyPlayerPacket(final UUID uuid, final String serverName) {
        this.uuid = uuid;
        this.serverName = serverName;
    }

    @Override
    public void read(ByteBuf byteBuf) throws Exception {
        int length;
        byte[] bytes;

        // UUID
        length = byteBuf.readInt();
        bytes = new byte[length];

        for(int i = 0; i < length; i++)
            bytes[i] = byteBuf.readByte();

        this.uuid = UUID.fromString(new String(bytes));

        // ServerName
        length = byteBuf.readInt();
        bytes = new byte[length];

        for(int i = 0; i < length; i++)
            bytes[i] = byteBuf.readByte();

        this.serverName = new String(bytes);
    }

    @Override
    public void write(ByteBuf byteBuf) throws Exception {
        byte[] bytes;

        // UUID
        bytes = this.uuid.toString().getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        // ServerName
        bytes = this.serverName.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
