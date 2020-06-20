package de.imfactions.database;

import de.imfactions.IMFactions;
import de.imfactions.functions.plots.FactionPlot;
import io.netty.handler.codec.http.HttpContentEncoder;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class FactionManager {

    private ArrayList<Faction> factions;

    public FactionManager() {
        IMFactions.getInstance().getData().getMySQL().update("CREATE TABLE IF NOT EXISTS factions (factionId id MEDIUMINT NOT NULL AUTO_INCREMENT, userAmount INT(10), name VARCHAR(64), raidsWon INT(10), foundingDate DATETIME, raidProtection BIGINT, PRIMARY KEY (factionId))");
        factions = new ArrayList<>();
        loadFactions();
        Bukkit.getScheduler().runTaskTimerAsynchronously(IMFactions.getInstance(), new Runnable() {
            @Override
            public void run() {
                saveFactions();
            }
        }, 0, 10 * 60 * 20);
    }

    public void createFaction(String name, int userAmount, int raidsWon, Date foundingDate, long raidProtection) {
        if (!isFactionExists(name)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            IMFactions.getInstance().getData().getMySQL().update("INSERT INTO factions (userAmount, name, raidsWon, foundingDate, raidProtection) VALUES ('" + userAmount + "', '" + name + "', '" + raidsWon + "', '" + sdf.format(foundingDate) + "', '" + raidProtection + "')");
        }
    }

    public void createFaction(int factionId, String name) {
        new Faction(factionId, name);
    }

    public Faction getFaction(int factionId) {
        for (Faction faction : factions) {
            if (faction.getId() == factionId) {
                return faction;
            }
        }
        return null;
    }

    public Faction getFaction(String name) {
        for (Faction faction : factions) {
            if (faction.getName().equals(name)) {
                return faction;
            }
        }
        return null;
    }

    public boolean isFactionExists(int factionId) {
        for (Faction faction : factions) {
            if (faction.getId() == factionId) {
                return true;
            }
        }
        return false;
    }

    public boolean isFactionExists(String name) {
        for (Faction faction : factions) {
            if (faction.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void loadFactions() {
        try {
            ResultSet rs = IMFactions.getInstance().getData().getMySQL().querry("SELECT factionId, userAmount, name, raidsWon, foundingDate, raidProtection FROM `factions` WHERE 1");
            while (rs.next()) {
                Faction faction = new Faction(rs.getInt("factionId"), rs.getString("name"), rs.getInt("userAmount"), rs.getInt("raidsWon"), rs.getDate("foundingDate"), rs.getLong("raidProtection"));
                factions.add(faction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveFactions() {
        for (Faction faction : factions) {
            faction.save();
        }
    }

    public ArrayList<Faction> getFactions() {
        ArrayList<Faction> factions = new ArrayList<>();
        try {
            ResultSet rs = IMFactions.getInstance().getData().getMySQL().querry("SELECT factionId, userAmount, name, raidsWon, foundingDate FROM `factions` WHERE 1");
            while (rs.next()) {
                Faction faction = new Faction(rs.getInt("factionId"), rs.getString("name"), rs.getInt("userAmount"), rs.getInt("raidsWon"), rs.getDate("foundingDate"), rs.getLong("raidProtection"));
                factions.add(faction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factions;
    }

    public int getHighestFactionId() {
        try {
            ResultSet rs = IMFactions.getInstance().getData().getMySQL().querry("SELECT MAX(factionId) AS factionId FROM factions WHERE 1");
            if (rs.next()) {
                return rs.getInt("factionId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public class Faction {

        private int factionId;
        private int userAmount;
        private String name;
        private int raidsWon;
        private Date foundingDate;
        private long raidProtection;

        private Faction(int factionId, String name, int userAmount, int raidsWon, Date foundingDate, long raidProtection) {
            this.factionId = factionId;
            this.name = name;
            this.userAmount = userAmount;
            this.raidsWon = raidsWon;
            this.foundingDate = foundingDate;
            this.raidProtection = raidProtection;
        }

        public Faction(int factionId, String name) {
            this.factionId = factionId;
            this.name = name;
            userAmount = 1;
            raidsWon = 0;
            foundingDate = Date.from(Instant.now());
            raidProtection = 3600 * 24;
            createFaction(name, userAmount, raidsWon, foundingDate, raidProtection);
            save();
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return factionId;
        }

        public int getRaidsWon() {
            return raidsWon;
        }

        public int getUserAmount() {
            return userAmount;
        }

        public Date getFoundingDate() {
            return foundingDate;
        }

        public long getRaidProtection() {
            return raidProtection;
        }

        public void addRaidProtection(long raidProtection) {
            this.raidProtection += raidProtection;
        }

        public void setRaidProtection(long raidProtection) {
            this.raidProtection = raidProtection;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setFoundingDate(Date foundingDate) {
            this.foundingDate = foundingDate;
        }

        public void setId(int factionId) {
            this.factionId = factionId;
        }

        public void setRaidsWon(int raidsWon) {
            this.raidsWon = raidsWon;
        }

        public void setUserAmount(int userAmount) {
            this.userAmount = userAmount;
        }

        public void save() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            IMFactions.getInstance().getData().getMySQL().update("UPDATE factions SET name = " + name + ", userAmount = '" + userAmount + ", raidsWon = " + raidsWon + ", foundingDate = " + sdf.format(foundingDate) + ", raidProtection = " + raidProtection + " WHERE factionId = '" + factionId + "'");
        }

        public void deleteFaction() {
            IMFactions.getInstance().getData().getMySQL().update("DELETE FROM factions WHERE factionId = '" + factionId + "'");
            factions.remove(this);
        }
    }
}

