package de.imfactions.functions;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.database.faction.FactionManager;
import de.imfactions.database.faction.FactionPlotManager;
import de.imfactions.database.faction.FactionUserManager;
import de.imfactions.database.faction.RaidManager;
import de.imfactions.util.UUIDFetcher;
import org.apache.logging.log4j.core.appender.rolling.action.IfAll;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Scheduler {

    private IMFactions imFactions;
    private FactionPlotManager factionPlotManager;
    private FactionUserManager factionUserManager;
    private FactionManager factionManager;
    private RaidManager raidManager;
    private HashMap<Player, Integer> countdowns;
    private HashMap<Player, Location> locations;
    private HashMap<Player, Location> startingRaids;
    private ArrayList<Integer> loadingFactionPlotsTime;
    private Data data;

    private BukkitTask teleportScheduler;

    public Scheduler(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionManager = data.getFactionManager();
        factionPlotManager = data.getFactionPlotManager();
        factionUserManager = data.getFactionUserManager();
        raidManager = data.getRaidManager();
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
                if (!countdowns.isEmpty()) {
                    countdowns.forEach((player, integer) -> {

                        /**
                         * Teleports for Spawn and Home
                         */

                        if(locations.containsKey(player)) {
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
                                player.sendTitle("§a§l" + integer, "", 2, 16, 2);
                                Bukkit.getScheduler().runTask(imFactions, new Runnable() {
                                    @Override
                                    public void run() {
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100000, 0));
                                        if(integer <= 5) {
                                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                                        }
                                    }
                                });

                                countdowns.replace(player, integer, integer - 1);
                            }

                            /**
                             * Teleports for Raids
                             */

                        }else if(startingRaids.containsKey(player)){
                            if (integer == 0) {
                                Location location = startingRaids.get(player);
                                Bukkit.getScheduler().runTask(imFactions, new Runnable() {
                                    @Override
                                    public void run() {
                                        player.teleport(location);
                                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

                                        /**
                                         * starting Raid is now a scouting raid
                                         */

                                        //raid
                                        UUID uuid = UUIDFetcher.getUUID(player);
                                        FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(uuid);
                                        int factionID = factionUser.getFactionID();
                                        int raidID = raidManager.getActiveRaidID(factionID);
                                        if(!raidManager.getRaid(raidID).getRaidState().equals("scouting")) {
                                            raidManager.getRaid(raidID).setRaidState("scouting");
                                        }
                                        //countdown for automatic beginning
                                        countdowns.remove(player);
                                        countdowns.put(player, 60);
                                    }
                                });

                                startingRaids.remove(player);
                            } else {
                                player.sendTitle("§4§l" + integer, "", 2, 16, 2);
                                Bukkit.getScheduler().runTask(imFactions, new Runnable() {
                                    @Override
                                    public void run() {
                                        if(integer <= 10) {
                                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                                        }
                                    }
                                });

                                countdowns.replace(player, integer, integer - 1);
                            }

                            /**
                             * Countdown for automatic Beginning while scouting
                             */

                        }else{
                            if(integer == 0){

                                /**
                                 * scouting Raid is now a active Raid
                                 */

                                //raid
                                UUID uuid = UUIDFetcher.getUUID(player);
                                FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(uuid);
                                int factionID = factionUser.getFactionID();
                                int raidID = raidManager.getActiveRaidID(factionID);
                                RaidManager.Raid raid = raidManager.getRaid(raidID);
                                if(!raid.getRaidState().equals("active")) {
                                    raid.setRaidState("active");
                                }
                                Bukkit.getScheduler().runTask(imFactions, new Runnable() {
                                    @Override
                                    public void run() {
                                        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
                                        player.sendTitle("§4§lRaid has begun", "", 2, 20 , 2);
                                    }
                                });
                                countdowns.remove(player);
                            }else{
                                if(integer == 60 || integer == 30 || integer == 10 || integer <= 5){
                                    Bukkit.getScheduler().runTask(imFactions, new Runnable() {
                                        @Override
                                        public void run() {
                                           player.sendMessage("§cThe time limit for scouting will be over in " + integer + " seconds and the Raid will start automatically");
                                        }
                                    });
                                }
                                countdowns.replace(player, integer, integer - 1);
                            }
                        }
                    });

                }

                /**
                 * Countdown if FactionPlot is reachable after founding a Faction
                 */

                if (!loadingFactionPlotsTime.isEmpty()) {
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

    public void stopSchedulers() {
        teleportScheduler.cancel();
    }

    public HashMap<Player, Integer> getCountdowns() {
        return countdowns;
    }

    public HashMap<Player, Location> getLocations() {
        return locations;
    }

    public HashMap<Player, Location> getRaids(){
        return startingRaids;
    }
}
