package de.imfactions.functions.pvp;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.functions.factionPlot.FactionPlotUtil;
import de.imfactions.functions.user.UserUtil;
import de.imfactions.util.LocationChecker;
import de.imfactions.util.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerPortalEvent;

import static de.imfactions.util.ColorConverter.toHex;

public class PVPListener implements Listener {

    private IMFactions imFactions;
    private FactionUtil factionUtil;
    private Data data;
    private UserUtil userUtil;
    private FactionMemberUtil factionMemberUtil;
    private FactionPlotUtil factionPlotUtil;

    public PVPListener(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionUtil = data.getFactionUtil();
        userUtil = data.getUserUtil();
        factionMemberUtil = data.getFactionMemberUtil();
        factionPlotUtil = data.getFactionPlotUtil();
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

                    if (!factionMemberUtil.isFactionMemberExists(player.getUniqueId()))
                        return;
                    if (!factionMemberUtil.isFactionMemberExists(damager.getUniqueId()))
                        return;

                    int factionIDPlayer = factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID();
                    int factionIDDamager = factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(damager)).getFactionID();

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

            String health = String.format("%.0f", killer.getHealth() / 2);
            dead.sendMessage("§4You got killed by §c" + killer.getName() + "§7(§c" + health + ChatColor.of(toHex(153, 0, 0)) + "❤§7)");
            killer.sendMessage("§4You killed §c" + dead.getName());
            killer.sendMessage("§4You earned §c10 §4Ether");

            userUtil.getUser(killer.getName()).addEther(10);

            dead.teleport(data.getWorldSpawn());
            dead.playSound(dead.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

            killer.playSound(killer.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
        }
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
    public void onPortal(EntityPortalEvent event) {
        World world = event.getEntity().getWorld();
        if (!world.getName().equalsIgnoreCase("FactionPVP_world"))
            return;
        if (!(event.getEntity() instanceof Player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPortalPlayer(PlayerPortalEvent event) {
        World world = event.getPlayer().getWorld();
        if (!world.getName().equalsIgnoreCase("FactionPVP_world"))
            return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        player.teleport(data.getWorldSpawn());
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        World world = event.getLocation().getWorld();

        if (!world.getName().equalsIgnoreCase("FactionPVP_world"))
            return;
        if (!(event.getEntity() instanceof Mob))
            return;
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL))
            event.setCancelled(true);
    }
}
