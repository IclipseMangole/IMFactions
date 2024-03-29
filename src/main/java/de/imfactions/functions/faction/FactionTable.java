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
        mySQL.update("CREATE TABLE IF NOT EXISTS `factions` (`factionID` MEDIUMINT NOT NULL AUTO_INCREMENT, `memberAmount` INT(10), `name` VARCHAR(30), `shortcut` VARCHAR(3), `foundingDate` DATETIME, `raidProtection` BIGINT, `raidEnergy` INT(2), `gettingRaided` BOOL, PRIMARY KEY(`factionID`))");
    }

    public void createFaction(int factionID, String name, String shortcut, int memberAmount, Date foundingDate, long raidProtection, int raidEnergy, boolean gettingRaided) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int getRaided = 0;
        if (gettingRaided)
            getRaided = 1;
        data.getMySQL().update("INSERT INTO `factions` (`factionID`, `memberAmount`, `name`, `shortcut`, `foundingDate`, `raidProtection`, `raidEnergy`, `gettingRaided`) VALUES ('" + factionID + "', '" + memberAmount + "', '" + name + "', '" + shortcut + "', '" + sdf.format(foundingDate) + "', '" + raidProtection + "', '" + raidEnergy + "', '" + getRaided + "')");
    }

    public void loadFactions() {
        try {
            ResultSet rs = data.getMySQL().querry("SELECT `factionID`, `memberAmount`, `name`, `shortcut`, `foundingDate`, `raidProtection`, `raidEnergy`, `gettingRaided` FROM `factions` WHERE 1");
            while (rs.next()) {
                Faction faction = new Faction(rs.getInt("factionID"), rs.getString("name"), rs.getString("shortcut"), rs.getInt("memberAmount"), rs.getDate("foundingDate"), rs.getLong("raidProtection"), rs.getInt("raidEnergy"), rs.getBoolean("gettingRaided"));
                factionUtil.getFactions().add(faction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Faction> getFactions() {
        ArrayList<Faction> factions = new ArrayList<>();
        try {
            ResultSet rs = mySQL.querry("SELECT `factionID`, `memberAmount`, `name`, `shortcut`, `foundingDate`, `raidProtection`, `raidEnergy`, `gettingRaided` FROM `factions` WHERE 1");
            while (rs.next()) {
                Faction faction = new Faction(rs.getInt("factionID"), rs.getString("name"), rs.getString("shortcut"), rs.getInt("memberAmount"), rs.getDate("foundingDate"), rs.getLong("raidProtection"), rs.getInt("raidEnergy"), rs.getBoolean("gettingRaided"));
                factions.add(faction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factions;
    }

    public int getHighestFactionID() {
        int highest = 0;
        try {
            ResultSet rs = mySQL.querry("SELECT `factionID` FROM `factions` WHERE 1");
            while (rs.next()) {
                if (rs.getInt("factionID") > highest)
                    highest = rs.getInt("factionID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highest;
    }

    public void saveFaction(Faction faction) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mySQL.update("UPDATE `factions` SET `factionID` = " + faction.getId() + ", `memberAmount` = " + faction.getMemberAmount() + ", `name` = '" + faction.getName() + "', `shortcut` = '" + faction.getShortcut() + "', `foundingDate` = '" + sdf.format(faction.getFoundingDate()) + "', `raidProtection` = '" + faction.getRaidProtection() + "', `raidEnergy` = " + faction.getRaidEnergy() + ", `gettingRaided` = " + faction.isGettingRaided() + " WHERE `factionID` = " + faction.getId() + "");
    }

    public void deleteFaction(Faction faction){
        mySQL.update("DELETE FROM `factions` WHERE `factionID` = " + faction.getId() + "");
    }
}
