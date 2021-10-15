package de.imfactions.functions.npc.entities;

import de.imfactions.IMFactions;
import de.imfactions.functions.npc.NPC;
import de.imfactions.functions.texture.TextureFetcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import static de.imfactions.util.RainbowColor.rainbowColor;

public class Monte extends NPC {
    private IMFactions factions;
    private Location lotteryLocation;
    private BukkitTask task;

    public Monte(IMFactions factions, Location location) {
        super(factions, "§aLottery", factions.getData().getTextureUtil().getSkin("http://75.119.142.165/skins/Montanablack88.png"), location, true, false);
        this.factions = factions;
        lotteryLocation = location;
        show();
        enableRotation();
        startNameScheduler();
    }

    private void startNameScheduler(){
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(factions, new Runnable() {
            @Override
            public void run() {
                setDisplayName(rainbowColor(15) + "Lottery");
            }
        }, 1, 1);
    }

    @Override
    public void onInteract(Player player) {
        player.sendMessage("Verpiss dich du Hurensohn!");
    }
}
