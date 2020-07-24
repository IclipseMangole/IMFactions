package de.imfactions.listener;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.util.LocationChecker;
import de.imfactions.util.ScoreboardSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Date;


public class TeleportListener implements Listener {

    private IMFactions imFactions;
    private Data data;

    public TeleportListener(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        String world = player.getWorld().getName();
        Location playerLocation = event.getFrom();
        PlayerTeleportEvent.TeleportCause teleportCause = event.getCause();

        if (teleportCause.equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {

            //von Lobby zu PVP

            if (world.equals("world")) {

                Location leftPortal0 = new Location(Bukkit.getWorld(world), 10, 30, 67);
                Location leftPortal1 = new Location(Bukkit.getWorld(world), 1, 39, 67);
                Location rightPortal0 = new Location(Bukkit.getWorld(world), -1, 30, 67);
                Location rightPortal1 = new Location(Bukkit.getWorld(world), -10, 39, 67);

                //linkes Portal

                if (LocationChecker.isLocationInsideCube(playerLocation, leftPortal0, leftPortal1)) {
                    Location location = new Location(Bukkit.getWorld("FactionPVP_world"), 53, 79, 1276.5, playerLocation.getYaw()-180, playerLocation.getPitch());
                    player.teleport(location);

                    //rechtes Portal

                } else if (LocationChecker.isLocationInsideCube(playerLocation, rightPortal0, rightPortal1)) {
                    Location location = new Location(Bukkit.getWorld("FactionPVP_world"), 74, 79, 1276.5, playerLocation.getYaw()-180, playerLocation.getPitch());
                    player.teleport(location);
                }

                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1 ,1);

                player.sendTitle("§l§4PVP-ZONE", "", 10, 100, 10);

                //PVP zu Lobby

            } else if (world.equals("FactionPVP_world")) {

                Location leftPortal0 = new Location(Bukkit.getWorld(world), 78, 79, 1277);
                Location leftPortal1 = new Location(Bukkit.getWorld(world), 69, 88, 1277);
                Location rightPortal0 = new Location(Bukkit.getWorld(world), 57, 79, 1277);
                Location rightPortal1 = new Location(Bukkit.getWorld(world), 48, 88, 1277);

                //linkes Portal

                if (LocationChecker.isLocationInsideCube(playerLocation, leftPortal0, leftPortal1)) {
                    Location location = new Location(Bukkit.getWorld("world"), -5, 30, 66.5, playerLocation.getYaw()-180, playerLocation.getPitch());
                    player.teleport(location);

                    //rechtes Portal

                } else if (LocationChecker.isLocationInsideCube(playerLocation, rightPortal0, rightPortal1)) {
                    Location location = new Location(Bukkit.getWorld("world"), 6, 30, 66.5, playerLocation.getYaw()-180, playerLocation.getPitch());
                    player.teleport(location);
                }

            }
        }
    }

    //Teleport Abbruch
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location location = event.getFrom();
        String world = location.getWorld().getName();
        Player player = event.getPlayer();

            if (data.getScheduler().getLocations().containsKey(player)) {
                if(!(event.getTo().getX() == event.getFrom().getX() && event.getTo().getY() == event.getFrom().getY() && event.getTo().getZ() == event.getFrom().getZ())) {
                    player.sendMessage("§cYour Teleport has been interrupted at " + data.getScheduler().getCountdowns().get(player) + " seconds");
                    player.removePotionEffect(PotionEffectType.CONFUSION);
                    data.getScheduler().getCountdowns().remove(player);
                    data.getScheduler().getLocations().remove(player);
                }
        }
    }

    //Teleport Abbruch
    @EventHandler
    public void onDamageTeleport(EntityDamageEvent event){
        Location location = event.getEntity().getLocation();
        String world = location.getWorld().getName();

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
