package de.imfactions.functions.factionPlot;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.util.LocationChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;

public class FactionPlotUtil {
    private ArrayList<FactionPlot> factionPlots;
    private final IMFactions imFactions;
    private final Data data;
    private int loadingFactionPlots;
    private final FactionPlotTable factionPlotTable;

    public FactionPlotUtil(Data data) {
        this.data = data;
        imFactions = data.getImFactions();
        loadingFactionPlots = 0;
        factionPlotTable = new FactionPlotTable(this, data);
        factionPlots = factionPlotTable.getFactionPlots();
        Bukkit.getScheduler().runTaskTimerAsynchronously(imFactions, new Runnable() {
            @Override
            public void run() {
                saveFactionPlots();
            }
        }, 0, 10 * 60 * 20);
    }

    public FactionPlot getFactionPlot(int factionID) {
        for (FactionPlot factionPlot : factionPlots) {
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
        for (FactionPlot factionPlot : factionPlots) {
            if (factionPlot.getFactionID() == factionID) {
                return true;
            }
        }
        return false;
    }

    public boolean isPositionFree(int position){
        for(FactionPlot factionPlot : factionPlots){
            if(factionPlot.getPosition() == position){
                return false;
            }
        }
        return true;
    }

    public int getHighestPosition(){
        int position = 0;
        for(FactionPlot factionPlot : factionPlots){
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

    public FactionPlot getFactionPlot(Location location){

        for (FactionPlot factionPlot : factionPlots){
            Location CompletEdgeLeft = getCompleteEdgeLeft(factionPlot.getEdgeDownFrontLeft());
            Location CompleteEdgeRight = getCompleteEdgeRight(CompletEdgeLeft);

            if(LocationChecker.isLocationInsideCube(location, CompletEdgeLeft, CompleteEdgeRight)){
                return factionPlot;
            }
        }
        return null;
    }

    public void saveFactionPlots() {
        for (FactionPlot factionPlot : factionPlots) {
            factionPlotTable.saveFactionPlot(factionPlot);
        }
    }

    public ArrayList<FactionPlot> getFactionPlots() {
        return factionPlots;
    }

    public void deleteFactionPlot(FactionPlot factionPlot){
        factionPlotTable.deleteFactionPlot(factionPlot);
        factionPlots.remove(factionPlot);
    }

    public void createFactionPlot(int factionID, Location edgeDownFrontLeft, Location edgeUpBackRight, Location home, boolean loading, int position){
        factionPlotTable.createFactionPlot(factionID, edgeDownFrontLeft, edgeUpBackRight, home, loading, position);
    }

    public void createFactionPlot(int factionID){
        int position = getFreePosition();
        Location edgeDownFrontLeft = getEdgeDownFrontLeft(position);
        Location edgeUpBackRight = getEdgeUpBackRight(edgeDownFrontLeft);
        Location home = new Location(edgeDownFrontLeft.getWorld(), edgeDownFrontLeft.getX(), edgeDownFrontLeft.getY() + 17, edgeDownFrontLeft.getZ());
        factionPlotTable.createFactionPlot(factionID, edgeDownFrontLeft, edgeUpBackRight, home, true, position);
    }
}
