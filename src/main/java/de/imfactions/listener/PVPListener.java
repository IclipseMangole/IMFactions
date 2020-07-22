package de.imfactions.listener;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.commands.Ether;
import de.imfactions.database.UserManager;
import de.imfactions.database.faction.FactionManager;
import de.imfactions.database.faction.FactionPlotManager;
import de.imfactions.database.faction.FactionUserManager;
import de.imfactions.util.LocationChecker;
import de.imfactions.util.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R1.Entity;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import static de.imfactions.util.ColorConverter.toHex;
import static net.minecraft.server.v1_16_R1.DataWatcherRegistry.e;

import java.awt.Color;

public class PVPListener implements Listener {

    private IMFactions imFactions;
    private FactionManager factionManager;
    private Data data;
    private UserManager userManager;
    private FactionUserManager factionUserManager;
    private FactionPlotManager factionPlotManager;

    public PVPListener(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionManager = data.getFactionManager();
        userManager = data.getUserManager();
        factionUserManager = data.getFactionUserManager();
        factionPlotManager = data.getFactionPlotManager();
    }

    //keine Blöcke platzieren
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location location = event.getBlockPlaced().getLocation();
        String world = location.getWorld().getName();

        if (world.equals("FactionPVP_world")) {
            event.setCancelled(true);
        }
    }

    //keine Blöcke zerstören
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        String world = location.getWorld().getName();

        if (world.equals("FactionPVP_world")) {
            event.setCancelled(true);
        }
    }

    //kein PVP innerhalb der Safe_Zone und für Faction Mitglieder
    @EventHandler
    public void onPVP(EntityDamageByEntityEvent event) {
        Location location = event.getEntity().getLocation();
        String world = location.getWorld().getName();

        if (world.equals("FactionPVP_world")) {
            Location edgeDownFrontLeft = new Location(Bukkit.getWorld("FactionPVP_world"), 29, 69, 1245);
            Location edgeUpBackRight = new Location(Bukkit.getWorld("FactionPVP_world"), 97, 99, 1279);

            if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
                Player damager = (Player) event.getDamager();
                Player player = (Player) event.getEntity();

                if (!LocationChecker.isLocationInsideCube(player.getLocation(), edgeDownFrontLeft, edgeUpBackRight)) {
                    int factionIDPlayer = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId();
                    int factionIDDamager = factionUserManager.getFactionUser(UUIDFetcher.getUUID(damager)).getFactionId();

                    if (factionIDDamager == factionIDPlayer) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                    damager.sendMessage("§cPVP isn't enabled in the Safe-Zone");
                }
            }
        }
    }

    //Kein Schaden
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Location location = event.getEntity().getLocation();
        String world = location.getWorld().getName();

        if (world.equals("FactionPVP_world")) {
            Location edgeDownFrontLeft = new Location(Bukkit.getWorld("FactionPVP_world"), 29, 69, 1245);
            Location edgeUpBackRight = new Location(Bukkit.getWorld("FactionPVP_world"), 97, 99, 1279);
            if(event.getEntity() instanceof Player){
                Player player = (Player) event.getEntity();
                if(LocationChecker.isLocationInsideCube(player.getLocation(), edgeDownFrontLeft, edgeUpBackRight)){
                    event.setCancelled(true);
                }
            }
        }
    }


    //Sterben
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Location location = event.getEntity().getLocation();
        String world = location.getWorld().getName();
        Player dead = event.getEntity();
        Player killer = dead.getKiller();

        if (world.equals("FactionPVP_world")) {
            event.setDeathMessage(null);

            String health = String.format("%.1f", killer.getHealth());
            dead.sendMessage("§4You got killed by §c" + killer.getName() + "§7(§c" + health + ChatColor.of(toHex(153, 0, 0)) + "❤§7)");
            killer.sendMessage("§4You killed §c" + dead.getName());
            killer.sendMessage("§4You earned §c10 §4Ether");

            userManager.getUser(killer.getName()).addEther(10);

            dead.teleport(data.getWorldSpawn());
            dead.playSound(dead.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

            killer.playSound(killer.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
        }
    }

    //Teleport Abbruch
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location location = event.getFrom();
        String world = location.getWorld().getName();
        Player player = event.getPlayer();

        if (world.equals("FactionPVP_world")) {
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
    public void onDamageTeleport(EntityDamageEvent event){
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
