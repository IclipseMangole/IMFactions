package de.imfactions.functions.pvp.mobs.abilities;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.pvp.mobs.Orc;
import org.bukkit.ChatColor;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CustomMobListener implements Listener {

    private IMFactions imFactions;
    private Data data;

    public CustomMobListener(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
    }

    @EventHandler
    public void onZombieSpawnEgg(CreatureSpawnEvent event) {

        if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG))
            return;
        if (!(event.getEntity() instanceof Zombie))
            return;
        event.setCancelled(true);
        new Orc(ChatColor.GREEN + "Der Boss", event.getLocation());
    }
}
