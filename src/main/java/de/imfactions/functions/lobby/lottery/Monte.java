package de.imfactions.functions.lobby.lottery;

import de.imfactions.IMFactions;
import de.imfactions.functions.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

import static de.imfactions.util.ColorUtils.rainbowColor;

public class Monte extends NPC implements Listener {
    private IMFactions factions;
    private Location lotteryLocation;
    private BukkitTask task;
    private double lastSound = 0;

    public Monte(IMFactions factions, Location location) {
        super(factions, "Lottery", ChatColor.DARK_GREEN + "Montes ", ChatColor.DARK_GREEN, factions.getData().getTextureUtil().getSkin("http://75.119.142.165/skins/Montanablack88.png"), location, true, false);
        this.factions = factions;
        lotteryLocation = location;
        show();
        enableRotation();
        Bukkit.getPluginManager().registerEvents(this, factions);
    }


    @Override
    public void onInteract(Player player, boolean sneaking) {
        player.sendMessage("Verpiss dich du Hurensohn!");
        say(player.getLocation(), "factions:monte_wegschicken");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Location location = player.getLocation();
        if(lotteryLocation.distance(location) > 10) return;

        if(player.isSneaking()){
            say(location, "factions:monte_frauenrumlaufen", 200);
        }
    }

    public void say(Location location, String sound, int possibility){
        if(new Random().nextInt(possibility) != 0) return;
        say(location, sound);
    }

    public void say(Location location, String sound){
        rotate(location);
        if(System.currentTimeMillis() - 7500 < lastSound) return;
        Bukkit.getScheduler().runTaskLater(factions, new Runnable() {
            @Override
            public void run() {
                lotteryLocation.getWorld().playSound(lotteryLocation, sound, 1.0f, 1.0f);
                lastSound = System.currentTimeMillis();
            }
        }, 5);
    }
}
