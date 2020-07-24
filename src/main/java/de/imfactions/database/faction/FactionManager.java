package de.imfactions.database.faction;

import com.mysql.fabric.xmlrpc.base.Array;
import de.imfactions.IMFactions;
import org.bukkit.Bukkit;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Player;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class FactionManager {

    private ArrayList<Faction> factionsList;
    private IMFactions factions;
    private int raidEnergyCooldown;

    public FactionManager(IMFactions factions) {
        this.factions = factions;
        raidEnergyCooldown = 0;
        factions.getData().getMySQL().update("CREATE TABLE IF NOT EXISTS `factions` (`factionID` MEDIUMINT NOT NULL AUTO_INCREMENT, `userAmount` INT(10), `name` VARCHAR(30), `shortcut` VARCHAR(3), `foundingDate` DATETIME, `raidProtection` BIGINT, `raidEnergy` INT(2), PRIMARY KEY(`factionID`))");
        factionsList = new ArrayList<>();
        loadFactions();
        Bukkit.getScheduler().runTaskTimerAsynchronously(factions, new Runnable() {
            @Override
            public void run() {
                saveFactions();

                for(Faction faction : factionsList){
                    if(faction.getRaidEnergy() < 20){
                        if(raidEnergyCooldown == 6){
                            faction.setRaidEnergy(faction.getRaidEnergy() + 1);
                            raidEnergyCooldown = raidEnergyCooldown - 6;
                        }else{
                            raidEnergyCooldown = raidEnergyCooldown + 1;
                        }
                    }
                }

            }
        }, 0, 10 * 60 * 20);
    }

    public void createFaction(int factionID, String name, String shortcut, int userAmount, Date foundingDate, long raidProtection, int raidEnergy) {
        if (!isFactionExists(factionID)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            factions.getData().getMySQL().update("INSERT INTO factions (`factionID`, `userAmount`, `name`, `shortcut`, `foundingDate`, `raidProtection`, `raidEnergy`) VALUES ('" + factionID + "', '" + userAmount + "', '" + name + "', '" + shortcut + "', '" + sdf.format(foundingDate) + "', '" + raidProtection + "', '" + raidEnergy + "')");
        }
    }

    public ArrayList<Faction> getRaidableFactions(){
        ArrayList<Faction> raidableFactions = new ArrayList<>();
        for(Faction faction : factionsList){
            if(faction.isRaidable()){
                raidableFactions.add(faction);
            }
        }
        return raidableFactions;
    }

    public void createFaction(int factionID, String name, String shortcut) {
        new Faction(factionID, name, shortcut);
    }

    public Faction getFaction(int factionID) {
        for (Faction faction : factionsList) {
            if (faction.getId() == factionID) {
                return faction;
            }
        }
        return null;
    }

    public Faction getFaction(String name) {
        for (Faction faction : factionsList) {
            if (faction.getName().equals(name)) {
                return faction;
            }
        }
        return null;
    }

    public String getFactionName(int factionID){
        for(Faction faction : factionsList){
            if(faction.getId() == factionID){
                return faction.getName();
            }
        }
        return "";
    }

    public int getFactionID(String name){
        for(Faction faction : factionsList){
            if(faction.name.equals(name)){
                return faction.getId();
            }
        }
        return -1;
    }

    public Faction getRandomFactionForRaid(int factionID){
        Random random = new Random();

        ArrayList<Faction> raidableFactions = getRaidableFactions();
        Faction faction = raidableFactions.get(random.nextInt(factionsList.size()));
        while(faction.getId() == factionID){
            faction = factionsList.get(random.nextInt(factionsList.size()));
        }
        return faction;
    }

    public boolean isFactionExists(int factionID) {
        for (Faction faction : factionsList) {
            if (faction.getId() == factionID) {
                return true;
            }
        }
        return false;
    }

    public boolean isFactionExists(String name) {
        for (Faction faction : factionsList) {
            if (faction.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void loadFactions() {
        try {
            ResultSet rs = factions.getData().getMySQL().querry("SELECT `factionID`, `userAmount`, `name`, `shortcut`, `foundingDate`, `raidProtection`, `raidEnergy` FROM `factions` WHERE 1");
            while (rs.next()) {
                Faction faction = new Faction(rs.getInt("factionID"), rs.getString("name"), rs.getString("shortcut"), rs.getInt("userAmount"), rs.getDate("foundingDate"), rs.getLong("raidProtection"), rs.getInt("raidEnergy"));
                factionsList.add(faction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveFactions() {
        for (Faction faction : factionsList) {
            faction.save();
        }
    }

    public ArrayList<Faction> getFactions() {
        ArrayList<Faction> factions = new ArrayList<>();
        try {
            ResultSet rs = this.factions.getData().getMySQL().querry("SELECT `factionID`, `userAmount`, `name`, `shortcut`, `foundingDate`, `raidProtection`, `raidEnergy` FROM `factions` WHERE 1");
            while (rs.next()) {
                Faction faction = new Faction(rs.getInt("factionID"), rs.getString("name"), rs.getString("shortcut"), rs.getInt("userAmount"), rs.getDate("foundingDate"), rs.getLong("raidProtection"), rs.getInt("raidEnergy"));
                factions.add(faction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factions;
    }

    public int getHighestFactionID() {
        try {
            ResultSet rs = this.factions.getData().getMySQL().querry("SELECT MAX(`factionID`) AS `factionID` FROM `factions` WHERE 1");
            if (rs.next()) {
                return rs.getInt("factionID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public class Faction {

        private int factionID;
        private int userAmount;
        private String name;
        private String shortcut;
        private Date foundingDate;
        private long raidProtection;
        private int raidEnergy;

        private Faction(int factionID, String name, String shortcut, int userAmount, Date foundingDate, long raidProtection, int raidEnergy) {
            this.factionID = factionID;
            this.name = name;
            this.shortcut = shortcut;
            this.userAmount = userAmount;
            this.foundingDate = foundingDate;
            this.raidProtection = raidProtection;
            this.raidEnergy = raidEnergy;
        }

        public Faction(int factionID, String name, String shortcut) {
            this.factionID = factionID;
            this.name = name;
            this.shortcut = shortcut;
            userAmount = 1;
            foundingDate = Date.from(Instant.now());
            raidProtection = System.currentTimeMillis() +  3*60*60*1000;
            raidEnergy = 20;
            createFaction(factionID, name, shortcut, userAmount, foundingDate, raidProtection, raidEnergy);
            factionsList.add(this);
            save();
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return factionID;
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

        public String getShortcut() {
            return shortcut;
        }

        public boolean isRaidable(){
            if(System.currentTimeMillis() >= raidProtection){
                return true;
            }
            return false;
        }

        public void setShortcut(String shortcut) {
            this.shortcut = shortcut;
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

        public void setId(int factionID) {
            this.factionID = factionID;
        }

        public void setUserAmount(int userAmount) {
            this.userAmount = userAmount;
        }

        public int getRaidEnergy() {
            return raidEnergy;
        }

        public void setRaidEnergy(int raidEnergy) {
            this.raidEnergy = raidEnergy;
        }

        public void save() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            factions.getData().getMySQL().update("UPDATE factions SET `name` = " + name + ", `shortcut` = " + shortcut + ", `userAmount` = '" + userAmount + ", `foundingDate` = " + sdf.format(foundingDate) + ", `raidProtection` = " + raidProtection + ", `raidEnergy` = " +raidEnergy + " WHERE `factionID` = '" + factionID + "'");
        }

        public void deleteFaction() {
            factions.getData().getMySQL().update("DELETE FROM factions WHERE `factionID` = '" + factionID + "'");
            factionsList.remove(this);
        }
    }
}


