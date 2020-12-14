package de.steuer.cloud.lib.networking.packet;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public abstract class Packet {

    private UUID uniqueId = UUID.randomUUID();

    public abstract void read(final ByteBuf byteBuf) throws Exception;
    public abstract void write(final ByteBuf byteBuf) throws Exception;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "uniqueId=" + uniqueId +
                '}';
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
}
