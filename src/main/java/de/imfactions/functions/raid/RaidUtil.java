package de.imfactions.functions.raid;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.Scheduler;
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
    private HashMap<Raid, FactionUserManager.FactionUser> raidTeams;
    private Scheduler scheduler;
    private FactionUserManager factionUserManager;
    private FactionManager factionManager;
    private Data data;

    public RaidManager(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        scheduler = data.getScheduler();
        factionUserManager = data.getFactionUserManager();
        factionManager = data.getFactionManager();
        imFactions.getData().getMySQL().update("CREATE TABLE IF NOT EXISTS `raids` (`raidID` MEDIUMINT NOT NULL AUTO_INCREMENT, `raidState` VARCHAR(64), `factionIdAttackers` INT(10), `factionIdDefenders` INT(10), `start` DATETIME, `time` BIGINT, PRIMARY KEY(`raidID`))");

        raids = new ArrayList<>();
        raidTeams = new HashMap<>();
        loadRaids();
        Bukkit.getScheduler().runTaskTimerAsynchronously(imFactions, new Runnable() {
            @Override
            public void run() {
                saveRaids();
            }
        }, 0, 20 * 60 * 10);
    }

    private void loadRaids() {
        try {
            ResultSet rs = imFactions.getData().getMySQL().querry("SELECT `raidID`, `raidState`, `factionIdAttackers`, `factionIdDefenders`, `start`, `time` FROM raids WHERE 1");
            while (rs.next()) {
                raids.add(new Raid(rs.getInt("raidID"), rs.getString("raidState"), rs.getInt("factionIdAttackers"), rs.getInt("factionIdDefenders"), rs.getDate("start"), rs.getLong("time")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public FactionManager.Faction getFactionForScout(int raidID, int factionIDScouting){
        Raid raid = getRaid(raidID);
        FactionManager.Faction faction = factionManager.getRandomFactionForRaid(raid.getFactionIdAttackers());
        while(faction.getId() == factionIDScouting){
            faction = factionManager.getRandomFactionForRaid(raid.getFactionIdAttackers());
        }
        return faction;
    }

    public void createStartingRaid(int raidID, int factionIDAttackers){
        raids.add(new Raid(raidID, "starting", factionIDAttackers, -1, null, 0));
    }

    public void updateRaidToActive(int raidID, int factionIDDefenders){
        Raid raid = getRaid(raidID);

        raid.setRaidState("active");
        raid.setTime(0);
        raid.setStart(Date.from(Instant.now()));
        raid.setFactionIdDefenders(factionIDDefenders);
    }

    public void updateRaidToDone(int raidID){
        Raid raid = getRaid(raidID);

        raid.setRaidState("done");
        raid.setTime(System.currentTimeMillis() - raid.getStart().getTime());
    }

    public ArrayList<FactionUserManager.FactionUser> getRaidTeam(int raidID){
        ArrayList<FactionUserManager.FactionUser> team = new ArrayList<>();
        Raid raid = getRaid(raidID);

        for(Map.Entry<Raid, FactionUserManager.FactionUser> entry : raidTeams.entrySet()){
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

        for (Map.Entry<Raid, FactionUserManager.FactionUser> entry : raidTeams.entrySet()) {
            FactionUserManager.FactionUser factionUser = entry.getValue();
            if (entry.getKey().equals(raid)) {
                Player player = factionUserManager.getPlayer(factionUser.getUuid());
                if(scheduler.getCountdowns().containsKey(player)) {
                    return scheduler.getCountdowns().get(player);
                }
            }
        }
        return -1;
    }


    public Location getRaidSpawn(int raidID){
        Raid raid= getRaid(raidID);

        for (Map.Entry<Raid, FactionUserManager.FactionUser> entry : raidTeams.entrySet()) {
            FactionUserManager.FactionUser factionUser = entry.getValue();
            if(entry.getKey().equals(raid)){
                Player player = factionUserManager.getPlayer(factionUser.getUuid());
                return scheduler.getRaids().get(player);
            }
        }
        return null;
    }

    public boolean isFactionUserUserJoinedRaid(FactionUserManager.FactionUser factionUser){
        if(raidTeams.containsValue(factionUser)){
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
     *  starting,
     *  scouting,
     *  active,
     *  done
     */
    public String getRaidState(int raidID){
        Raid raid = getRaid(raidID);
        return raid.getRaidState();
    }

    public void saveRaids() {
        for (Raid raid : raids) {
            raid.save();
        }
    }

    public HashMap<Raid, FactionUserManager.FactionUser> getRaidTeams() {
        return raidTeams;
    }
}
