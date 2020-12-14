package de.steuer.cloud.lib.server;

import io.netty.buffer.ByteBuf;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

public interface ICloudServer extends ICloudServerData {

    void startServer();

    void stopServer();
    void forceStopServer();

}
