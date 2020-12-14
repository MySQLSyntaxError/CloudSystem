package de.steuer.cloud.lib.networking;

import de.steuer.cloud.lib.networking.client.NetworkingClient;
import de.steuer.cloud.lib.networking.packet.Packet;
import de.steuer.cloud.lib.networking.server.NetworkingServer;
import io.netty.channel.Channel;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class NetworkingHandler {

    public static boolean DEBUG = false;

    private static final List<AbstractPacketHandler> ABSTRACT_PACKET_HANDLERS = new ArrayList<>();
    private static final List<AbstractConnectionListener> ABSTRACT_CONNECTION_LISTENERS = new ArrayList<>();
    private static final Map<String, Channel> CLIENTS = new LinkedHashMap<>();
    private final Map<UUID, ArrayList<Consumer<Packet>>> packetCallbacks = new HashMap<>();

    private AbstractPacketHandler abstractPacketHandler;

    private Type type = Type.CLIENT;

    private NetworkingClient networkingClient;
    private NetworkingServer networkingServer;

    private static NetworkingHandler instance;

    public NetworkingHandler() {
        instance = this;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(networkingClient != null)
                networkingClient.disconnect();

            if(networkingServer != null)
                networkingServer.stopServer();
        }));
    }

    public void clientReconnect() {
        if(this.networkingClient == null)
            return;

        this.networkingClient.reconnect();
    }

    public void clientReconnect(final int delay) {
        if(this.networkingClient == null)
            return;

        this.networkingClient.scheduledConnect(delay * 1000);
    }

    public void clientConnect(final String host, final int port) {
        this.type = Type.CLIENT;

        this.clearPacketHandlers();
        this.clearConnectionListeners();

        if(this.networkingServer != null)
            this.networkingServer.stopServer();

        if(this.networkingClient != null)
            this.networkingClient.disconnect();

        this.networkingClient = new NetworkingClient();
        this.networkingClient.connect(host, port);
    }

    public void startServer(final int port) {
        this.type = Type.SERVER;

        this.clearPacketHandlers();
        this.clearConnectionListeners();

        if(this.networkingServer != null)
            this.networkingServer.stopServer();

        if(this.networkingClient != null)
            this.networkingClient.disconnect();

        this.networkingServer = new NetworkingServer();
        this.networkingServer.startServer(port);
    }

    public void registerPacketHandler(final AbstractPacketHandler abstractPacketHandler) {
        if(ABSTRACT_PACKET_HANDLERS.contains(abstractPacketHandler))
            return;

        ABSTRACT_PACKET_HANDLERS.add(abstractPacketHandler);
    }

    public void unregisterPacketHandler(final AbstractPacketHandler abstractPacketHandler) {
        if(!ABSTRACT_PACKET_HANDLERS.contains(abstractPacketHandler))
            return;

        ABSTRACT_PACKET_HANDLERS.remove(abstractPacketHandler);
    }

    public void clearPacketHandlers() {
        ABSTRACT_PACKET_HANDLERS.clear();
    }

    public void registerConnectionListener(final AbstractConnectionListener abstractConnectionListener) {
        if(ABSTRACT_CONNECTION_LISTENERS.contains(abstractConnectionListener))
            return;

        ABSTRACT_CONNECTION_LISTENERS.add(abstractConnectionListener);
    }

    public void unregisterConnectionListener(final AbstractConnectionListener abstractConnectionListener) {
        if(!ABSTRACT_CONNECTION_LISTENERS.contains(abstractConnectionListener))
            return;

        ABSTRACT_CONNECTION_LISTENERS.remove(abstractConnectionListener);
    }

    public void clearConnectionListeners() {
        ABSTRACT_CONNECTION_LISTENERS.clear();
    }

    public String getClient(final Channel channel) {
        for(final Map.Entry<String, Channel> entry : CLIENTS.entrySet()) {
            if(entry.getValue().equals(channel))
                return entry.getKey();
        }

        return null;
    }

    public void addPacketCallback(final Packet packet, final Consumer<Packet> packetConsumer) {
        if(!this.packetCallbacks.containsKey(packet.getUniqueId()))
            this.packetCallbacks.put(packet.getUniqueId(), new ArrayList<>());

        final ArrayList<Consumer<Packet>> packetConsumers = this.packetCallbacks.get(packet.getUniqueId());

        packetConsumers.add(packetConsumer);

        this.packetCallbacks.put(packet.getUniqueId(), packetConsumers);
    }

    public void removePacketCallback(final Packet packet) {
        if(!this.packetCallbacks.containsKey(packet.getUniqueId()))
            return;

        this.packetCallbacks.remove(packet.getUniqueId());
    }

    public void runPacketCallbacks(final Packet packet) {
        if(!this.packetCallbacks.containsKey(packet.getUniqueId()))
            return;

        for(final Consumer<Packet> consumer : this.packetCallbacks.get(packet.getUniqueId()))
            consumer.accept(packet);
    }

    public enum Type {

        SERVER,
        CLIENT

    }

    public AbstractPacketHandler getAbstractPacketHandler() {
        return abstractPacketHandler;
    }

    public static List<AbstractConnectionListener> getAbstractConnectionListeners() {
        return ABSTRACT_CONNECTION_LISTENERS;
    }

    public static List<AbstractPacketHandler> getAbstractPacketHandlers() {
        return ABSTRACT_PACKET_HANDLERS;
    }

    public static Map<String, Channel> getClients() {
        return CLIENTS;
    }

    public Map<UUID, ArrayList<Consumer<Packet>>> getPacketCallbacks() {
        return packetCallbacks;
    }

    public static NetworkingHandler getInstance() {
        return instance;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setAbstractPacketHandler(AbstractPacketHandler abstractPacketHandler) {
        this.abstractPacketHandler = abstractPacketHandler;
    }

    public NetworkingClient getNetworkingClient() {
        return networkingClient;
    }
}
