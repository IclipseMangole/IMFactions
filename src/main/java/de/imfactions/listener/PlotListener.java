package de.imfactions.listener;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.database.faction.FactionManager;
import de.imfactions.database.faction.FactionPlotManager;
import de.imfactions.database.faction.FactionUserManager;
import de.imfactions.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

public class PlotListener implements Listener {

    private IMFactions imFactions;
    private Data data;
    private FactionManager factionManager;
    private FactionPlotManager factionPlotManager;
    private FactionUserManager factionUserManager;

    public PlotListener(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionManager = data.getFactionManager();
        factionPlotManager = data.getFactionPlotManager();
        factionUserManager = data.getFactionUserManager();
    }

    //Blöcke abbauen nur im Plot
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        String world = location.getWorld().getName();
        Player player = event.getPlayer();
        int factionID = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId();
        FactionPlotManager.FactionPlot factionPlot = factionPlotManager.getFactionPlot(factionID);

        if (world.equals("FactionPlots_world")) {

            Location edgeDownFrontLeft = factionPlot.getEdgeDownFrontLeft();
            Location edgeUpBackRight = factionPlot.getEdgeUpBackRight();

            if (!isLocationInsideCube(event.getBlock().getLocation(), edgeDownFrontLeft, edgeUpBackRight)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExplosion(BlockExplodeEvent event){
        Location location = event.getBlock().getLocation();
        String world = location.getWorld().getName();

        if(world.equals("FactionPlots_world")) {
            if (factionPlotManager.getFactionPlot(location) != null) {
                FactionPlotManager.FactionPlot factionPlot = factionPlotManager.getFactionPlot(location);
                Location edgeDownFrontLeft = factionPlot.getEdgeDownFrontLeft();
                Location raidEdgeLeft = factionPlotManager.getRaidEdgeLeft(edgeDownFrontLeft);
                Location raidEdgeRight = factionPlotManager.getRaidEdgeRight(raidEdgeLeft);


                    event.blockList().clear();

            }
        }

    }

    //Blöcke hinbauen nur im Plot
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location location = event.getBlock().getLocation();
        String world = location.getWorld().getName();
        Player player = event.getPlayer();
        int factionID = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId();
        FactionPlotManager.FactionPlot factionPlot = factionPlotManager.getFactionPlot(factionID);

        if (world.equals("FactionPlots_world")) {

            Location edgeDownFrontLeft = factionPlot.getEdgeDownFrontLeft();
            Location edgeUpBackRight = factionPlot.getEdgeUpBackRight();

            if (!isLocationInsideCube(event.getBlock().getLocation(), edgeDownFrontLeft, edgeUpBackRight)) {
                event.setCancelled(true);
            }
        }
    }

    //kein PVP für die Faction Mitglieder
    @EventHandler
    public void onPVP(EntityDamageByEntityEvent event) {
        Location location = event.getEntity().getLocation();
        String world = location.getWorld().getName();

        if (world.equals("FactionPlots_world")) {
            if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {

                Player damager = (Player) event.getDamager();
                Player player = (Player) event.getEntity();

                int factionIDPlayer = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId();
                int factionIDDamager = factionUserManager.getFactionUser(UUIDFetcher.getUUID(damager)).getFactionId();

                if (factionIDDamager == factionIDPlayer) {
                    event.setCancelled(true);
                }
            }
        }
    }

    //Wenn man außerhalb nach unten fällt
    @EventHandler
    public void onVoidDamage(EntityDamageEvent event) {
        Location location = event.getEntity().getLocation();
        String world = location.getWorld().getName();

        if (world.equals("FactionPlots_world")) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                    event.setCancelled(true);
                    int factionID = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId();
                    FactionPlotManager.FactionPlot factionPlot = factionPlotManager.getFactionPlot(factionID);
                    player.teleport(factionPlot.getHome());
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
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

        if (world.equals("FactionPlots_world")) {
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
    public void onDamage(EntityDamageEvent event){
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

    public boolean isLocationInsideCube(Location location, Location edgeDownFrontLeft, Location edgeUpBackRight) {
        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();

        int x1 = (int) edgeDownFrontLeft.getX();
        int y1 = (int) edgeDownFrontLeft.getY();
        int z1 = (int) edgeDownFrontLeft.getZ();

        int x2 = (int) edgeUpBackRight.getX();
        int y2 = (int) edgeUpBackRight.getY();
        int z2 = (int) edgeUpBackRight.getZ();


        for (int i = Math.min(x1, x2); i <= Math.max(x1, x2); i++) {
            for (int i1 = Math.min(y1, y2); i1 <= Math.max(y1, y2); i1++) {
                for (int i2 = Math.min(z1, z2); i2 <= Math.max(z1, z2); i2++) {
                    if (x == i && y == i1 && z == i2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
