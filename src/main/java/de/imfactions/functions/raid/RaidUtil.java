package de.imfactions.functions.raid;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.Scheduler;
import de.imfactions.functions.faction.Faction;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMember;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.functions.factionPlot.FactionPlot;
import de.imfactions.functions.factionPlot.FactionPlotUtil;
import de.imfactions.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private HashMap<Raid, FactionMember> raidTeams;
    private Scheduler scheduler;
    private FactionMemberUtil factionMemberUtil;
    private FactionUtil factionUtil;
    private RaidTable raidTable;
    private FactionPlotUtil factionPlotUtil;
    private Data data;
    private RaidScheduler raidScheduler;

    public RaidUtil(Data data) {
        this.data = data;
        imFactions = data.getImFactions();
        raidTeams = new HashMap<>();
        Bukkit.getScheduler().runTaskTimerAsynchronously(imFactions, new Runnable() {
            @Override
            public void run() {
                saveRaids();
            }
        }, 20 * 60, 20 * 60 * 10);
    }

    public void loadUtils() {
        scheduler = data.getScheduler();
        factionMemberUtil = data.getFactionMemberUtil();
        factionUtil = data.getFactionUtil();
        raidTable = new RaidTable(this, data);
        factionPlotUtil = data.getFactionPlotUtil();
        raids = raidTable.getRaids();
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

    public ArrayList<Faction> getScoutableFactions(int raidID, int currentlyScouted) {
        ArrayList<Faction> factions = new ArrayList<>();
        Raid raid = getRaid(raidID);
        for (Faction faction : factionUtil.getRaidableFactions(raid.getFactionIdAttackers())) {
            if (faction.getId() != currentlyScouted) {
                factions.add(faction);
            }
        }
        return factions;
    }

    public Faction getFactionForScout(int raidID, int currentlyScouted) {
        Random random = new Random();
        if (getScoutableFactions(raidID, currentlyScouted).size() == 0)
            return null;
        ArrayList<Faction> factions = getScoutableFactions(raidID, currentlyScouted);
        return factions.get(random.nextInt(factions.size()));
    }

    public void createPreparingRaid(int raidID, int factionIDAttackers) {
        Raid raid = new Raid(raidID, RaidState.PREPARING, factionIDAttackers, -1, Date.from(Instant.now()), 0);
        raids.add(raid);
        raidTable.createRaid(raidID, raid.getRaidState(), factionIDAttackers, raid.getFactionIdDefenders(), raid.getStart(), raid.getTime());
    }

    public void updateRaidToScouting(int raidID) {
        Raid raid = getRaid(raidID);
        Faction faction = factionUtil.getFaction(raid.getFactionIdAttackers());
        faction.raid();
        raid.setRaidState(RaidState.SCOUTING);
    }

    public void updateRaidToRaiding(int raidID, int factionIDDefenders){
        Raid raid = getRaid(raidID);

        raid.setRaidState(RaidState.RAIDING);
        raid.setTime(0);
        raid.setStart(Date.from(Instant.now()));
        raid.setFactionIdDefenders(factionIDDefenders);
    }

    public void updateRaidToDone(int raidID){
        Raid raid = getRaid(raidID);

        raid.setRaidState(RaidState.DONE);
        raid.setTime(System.currentTimeMillis() - raid.getStart().getTime());
    }

    public ArrayList<FactionMember> getRaidTeam(int raidID){
        ArrayList<FactionMember> team = new ArrayList<>();
        Raid raid = getRaid(raidID);

        for(Map.Entry<Raid, FactionMember> entry : raidTeams.entrySet()){
            if(entry.getKey().equals(raid)){
                team.add(entry.getValue());
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

    public int getStartingSecondsLeft(int raidID){
        Raid raid = getRaid(raidID);

        for (Map.Entry<Raid, FactionMember> entry : raidTeams.entrySet()) {
            FactionMember FactionMember = entry.getValue();
            if (entry.getKey().equals(raid)) {
                Player player = factionMemberUtil.getPlayer(FactionMember.getUuid());
                if(scheduler.getCountdowns().containsKey(player)) {
                    return scheduler.getCountdowns().get(player);
                }
            }
        }
        return -1;
    }


    public Location getRaidSpawn(int raidID){
        Raid raid= getRaid(raidID);

        for (Map.Entry<Raid, FactionMember> entry : raidTeams.entrySet()) {
            FactionMember FactionMember = entry.getValue();
            if(entry.getKey().equals(raid)){
                Player player = factionMemberUtil.getPlayer(FactionMember.getUuid());
                return scheduler.getRaids().get(player);
            }
        }
        return null;
    }

    public boolean isFactionMemberJoinedRaid(FactionMember FactionMember){
        if(raidTeams.containsValue(FactionMember)){
            return true;
        }
        return false;
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

    /**
     * RaidStates:
     * preparing,
     * scouting,
     * raiding,
     * done
     */
    public RaidState getRaidState(int raidID) {
        Raid raid = getRaid(raidID);
        return raid.getRaidState();
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

    public HashMap<Raid, FactionMember> getRaidTeams() {
        return raidTeams;
    }

    public RaidScheduler getRaidScheduler() {
        return raidScheduler;
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
