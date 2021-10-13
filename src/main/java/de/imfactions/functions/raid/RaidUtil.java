package de.imfactions.functions.raid;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.Scheduler;
import de.imfactions.functions.faction.Faction;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMember;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private Data data;
    private RaidScheduler raidScheduler;

    public RaidUtil(Data data) {
        this.data = data;
        imFactions = data.getImFactions();
        raids = raidTable.getRaids();
        raidTeams = new HashMap<>();
        Bukkit.getScheduler().runTaskTimerAsynchronously(imFactions, new Runnable() {
            @Override
            public void run() {
                saveRaids();
            }
        }, 0, 20 * 60 * 10);
    }

    public void loadUtils(){
        scheduler = data.getScheduler();
        factionMemberUtil = data.getFactionMemberUtil();
        factionUtil = data.getFactionUtil();
        raidTable = new RaidTable(this, data);
        raidScheduler = new RaidScheduler(data);
    }

    public ArrayList<Raid> getRaids(){
        return raids;
    }

    public boolean isFactionRaiding(int factionID){
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
        raids.add(new Raid(raidID, RaidState.PREPARING, factionIDAttackers, -1, null, 0));
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

    public boolean isRaidExists(int raidID){
        for(Raid raid : raids){
            if(raid.getRaidID() == raidID){
                return true;
            }
        }
        return false;
    }

    /** RaidStates:
     *  preparing,
     *  scouting,
     *  raiding,
     *  done
     */
    public RaidState getRaidState(int raidID){
        Raid raid = getRaid(raidID);
        return raid.getRaidState();
    }

    public void saveRaids() {
        for (Raid raid : raids) {
            raidTable.saveRaid(raid);
        }
    }

    public HashMap<Raid, FactionMember> getRaidTeams() {
        return raidTeams;
    }

    public RaidScheduler getRaidScheduler() {
        return raidScheduler;
    }

    public void deleteRaid(Raid raid) {
        raids.remove(raid);
        raidTable.deleteRaid(raid);
    }
}
