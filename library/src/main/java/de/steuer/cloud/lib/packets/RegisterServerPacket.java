package de.steuer.cloud.lib.packets;

import de.steuer.cloud.lib.networking.packet.Packet;
import de.steuer.cloud.lib.server.ICloudServer;
import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

@NoArgsConstructor
public class RegisterServerPacket extends Packet {

    private ICloudServer cloudServer;

    public RegisterServerPacket(final ICloudServer cloudServer) {
        this.cloudServer = cloudServer;
    }

    @Override
    public void read(ByteBuf byteBuf) throws Exception {
        this.cloudServer.fromByteBuf(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) throws Exception {
        this.cloudServer.toByteBuf(byteBuf);
    }

    @Override
    public String toString() {
        return "RegisterServerPacket{" +
                "cloudServer=" + cloudServer +
                ", uniqueId=" + getUniqueId() +
                '}';
    }

    public ICloudServer getCloudServer() {
        return cloudServer;
    }
}
