package de.imfactions.functions.npc;

import de.imfactions.IMFactions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class NPCManager implements Listener {

    protected static IMFactions factions;
    private PacketReader reader;

    public NPCManager(IMFactions factions){
        factions = this.factions;
        reader = new PacketReader();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            reader.inject(onlinePlayer);
        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event){
        for(NPC npc : NPC.npcs){
            npc.remove();
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            reader.eject(onlinePlayer);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        reader.inject(event.getPlayer());
        NPC.npcs.forEach(npc -> {
            npc.show(event.getPlayer());
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        reader.eject(event.getPlayer());
    }
}
