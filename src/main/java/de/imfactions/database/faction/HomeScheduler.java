package de.imfactions.database.faction;

import de.imfactions.IMFactions;
import org.apache.logging.log4j.core.appender.rolling.action.IfAll;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class HomeScheduler {

    private IMFactions imFactions;
    private FactionPlotManager factionPlotManager;
    private FactionUserManager factionUserManager;
    private FactionManager factionManager;
    private HashMap<Player, Integer> countdowns;
    private HashMap<Player, Location> locations;

    public HomeScheduler(IMFactions imFactions) {
        this.imFactions = imFactions;
        factionManager = imFactions.getData().getFactionManager();
        factionPlotManager = imFactions.getData().getFactionPlotManager();
        factionUserManager = imFactions.getData().getFactionUserManager();
        countdowns = new HashMap<>();
        locations = new HashMap<>();

        Bukkit.getScheduler().runTaskTimerAsynchronously(imFactions, new Runnable() {
            @Override
            public void run() {
                countdowns.forEach((player, integer) -> {
                    //teleport
                    if(integer == 0){
                        Location home = locations.get(player);
                        player.teleport(home);
                        player.sendMessage("§aYou are now at home");

                        countdowns.remove(player);
                        locations.remove(player);
                    }else{
                        //countdown
                        player.sendMessage("§aYou will get teleported in " + integer + "seconds");
                        integer--;
                        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20, 1));
                    }
                });
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
