package de.steuer.cloud.lib.packets;

import de.steuer.cloud.lib.CloudLibrary;
import de.steuer.cloud.lib.networking.packet.Packet;
import de.steuer.cloud.lib.server.ICloudServerGroup;
import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

@NoArgsConstructor
public class SaveServerPacket extends Packet {

    private String targetServerName;
    private ICloudServerGroup cloudServerGroup;

    public SaveServerPacket(final String targetServerName, final ICloudServerGroup cloudServerGroup) {
        this.targetServerName = targetServerName;
        this.cloudServerGroup = cloudServerGroup;
    }

    @Override
    public void read(ByteBuf byteBuf) throws Exception {
        final int length = byteBuf.readInt();
        final byte[] bytes = new byte[length];

        for(int i = 0; i < length; i++)
            bytes[i] = byteBuf.readByte();

        this.targetServerName = new String(bytes);

        this.cloudServerGroup = CloudLibrary.getCloudServerGroupProvider().fromByteBuf(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) throws Exception {
        final byte[] bytes = this.targetServerName.getBytes(StandardCharsets.UTF_8);

        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        CloudLibrary.getCloudServerGroupProvider().toByteBuf(this.cloudServerGroup, byteBuf);
    }

    @Override
    public String toString() {
        return "SaveServerPacket{" +
                "targetServerName='" + targetServerName + '\'' +
                ", cloudServerGroup=" + cloudServerGroup +
                ", uniqueId=" + getUniqueId() +
                '}';
    }

    public ICloudServerGroup getCloudServerGroup() {
        return cloudServerGroup;
    }

    public String getTargetServerName() {
        return targetServerName;
    }
}
