package de.imfactions.functions.pvp;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.util.LocationChecker;
import de.imfactions.util.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import static de.imfactions.util.ColorConverter.toHex;

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
                    int factionIDPlayer = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID();
                    int factionIDDamager = factionUserManager.getFactionUser(UUIDFetcher.getUUID(damager)).getFactionID();

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

}
