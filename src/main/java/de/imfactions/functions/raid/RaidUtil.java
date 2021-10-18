package de.imfactions.functions.raid;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.Scoreboard;
import de.imfactions.functions.faction.Faction;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMember;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.functions.factionPlot.FactionPlot;
import de.imfactions.functions.factionPlot.FactionPlotUtil;
import de.imfactions.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

public class RaidUtil {

    private final IMFactions imFactions;
    private ArrayList<Raid> raids;
    private final HashMap<FactionMember, Raid> raidTeams;
    private final HashMap<Faction, Raid> scoutedFactions;
    private FactionMemberUtil factionMemberUtil;
    private FactionUtil factionUtil;
    private RaidTable raidTable;
    private FactionPlotUtil factionPlotUtil;
    private final Data data;
    private RaidScheduler raidScheduler;
    private Scoreboard scoreboard;

    public RaidUtil(Data data) {
        this.data = data;
        imFactions = data.getImFactions();
        raidTeams = new HashMap<>();
        scoutedFactions = new HashMap<>();
        Bukkit.getScheduler().runTaskTimerAsynchronously(imFactions, new Runnable() {
            @Override
            public void run() {
                saveRaids();
            }
        }, 20 * 60, 20 * 60 * 10);
    }

    public void loadUtils() {
        factionMemberUtil = data.getFactionMemberUtil();
        factionUtil = data.getFactionUtil();
        raidTable = new RaidTable(this, data);
        factionPlotUtil = data.getFactionPlotUtil();
        raids = raidTable.getRaids();
        scoreboard = data.getScoreboard();
    }

    public void loadSchedulers() {
        raidScheduler = data.getRaidScheduler();
    }

    public ArrayList<Raid> getRaids() {
        return this.raids;
    }

    public boolean isFactionRaiding(int factionID) {
        for (Raid raid : raids) {
            if (raid.getFactionIdAttackers() == factionID) {
                if (!raid.getRaidState().equals(RaidState.DONE)) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<Faction> getScoutableFactions(Raid raid) {
        ArrayList<Faction> factions = new ArrayList<>();
        for (Faction faction : factionUtil.getRaidableFactions(raid.getFactionIdAttackers())) {
            if (scoutedFactions.get(faction) == null) {
                factions.add(faction);
            }
        }
        return factions;
    }

    public Faction getFactionForScout(int raidID) {
        Random random = new Random();
        Raid raid = getRaid(raidID);
        if (getScoutableFactions(raid).size() == 0)
            return null;
        ArrayList<Faction> factions = getScoutableFactions(raid);
        return factions.get(random.nextInt(factions.size()));
    }

    public void createPreparingRaid(FactionMember factionMember) {
        int raidID = getHighestRaidID() + 1;
        Raid raid = new Raid(raidID, RaidState.PREPARING, factionMember.getFactionID(), -1, Date.from(Instant.now()), 0);
        raids.add(raid);
        raidTeams.put(factionMember, raid);
        raidTable.createRaid(raidID, raid.getRaidState(), factionMember.getFactionID(), raid.getFactionIdDefenders(), raid.getStart(), raid.getTime());
        raidScheduler.startPreparingRaid(raidID, 60);
        Player player = Bukkit.getPlayer(factionMember.getUuid());
        scoreboard.setRaidScoreboard(player, raid);
    }

    public void updateRaidToScouting(int raidID, int factionIDDefenders) {
        Raid raid = getRaid(raidID);
        Faction faction = factionUtil.getFaction(raid.getFactionIdAttackers());
        faction.raid();
        raid.setRaidState(RaidState.SCOUTING);
        raid.setFactionIdDefenders(factionIDDefenders);
        for (FactionMember member : getRaidTeam(raidID)) {
            scoreboard.setRaidScoreboard(Bukkit.getPlayer(member.getUuid()), raid);
        }
    }

    public void updateRaidToRaiding(int raidID, int factionIDDefenders) {
        Raid raid = getRaid(raidID);

        raid.setRaidState(RaidState.RAIDING);
        raid.setTime(0);
        raid.setStart(Date.from(Instant.now()));
        raid.setFactionIdDefenders(factionIDDefenders);
        for (FactionMember member : getRaidTeam(raidID)) {
            scoreboard.setRaidScoreboard(Bukkit.getPlayer(member.getUuid()), raid);
        }
    }

    public void updateRaidToDone(int raidID){
        Raid raid = getRaid(raidID);

        raid.setRaidState(RaidState.DONE);
        raid.setTime(System.currentTimeMillis() - raid.getStart().getTime());
    }

    public ArrayList<FactionMember> getRaidTeam(int raidID) {
        ArrayList<FactionMember> team = new ArrayList<>();
        Raid raid = getRaid(raidID);

        for (Map.Entry<FactionMember, Raid> entry : raidTeams.entrySet()) {
            if (entry.getValue().equals(raid)) {
                team.add(entry.getKey());
            }
        }
        return team;
    }

    public int getHighestRaidID() {
        try {
            ResultSet rs = data.getMySQL().querry("SELECT MAX(`raidID`) AS `raidID` FROM `raids` WHERE 1");
            if (rs.next()) {
                return rs.getInt("raidID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Raid getRaid(int raidID) {
        for (Raid raid : raids) {
            if (raid.getRaidID() == raidID) {
                return raid;
            }
        }
        return null;
    }

    public boolean isFactionMemberJoinedRaid(FactionMember FactionMember){
        return raidTeams.containsKey(FactionMember);
    }

    public int getActiveRaidID(int factionID){
        for (Raid raid : raids) {
            if (raid.getFactionIdAttackers() == factionID) {
                if (!raid.getRaidState().equals(RaidState.DONE))
                    return raid.getRaidID();
            }
        }
        return -1;
    }

    public ArrayList<Raid> getActiveRaids(){
        ArrayList<Raid> activeRaids = new ArrayList<>();
        for(Raid raid : raids){
            if(!raid.getRaidState().equals(RaidState.DONE)){
                activeRaids.add(raid);
            }
        }
        return activeRaids;
    }

    public boolean isRaidExists(int raidID) {
        for (Raid raid : raids) {
            if (raid.getRaidID() == raidID) {
                return true;
            }
        }
        return false;
    }

    public void memberJoinRaid(FactionMember factionMember, Raid raid) {
        Player player = Bukkit.getPlayer(factionMember.getUuid());
        raidTeams.put(factionMember, raid);
        scoreboard.setRaidScoreboard(player, raid);
    }

    public void memberLeaveRaid(FactionMember factionMember, Raid raid) {
        Player player = Bukkit.getPlayer(factionMember.getUuid());
        raidTeams.remove(factionMember);
        scoreboard.setScoreboard(player);
    }

    public void scoutNextFaction(Raid raid) {
        Faction defenders = getFactionForScout(raid.getRaidID());
        updateRaidToScouting(raid.getRaidID(), defenders.getId());
        raidScheduler.cancelPreparingRaid(raid.getRaidID());
        raidScheduler.startScoutingRaid(raid.getRaidID(), defenders.getId(), 300);
        scoutedFactions.put(defenders, raid);
    }

    public void raidFaction(Raid raid) {
        updateRaidToRaiding(raid.getRaidID(), raid.getFactionIdDefenders());
        raidScheduler.cancelScoutingRaid(raid.getRaidID());
        raidScheduler.startRaidingRaid(raid.getRaidID(), 60 * 30);
        clearScoutedFactions(raid);
    }

    public void endRaid(Raid raid) {
        int raidID = raid.getRaidID();
        if (raid.getRaidState().equals(RaidState.PREPARING)) {
            for (Player member : factionMemberUtil.getOnlineMembers(raid.getFactionIdAttackers())) {
                member.sendMessage(ChatColor.RED + "The Raid got canceled");
            }
            raidScheduler.cancelPreparingRaid(raidID);
            deleteRaid(raid);
        }
        Faction raided = factionUtil.getFaction(raid.getFactionIdDefenders());
        raided.setGettingRaided(false);
        if (raid.getRaidState().equals(RaidState.SCOUTING)) {
            raidScheduler.cancelScoutingRaid(raidID);
            updateRaidToDone(raidID);
            teleportRaidTeamHome(raidID);
        }
        if (raid.getRaidState().equals(RaidState.RAIDING)) {
            raidScheduler.cancelRaidingRaid(raidID);
            updateRaidToDone(raidID);
            raided.setRaidProtection(System.currentTimeMillis() + 1000 * 60);
            teleportRaidTeamHome(raidID);
        }
        for (Player member : factionMemberUtil.getOnlineMembers(raid.getFactionIdAttackers())) {
            scoreboard.setScoreboard(member);
        }
    }

    public boolean isRaidingOtherFaction(Player player) {
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
        if (!isFactionRaiding(faction.getId()))
            return false;
        if (!isFactionMemberJoinedRaid(factionMember))
            return false;
        int raidID = getActiveRaidID(faction.getId());
        Raid raid = getRaid(raidID);
        if (!factionPlotUtil.isLocationOnFactionPlot(player.getLocation()))
            return false;
        FactionPlot currentPlot = factionPlotUtil.getFactionPlot(player.getLocation());
        return raid.getFactionIdDefenders() == currentPlot.getFactionID();
    }

    public void saveRaids() {
        for (Raid raid : raids) {
            if (raid.getRaidState().equals(RaidState.PREPARING)) {
                raidScheduler.cancelPreparingRaid(raid.getRaidID());
                continue;
            }
            if (raid.getRaidState().equals(RaidState.SCOUTING)) {
                teleportRaidTeamHome(raid.getRaidID());
                raidScheduler.cancelScoutingRaid(raid.getRaidID());
                continue;
            }
            if (raid.getRaidState().equals(RaidState.RAIDING)) {
                raid.setRaidState(RaidState.DONE);
                teleportRaidTeamHome(raid.getRaidID());
                raidScheduler.cancelRaidingRaid(raid.getRaidID());
            }
            raidTable.saveRaid(raid);
        }
    }

    public void teleportRaidTeamHome(int raidID) {
        for (FactionMember factionMember : getRaidTeam(raidID)) {
            Player player = Bukkit.getPlayer(factionMember.getUuid());
            FactionPlot factionPlot = factionPlotUtil.getFactionPlot(factionMember.getFactionID());
            player.teleport(factionPlot.getHome());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }

    public HashMap<FactionMember, Raid> getRaidTeams() {
        return this.raidTeams;
    }

    public RaidScheduler getRaidScheduler() {
        return raidScheduler;
    }

    public void clearScoutedFactions(Raid raid) {
        for (Map.Entry<Faction, Raid> entry : scoutedFactions.entrySet()) {
            if (entry.getValue() == raid) {
                scoutedFactions.remove(entry.getKey());
            }
        }
    }

    public ArrayList<Raid> getRaidsFromFaction(int factionID) {
        ArrayList<Raid> raidsFromFaction = new ArrayList<>();
        for (Raid raid : this.raids) {
            if (raid.getFactionIdAttackers() == factionID)
                raidsFromFaction.add(raid);
        }
        return raidsFromFaction;
    }

    public void deleteRaidsFromFaction(int factionID) {
        for (Raid raid : getRaidsFromFaction(factionID)) {
            deleteRaid(raid);
        }
    }

    public void deleteRaid(Raid raid) {
        raids.remove(raid);
        raidTable.deleteRaid(raid);
    }
}
