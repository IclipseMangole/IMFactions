package de.imfactions.functions.factionPlot;

import de.imfactions.util.LocationBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class FactionPlot {

    int factionID;
    Location edgeDownFrontLeft;
    Location edgeUpBackRight;
    Location home;
    boolean loading;
    int position;

    public FactionPlot(int factionID, Location edgeDownFrontLeft, Location edgeUpBackRight, Location home, boolean loading, int position) {
        this.factionID = factionID;
        this.edgeDownFrontLeft = edgeDownFrontLeft;
        this.edgeUpBackRight = edgeUpBackRight;
        this.home = home;
        this.loading = loading;
        this.position = position;
    }

    public Location getRaidSpawn(){
        return new Location(Bukkit.getWorld("FactionPlots_world"), edgeDownFrontLeft.getX() - 16, 17, edgeDownFrontLeft.getZ() - 16);
    }


    public int getFactionID() {
        return factionID;
    }

    public void setFactionID(int factionID) {
        this.factionID = factionID;
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

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isLoading() {
        return loading;
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
}
