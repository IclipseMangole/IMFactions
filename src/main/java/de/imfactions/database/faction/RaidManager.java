package de.imfactions.database.faction;

import de.imfactions.IMFactions;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class RaidManager {

    private IMFactions imFactions;
    private ArrayList<Raid> raids;

    public RaidManager(IMFactions imFactions) {
        this.imFactions = imFactions;
        imFactions.getData().getMySQL().update("CREATE TABLE IF NOT EXISTS `raids` (`raidId` INT(10), `factionIdAttackers` INT(10), `factionIdDefenders` INT(10), `start` DATETIME, `time` BIGINT, PRIMARY KEY(`raidId`))");
        raids = new ArrayList<>();
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
            ResultSet rs = imFactions.getData().getMySQL().querry("SELECT `raidId`, `factionIdAttackers`, `factionIdDefenders`, `start`, `time` FROM raids WHERE 1");
            while (rs.next()) {
                raids.add(new Raid(rs.getInt("raidId"), rs.getInt("factionIdAttackers"), rs.getInt("factionIdDefenders"), rs.getDate("start"), rs.getLong("time")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveRaids() {
        for (Raid raid : raids) {
            raid.save();
        }
    }

    public class Raid {
        int raidId;
        int factionIdAttackers;
        int factionIdDefenders;
        Date start;
        long time;

        public Raid(int raidId, int factionIdAttackers, int factionIdDefenders, Date start, long time) {
            this.raidId = raidId;
            this.factionIdAttackers = factionIdAttackers;
            this.factionIdDefenders = factionIdDefenders;
            this.start = start;
            this.time = time;
        }

        public Raid(int raidId, int factionIdAttackers, int factionIdDefenders) {
            this.raidId = raidId;
            this.factionIdAttackers = factionIdAttackers;
            this.factionIdDefenders = factionIdDefenders;
            start = Date.from(Instant.now());
            time = 0;
        }

        public int getFactionIdAttackers() {
            return factionIdAttackers;
        }

        public void setFactionIdAttackers(int factionIdAttackers) {
            this.factionIdAttackers = factionIdAttackers;
        }

        public int getFactionIdDefenders() {
            return factionIdDefenders;
        }

        public void setFactionIdDefenders(int factionIdDefenders) {
            this.factionIdDefenders = factionIdDefenders;
        }

        public Date getStart() {
            return start;
        }

        public void setStart(Date start) {
            this.start = start;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public int getRaidId() {
            return raidId;
        }

        public void setRaidId(int raidId) {
            this.raidId = raidId;
        }

        public void save() {
            imFactions.getData().getMySQL().update("UPDATE raids SET `raidId` = '" + raidId + "', `factionIdAttackers` = '" + factionIdAttackers + "', `factionIdDefenders` = '" + factionIdDefenders + "', `start` = '" + start + "', `time` = '" + start + "'");
        }
    }
}
