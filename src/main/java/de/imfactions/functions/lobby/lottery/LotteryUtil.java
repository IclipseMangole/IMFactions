package de.imfactions.functions.lobby.lottery;

import de.imfactions.IMFactions;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LotteryUtil {
    private IMFactions factions;
    private Monte monte;

    public LotteryUtil(IMFactions factions) {
        this.factions = factions;
        this.monte = new Monte(factions, new Location(Bukkit.getWorld("world"), 34.5, 32, 16.5, 90, 0));
    }
}
