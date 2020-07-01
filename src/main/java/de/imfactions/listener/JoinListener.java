package de.imfactions.listener;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import de.imfactions.IMFactions;
import de.imfactions.database.UserManager;
import de.imfactions.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

/**
 * Created by Iclipse on 16.06.2020
 */
public class JoinListener implements Listener {

    private IMFactions factions;
    private UserManager manager;


    public JoinListener(IMFactions factions) {
        this.factions = factions;
        manager = factions.getData().getUserManager();
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (!event.getHostname().equalsIgnoreCase("207.180.241.195:25565")) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage("§4Please join our Proxy to connect to this Server.\n Thank you!");
        }
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = UUIDFetcher.getUUID(player);
        UserManager.User user;
        if (!manager.isUserExists(uuid)) {
            user = manager.createUser(uuid);
        } else {
            user = manager.getUser(uuid);
        }
        user.setLastSeen(System.currentTimeMillis());


        if (Bukkit.getOnlinePlayers().size() < 10) {
            event.setJoinMessage(player.getDisplayName() + "§8[§a+§8]");
        } else {
            event.setJoinMessage(null);
        }
    }
}
