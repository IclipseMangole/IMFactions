package de.imfactions.functions;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.factionPlot.FactionPlotUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public class Scheduler {

    private IMFactions imFactions;
    private FactionPlotUtil factionPlotUtil;
    private HashMap<Player, Integer> countdowns;
    private HashMap<Player, Location> locations;
    private HashMap<Player, Location> startingRaids;
    private ArrayList<Integer> loadingFactionPlotsTime;
    private Data data;

    private BukkitTask teleportScheduler;

    public Scheduler(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionPlotUtil = data.getFactionPlotUtil();
        countdowns = new HashMap<>();
        locations = new HashMap<>();
        startingRaids = new HashMap<>();
        loadingFactionPlotsTime = new ArrayList<>();

        startTeleportScheduler();
    }

    private void startTeleportScheduler() {

        teleportScheduler = Bukkit.getScheduler().runTaskTimerAsynchronously(imFactions, new Runnable() {
            @Override
            public void run() {
                /**
                 * Countdown if FactionPlot is reachable after founding a Faction
                 */
                if (!loadingFactionPlotsTime.isEmpty()) {
                    loadingFactionPlotsTime.forEach(integer -> {
                        if (integer == 0) {
                            loadingFactionPlotsTime.remove(integer);
                            factionPlotUtil.addLoadingFactionPlots(-1);
                        } else {
                            loadingFactionPlotsTime.set(loadingFactionPlotsTime.indexOf(integer), integer - 1);
                        }
                    });
                }
            }
        }, 0, 20);
    }

    public void stopSchedulers() {
        teleportScheduler.cancel();
    }

    public HashMap<Player, Integer> getCountdowns() {
        return countdowns;
    }

    public HashMap<Player, Location> getLocations() {
        return locations;
    }

    public HashMap<Player, Location> getRaids() {
        return startingRaids;
    }
}
