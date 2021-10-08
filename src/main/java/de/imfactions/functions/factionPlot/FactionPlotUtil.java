package de.imfactions.functions.factionPlot;

import de.imfactions.IMFactions;
import de.imfactions.util.LocationBuilder;
import de.imfactions.util.LocationChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FactionPlotUtil {
    private ArrayList<de.imfactions.database.faction.FactionPlotManager.FactionPlot> factionPlots;
    private IMFactions factions;
    private int loadingFactionPlots;

    public FactionPlotManager(IMFactions factions) {
        this.factions = factions;
        loadingFactionPlots = 0;
        factions.getData().getMySQL().update("CREATE TABLE IF NOT EXISTS `factionPlots` (`factionID` INT(10), `edgeDownFrontLeft` VARCHAR(100), `edgeUpBackRight` VARCHAR(100), `home` VARCHAR(100), `reachable` BIGINT, `position` INT(10), PRIMARY KEY(`factionID`))");

        factionPlots = new ArrayList<>();
        loadFactionPlots();
        Bukkit.getScheduler().runTaskTimerAsynchronously(factions, new Runnable() {
            @Override
            public void run() {
                saveFactionPlots();
            }
        }, 0, 10 * 60 * 20);
    }

    public void createFactionPlot(int factionID, Location edgeDownFrontLeft, Location edgeUpBackRight, Location home, long reachable, int position) {
        if (!isFactionPlotExists(factionID)) {
            factions.getData().getMySQL().update("INSERT INTO factionPlots (`factionID`, `edgeDownFrontLeft`, `edgeUpBackRight`, `home`, `reachable`, `position`) VALUES ('" + factionID + "', '" + LocationBuilder.toString(edgeDownFrontLeft) + "', '" + LocationBuilder.toString(edgeUpBackRight) + "', '" + LocationBuilder.toString(home) + "', '" + reachable + "', '" + position + "')");
            factionPlots.add(new de.imfactions.database.faction.FactionPlotManager.FactionPlot(factionID, edgeDownFrontLeft, edgeUpBackRight, home, reachable, position));
        }
    }

    public de.imfactions.database.faction.FactionPlotManager.FactionPlot getFactionPlot(int factionID) {
        for (de.imfactions.database.faction.FactionPlotManager.FactionPlot factionPlot : factionPlots) {
            if (factionPlot.getFactionID() == factionID) {
                return factionPlot;
            }
        }
        return null;
    }

    public int getLoadingFactionPlots() {
        return loadingFactionPlots;
    }

    public void setLoadingFactionPlots(int loadingFactionPlots) {
        this.loadingFactionPlots = loadingFactionPlots;
    }

    public void addLoadingFactionPlots(int loadingFactionPlots){
        this.loadingFactionPlots += loadingFactionPlots;
    }

    public boolean isFactionPlotExists(int factionID) {
        for (de.imfactions.database.faction.FactionPlotManager.FactionPlot factionPlot : factionPlots) {
            if (factionPlot.getFactionID() == factionID) {
                return true;
            }
        }
        return false;
    }

    public boolean isPositionFree(int position){
        for(de.imfactions.database.faction.FactionPlotManager.FactionPlot factionPlot : factionPlots){
            if(factionPlot.getPosition() == position){
                return false;
            }
        }
        return true;
    }

    public int getHighestPosition(){
        int position = 0;
        for(de.imfactions.database.faction.FactionPlotManager.FactionPlot factionPlot : factionPlots){
            if(factionPlot.getPosition() > position){
                position = factionPlot.getPosition();
            }
        }
        return position;
    }

    public int getFreePosition(){
        int position = 0;
        for(int i = 0; i < getHighestPosition()+1; i++){
            if(isPositionFree(i)){
                return position;
            }
        }
        return getHighestPosition() + 1;
    }

    public Location getEdgeDownFrontLeft(int position){
        return new Location(Bukkit.getWorld("FactionPlots_world"), (position % 10) * 700 , 0, (position / 10) * 700);
    }

    public Location getEdgeUpBackRight(Location edgeDownFrontLeft){
        return new Location(Bukkit.getWorld("FactionPlots_world"), edgeDownFrontLeft.getX() + 99, edgeDownFrontLeft.getY() + 150, edgeDownFrontLeft.getZ() + 99);
    }

    public Location getRaidEdgeLeft(Location edgeDownFrontLeft){
        return new Location(Bukkit.getWorld("FactionPlots_world"), edgeDownFrontLeft.getX() - 14, edgeDownFrontLeft.getY(), edgeDownFrontLeft.getZ() - 14);
    }

    public Location getRaidEdgeRight(Location RaidEdgeLeft){
        return new Location(Bukkit.getWorld("FactionPlots_world"), RaidEdgeLeft.getX() + 128, RaidEdgeLeft.getY() + 150, RaidEdgeLeft.getZ() + 128);
    }

    public Location getCompleteEdgeLeft(Location edgeDownFrontLeft){
        return new Location(Bukkit.getWorld("FactionPlots_world"), edgeDownFrontLeft.getX() - 46, edgeDownFrontLeft.getY(), edgeDownFrontLeft.getZ() - 46);
    }

    public Location getCompleteEdgeRight(Location CompleteEdgeLeft){
        return new Location(Bukkit.getWorld("FactionPlots_world"), CompleteEdgeLeft.getX() + 191, CompleteEdgeLeft.getY() + 150, CompleteEdgeLeft.getZ() + 191);
    }

    public de.imfactions.database.faction.FactionPlotManager.FactionPlot getFactionPlot(Location location){

        for (de.imfactions.database.faction.FactionPlotManager.FactionPlot factionPlot : factionPlots){
            Location CompletEdgeLeft = getCompleteEdgeLeft(factionPlot.getEdgeDownFrontLeft());
            Location CompleteEdgeRight = getCompleteEdgeRight(CompletEdgeLeft);

            if(LocationChecker.isLocationInsideCube(location, CompletEdgeLeft, CompleteEdgeRight)){
                return factionPlot;
            }
        }
        return null;
    }

    public void loadFactionPlots() {
        try {
            ResultSet rs = factions.getData().getMySQL().querry("SELECT `factionID`, `edgeDownFrontLeft`, `edgeUpBackRight`, `home`, `reachable`, `position` FROM factionPlots WHERE 1");
            while (rs.next()) {
                de.imfactions.database.faction.FactionPlotManager.FactionPlot factionPlot = new de.imfactions.database.faction.FactionPlotManager.FactionPlot(rs.getInt("factionID"), LocationBuilder.fromString(rs.getString("edgeDownFrontLeft")), LocationBuilder.fromString(rs.getString("edgeUpBackRight")), LocationBuilder.fromString(rs.getString("home")), rs.getLong("reachable"), rs.getInt("position"));
                factionPlots.add(factionPlot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveFactionPlots() {
        for (de.imfactions.database.faction.FactionPlotManager.FactionPlot factionPlot : factionPlots) {
            factionPlot.save();
        }
    }

    public ArrayList<de.imfactions.database.faction.FactionPlotManager.FactionPlot> getFactionPlots() {
        return factionPlots;
    }
}
