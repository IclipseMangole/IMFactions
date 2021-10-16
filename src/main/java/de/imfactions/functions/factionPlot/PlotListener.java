package de.imfactions.functions.factionPlot;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMember;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.functions.raid.RaidUtil;
import de.imfactions.util.LocationChecker;
import de.imfactions.util.UUIDFetcher;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.ArrayList;
import java.util.UUID;

public class PlotListener implements Listener {

    private final IMFactions imFactions;
    private final Data data;
    private final FactionUtil factionUtil;
    private final RaidUtil raidUtil;
    private final FactionPlotUtil factionPlotUtil;
    private final FactionMemberUtil factionMemberUtil;

    public PlotListener(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionUtil = data.getFactionUtil();
        factionPlotUtil = data.getFactionPlotUtil();
        factionMemberUtil = data.getFactionMemberUtil();
        raidUtil = data.getRaidUtil();
    }

    //Blöcke abbauen nur im Plot
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        World world = location.getWorld();
        Player player = event.getPlayer();
        UUID uuid = UUIDFetcher.getUUID(player);

        if (!world.getName().equalsIgnoreCase("FactionPlots_world"))
            return;
        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            event.setCancelled(true);
            return;
        }
        if (raidUtil.isRaidingOtherFaction(player))
            return;
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        FactionPlot factionPlot = factionPlotUtil.getFactionPlot(factionMember.getFactionID());
        if (!factionPlotUtil.isLocationOnFactionPlot(location)) {
            event.setCancelled(true);
            return;
        }
        FactionPlot currentPlot = factionPlotUtil.getFactionPlot(location);
        if (currentPlot != factionPlot) {
            event.setCancelled(true);
            return;
        }
        Location edgeDownFrontLeft = factionPlot.getEdgeDownFrontLeft();
        Location edgeUpBackRight = factionPlot.getEdgeUpBackRight();
        if (!LocationChecker.isLocationInsideCube(location, edgeDownFrontLeft, edgeUpBackRight))
            event.setCancelled(true);
    }

    //Blöcke hinbauen nur im Plot
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location location = event.getBlock().getLocation();
        World world = location.getWorld();
        Player player = event.getPlayer();
        UUID uuid = UUIDFetcher.getUUID(player);

        if (!world.getName().equalsIgnoreCase("FactionPlots_world"))
            return;
        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            event.setCancelled(true);
            return;
        }
        if (raidUtil.isRaidingOtherFaction(player))
            return;
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        FactionPlot factionPlot = factionPlotUtil.getFactionPlot(factionMember.getFactionID());
        if (!factionPlotUtil.isLocationOnFactionPlot(location)) {
            event.setCancelled(true);
            return;
        }
        FactionPlot currentPlot = factionPlotUtil.getFactionPlot(location);
        if (currentPlot != factionPlot) {
            event.setCancelled(true);
            return;
        }
        Location edgeDownFrontLeft = factionPlot.getEdgeDownFrontLeft();
        Location edgeUpBackRight = factionPlot.getEdgeUpBackRight();
        if (!LocationChecker.isLocationInsideCube(location, edgeDownFrontLeft, edgeUpBackRight))
            event.setCancelled(true);
    }

    @EventHandler
    public void onExplosion(BlockExplodeEvent event) {
        Location location = event.getBlock().getLocation();
        World world = location.getWorld();

        if (!world.getName().equalsIgnoreCase("FactionPlots_world"))
            return;
        for (Block block : event.blockList()) {
            Location blockLocation = block.getLocation();
            if (factionPlotUtil.getFactionPlot(blockLocation) == null) {
                return;
            }
            FactionPlot factionPlot = factionPlotUtil.getFactionPlot(blockLocation);
            Location edgeDownFrontLeft = factionPlot.getEdgeDownFrontLeft();
            Location raidEdgeLeft = factionPlotUtil.getRaidEdgeLeft(edgeDownFrontLeft);
            Location raidEdgeRight = factionPlotUtil.getRaidEdgeRight(raidEdgeLeft);
            if (LocationChecker.isLocationInsideCube(blockLocation, raidEdgeLeft, raidEdgeRight))
                return;
            event.blockList().remove(block);
        }
    }

    //kein PVP für die Faction Mitglieder
    @EventHandler
    public void onPVP(EntityDamageByEntityEvent event) {
        Location location = event.getEntity().getLocation();
        World world = location.getWorld();

        if (!world.getName().equalsIgnoreCase("FactionPlots_world"))
            return;
        if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Player))
            return;
        Player damager = (Player) event.getDamager();
        Player player = (Player) event.getEntity();
        UUID damagerUuid = damager.getUniqueId();
        UUID playerUuid = player.getUniqueId();
        if(!(factionMemberUtil.isFactionMemberExists(damagerUuid) && factionMemberUtil.isFactionMemberExists(playerUuid)))
            return;
        FactionMember factionMemberDamager = factionMemberUtil.getFactionMember(damagerUuid);
        FactionMember factionMemberPlayer = factionMemberUtil.getFactionMember(playerUuid);
        ArrayList<FactionMember> members = factionMemberUtil.getFactionMembers(factionMemberDamager.getFactionID());
        if(!members.contains(factionMemberPlayer))
            return;
        event.setCancelled(true);

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Location location = event.getEntity().getLocation();
        World world = location.getWorld();

        if (!world.getName().equalsIgnoreCase("FactionPlots_world"))
            return;
        Player player = event.getEntity();
        UUID uuid = UUIDFetcher.getUUID(player);

        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            player.teleport(data.getWorldSpawn());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            return;
        }
        if (!factionPlotUtil.isLocationOnFactionPlot(location))
            return;

        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        FactionPlot currentPlot = factionPlotUtil.getFactionPlot(player.getLocation());
        FactionPlot factionPlot = factionPlotUtil.getFactionPlot(factionMember.getFactionID());
        if (currentPlot == factionPlot) {
            player.teleport(factionPlot.getHome());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }

    @EventHandler
    public void onPortalBuild(PortalCreateEvent event) {
        World world = event.getWorld();

        if (world.getName().equalsIgnoreCase("FactionPlots_world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        World world = event.getLocation().getWorld();

        if (!world.getName().equalsIgnoreCase("FactionPlots_world"))
            return;
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL))
            event.setCancelled(true);
    }

    @EventHandler
    public void onMoveOutOfPlot(PlayerMoveEvent event) {
        World world = event.getPlayer().getWorld();
        Location to = event.getTo();
        Location from = event.getFrom();

        if (!world.getName().equalsIgnoreCase("FactionPlots_world"))
            return;
        if (!factionPlotUtil.isLocationOnFactionPlot(from))
            return;
        if (!factionPlotUtil.isLocationOnFactionPlot(to))
            event.setCancelled(true);
    }
}
