package de.steuer.cloud.lib.server;

import java.util.List;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

public interface ICloudServerGroup {

    List<ICloudServerGroup> getServerGroups();
    List<String> getProxyFallbacks();

    int getMinOnlineServers();
    int getMaxOnlineServers();
    int getMaxMemory();

    String getTemplatePath();
    String getGroupName();

    ServerType getServerType();

    enum ServerType {

        BUNGEE_CORD,
        SPIGOT

    }

}
