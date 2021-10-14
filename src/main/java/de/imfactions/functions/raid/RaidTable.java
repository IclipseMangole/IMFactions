package de.imfactions.functions.raid;

import de.imfactions.Data;
import de.imfactions.util.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RaidTable {

    private final Data data;
    private final MySQL mySQL;
    private final RaidUtil raidUtil;

    public RaidTable(RaidUtil raidUtil, Data data){
        this.data = data;
        this.raidUtil = raidUtil;
        mySQL = data.getMySQL();
        createRaidTable();
    }
    
    private void createRaidTable(){
        mySQL.update("CREATE TABLE IF NOT EXISTS `raids` (`raidID` MEDIUMINT NOT NULL AUTO_INCREMENT, `raidState` VARCHAR(64), `factionIdAttackers` INT(10), `factionIdDefenders` INT(10), `start` DATETIME, `time` BIGINT, PRIMARY KEY(`raidID`))");
    }

    public ArrayList<Raid> getRaids() {
        ArrayList<Raid> raids = new ArrayList<>();
        try {
            ResultSet rs = mySQL.querry("SELECT `raidID`, `raidState`, `factionIdAttackers`, `factionIdDefenders`, `start`, `time` FROM raids WHERE 1");
            while (rs.next()) {
                raids.add(new Raid(rs.getInt("raidID"), RaidState.getStateFromString(rs.getString("raidState")), rs.getInt("factionIdAttackers"), rs.getInt("factionIdDefenders"), rs.getDate("start"), rs.getLong("time")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return raids;
    }

    public void saveRaid(Raid raid) {
        mySQL.update("UPDATE `raids` SET `raidID` = '" + raid.raidID + "', `raidState` = '" + RaidState.getStringFromState(raid.raidState) + "', `factionIdAttackers` = " + raid.factionIdAttackers + ", `factionIdDefenders` = " + raid.factionIdDefenders + ", `start` = '" + raid.start + "', `time` = '" + raid.time + "'");
    }

    public void deleteRaid(Raid raid) {
        mySQL.update("DELETE FROM `raids` WHERE `raidID` = '" + raid.raidID + "'");
    }
}
