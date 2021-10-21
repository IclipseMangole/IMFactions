package de.imfactions.functions.raid;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.faction.Faction;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMember;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.functions.factionPlot.FactionPlot;
import de.imfactions.functions.factionPlot.FactionPlotUtil;
import de.imfactions.util.LocationChecker;
import de.imfactions.util.UUIDFetcher;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RaidListener implements Listener {

    private final IMFactions imFactions;
    private final Data data;
    private final RaidUtil raidUtil;
    private final FactionPlotUtil factionPlotUtil;
    private final FactionMemberUtil factionMemberUtil;
    private final FactionUtil factionUtil;
    private HashMap<Block, Integer> obsidian = new HashMap<>();

    public RaidListener(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        raidUtil = data.getRaidUtil();
        factionPlotUtil = data.getFactionPlotUtil();
        factionMemberUtil = data.getFactionMemberUtil();
        factionUtil = data.getFactionUtil();
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player player = (event.getPlayer());
        if (!event.getFrom().getWorld().getName().equalsIgnoreCase("world"))
            return;
        if (!factionMemberUtil.isFactionMemberExists(player.getUniqueId()))
            return;
        FactionMember factionMember = factionMemberUtil.getFactionMember(player.getUniqueId());
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());
        if (!raidUtil.isFactionRaiding(faction.getId()))
            return;
        if (raidUtil.isFactionMemberJoinedRaid(factionMember)) {
            player.chat("/raid leave");
            player.sendMessage(ChatColor.RED + "You got kicked from the Raid because you joined the PVP Zone");
        }
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        World world = player.getWorld();
        if (!world.getName().equalsIgnoreCase("FactionPlots_world"))
            return;

        if (raidUtil.isRaidingOtherFaction(player)) {
            if (!factionPlotUtil.isLocationOnFactionPlot(player.getLocation()))
                return;
            FactionPlot currentPlot = factionPlotUtil.getFactionPlot(player.getLocation());
            Bukkit.getScheduler().runTaskLater(imFactions, new Runnable() {
                @Override
                public void run() {
                    player.teleport(currentPlot.getRaidSpawn());
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }, 2);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        UUID uuid = UUIDFetcher.getUUID(player);
        Location location = event.getBlock().getLocation();

        if (!world.getName().equalsIgnoreCase("FactionPlots_world"))
            return;
        if (!raidUtil.isRaidingOtherFaction(player))
            return;

        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        int raidID = raidUtil.getActiveRaidID(factionMember.getFactionID());
        Raid raid = raidUtil.getRaid(raidID);
        FactionPlot currentPlot = factionPlotUtil.getFactionPlot(location);
        FactionPlot factionPlot = factionPlotUtil.getFactionPlot(raid.getFactionIdDefenders());
        if (currentPlot != factionPlot) {
            event.setCancelled(true);
            return;
        }
        Location edgeDownFrontLeft = factionPlot.getEdgeDownFrontLeft();
        Location edgeUpBackRight = factionPlot.getEdgeUpBackRight();
        Location raidEdgeLeft = factionPlotUtil.getRaidEdgeLeft(factionPlot.getEdgeDownFrontLeft());
        Location raidEdgeRight = factionPlotUtil.getRaidEdgeRight(factionPlot.getEdgeUpBackRight());
        if (!raid.getRaidState().equals(RaidState.RAIDING)) {
            event.setCancelled(true);
            return;
        }
        if (!LocationChecker.isLocationInsideCube(location, raidEdgeLeft, raidEdgeRight)) {
            event.setCancelled(true);
            return;
        }
        if (LocationChecker.isLocationInsideCube(location, edgeDownFrontLeft, edgeUpBackRight))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        UUID uuid = UUIDFetcher.getUUID(player);
        Location location = event.getBlock().getLocation();

        if (!world.getName().equalsIgnoreCase("FactionPlots_world"))
            return;
        if (!raidUtil.isRaidingOtherFaction(player))
            return;

        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        int raidID = raidUtil.getActiveRaidID(factionMember.getFactionID());
        Raid raid = raidUtil.getRaid(raidID);
        FactionPlot currentPlot = factionPlotUtil.getFactionPlot(location);
        FactionPlot factionPlot = factionPlotUtil.getFactionPlot(raid.getFactionIdDefenders());
        if (currentPlot != factionPlot) {
            event.setCancelled(true);
            return;
        }
        Location edgeDownFrontLeft = factionPlot.getEdgeDownFrontLeft();
        Location edgeUpBackRight = factionPlot.getEdgeUpBackRight();
        Location raidEdgeLeft = factionPlotUtil.getRaidEdgeLeft(factionPlot.getEdgeDownFrontLeft());
        Location raidEdgeRight = factionPlotUtil.getRaidEdgeRight(factionPlot.getEdgeUpBackRight());
        if (!raid.getRaidState().equals(RaidState.RAIDING)) {
            event.setCancelled(true);
            return;
        }
        if (!LocationChecker.isLocationInsideCube(location, raidEdgeLeft, raidEdgeRight)) {
            event.setCancelled(true);
            return;
        }
        if (LocationChecker.isLocationInsideCube(location, edgeDownFrontLeft, edgeUpBackRight))
            event.setCancelled(true);
    }

    @EventHandler
    public void onExplosionObisidian(ExplosionPrimeEvent event) {
        World world = event.getEntity().getWorld();

        if (!world.getName().equalsIgnoreCase("FactionPlots_world"))
            return;
        Block source = event.getEntity().getLocation().getBlock();
        if (source.isLiquid())
            return;
        for (Block damagedObsidian : getDamagedObsidian(source, 2.0F)) {
            if (!obsidian.containsKey(damagedObsidian)) {
                obsidian.put(damagedObsidian, 0);
                continue;
            }
            int damage = obsidian.get(damagedObsidian);
            obsidian.replace(damagedObsidian, damage + 1);
            if (obsidian.get(damagedObsidian) >= 2) {
                damagedObsidian.setType(Material.AIR);
                obsidian.remove(damagedObsidian);
            }
        }
    }


    private ArrayList<Block> getDamagedObsidian(Block source, float radius) {
        ArrayList<Block> damagedObsidian = new ArrayList<>();
        World world = source.getWorld();
        int r = (int) Math.ceil(radius);
        Location s = source.getLocation();

        for (int x = -r; x < r; x++) {
            for (int y = -r; y < r; y++) {
                for (int z = -r; z < r; z++) {
                    Block obsidian = world.getBlockAt(s.getBlockX() + x, s.getBlockY() + y, s.getBlockZ() + z);
                    if (!obsidian.getType().equals(Material.OBSIDIAN))
                        continue;
                    if (s.distance(obsidian.getLocation()) <= r)
                        damagedObsidian.add(obsidian);
                }
            }
        }
        return damagedObsidian;
    }
}
