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
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

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
    public void onPortal(EntityPortalEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if (!event.getFrom().getWorld().getName().equalsIgnoreCase("world"))
            return;
        if (!factionMemberUtil.isFactionMemberExists(player.getUniqueId()))
            return;
        FactionMember factionMember = factionMemberUtil.getFactionMember(player.getUniqueId());
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());
        if (!raidUtil.isFactionRaiding(faction.getId()))
            return;
        int raidID = raidUtil.getActiveRaidID(faction.getId());
        if (raidUtil.isFactionMemberJoinedRaid(factionMember)) {
            player.chat("/raid leave");
            player.sendMessage(ChatColor.RED + "You got kicked from the Raid because you joined the PVP Zone");
        }
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (isRaidingOtherFaction(player)) {
            FactionPlot currentPlot = factionPlotUtil.getFactionPlot(player.getLocation());
            player.teleport(currentPlot.getRaidSpawn());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            return;
        }
        player.teleport(data.getWorldSpawn());
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID uuid = UUIDFetcher.getUUID(player);

        if (!isRaidingOtherFaction(player))
            return;

        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        int raidID = raidUtil.getActiveRaidID(factionMember.getFactionID());
        Raid raid = raidUtil.getRaid(raidID);
        FactionPlot factionPlot = factionPlotUtil.getFactionPlot(raid.getFactionIdDefenders());
        Location raidEdgeLeft = factionPlotUtil.getRaidEdgeLeft(factionPlot.getEdgeDownFrontLeft());
        Location raidEdgeRight = factionPlotUtil.getRaidEdgeRight(factionPlot.getEdgeUpBackRight());
        if (raid.getRaidState().equals(RaidState.SCOUTING)) {
            event.setCancelled(true);
            return;
        }
        if (!LocationChecker.isLocationInsideCube(event.getBlock().getLocation(), raidEdgeLeft, raidEdgeRight)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = UUIDFetcher.getUUID(player);

        if (!isRaidingOtherFaction(player))
            return;

        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        int raidID = raidUtil.getActiveRaidID(factionMember.getFactionID());
        Raid raid = raidUtil.getRaid(raidID);
        FactionPlot factionPlot = factionPlotUtil.getFactionPlot(raid.getFactionIdDefenders());
        Location raidEdgeLeft = factionPlotUtil.getRaidEdgeLeft(factionPlot.getEdgeDownFrontLeft());
        Location raidEdgeRight = factionPlotUtil.getRaidEdgeRight(factionPlot.getEdgeUpBackRight());
        if (raid.getRaidState().equals(RaidState.SCOUTING)) {
            event.setCancelled(true);
            return;
        }
        if (!LocationChecker.isLocationInsideCube(event.getBlock().getLocation(), raidEdgeLeft, raidEdgeRight)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplosionObisidian(ExplosionPrimeEvent event) {
        World world = event.getEntity().getWorld();

        if (world.getName().equalsIgnoreCase("FactionPlots_world"))
            return;
        Block source = event.getEntity().getLocation().getBlock();
        if (source.isLiquid())
            return;
        for (Block damagedObsidian : getDamagedObsidian(source, event.getRadius())) {
            if (!obsidian.containsKey(damagedObsidian))
                obsidian.put(damagedObsidian, 0);
            int damage = obsidian.get(damagedObsidian);
            obsidian.replace(damagedObsidian, damage, damage + 1);
            if (obsidian.get(damagedObsidian) >= 2)
                damagedObsidian.setType(Material.AIR);
            obsidian.remove(damagedObsidian);
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


    private boolean isRaidingOtherFaction(Player player) {
        UUID uuid = UUIDFetcher.getUUID(player);
        World world = player.getWorld();

        if (!world.getName().equalsIgnoreCase("FactionPlots_world"))
            return false;
        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            player.teleport(data.getWorldSpawn());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            return false;
        }
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());
        if (!raidUtil.isFactionRaiding(faction.getId()))
            return false;
        if (!raidUtil.isFactionMemberJoinedRaid(factionMember))
            return false;
        int raidID = raidUtil.getActiveRaidID(faction.getId());
        Raid raid = raidUtil.getRaid(raidID);
        FactionPlot currentPlot = factionPlotUtil.getFactionPlot(player.getLocation());
        return raid.getFactionIdDefenders() == currentPlot.getFactionID();
    }
}
