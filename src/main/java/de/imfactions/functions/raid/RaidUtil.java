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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RaidUtil {

    private IMFactions imFactions;
    private ArrayList<Raid> raids;
    private HashMap<Raid, FactionMember> raidTeams;
    private Scheduler scheduler;
    private FactionMemberUtil factionMemberUtil;
    private FactionUtil factionUtil;
    private RaidTable raidTable;
    private Data data;

    public RaidUtil(Data data) {
        this.data = data;
        imFactions = data.getImFactions();
        scheduler = data.getScheduler();
        factionMemberUtil = data.getFactionMemberUtil();
        factionUtil = data.getFactionUtil();
        raidTable = new RaidTable(this, data);
        raids = raidTable.getRaids();
        raidTeams = new HashMap<>();
        Bukkit.getScheduler().runTaskTimerAsynchronously(imFactions, new Runnable() {
            @Override
            public void run() {
                saveRaids();
            }
        }, 0, 20 * 60 * 10);
    }

    public ArrayList<Raid> getRaids(){
        return raids;
    }

    public boolean isFactionRaiding(int factionID){
        for(Raid raid : raids){
            if(raid.getFactionIdAttackers() == factionID) {
                if (!raid.getRaidState().equals("done")) {
                    return true;
                }
            }
        }
        return false;
    }

    public Faction getFactionForScout(int raidID, int factionIDScouting){
        Raid raid = getRaid(raidID);
        Faction faction = factionUtil.getRandomFactionForRaid(raid.getFactionIdAttackers());
        while(faction.getId() == factionIDScouting){
            faction = factionUtil.getRandomFactionForRaid(raid.getFactionIdAttackers());
        }
        return faction;
    }

    public void createPreparingRaid(int raidID, int factionIDAttackers){
        raids.add(new Raid(raidID, RaidState.PREPARING, factionIDAttackers, -1, null, 0));
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
                if (!raid.getRaidState().equals("done"))
                    return raid.getRaidID();
            }
        }
        return -1;
    }

    public ArrayList<Raid> getActiveRaids(){
        ArrayList<Raid> activeRaids = new ArrayList<>();
        for(Raid raid : raids){
            if(raid.getRaidState().equals("active")){
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
}
