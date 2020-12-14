package de.steuer.cloud.lib.server;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

public class TestClass {

    public static void main(String[] args) {
        final List<ICloudServerGroup> serverGroups = new ArrayList<>();

        serverGroups.add(new ICloudServerGroup() {
            @Override
            public List<ICloudServerGroup> getServerGroups() {
                return null;
            }

            @Override
            public List<String> getProxyFallbacks() {
                return null;
            }

            @Override
            public int getMinOnlineServers() {
                return 0;
            }

            @Override
            public int getMaxOnlineServers() {
                return 0;
            }

            @Override
            public int getMaxMemory() {
                return 0;
            }

            @Override
            public String getTemplatePath() {
                return null;
            }

            @Override
            public String getGroupName() {
                return "test123";
            }

            @Override
            public ServerType getServerType() {
                return ServerType.BUNGEE_CORD;
            }
        });

        serverGroups.add(new ICloudServerGroup() {
            @Override
            public List<ICloudServerGroup> getServerGroups() {
                return null;
            }

            @Override
            public List<String> getProxyFallbacks() {
                return null;
            }

            @Override
            public int getMinOnlineServers() {
                return 0;
            }

            @Override
            public int getMaxOnlineServers() {
                return 0;
            }

            @Override
            public int getMaxMemory() {
                return 0;
            }

            @Override
            public String getTemplatePath() {
                return null;
            }

            @Override
            public String getGroupName() {
                return "test1234";
            }

            @Override
            public ServerType getServerType() {
                return ServerType.SPIGOT;
            }
        });

        System.out.println(serverGroups.stream().filter(iCloudServerGroup -> iCloudServerGroup.getGroupName().equalsIgnoreCase("test123")).findFirst().get().getGroupName());
    }

}
