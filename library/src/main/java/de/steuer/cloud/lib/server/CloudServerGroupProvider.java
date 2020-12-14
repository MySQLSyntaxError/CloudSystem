package de.steuer.cloud.lib.server;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

public class CloudServerGroupProvider {

    private static final List<ICloudServerGroup> CLOUD_SERVER_GROUPS = new ArrayList<>();

    public List<ICloudServerGroup> getProxyGroups() {
        return CLOUD_SERVER_GROUPS.stream().filter(iCloudServerGroup -> iCloudServerGroup.getServerType().equals(ICloudServerGroup.ServerType.BUNGEE_CORD)).collect(Collectors.toList());
    }

    public List<ICloudServerGroup> getSpigotGroups() {
        return CLOUD_SERVER_GROUPS.stream().filter(iCloudServerGroup -> iCloudServerGroup.getServerType().equals(ICloudServerGroup.ServerType.SPIGOT)).collect(Collectors.toList());
    }

    public ICloudServerGroup getServerGroup(final String serverGroupName) {
        return CLOUD_SERVER_GROUPS.stream().filter(iCloudServerGroup -> iCloudServerGroup.getGroupName().equalsIgnoreCase(serverGroupName)).findAny().get();
    }

    public void toByteBuf(final ICloudServerGroup cloudServerGroup, final ByteBuf byteBuf) {
        byte[] bytes;

        // ServerType
        bytes = cloudServerGroup.getServerType().name().getBytes(StandardCharsets.UTF_8);
        write(byteBuf, bytes);

        // GroupName
        bytes = cloudServerGroup.getGroupName().getBytes(StandardCharsets.UTF_8);
        write(byteBuf, bytes);

        // MinOnlineServers
        byteBuf.writeInt(cloudServerGroup.getMinOnlineServers());

        // MaxOnlineServers
        byteBuf.writeInt(cloudServerGroup.getMaxOnlineServers());

        // MaxMemory
        byteBuf.writeInt(cloudServerGroup.getMaxMemory());

        // TemplatePath
        bytes = cloudServerGroup.getTemplatePath().getBytes(StandardCharsets.UTF_8);
        write(byteBuf, bytes);

        // ProxyFallbacks
        byteBuf.writeInt(cloudServerGroup.getProxyFallbacks().size());

        for(final String fallback : cloudServerGroup.getProxyFallbacks()) {
            bytes = fallback.getBytes(StandardCharsets.UTF_8);
            write(byteBuf, bytes);
        }
    }

    public ICloudServerGroup fromByteBuf(final ByteBuf byteBuf) {
        int length;
        byte[] bytes;

        // ServerType
        length = byteBuf.readInt();
        bytes = new byte[length];

        for(int i = 0; i < length; i++)
            bytes[i] = byteBuf.readByte();

        final String serverType = new String(bytes);

        // Name
        length = byteBuf.readInt();
        bytes = new byte[length];

        for(int i = 0; i < length; i++)
            bytes[i] = byteBuf.readByte();

        final String name = new String(bytes);

        // MinOnlineServers
        final int minOnlineServers = byteBuf.readInt();

        // MaxOnlineServers
        final int maxOnlineServers = byteBuf.readInt();

        // MaxMemory
        final int maxMemory = byteBuf.readInt();

        // TemplatePath
        length = byteBuf.readInt();
        bytes = new byte[length];

        for(int i = 0; i < length; i++)
            bytes[i] = byteBuf.readByte();

        final String templatePath = new String(bytes);

        // ProxyFallbacks
        final List<String> proxyFallbacks = new ArrayList<>();
        final int arraySize = byteBuf.readInt();

        for(int i = 0; i < arraySize; i++) {
            length = byteBuf.readInt();
            bytes = new byte[length];

            for(int j = 0; j < length; j++)
                bytes[j] = byteBuf.readByte();

            proxyFallbacks.add(new String(bytes));
        }

        return new ICloudServerGroup() {
            @Override
            public List<ICloudServerGroup> getServerGroups() {
                return CLOUD_SERVER_GROUPS;
            }

            @Override
            public List<String> getProxyFallbacks() {
                return proxyFallbacks;
            }

            @Override
            public int getMinOnlineServers() {
                return minOnlineServers;
            }

            @Override
            public int getMaxOnlineServers() {
                return maxOnlineServers;
            }

            @Override
            public int getMaxMemory() {
                return maxMemory;
            }

            @Override
            public String getTemplatePath() {
                return templatePath;
            }

            @Override
            public String getGroupName() {
                return name;
            }

            @Override
            public ServerType getServerType() {
                return ServerType.valueOf(serverType);
            }
        };
    }

    private void write(final ByteBuf byteBuf, final byte[] bytes) {
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

}
