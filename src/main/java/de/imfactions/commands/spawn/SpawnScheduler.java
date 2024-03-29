package de.imfactions.commands.spawn;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class SpawnScheduler implements Listener {

    private final IMFactions imFactions;
    private final Data data;
    private final HashMap<Player, BukkitTask> activeTasks = new HashMap<>();

    public SpawnScheduler(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
    }

    public void teleportSpawn(Player player, int seconds) {
        if(!activeTasks.containsKey(player)) {
            activeTasks.put(player, Bukkit.getScheduler().runTaskTimer(imFactions, new Runnable() {
                int timer = seconds;

                @Override
                public void run() {
                    if (timer <= 0) {
                        player.teleport(data.getWorldSpawn());
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        cancelTeleport(player);
                    }
                    player.sendTitle(ChatColor.GREEN + "" + timer, "", 3, 14, 3);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, 0.5f);
                    if (!player.hasPotionEffect(PotionEffectType.CONFUSION))
                        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, timer * 20, 0));
                    timer--;
                }
            }, 0, 20));
        }else{
            player.sendMessage(ChatColor.RED + "You are already teleporting");
        }
    }

    public void cancelTeleport(Player player){
        activeTasks.get(player).cancel();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!activeTasks.containsKey(player))
            return;
        Location to = event.getTo();
        Location from = event.getFrom();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
            return;
        cancelTeleport(player);
        activeTasks.remove(player);

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(!activeTasks.containsKey(player))
            return;
        cancelTeleport(player);
        activeTasks.remove(player);
    }
}
