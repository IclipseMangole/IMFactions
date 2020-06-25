package de.imfactions.database.faction;

import com.mysql.fabric.xmlrpc.base.Array;
import de.imfactions.IMFactions;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.imfactions.util.LocationBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class FactionPlotManager {

    private ArrayList<FactionPlot> factionPlots;
    private HashMap<FactionPlot, Integer> plotPositions;

    public FactionPlotManager() {
        IMFactions.getInstance().getData().getMySQL().update("CREATE TABLE IF NOT EXISTS `factionPlots` (`factionId` INT(10), `edgeDownFrontRight` VARCHAR(100), `edgeUpBackLeft` VARCHAR(100), `home` VARCHAR(100), `reachable` BIGINT, `position` INT(10) PRIMARY KEY(`factionId`))");
        factionPlots = new ArrayList<>();
        plotPositions = new HashMap<>();
        loadFactionPlots();
        Bukkit.getScheduler().runTaskTimerAsynchronously(IMFactions.getInstance(), new Runnable() {
            @Override
            public void run() {
                saveFactionPlots();
            }
        }, 0, 10 * 60 * 20);
    }

    public void createFactionPlot(int factionId, Location edgeDownFrontRight, Location edgeUpBackLeft, Location home, long reachable, int position) {
        if (!isFactionPlotExists(factionId)) {
            IMFactions.getInstance().getData().getMySQL().update("INSERT INTO `factionPlots` (`factionId`, `edgeDownFrontRight`, `edgeUpBackLeft`, `home`, `reachable`, `position`) VALUES ('" + factionId + "', '" + edgeDownFrontRight + "', '" + edgeUpBackLeft + "', '" + home + "', '" + reachable + "', '" + position + "')");
            new FactionPlot(factionId, edgeDownFrontRight, edgeUpBackLeft, home, reachable, position);
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

    public boolean isFactionPlotExists(int factionId) {
        for (FactionPlot factionPlot : factionPlots) {
            if (factionPlot.getFactionId() == factionId) {
                return true;
            }
        }
        return false;
    }

    public void loadFactionPlots() {
        try {
            ResultSet rs = IMFactions.getInstance().getData().getMySQL().querry("SELECT `factionId`, `edgeDownFrontRight`, `edgeUpBackLeft`, `home`, `reachable`, `position` FROM `factionPlots` WHERE 1");
            while (rs.next()) {
                new FactionPlot(rs.getInt("factionId"), LocationBuilder.fromString(rs.getString("edgeDownFrontRight")), LocationBuilder.fromString(rs.getString("edgeUpBackLeft")), LocationBuilder.fromString(rs.getString("home")), rs.getLong("reachable"), rs.getInt("position"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveFactionPlots() {
        for (FactionPlot factionPlot : factionPlots) {
            factionPlot.save();
            plotPositions.put(factionPlot, factionPlot.getPosition());
        }
    }

    public ArrayList<FactionPlot> getFactionPlots() {
        ArrayList<FactionPlot> factionPlots = new ArrayList<>();
        try {
            ResultSet rs = IMFactions.getInstance().getData().getMySQL().querry("SELECT `factionId`, `edgeDownFrontRight`, `edgeUpBackLeft`, `home`, `reachable`, `position` FROM `factionPlots` WHERE 1");
            while (rs.next()) {
                FactionPlot factionPlot = new FactionPlot(rs.getInt("factionId"), LocationBuilder.fromString(rs.getString("edgeDownFrontRight")), LocationBuilder.fromString(rs.getString("edgeUpBackLeft")), LocationBuilder.fromString(rs.getString("home")), rs.getLong("reachable"), rs.getInt("position"));
                factionPlots.add(factionPlot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factionPlots;
    }

    public int getNextPosition(){
        for(int i = 0; i < getHighestFactionPosition() + 1; i++){
            if(!plotPositions.containsValue(i)){
                return i;
            }
        }
        return plotPositions.size();
    }

    public Location getLocationFromPosition(int position, String LocationType){
        if(LocationType.equalsIgnoreCase("edgeDownFrontRight")){
            return new Location(Bukkit.getWorld("FactionPlots_world"), 700 * (position%10), 0 , 700 * (position/10));
        }else if(LocationType.equalsIgnoreCase("edgeUpBackLeft")){
            return new Location(Bukkit.getWorld("FactionPlots_world"), 100 + 700 * (position%10), 200 , 100 + 700 * (position/10));
        }
        return new Location(Bukkit.getWorld("FactionPlots_world"), -1000 , 0, -1000);
    }

    public int getHighestFactionPlotId() {
        try {
            ResultSet rs = IMFactions.getInstance().getData().getMySQL().querry("SELECT MAX(`factionId`) AS `factionId` FROM `factionPlots` WHERE 1");
            if (rs.next()) {
                return rs.getInt("factionId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getHighestFactionPosition() {
        try {
            ResultSet rs = IMFactions.getInstance().getData().getMySQL().querry("SELECT MAX(`position`) AS `position` FROM `factionPlots` WHERE 1");
            if (rs.next()) {
                return rs.getInt("position");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public class FactionPlot {

        int factionId;
        //Look from the front
        Location edgeDownFrontRight;
        Location edgeUpBackLeft;
        Location home;
        long reachable;
        int position;

        public FactionPlot(int factionId, Location edgeDownFrontRight, Location edgeUpBackLeft, Location home, long reachable, int position) {
            this.factionId = factionId;
            this.edgeDownFrontRight = edgeDownFrontRight;
            this.edgeUpBackLeft = edgeUpBackLeft;
            this.home = home;
            this.reachable = reachable;
            this.position = position;
            factionPlots.add(this);
            plotPositions.put(this, position);
        }

        public int getFactionId() {
            return factionId;
        }

        public void setFactionId(int factionId) {
            this.factionId = factionId;
        }

        public long getReachable() {
            return reachable;
        }

        public void setReachable(long reachable) {
            this.reachable = reachable;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public Location getHome() {
            return home;
        }

        public void setHome(Location home) {
            this.home = home;
        }

        public Location getEdgeDownFrontRight() {
            return edgeDownFrontRight;
        }

        public Location getEdgeUpBackLeft() {
            return edgeUpBackLeft;
        }

        public void setEdgeDownFrontRight(Location edgeDownFrontRight) {
            this.edgeDownFrontRight = edgeDownFrontRight;
        }

        public void setEdgeUpBackLeft(Location edgeUpBackLeft) {
            this.edgeUpBackLeft = edgeUpBackLeft;
        }

        public void save() {
            IMFactions.getInstance().getData().getMySQL().update("UPDATE `factionPlots` SET `edgeDownFrontRight` = '" + LocationBuilder.toString(edgeDownFrontRight) + "', `edgeUpBackLeft` = '" + LocationBuilder.toString(edgeUpBackLeft) + "', `home` = '" + LocationBuilder.toString(home) + "', `reachable` = '" + reachable + "', `position` = '" + position + "' WHERE `factionId` = '" + factionId + "'");
        }

        public void deleteFactionPlot() {
            IMFactions.getInstance().getData().getMySQL().update("DELETE FROM `factionPlots` WHERE `factionId` = '" + factionId + "'");
            factionPlots.remove(this);
            plotPositions.remove(this);
        }


    }
}
