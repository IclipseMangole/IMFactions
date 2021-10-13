package de.imfactions.functions.lobby;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;

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

    @EventHandler
    public void onExplode(BlockExplodeEvent event){
        World world = event.getBlock().getWorld();
        if(!world.getName().equalsIgnoreCase("world"))
            return;
        event.blockList().clear();
        event.setCancelled(true);
    }

    @EventHandler
    public void onBurn(BlockBurnEvent event){
        World world = event.getBlock().getWorld();
        if(!world.getName().equalsIgnoreCase("world"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onFireSpread(BlockSpreadEvent event){
        World world = event.getBlock().getWorld();
        if(!world.getName().equalsIgnoreCase("world"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event){
        World world = event.getBlock().getWorld();
        if(!world.getName().equalsIgnoreCase("world"))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPortal(EntityPortalEnterEvent event){
        World world = event.getEntity().getWorld();
        if(!world.getName().equalsIgnoreCase("world"))
            return;
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        player.teleport(data.getPVP_worldSpawn());
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
        player.sendTitle(ChatColor.DARK_RED + "PVP", ChatColor.DARK_RED + "ZONE", 5, 20, 5);
    }
}
