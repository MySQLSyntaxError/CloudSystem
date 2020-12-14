package de.steuer.cloud.lib.networking.client;

import de.steuer.cloud.lib.networking.AbstractConnectionListener;
import de.steuer.cloud.lib.networking.AbstractPacketHandler;
import de.steuer.cloud.lib.networking.NetworkingHandler;
import de.steuer.cloud.lib.networking.packet.Packet;
import de.steuer.cloud.lib.networking.packet.defaults.DisconnectPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class NetworkingClientHandler extends SimpleChannelInboundHandler<Packet> {

    private Channel channel;

    private NetworkingClient networkingClient;

    public NetworkingClientHandler(final NetworkingClient networkingClient) {
        this.networkingClient = networkingClient;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        if(NetworkingHandler.DEBUG)
            super.exceptionCaught(context, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Packet packet) throws Exception {
        if(packet instanceof DisconnectPacket)
            this.channel.close();

        for(final AbstractPacketHandler abstractPacketHandler : NetworkingHandler.getAbstractPacketHandlers()) {
            abstractPacketHandler.handlePacket(packet, this.channel);
        }

        NetworkingHandler.getInstance().runPacketCallbacks(packet);
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        this.channel = context.channel();

        for(final AbstractConnectionListener abstractConnectionListener : NetworkingHandler.getAbstractConnectionListeners())
            abstractConnectionListener.channelConnected(context);

        this.networkingClient.setChannel(context.channel());

        if(NetworkingHandler.getAbstractPacketHandlers().size() > 0) {
            if(AbstractPacketHandler.PACKETS_TO_SEND.size() > 0) {
                for(final Packet packet : AbstractPacketHandler.PACKETS_TO_SEND)
                    NetworkingHandler.getAbstractPacketHandlers().get(0).sendPacket(packet);
                AbstractPacketHandler.PACKETS_TO_SEND.clear();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        for(final AbstractConnectionListener abstractConnectionListener : NetworkingHandler.getAbstractConnectionListeners())
            abstractConnectionListener.channelDisconnected(context);
    }
}
