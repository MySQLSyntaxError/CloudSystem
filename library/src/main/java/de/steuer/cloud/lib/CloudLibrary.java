package de.steuer.cloud.lib;

import de.steuer.cloud.lib.server.CloudServerGroupProvider;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class CloudLibrary {

    private static CloudServerGroupProvider cloudServerGroupProvider;

    public static CloudServerGroupProvider getCloudServerGroupProvider() {
        return cloudServerGroupProvider == null ? cloudServerGroupProvider = new CloudServerGroupProvider() : cloudServerGroupProvider;
    }
}
