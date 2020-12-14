package de.steuer.cloud.api.bukkit.listener;

import de.steuer.cloud.api.bukkit.SpigotCloudAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * @author Steuer
 * Created: 14.12.2020
 */

public class PlayerLoginListener implements Listener {

    private final SpigotCloudAPI spigotCloudAPI;

    public PlayerLoginListener(final SpigotCloudAPI spigotCloudAPI) {
        this.spigotCloudAPI = spigotCloudAPI;
    }

    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event) {
        final Player player = event.getPlayer();

        if(this.spigotCloudAPI.getAllowedPlayers().contains(player.getUniqueId()))
            this.spigotCloudAPI.getAllowedPlayers().remove(player.getUniqueId());
        else
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cConnection failed");
    }
}
