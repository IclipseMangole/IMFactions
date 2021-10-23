package de.imfactions.functions.lobby;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerPortalEvent;

public class LobbyListener implements Listener {

    private final IMFactions imFactions;
    private final Data data;

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
            if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
                Player player = (Player) event.getDamager();
                player.sendMessage("§cHere is no PVP. Go into the PVP-Zone to kill someone");
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        World world = player.getWorld();

        if (!world.getName().equalsIgnoreCase("world")) {
            return;
        }
        if (event.getFoodLevel() <= 2) {
            event.setCancelled(true);
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
    public void onPortal(EntityPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPortalPlayer(PlayerPortalEvent event) {
        World world = event.getFrom().getWorld();
        if (world.getName().equalsIgnoreCase("world")) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            player.teleport(data.getPVP_worldSpawn());
            player.playSound(data.getPVP_worldSpawn(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
            player.sendTitle(net.md_5.bungee.api.ChatColor.of("#851818") + "PVP", net.md_5.bungee.api.ChatColor.of("#851818") + "ZONE", 5, 20, 5);
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        World world = event.getLocation().getWorld();

        if (!world.getName().equalsIgnoreCase("world"))
            return;
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL))
            event.setCancelled(true);
    }
}
