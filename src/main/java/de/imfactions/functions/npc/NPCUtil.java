package de.imfactions.functions.npc;

import de.imfactions.IMFactions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

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
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            reader.eject(onlinePlayer);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        reader.inject(event.getPlayer());
        NPC.npcs.forEach(npc -> {
            npc.show(event.getPlayer());
        });
    }

    @EventHandler
    public void onSwitch(PlayerChangedWorldEvent event) {
        /*
        Player player = event.getPlayer();
        NPC.npcs.forEach(npc -> {
            if (event.getPlayer().getWorld().equals(npc.getEntityPlayer().getWorld().getWorld())) {
                System.out.println("Wird " + player.getName() + " gezeigt!");
                npc.show(player);
            }else if(event.getFrom().equals(npc.getEntityPlayer().getWorld().getWorld())){
                npc.remove();
            }
        });

         */

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        reader.eject(event.getPlayer());
    }
}
