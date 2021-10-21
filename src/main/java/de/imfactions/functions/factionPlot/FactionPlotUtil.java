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
    private FactionPlotTable factionPlotTable;

    public FactionPlotUtil(Data data) {
        this.data = data;
        imFactions = data.getImFactions();
        loadingFactionPlots = 0;
        Bukkit.getScheduler().runTaskTimerAsynchronously(imFactions, new Runnable() {
            @Override
            public void run() {
                saveFactionPlots();
            }
        }, 20 * 60, 10 * 60 * 20);
    }

    public void loadUtils(){
        factionPlotTable = new FactionPlotTable(this, data);
        factionPlots = factionPlotTable.getFactionPlots();
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
        return new Location(Bukkit.getWorld("FactionPlots_world"), edgeDownFrontLeft.getX() + 79, edgeDownFrontLeft.getY() + 128, edgeDownFrontLeft.getZ() + 79);
    }

    public Location getRaidEdgeLeft(Location edgeDownFrontLeft){
        return new Location(Bukkit.getWorld("FactionPlots_world"), edgeDownFrontLeft.getX() - 19, edgeDownFrontLeft.getY(), edgeDownFrontLeft.getZ() - 19);
    }

    public Location getRaidEdgeRight(Location RaidEdgeLeft){
        return new Location(Bukkit.getWorld("FactionPlots_world"), RaidEdgeLeft.getX() + 118, RaidEdgeLeft.getY() + 128, RaidEdgeLeft.getZ() + 118);
    }

    public Location getCompleteEdgeLeft(Location edgeDownFrontLeft) {
        return new Location(Bukkit.getWorld("FactionPlots_world"), edgeDownFrontLeft.getX() - 55, 0, edgeDownFrontLeft.getZ() - 55);
    }

    public Location getCompleteEdgeRight(Location CompleteEdgeLeft) {
        return new Location(Bukkit.getWorld("FactionPlots_world"), CompleteEdgeLeft.getX() + 189, CompleteEdgeLeft.getY() + 128, CompleteEdgeLeft.getZ() + 189);
    }

    public boolean isLocationOnFactionPlot(Location location) {
        for (FactionPlot factionPlot : factionPlots) {
            Location CompletEdgeLeft = getCompleteEdgeLeft(factionPlot.getEdgeDownFrontLeft());
            Location CompleteEdgeRight = getCompleteEdgeRight(CompletEdgeLeft);

            if (LocationChecker.isLocationInsideSquare(location, CompletEdgeLeft, CompleteEdgeRight)) {
                return true;
            }
        }
        return false;
    }

    public FactionPlot getFactionPlot(Location location) {
        for (FactionPlot factionPlot : factionPlots) {
            Location CompletEdgeLeft = getCompleteEdgeLeft(factionPlot.getEdgeDownFrontLeft());
            Location CompleteEdgeRight = getCompleteEdgeRight(CompletEdgeLeft);

            if (LocationChecker.isLocationInsideSquare(location, CompletEdgeLeft, CompleteEdgeRight)) {
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

        FactionPlot factionPlot = new FactionPlot(factionID, edgeDownFrontLeft, edgeUpBackRight, home, false, position);
        factionPlots.add(factionPlot);
        factionPlotTable.createFactionPlot(factionID, edgeDownFrontLeft, edgeUpBackRight, home, false, position);
    }
}
