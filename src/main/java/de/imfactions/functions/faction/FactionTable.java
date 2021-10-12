package de.imfactions.functions.faction;

import de.imfactions.Data;
import de.imfactions.util.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FactionTable {

    private final Data data;
    private final MySQL mySQL;
    private final FactionUtil factionUtil;

    public FactionTable(FactionUtil factionUtil, Data data){
        this.data = data;
        this.factionUtil = factionUtil;
        mySQL = data.getMySQL();
        createFactionTable();
    }

    private void createFactionTable(){
        mySQL.update("CREATE TABLE IF NOT EXISTS `factions` (`factionID` MEDIUMINT NOT NULL AUTO_INCREMENT, `userAmount` INT(10), `name` VARCHAR(30), `shortcut` VARCHAR(3), `foundingDate` DATETIME, `raidProtection` BIGINT, `raidEnergy` INT(2), PRIMARY KEY(`factionID`))");
    }

    public void createFaction(int factionID, String name, String shortcut, int userAmount, Date foundingDate, long raidProtection, int raidEnergy) {
        if (!factionUtil.isFactionExists(factionID)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            data.getMySQL().update("INSERT INTO factions (`factionID`, `userAmount`, `name`, `shortcut`, `foundingDate`, `raidProtection`, `raidEnergy`) VALUES ('" + factionID + "', '" + userAmount + "', '" + name + "', '" + shortcut + "', '" + sdf.format(foundingDate) + "', '" + raidProtection + "', '" + raidEnergy + "')");
        }
    }

    public void loadFactions() {
        try {
            ResultSet rs = data.getMySQL().querry("SELECT `factionID`, `userAmount`, `name`, `shortcut`, `foundingDate`, `raidProtection`, `raidEnergy` FROM `factions` WHERE 1");
            while (rs.next()) {
                Faction faction = new Faction(rs.getInt("factionID"), rs.getString("name"), rs.getString("shortcut"), rs.getInt("userAmount"), rs.getDate("foundingDate"), rs.getLong("raidProtection"), rs.getInt("raidEnergy"));
                factionUtil.getFactions().add(faction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Faction> getFactions() {
        ArrayList<Faction> factions = new ArrayList<>();
        try {
            ResultSet rs = mySQL.querry("SELECT `factionID`, `userAmount`, `name`, `shortcut`, `foundingDate`, `raidProtection`, `raidEnergy` FROM `factions` WHERE 1");
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
            ResultSet rs = mySQL.querry("SELECT MAX(`factionID`) AS `factionID` FROM `factions` WHERE 1");
            if (rs.next()) {
                return rs.getInt("factionID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void saveFaction(Faction faction){

    }

    public void deleteFaction(Faction faction){

    }
}
