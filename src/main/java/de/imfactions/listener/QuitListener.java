package de.imfactions.listener;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import de.imfactions.IMFactions;
import de.imfactions.database.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Iclipse on 16.06.2020
 */
public class QuitListener implements Listener {

    private IMFactions factions;
    private UserManager manager;

    public QuitListener(IMFactions factions) {
        this.factions = factions;
        manager = factions.getData().getUserManager();
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UserManager.User user = manager.getUser(player);
        user.addOnlineTime(System.currentTimeMillis() - user.getLastSeen());
        user.setLastSeen(System.currentTimeMillis());


        if (Bukkit.getOnlinePlayers().size() < 10) {
            event.setQuitMessage(player.getDisplayName() + "§8[§4-§8]");
        } else {
            event.setQuitMessage(null);
        }
    }
}
