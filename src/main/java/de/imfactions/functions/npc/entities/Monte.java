package de.imfactions.functions.npc.entities;

import de.imfactions.IMFactions;
import de.imfactions.functions.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import static de.imfactions.util.ColorUtils.rainbowColor;

public class Monte extends NPC {
    private IMFactions factions;
    private Location lotteryLocation;
    private BukkitTask task;

    public Monte(IMFactions factions, Location location) {
        super(factions, "Lottery", ChatColor.DARK_GREEN + "Montes ", ChatColor.DARK_GREEN, factions.getData().getTextureUtil().getSkin("http://75.119.142.165/skins/Montanablack88.png"), location, true, false);
        this.factions = factions;
        lotteryLocation = location;
        show();
        enableRotation();
    }


    @Override
    public void onInteract(Player player, boolean sneaking) {
        player.sendMessage("Verpiss dich du Hurensohn!");
    }
}
