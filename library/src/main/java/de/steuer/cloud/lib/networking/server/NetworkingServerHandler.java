package de.steuer.cloud.lib.networking.server;

import de.steuer.cloud.lib.networking.AbstractConnectionListener;
import de.steuer.cloud.lib.networking.AbstractPacketHandler;
import de.steuer.cloud.lib.networking.NetworkingHandler;
import de.steuer.cloud.lib.networking.packet.Packet;
import de.steuer.cloud.lib.networking.packet.defaults.DisconnectPacket;
import de.steuer.cloud.lib.networking.packet.defaults.SetNamePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class NetworkingServerHandler extends SimpleChannelInboundHandler<Packet> {

    private Channel channel;

    private NetworkingServer networkingServer;

    public NetworkingServerHandler(final NetworkingServer networkingServer) {
        this.networkingServer = networkingServer;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        if(NetworkingHandler.DEBUG)
            super.exceptionCaught(context, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Packet packet) throws Exception {
        for(final AbstractPacketHandler abstractPacketHandler : NetworkingHandler.getAbstractPacketHandlers())
            abstractPacketHandler.handlePacket(packet, this.channel);

        if(packet instanceof DisconnectPacket)
            this.channel.close();

        if(packet instanceof SetNamePacket) {
            final SetNamePacket setNamePacket = (SetNamePacket) packet;

            String oldName = "";

            for(final Map.Entry<String, Channel> entry : NetworkingHandler.getClients().entrySet()) {
                final String name = entry.getKey();
                final Channel channel = entry.getValue();

                if(channel.equals(context.channel())) {
                    oldName = name;
                    break;
                }
            }

            if(!oldName.equalsIgnoreCase("")) {
                NetworkingHandler.getClients().remove(oldName);
                NetworkingHandler.getClients().put(setNamePacket.getName(), context.channel());
            }
        }

        NetworkingHandler.getInstance().runPacketCallbacks(packet);
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        this.channel = context.channel();

        for(final AbstractConnectionListener abstractConnectionListener : NetworkingHandler.getAbstractConnectionListeners())
            abstractConnectionListener.channelConnected(context);

        NetworkingHandler.getClients().put("Channel" + NetworkingHandler.getClients().size() + 1, context.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        for(final AbstractConnectionListener abstractConnectionListener : NetworkingHandler.getAbstractConnectionListeners())
            abstractConnectionListener.channelDisconnected(context);

        if(NetworkingHandler.getClients().containsValue(context.channel())) {
            String name = "";

            for(final Map.Entry<String, Channel> entry : NetworkingHandler.getClients().entrySet()) {
                if(entry.getValue().equals(context.channel())) {
                    name = entry.getKey();
                    break;
                }
            }

            if(!name.equalsIgnoreCase(""))
                NetworkingHandler.getClients().remove(name);
        }
    }
}
