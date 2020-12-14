package de.steuer.cloud.lib.packets;

import de.steuer.cloud.lib.networking.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

@NoArgsConstructor
public class DispatchCommandPacket extends Packet {

    private String commandLine;

    public DispatchCommandPacket(final String commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    public void read(ByteBuf byteBuf) throws Exception {
        final int length = byteBuf.readInt();
        final byte[] bytes = new byte[length];

        for(int i = 0; i < length; i++)
            bytes[i] = byteBuf.readByte();

        this.commandLine = new String(bytes);
    }

    @Override
    public void write(ByteBuf byteBuf) throws Exception {
        final byte[] bytes = this.commandLine.getBytes(StandardCharsets.UTF_8);

        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    @Override
    public String toString() {
        return "DispatchCommandPacket{" +
                "commandLine='" + commandLine + '\'' +
                ", uniqueId=" + this.getUniqueId() +
                '}';
    }

    public String getCommandLine() {
        return commandLine;
    }
}
