package de.imfactions.functions.npc;

import de.imfactions.IMFactions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class NPCUtil implements Listener {

    private IMFactions factions;
    private PacketReader reader;

    public NPCUtil(IMFactions factions) {
        this.factions = factions;
        reader = new PacketReader();
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event) {
        for (NPC npc : NPC.npcs) {
            npc.remove();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("Der Server restartet/reloadet");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        reader.inject(player);
        Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam("npcHideName") == null ? scoreboard.registerNewTeam("npcHideName") : scoreboard.getTeam("npcHideName");
        team.addEntry("Grumm");
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        NPC.npcs.forEach(npc -> {
            npc.show(player);
        });
    }


    @EventHandler
    public void onSwitch(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        NPC.npcs.forEach(npc -> {
            npc.show(player);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        reader.eject(event.getPlayer());
    }
}