package de.steuer.cloud.lib.server;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

public interface ICloudServerData {

    ICloudServerGroup getCloudServerGroup();

    UUID getUniqueId();

    int getId();
    int getPort();
    int getSlots();
    int getOnlinePlayers();

    ServerMode getServerMode();

    String getHost();
    String getName();
    String getMOTD();

    void toByteBuf(final ByteBuf byteBuf);
    void fromByteBuf(final ByteBuf byteBuf);

}
