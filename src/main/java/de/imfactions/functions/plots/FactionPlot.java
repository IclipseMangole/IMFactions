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
        for(int x = (int) factionPlot.getEdgeDownFrontRight().getX(); x < factionPlot.getEdgeUpBackLeft().getX(); x++){
            for(int y = (int) factionPlot.getEdgeDownFrontRight().getY(); y < factionPlot.getEdgeUpBackLeft().getY(); y++){
                for(int z = (int) factionPlot.getEdgeDownFrontRight().getZ(); z < factionPlot.getEdgeUpBackLeft().getZ(); z++){
                    if(player.getLocation().equals(new Location(factionPlot.getEdgeDownFrontRight().getWorld(), x, y, z))){
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
