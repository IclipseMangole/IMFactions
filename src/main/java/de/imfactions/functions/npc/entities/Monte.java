package de.imfactions.functions.npc.entities;

import de.imfactions.IMFactions;
import de.imfactions.functions.npc.NPC;
import de.imfactions.functions.texture.TextureFetcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Monte extends NPC {
    private IMFactions factions;
    private Location lotteryLocation;
    private BukkitTask task;

    public Monte(IMFactions factions, Location location) {
        super("§aLottery", factions.getData().getTextureUtil().getSkin("http://75.119.142.165/skins/Montanablack88.png"), location, true, false);
        this.factions = factions;
        lotteryLocation = location;
        show();
        enableRotation();
    }

    @Override
    public void onInteract(Player player) {

    }
}
