package de.steuer.cloud.lib.networking;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public abstract class AbstractConnectionListener {

    public abstract void channelConnected(final ChannelHandlerContext context);
    public abstract void channelDisconnected(final ChannelHandlerContext context);

}
