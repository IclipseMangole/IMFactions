package de.imfactions.functions.lobby.lottery;

import de.imfactions.IMFactions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;

import static de.imfactions.util.ColorUtils.rainbowColor;

public class LotteryCube implements Listener {
    private IMFactions factions;
    private Location lotteryLocation;
    private MagmaCube magmaCube;
    private BukkitTask task;

    public LotteryCube(IMFactions factions) {
        this.factions = factions;
        Bukkit.getPluginManager().registerEvents(this, factions);
        lotteryLocation = new Location(Bukkit.getWorld("world"), 34.5, 32, 16.5, 90, 0);
        spawnSlime();
        startNameScheduler();
    }

    private void spawnSlime(){
        magmaCube = (MagmaCube) lotteryLocation.getWorld().spawnEntity(lotteryLocation, EntityType.MAGMA_CUBE);
        magmaCube.setSize(4);
        magmaCube.setVisualFire(true);
        magmaCube.setAI(false);
        magmaCube.setRemoveWhenFarAway(false);
        magmaCube.setCollidable(false);
        magmaCube.setCustomName("§aLottery");
        magmaCube.setCustomNameVisible(true);
        magmaCube.setGravity(false);
    }

    private void startNameScheduler(){
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(factions, new Runnable() {
            @Override
            public void run() {
                magmaCube.setCustomName(rainbowColor(10, "Lottery", true));

                Location location = lotteryLocation.clone().add(0, getDifY(), 0);
                magmaCube.teleport(location);
                magmaCube.setRotation(getYaw(), 0);

            }
        }, 1, 1);
    }

    private int getYaw(){
        int yaw = (int) ((System.currentTimeMillis() / 200) % 360);
        if(yaw >= 180){
            yaw =yaw - 360;
        }
        return yaw;
    }


    private double getDifY(){
        double difY = ((System.currentTimeMillis() % 8000.0) / 6000.0);
        if(difY > (2.0/3.0)){
            difY = 1 - difY;
        }
        return difY;
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event){
        task.cancel();
        magmaCube.remove();
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event){
        Player player = event.getPlayer();
        if(event.getRightClicked().equals(magmaCube)){
            if(event.getHand() != EquipmentSlot.OFF_HAND) {
                player.sendMessage("MagmaCube gerechtsklickt!");
            }
        }
    }

    @EventHandler
    public void onInteract(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player player = (Player) event.getDamager();
            if(event.getEntity() == magmaCube){
                player.sendMessage("MagmaCube gelinksklickt!");
                event.setCancelled(true);
            }
        }
    }

}
