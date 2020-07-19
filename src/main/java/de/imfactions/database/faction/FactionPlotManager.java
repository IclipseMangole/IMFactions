package de.imfactions.database.faction;

import de.imfactions.IMFactions;
import de.imfactions.util.LocationBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FactionPlotManager {
    private ArrayList<FactionPlot> factionPlots;
    private IMFactions factions;
    private int loadingFactionPlots;

    public FactionPlotManager(IMFactions factions) {
        this.factions = factions;
        loadingFactionPlots = 0;
        factions.getData().getMySQL().update("CREATE TABLE IF NOT EXISTS `factionPlots` (`factionId` INT(10), `edgeDownFrontLeft` VARCHAR(100), `edgeUpBackRight` VARCHAR(100), `home` VARCHAR(100), `reachable` BIGINT, `position` INT(10), PRIMARY KEY(`factionId`))");
        factionPlots = new ArrayList<>();
        loadFactionPlots();
        Bukkit.getScheduler().runTaskTimerAsynchronously(factions, new Runnable() {
            @Override
            public void run() {
                saveFactionPlots();
            }
        }, 0, 10 * 60 * 20);
    }

    public void createFactionPlot(int factionId, Location edgeDownFrontLeft, Location edgeUpBackRight, Location home, long reachable, int position) {
        if (!isFactionPlotExists(factionId)) {
            factions.getData().getMySQL().update("INSERT INTO factionPlots (`factionId`, `edgeDownFrontLeft`, `edgeUpBackRight`, `home`, `reachable`, `position`) VALUES ('" + factionId + "', '" + LocationBuilder.toString(edgeDownFrontLeft) + "', '" + LocationBuilder.toString(edgeUpBackRight) + "', '" + LocationBuilder.toString(home) + "', '" + reachable + "', '" + position + "')");
            factionPlots.add(new FactionPlot(factionId, edgeDownFrontLeft, edgeUpBackRight, home, reachable, position));
        }
    }

    public FactionPlot getFactionPlot(int factionId) {
        for (FactionPlot factionPlot : factionPlots) {
            if (factionPlot.getFactionId() == factionId) {
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

    public boolean isFactionPlotExists(int factionId) {
        for (FactionPlot factionPlot : factionPlots) {
            if (factionPlot.getFactionId() == factionId) {
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
        return new Location(Bukkit.getWorld("FactionPlots_world"), edgeDownFrontLeft.getX() + 99, edgeDownFrontLeft.getY() + 149, edgeDownFrontLeft.getZ() + 99);
    }

    public void loadFactionPlots() {
        try {
            ResultSet rs = factions.getData().getMySQL().querry("SELECT `factionId`, `edgeDownFrontLeft`, `edgeUpBackRight`, `home`, `reachable`, `position` FROM factionPlots WHERE 1");
            while (rs.next()) {
                FactionPlot factionPlot = new FactionPlot(rs.getInt("factionId"), LocationBuilder.fromString(rs.getString("edgeDownFrontLeft")), LocationBuilder.fromString(rs.getString("edgeUpBackRight")), LocationBuilder.fromString(rs.getString("home")), rs.getLong("reachable"), rs.getInt("position"));
                factionPlots.add(factionPlot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveFactionPlots() {
        for (FactionPlot factionPlot : factionPlots) {
            factionPlot.save();
        }
    }

    public ArrayList<FactionPlot> getFactionPlots() {
        return factionPlots;
    }

    public class FactionPlot {

        int factionId;
        Location edgeDownFrontLeft;
        Location edgeUpBackRight;
        Location home;
        long reachable;
        int position;

        public FactionPlot(int factionId, Location edgeDownFrontLeft, Location edgeUpBackRight, Location home, long reachable, int position) {
            this.factionId = factionId;
            this.edgeDownFrontLeft = edgeDownFrontLeft;
            this.edgeUpBackRight = edgeUpBackRight;
            this.home = home;
            this.reachable = reachable;
            this.position = position;
        }

        public int getFactionId() {
            return factionId;
        }

        public void setFactionId(int factionId) {
            this.factionId = factionId;
        }

        public Location getEdgeDownFrontLeft() {
            return edgeDownFrontLeft;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        public void setReachable(long reachable) {
            this.reachable = reachable;
        }

        public long getReachable() {
            return reachable;
        }

        public void setEdgeDownFrontLeft(Location edgeDownFrontLeft) {
            this.edgeDownFrontLeft = edgeDownFrontLeft;
        }

        public Location getEdgeUpBackRight() {
            return edgeUpBackRight;
        }

        public void setEdgeUpBackRight(Location edgeUpBackRight) {
            this.edgeUpBackRight = edgeUpBackRight;
        }

        public Location getHome() {
            return home;
        }

        public void setHome(Location home) {
            this.home = home;
        }

        public void save() {
            factions.getData().getMySQL().update("UPDATE factionPlots SET `edgeDownFrontLeft` = '" + LocationBuilder.toString(edgeDownFrontLeft) + "', `edgeUpBackRight` = '" + LocationBuilder.toString(edgeUpBackRight) + "', `home` = '" + LocationBuilder.toString(home) + "', `reachable` = '" + reachable + "', `position` = '" + position + "' WHERE `factionId` = '" + factionId + "'");
        }

        public void deleteFactionPlot() {
            factions.getData().getMySQL().update("DELETE FROM factionPlots WHERE `factionId` = '" + factionId + "'");
            factionPlots.remove(this);
        }


    }
}
