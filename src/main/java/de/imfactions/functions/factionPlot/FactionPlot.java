package de.imfactions.functions.factionPlot;

import de.imfactions.util.LocationBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class FactionPlot {

    int factionID;
    Location edgeDownFrontLeft;
    Location edgeUpBackRight;
    Location home;
    long reachable;
    int position;

    public FactionPlot(int factionID, Location edgeDownFrontLeft, Location edgeUpBackRight, Location home, long reachable, int position) {
        this.factionID = factionID;
        this.edgeDownFrontLeft = edgeDownFrontLeft;
        this.edgeUpBackRight = edgeUpBackRight;
        this.home = home;
        this.reachable = reachable;
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
        factions.getData().getMySQL().update("UPDATE factionPlots SET `edgeDownFrontLeft` = '" + LocationBuilder.toString(edgeDownFrontLeft) + "', `edgeUpBackRight` = '" + LocationBuilder.toString(edgeUpBackRight) + "', `home` = '" + LocationBuilder.toString(home) + "', `reachable` = '" + reachable + "', `position` = '" + position + "' WHERE `factionID` = '" + factionID + "'");
    }

    public void deleteFactionPlot() {
        factions.getData().getMySQL().update("DELETE FROM factionPlots WHERE `factionID` = '" + factionID + "'");
        factionPlots.remove(this);
    }


}
