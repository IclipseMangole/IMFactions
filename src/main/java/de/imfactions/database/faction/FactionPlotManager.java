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

    public FactionPlotManager(IMFactions factions) {
        this.factions = factions;
        factions.getData().getMySQL().update("CREATE TABLE IF NOT EXISTS factionPlots (`factionId` INT(10), `edgeDownFrontLeft` VARCHAR(100), `edgeUpBackRight` VARCHAR(100), `home` VARCHAR(100), `reachable` BIGINT, `position` INT(10), PRIMARY KEY(`factionId`))");
        factionPlots = new ArrayList<>();
        loadFactionPlots();
        Bukkit.getScheduler().runTaskTimerAsynchronously(factions, new Runnable() {
            @Override
            public void run() {
                saveFactionPlots();
            }
        }, 0, 10 * 60 * 20);
    }

    public void createFactionPlot(int factionId, Location edgeDownFrontRight, Location edgeUpBackLeft, Location home, long reachable, int position) {
        if (!isFactionPlotExists(factionId)) {
            factions.getData().getMySQL().update("INSERT INTO factionPlots (`factionId`, `edgeDownFrontLeft`, `edgeUpBackRight`, `home`, `reachable`, `position`) VALUES ('" + factionId + "', '" + edgeDownFrontRight + "', '" + edgeUpBackLeft + "', '" + home + "', '" + reachable + "', '" + position + "')");
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
        ArrayList<FactionPlot> factionPlots = new ArrayList<>();
        try {
            ResultSet rs = factions.getData().getMySQL().querry("SELECT `factionId`, `edgeDownFrontLeft`, `edgeUpBackRight`, `home`, `reachable`, `position` FROM factionPlots WHERE 1");
            while (rs.next()) {
                //    FactionPlot factionPlot = new FactionPlot();
                //   factionPlots.add(factionPlot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
