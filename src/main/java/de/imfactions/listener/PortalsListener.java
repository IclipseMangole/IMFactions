package de.imfactions.listener;

import de.imfactions.IMFactions;
import de.imfactions.util.ScoreboardSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scoreboard.Scoreboard;


public class PortalsListener implements Listener {

    private IMFactions imFactions;

    public PortalsListener(IMFactions imFactions) {
        this.imFactions = imFactions;
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        String world = player.getWorld().getName();
        Location playerLocation = event.getFrom();
        PlayerTeleportEvent.TeleportCause teleportCause = event.getCause();

        if (teleportCause.equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {

            //von Lobby zu PVP

            if (world.equals("world")) {

                Location leftPortal0 = new Location(Bukkit.getWorld(world), 10, 30, 67);
                Location leftPortal1 = new Location(Bukkit.getWorld(world), 1, 39, 67);
                Location rightPortal0 = new Location(Bukkit.getWorld(world), -1, 30, 67);
                Location rightPortal1 = new Location(Bukkit.getWorld(world), -10, 39, 67);

                //linkes Portal

                if (isPlayerInsideCube(playerLocation, leftPortal0, leftPortal1)) {
                    Location location = new Location(Bukkit.getWorld("FactionPVP_world"), 53, 79, 1276.5, playerLocation.getYaw()-180, playerLocation.getPitch());
                    player.teleport(location);

                    //rechtes Portal

                } else if (isPlayerInsideCube(playerLocation, rightPortal0, rightPortal1)) {
                    Location location = new Location(Bukkit.getWorld("FactionPVP_world"), 74, 79, 1276.5, playerLocation.getYaw()-180, playerLocation.getPitch());
                    player.teleport(location);
                }

                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1 ,1);

                player.sendTitle("§l§4PVP-ZONE", "", 10, 100, 10);

                //PVP zu Lobby

            } else if (world.equals("FactionPVP_world")) {

                Location leftPortal0 = new Location(Bukkit.getWorld(world), 78, 79, 1277);
                Location leftPortal1 = new Location(Bukkit.getWorld(world), 69, 88, 1277);
                Location rightPortal0 = new Location(Bukkit.getWorld(world), 57, 79, 1277);
                Location rightPortal1 = new Location(Bukkit.getWorld(world), 48, 88, 1277);

                //linkes Portal

                if (isPlayerInsideCube(playerLocation, leftPortal0, leftPortal1)) {
                    Location location = new Location(Bukkit.getWorld("world"), -5, 30, 66.5, playerLocation.getYaw()-180, playerLocation.getPitch());
                    player.teleport(location);

                    //rechtes Portal

                } else if (isPlayerInsideCube(playerLocation, rightPortal0, rightPortal1)) {
                    Location location = new Location(Bukkit.getWorld("world"), 6, 30, 66.5, playerLocation.getYaw()-180, playerLocation.getPitch());
                    player.teleport(location);
                }

            }
        }
    }

    public boolean isPlayerInsideCube(Location playerLocation, Location edgeDownFrontLeft, Location edgeUpBackRight) {
        int x = (int) playerLocation.getX();
        int y = (int) playerLocation.getY();
        int z = (int) playerLocation.getZ();

        int x1 = (int) edgeDownFrontLeft.getX();
        int y1 = (int) edgeDownFrontLeft.getY();
        int z1 = (int) edgeDownFrontLeft.getZ();

        int x2 = (int) edgeUpBackRight.getX();
        int y2 = (int) edgeUpBackRight.getY();
        int z2 = (int) edgeUpBackRight.getZ();


        for (int i = Math.min(x1, x2); i <= Math.max(x1, x2); i++) {
            for (int i1 = Math.min(y1, y2); i1 <= Math.max(y1, y2); i1++) {
                for (int i2 = Math.min(z1, z2); i2 <= Math.max(z1, z2); i2++) {
                    if (x == i && y == i1 && z == i2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
