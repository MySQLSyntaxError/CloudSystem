package de.steuer.cloud.api.bungee;

import de.steuer.cloud.api.bungee.listener.ServerConnectListener;
import de.steuer.cloud.api.bungee.networking.Networking;
import de.steuer.cloud.lib.configuration.Configuration;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

public class BungeeCloudAPI extends Plugin {

    public static final String VERSION = "1.0.0";

    private static BungeeCloudAPI instance;

    private Configuration mainConfiguration;

    private Networking networking;

    private String serverName;

    private UUID serverUniqueId;

    private List<String> proxyFallbacks = new ArrayList<>();

    private final ExecutorService pool = Executors.newCachedThreadPool();

    public BungeeCloudAPI() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.initMainConfiguration();

        this.registerCommands();
        this.registerEvents();

        if(ProxyServer.getInstance().getConfig().getListeners().iterator().next().getServerPriority().contains("lobby"))
            ProxyServer.getInstance().getConfig().getListeners().iterator().next().getServerPriority().remove("lobby");

        ProxyServer.getInstance().getServers().remove("lobby");

        final Configuration dataConfiguration = new Configuration(new File(this.getDataFolder(), "data.json"), Configuration.JSON);

        final String host = dataConfiguration.getString("address");
        final int port = dataConfiguration.getInt("port");

        this.serverName = dataConfiguration.getString("serverName");
        this.serverUniqueId = UUID.fromString(dataConfiguration.getString("uuid"));
        this.proxyFallbacks = dataConfiguration.getStringList("fallbacks");

        this.networking = new Networking(this, host, port);

        FileUtils.deleteQuietly(new File(this.getDataFolder(), "data.json"));
    }

    @Override
    public void onDisable() {

    }

    private void initMainConfiguration() {
        if(!this.getDataFolder().isDirectory())
            this.getDataFolder().mkdirs();

        final File configFile = new File(this.getDataFolder(), "config.json");

        this.mainConfiguration = new Configuration(configFile, Configuration.JSON);
    }

    private void registerCommands() {

    }

    private void registerEvents() {
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ServerConnectListener(this));
    }

    public static BungeeCloudAPI getInstance() {
        return instance;
    }

    public Configuration getMainConfiguration() {
        return mainConfiguration;
    }

    public String getServerName() {
        return serverName;
    }

    public UUID getServerUniqueId() {
        return serverUniqueId;
    }

    public List<String> getProxyFallbacks() {
        return proxyFallbacks;
    }

    public ExecutorService getPool() {
        return pool;
    }

    public Networking getNetworking() {
        return networking;
    }
}
