package de.steuer.cloud.api.bukkit;

import de.steuer.cloud.api.bukkit.listener.PlayerLoginListener;
import de.steuer.cloud.api.bukkit.networking.Networking;
import de.steuer.cloud.lib.configuration.Configuration;
import de.steuer.cloud.lib.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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

public class SpigotCloudAPI extends JavaPlugin {

    public static final String VERSION = "1.0.0";

    private static SpigotCloudAPI instance;

    private Configuration mainConfiguration;

    private Networking networking;

    private String serverName;

    private UUID serverUniqueId;

    private final ExecutorService pool = Executors.newCachedThreadPool();

    private final List<UUID> allowedPlayers = new ArrayList<>();

    public SpigotCloudAPI() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.initMainConfiguration();

        this.registerCommands();
        this.registerEvents();

        final Configuration dataConfiguration  =new Configuration(new File(this.getDataFolder(), "data.json"), Configuration.JSON);

        final String host = dataConfiguration.getString("address");
        final int port = dataConfiguration.getInt("port");

        this.serverName = dataConfiguration.getString("serverName");
        this.serverUniqueId = UUID.fromString(dataConfiguration.getString("uuid"));

        this.networking = new Networking(this, host, port);

        FileUtils.deleteQuietly(new File(this.getDataFolder(), "data.json"));

        Logger.getGlobal().info("CloudServer loaded");
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
        Bukkit.getPluginManager().registerEvents(new PlayerLoginListener(this), this);
    }

    public static SpigotCloudAPI getInstance() {
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

    public ExecutorService getPool() {
        return pool;
    }

    public List<UUID> getAllowedPlayers() {
        return allowedPlayers;
    }

    public Networking getNetworking() {
        return networking;
    }
}
