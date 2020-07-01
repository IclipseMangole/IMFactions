package de.imfactions.functions.plots;

import de.imfactions.IMFactions;
import de.imfactions.database.faction.FactionManager;
import de.imfactions.database.faction.FactionPlotManager;
import de.imfactions.database.faction.FactionUserManager;
import de.imfactions.util.UUIDFetcher;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FactionPlot {

    FactionPlotManager factionPlotManager;
    FactionUserManager factionUserManager;

    public FactionPlot() {
        this.factionPlotManager = IMFactions.getInstance().getData().getFactionPlotManager();
        this.factionUserManager = IMFactions.getInstance().getData().getFactionUserManager();
    }

    public boolean isPlayerInPlot(FactionPlotManager.FactionPlot factionPlot, Player player){
        for(int x = (int) factionPlot.getEdgeDownFrontLeft().getX(); x < factionPlot.getEdgeUpBackRight().getX(); x++){
            for(int y = (int) factionPlot.getEdgeDownFrontLeft().getY(); y < factionPlot.getEdgeUpBackRight().getY(); y++){
                for(int z = (int) factionPlot.getEdgeDownFrontLeft().getZ(); z < factionPlot.getEdgeUpBackRight().getZ(); z++){
                    if(player.getLocation().equals(new Location(factionPlot.getEdgeDownFrontLeft().getWorld(), x, y, z))){
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
