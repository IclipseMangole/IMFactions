package de.imfactions.functions.lobby;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class LobbyListener implements Listener {

    private IMFactions imFactions;
    private Data data;

    public LobbyListener(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
    }

    //Kein Schaden
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Location location = event.getEntity().getLocation();
        String world = location.getWorld().getName();

        if (world.equals("world")) {
            event.setCancelled(true);
        }
    }

    //kein PVP
    @EventHandler
    public void onDamagePlayer(EntityDamageByEntityEvent event) {
        Location location = event.getEntity().getLocation();
        String world = location.getWorld().getName();

        if (world.equals("world")) {
            event.setCancelled(true);
            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                player.sendMessage("§cHere is no PVP. Go into the PVP-Zone to kill someone");
            }
        }
    }

    //kein Blöcke Platzieren
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location location = event.getBlock().getLocation();
        String world = location.getWorld().getName();

        if (world.equals("world")) {
            event.setCancelled(true);
        }
    }

    //keine Blöcke zerstören
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        String world = location.getWorld().getName();

        if (world.equals("world")) {
            event.setCancelled(true);
        }
    }
}
