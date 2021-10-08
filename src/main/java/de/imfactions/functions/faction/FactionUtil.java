package de.imfactions.functions.faction;

import de.imfactions.IMFactions;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class FactionUtil {

    private ArrayList<FactionManager.Faction> factionsList;
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

                for(FactionManager.Faction faction : factionsList){
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

    public ArrayList<FactionManager.Faction> getRaidableFactions(){
        ArrayList<FactionManager.Faction> raidableFactions = new ArrayList<>();
        for(FactionManager.Faction faction : factionsList){
            if(faction.isRaidable()){
                raidableFactions.add(faction);
            }
        }
        return raidableFactions;
    }

    public void createFaction(int factionID, String name, String shortcut) {
        new FactionManager.Faction(factionID, name, shortcut);
    }

    public FactionManager.Faction getFaction(int factionID) {
        for (FactionManager.Faction faction : factionsList) {
            if (faction.getId() == factionID) {
                return faction;
            }
        }
        return null;
    }

    public FactionManager.Faction getFaction(String name) {
        for (FactionManager.Faction faction : factionsList) {
            if (faction.getName().equals(name)) {
                return faction;
            }
        }
        return null;
    }

    public String getFactionName(int factionID){
        for(FactionManager.Faction faction : factionsList){
            if(faction.getId() == factionID){
                return faction.getName();
            }
        }
        return "";
    }

    public int getFactionID(String name){
        for(FactionManager.Faction faction : factionsList){
            if(faction.name.equals(name)){
                return faction.getId();
            }
        }
        return -1;
    }

    public FactionManager.Faction getRandomFactionForRaid(int factionID){
        Random random = new Random();

        ArrayList<FactionManager.Faction> raidableFactions = getRaidableFactions();
        FactionManager.Faction faction = raidableFactions.get(random.nextInt(factionsList.size()));
        while(faction.getId() == factionID){
            faction = factionsList.get(random.nextInt(factionsList.size()));
        }
        return faction;
    }

    public boolean isFactionExists(int factionID) {
        for (FactionManager.Faction faction : factionsList) {
            if (faction.getId() == factionID) {
                return true;
            }
        }
        return false;
    }

    public boolean isFactionExists(String name) {
        for (FactionManager.Faction faction : factionsList) {
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
                FactionManager.Faction faction = new FactionManager.Faction(rs.getInt("factionID"), rs.getString("name"), rs.getString("shortcut"), rs.getInt("userAmount"), rs.getDate("foundingDate"), rs.getLong("raidProtection"), rs.getInt("raidEnergy"));
                factionsList.add(faction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveFactions() {
        for (FactionManager.Faction faction : factionsList) {
            faction.save();
        }
    }

    public ArrayList<FactionManager.Faction> getFactions() {
        ArrayList<FactionManager.Faction> factions = new ArrayList<>();
        try {
            ResultSet rs = this.factions.getData().getMySQL().querry("SELECT `factionID`, `userAmount`, `name`, `shortcut`, `foundingDate`, `raidProtection`, `raidEnergy` FROM `factions` WHERE 1");
            while (rs.next()) {
                FactionManager.Faction faction = new FactionManager.Faction(rs.getInt("factionID"), rs.getString("name"), rs.getString("shortcut"), rs.getInt("userAmount"), rs.getDate("foundingDate"), rs.getLong("raidProtection"), rs.getInt("raidEnergy"));
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
}
