package de.imfactions.database.faction;

import de.imfactions.IMFactions;
import org.apache.logging.log4j.core.appender.rolling.action.IfAll;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public class Scheduler {

    private IMFactions imFactions;
    private FactionPlotManager factionPlotManager;
    private FactionUserManager factionUserManager;
    private FactionManager factionManager;
    private HashMap<Player, Integer> countdowns;
    private HashMap<Player, Location> locations;
    private ArrayList<Integer> loadingFactionPlotsTime;

    public Scheduler(IMFactions imFactions) {
        this.imFactions = imFactions;
        factionManager = imFactions.getData().getFactionManager();
        factionPlotManager = imFactions.getData().getFactionPlotManager();
        factionUserManager = imFactions.getData().getFactionUserManager();
        countdowns = new HashMap<>();
        locations = new HashMap<>();
        loadingFactionPlotsTime = new ArrayList<>();

        Bukkit.getScheduler().runTaskTimerAsynchronously(imFactions, new Runnable() {
            @Override
            public void run() {
                if(!countdowns.isEmpty()) {
                    countdowns.forEach((player, integer) -> {
                        //teleport
                        if (integer == 0) {
                            Location location = locations.get(player);
                            Bukkit.getScheduler().runTask(imFactions, new Runnable() {
                                @Override
                                public void run() {
                                    player.teleport(location);
                                    player.removePotionEffect(PotionEffectType.CONFUSION);
                                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                                }
                            });

                            countdowns.remove(player);
                            locations.remove(player);
                        } else {
                            //countdown
                            player.sendTitle("§a§l" + integer, "", 2, 16, 2);
                            Bukkit.getScheduler().runTask(imFactions, new Runnable() {
                                @Override
                                public void run() {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100000, 0));
                                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                                }
                            });

                            countdowns.replace(player, integer, integer - 1);
                        }
                    });
                }

                if(!loadingFactionPlotsTime.isEmpty()) {
                    loadingFactionPlotsTime.forEach(integer -> {
                        if (integer == 0) {
                            loadingFactionPlotsTime.remove(integer);
                            factionPlotManager.addLoadingFactionPlots(-1);
                        } else {
                            loadingFactionPlotsTime.set(loadingFactionPlotsTime.indexOf(integer), integer - 1);
                        }
                    });
                }
            }
        }, 0, 20);
    }

    public HashMap<Player, Integer> getCountdowns() {
        return countdowns;
    }

    public HashMap<Player, Location> getLocations() {
        return locations;
    }
}
