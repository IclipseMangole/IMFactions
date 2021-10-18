package de.imfactions.listener;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMember;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.functions.raid.RaidUtil;
import de.imfactions.functions.user.User;
import de.imfactions.functions.user.UserUtil;
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
    private Data data;
    private UserUtil userUtil;
    private FactionMemberUtil factionMemberUtil;
    private FactionUtil factionUtil;
    private RaidUtil raidUtil;

    public QuitListener(IMFactions factions) {
        this.factions = factions;
        data = factions.getData();
        factionMemberUtil = data.getFactionMemberUtil();
        userUtil = factions.getData().getUserUtil();
        factionUtil = data.getFactionUtil();
        raidUtil = data.getRaidUtil();
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = userUtil.getUser(player);
        user.addOnlineTime(System.currentTimeMillis() - user.getLastSeen());
        user.setLastSeen(System.currentTimeMillis());

        if (Bukkit.getOnlinePlayers().size() < 10) {
            event.setQuitMessage(player.getDisplayName() + "§8[§4-§8]");
        } else {
            event.setQuitMessage(null);
        }

        if (!factionMemberUtil.isFactionMemberExists(player.getUniqueId()))
            return;
        FactionMember factionMember = factionMemberUtil.getFactionMember(player.getUniqueId());
        if (!raidUtil.isFactionMemberJoinedRaid(factionMember))
            return;
        player.chat("/faction leave");
    }
}
