package de.imfactions.functions.factionPlot;

import de.imfactions.Data;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.util.LocationBuilder;
import de.imfactions.util.MySQL;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FactionPlotTable {

    private Data data;
    private MySQL mySQL;
    private FactionPlotUtil factionPlotUtil;

    public FactionPlotTable(FactionPlotUtil factionPlotUtil, Data data){
        this.data = data;
        this.factionPlotUtil = factionPlotUtil;
        mySQL = data.getMySQL();
        createFactionPlotTable();
    }
    
    private void createFactionPlotTable(){
        mySQL.update("CREATE TABLE IF NOT EXISTS `factionPlots` (`factionID` INT(10), `edgeDownFrontLeft` VARCHAR(100), `edgeUpBackRight` VARCHAR(100), `home` VARCHAR(100), `loading` BOOLEAN, `position` INT(10), PRIMARY KEY(`factionID`))");
    }

    public void createFactionPlot(int factionID, Location edgeDownFrontLeft, Location edgeUpBackRight, Location home, boolean loading, int position) {
        if (!factionPlotUtil.isFactionPlotExists(factionID)) {
            mySQL.update("INSERT INTO factionPlots (`factionID`, `edgeDownFrontLeft`, `edgeUpBackRight`, `home`, `loading`, `position`) VALUES ('" + factionID + "', '" + LocationBuilder.toString(edgeDownFrontLeft) + "', '" + LocationBuilder.toString(edgeUpBackRight) + "', '" + LocationBuilder.toString(home) + "', '" + loading + "', '" + position + "')");
        }
    }

    public ArrayList<FactionPlot> getFactionPlots() {
        ArrayList<FactionPlot> factionPlots = new ArrayList<>();
        try {
            ResultSet rs = mySQL.querry("SELECT `factionID`, `edgeDownFrontLeft`, `edgeUpBackRight`, `home`, `loading`, `position` FROM factionPlots WHERE 1");
            while (rs.next()) {
                FactionPlot factionPlot = new FactionPlot(rs.getInt("factionID"), LocationBuilder.fromString(rs.getString("edgeDownFrontLeft")), LocationBuilder.fromString(rs.getString("edgeUpBackRight")), LocationBuilder.fromString(rs.getString("home")), rs.getBoolean("loading"), rs.getInt("position"));
                factionPlots.add(factionPlot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factionPlots;
    }

    public void saveFactionPlot(FactionPlot factionPlot){
        mySQL.update("UPDATE factionPlots SET `edgeDownFrontLeft` = '" + LocationBuilder.toString(factionPlot.edgeDownFrontLeft) + "', `edgeUpBackRight` = '" + LocationBuilder.toString(factionPlot.edgeUpBackRight) + "', `home` = '" + LocationBuilder.toString(factionPlot.home) + "', `loading` = '" + factionPlot.loading + "', `position` = '" + factionPlot.position + "' WHERE `factionID` = '" + factionPlot.factionID + "'");

    }

    public void deleteFactionPlot(FactionPlot factionPlot){
        mySQL.update("DELETE FROM factionPlots WHERE `factionID` = '" + factionPlot.factionID + "'");
    }
}
