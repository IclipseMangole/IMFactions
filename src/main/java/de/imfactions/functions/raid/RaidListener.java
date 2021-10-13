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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class RaidListener implements Listener {

    private final IMFactions imFactions;
    private final Data data;
    private final RaidUtil raidUtil;
    private final FactionPlotUtil factionPlotUtil;
    private final FactionMemberUtil factionMemberUtil;
    private final FactionUtil factionUtil;

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
