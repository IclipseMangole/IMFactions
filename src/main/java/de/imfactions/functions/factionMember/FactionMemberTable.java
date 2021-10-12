package de.imfactions.functions.factionMember;

import de.imfactions.Data;
import de.imfactions.util.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class FactionMemberTable {
    
    private Data data;
    private MySQL mySQL;
    private FactionMemberUtil factionMemberUtil;
    
    public FactionMemberTable(FactionMemberUtil factionMemberUtil, Data data){
        this.data = data;
        this.factionMemberUtil = factionMemberUtil;
        mySQL = data.getMySQL();
        createFactionMemberTable();
    }
    
    private void createFactionMemberTable(){
        mySQL.update("CREATE TABLE IF NOT EXISTS `factionMembers` (`uuid` VARCHAR(64), `factionId` INT(10), `rank` INT(10), PRIMARY KEY(`uuid`))");
    }

    public void createFactionMember(UUID uuid, int factionId, int rank) {
        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            mySQL.update("INSERT INTO factionMembers (`uuid`, `factionId`, `rank`) VALUES ('" + uuid + "', '" + factionId + "', '" + rank + "')");
        }
    }

    public ArrayList<FactionMember> getFactionMembers() {
        ArrayList<FactionMember> factionMembers = new ArrayList<>(); 
        try {
            ResultSet rs = mySQL.querry("SELECT `uuid`, `factionId`, `rank` FROM factionMembers WHERE 1");
            while (rs.next()) {
                factionMembers.add(new FactionMember(UUID.fromString(rs.getString("uuid")), rs.getInt("factionId"), rs.getInt("rank")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factionMembers;
    }

    public void saveFactionMember(FactionMember factionMember) {
        mySQL.update("UPDATE factionMembers SET `factionId` = '" + factionMember.getFactionID() + "', `rank` = '" + factionMember.getRank() + "' WHERE `uuid` = '" + factionMember.getUuid().toString() + "'");
    }

    public void deleteFactionMember(FactionMember factionMember) {
        mySQL.update("DELETE FROM factionMembers WHERE `uuid` = '" + factionMember.getUuid().toString() + "'");
    }
}
