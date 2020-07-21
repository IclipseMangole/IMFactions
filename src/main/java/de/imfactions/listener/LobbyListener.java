package de.imfactions.listener;

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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

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

    //Teleport Abbruch
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location location = event.getFrom();
        String world = location.getWorld().getName();
        Player player = event.getPlayer();

        if (world.equals("world")) {
            if (data.getScheduler().getCountdowns().containsKey(player)) {
                if(!(event.getTo().getX() == event.getFrom().getX() && event.getTo().getY() == event.getFrom().getY() && event.getTo().getZ() == event.getFrom().getZ())) {
                    player.sendMessage("§cYour Teleport has been interrupted at " + data.getScheduler().getCountdowns().get(player) + " seconds");
                    player.removePotionEffect(PotionEffectType.CONFUSION);
                    data.getScheduler().getCountdowns().remove(player);
                    data.getScheduler().getLocations().remove(player);
                }
            }
        }

    }

    //Teleport Abbruch
    @EventHandler
    public void onTeleportDamage(EntityDamageEvent event){
        Location location = event.getEntity().getLocation();
        String world = location.getWorld().getName();

        if(world.equals("FactionPlots_world")){
            if(event.getEntity() instanceof Player){
                Player player = (Player) event.getEntity();
                if(data.getScheduler().getLocations().containsKey(player)){
                    player.sendMessage("§cYour Teleport has been interrupted at " + data.getScheduler().getCountdowns().get(player) + " seconds");
                    player.removePotionEffect(PotionEffectType.CONFUSION);
                    data.getScheduler().getLocations().remove(player);
                    data.getScheduler().getCountdowns().remove(player);
                }
            }
        }
    }
}
