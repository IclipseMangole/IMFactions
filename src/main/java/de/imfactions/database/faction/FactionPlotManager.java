package de.imfactions.database.faction;

import de.imfactions.IMFactions;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FactionPlotManager {

    private ArrayList<FactionPlot> factionPlots;

    public FactionPlotManager() {
        IMFactions.getInstance().getData().getMySQL().update("CREATE TABLE IF NOT EXISTS factionPlots (factionId INT(10), , PRIMARY KEY(factionId))");
        factionPlots = new ArrayList<>();
        loadFactionPlots();
        Bukkit.getScheduler().runTaskTimerAsynchronously(IMFactions.getInstance(), new Runnable() {
            @Override
            public void run() {
                saveFactionPlots();
            }
        }, 0, 10 * 60 * 20);
    }

    public void createFactionPlot(int factionId, Location edgeDownFrontLeft, Location edgeUpBackRight, Location home) {
        if (!isFactionPlotExists(factionId)) {
            IMFactions.getInstance().getData().getMySQL().update("INSERT INTO factions (factionId, ) VALUES ()");
        }
    }

    public void createFactionPlot(int factionId) {
      //  new FactionPlot(factionId);
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
            ResultSet rs = IMFactions.getInstance().getData().getMySQL().querry("SELECT factionId, userAmount, name, foundingDate, raidProtection FROM `factions` WHERE 1");
            while (rs.next()) {
             //   FactionPlot factionPlot = new FactionPlot(rs.getInt("factionId"), rs.getString("name"), rs.getInt("userAmount"), rs.getDate("foundingDate"), rs.getLong("raidProtection"));
              //  factionPlots.add(factionPlot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveFactionPlots() {
        for (FactionPlot factionPlot : factionPlots) {
           // factionPlot.save();
        }
    }

    public ArrayList<FactionPlot> getFactionPlots() {
        ArrayList<FactionPlot> factionPlots = new ArrayList<>();
        try {
            ResultSet rs = IMFactions.getInstance().getData().getMySQL().querry("SELECT factionId, userAmount, name, foundingDate, raidProtection FROM `factions` WHERE 1");
            while (rs.next()) {
            //    FactionPlot factionPlot = new FactionPlot();
             //   factionPlots.add(factionPlot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factionPlots;
    }

    public class FactionPlot{

        int factionId;
        Location edgeDownFrontLeft;
        Location edgeUpBackRight;
        Location home;

        public FactionPlot(int factionId, Location edgeDownFrontLeft, Location edgeUpBackRight, Location home) {
            this.factionId = factionId;
            this.edgeDownFrontLeft = edgeDownFrontLeft;
            this.edgeUpBackRight = edgeUpBackRight;
            this.home = home;
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

       /* public void save() {
            IMFactions.getInstance().getData().getMySQL().update("UPDATE factions SET  = "  " WHERE factionId = '" + factionId + "'");
        }

        public void deleteFactionPlot() {
            IMFactions.getInstance().getData().getMySQL().update("DELETE FROM factions WHERE factionId = '" + factionId + "'");
            factionPlots.remove(this);
        }

        */
    }
}
