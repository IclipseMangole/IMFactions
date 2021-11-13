package de.imfactions.functions.pvp;

import de.imfactions.util.LocationChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

public enum PVPZone {

    SAFE,
    PEACEFULLY,
    EASY,
    MEDIUM,
    HARD,
    EPIC,
    LEGENDARY;

    public static PVPZone getZone(Location location) {
        Location safe1 = new Location(Bukkit.getWorld("FactionPVP_world"), -1, 77, 382);
        Location safe2 = new Location(Bukkit.getWorld("FactionPVP_world"), 108, 88, 361);
        Location pvp1 = new Location(Bukkit.getWorld("FactionPVP_world"), -127, 0, 383);
        Location pvp2 = new Location(Bukkit.getWorld("FactionPVP_world"), 255, 256, -383);

        if (LocationChecker.isLocationInsideSquare(location, pvp1, pvp2)) {
            if (LocationChecker.isLocationInsideCube(location, safe1, safe2))
                return SAFE;
            if (location.getBlockZ() >= 255)
                return PEACEFULLY;
            if (location.getBlockZ() >= 127)
                return EASY;
            if (location.getBlockZ() >= -1)
                return MEDIUM;
            if (location.getBlockZ() >= -129)
                return HARD;
            if (location.getBlockZ() >= -257)
                return EPIC;
            if (location.getBlockZ() >= -383)
                return LEGENDARY;
        }

        return null;
    }

    public static Location getRandomLocation(PVPZone zone) {
        Random random = new Random();
        int x = -122 + random.nextInt(377);
        int z = 0;
        World world = Bukkit.getWorld("FactionPVP_world");

        switch (zone) {
            case PEACEFULLY:
                z = 381 - random.nextInt(128);
                break;
            case EASY:
                z = 255 - random.nextInt(128);
                break;
            case MEDIUM:
                z = 127 - random.nextInt(128);
                break;
            case HARD:
                z = -1 - random.nextInt(128);
                break;
            case EPIC:
                z = -129 - random.nextInt(128);
                break;
            default:
                z = -257 - random.nextInt(123);
                break;
        }
        Block block = world.getHighestBlockAt(x, z);
        if (getSpawnLocation(block.getLocation()) != null)
            return getSpawnLocation(block.getLocation());
        return null;
    }

    public static ArrayList<PVPZone> getZonesWithPlayers() {
        ArrayList<PVPZone> pvpZones = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().getName().equalsIgnoreCase("FactionPVP_world")) {
                continue;
            }
            PVPZone pvpZone = getZone(player.getLocation());
            if (!pvpZones.contains(pvpZone))
                pvpZones.add(pvpZone);
            for (PVPZone pvpZone1 : getNearbyZones(pvpZone)) {
                if (!pvpZones.contains(pvpZone1))
                    pvpZones.add(pvpZone1);
            }
        }
        return pvpZones;
    }

    private static ArrayList<PVPZone> getNearbyZones(PVPZone pvpZone) {
        ArrayList<PVPZone> pvpZones = new ArrayList<>();
        int zone = zoneToInt(pvpZone);
        if (zone == 0) {
            pvpZones.add(PEACEFULLY);
            return pvpZones;
        }
        if (zone == 6) {
            pvpZones.add(EPIC);
            return pvpZones;
        }
        pvpZones.add(intToZone(zone + 1));
        pvpZones.add(intToZone(zone - 1));
        return pvpZones;
    }

    private static int zoneToInt(PVPZone pvpZone) {
        switch (pvpZone) {
            case SAFE:
                return 0;
            case PEACEFULLY:
                return 1;
            case EASY:
                return 2;
            case MEDIUM:
                return 3;
            case HARD:
                return 4;
            case EPIC:
                return 5;
            default:
                return 6;
        }
    }

    private static PVPZone intToZone(int pvpZone) {
        switch (pvpZone) {
            case 0:
                return SAFE;
            case 1:
                return PEACEFULLY;
            case 2:
                return EASY;
            case 3:
                return MEDIUM;
            case 4:
                return HARD;
            case 5:
                return EPIC;
            default:
                return LEGENDARY;
        }
    }

    private static Location getSpawnLocation(Location location) {
        for (int y = location.getBlockY(); y >= 0; y--) {
            Block block = location.getWorld().getBlockAt(location.getBlockX(), y, location.getBlockZ());
            if (canBlockSpawnMob(block))
                return block.getLocation().add(0, 1, 0);
        }
        return null;
    }

    private static boolean canBlockSpawnMob(Block block) {
        Location location = block.getLocation();
        World world = location.getWorld();
        if (world.getBlockAt(location.add(0, 1, 0)).getType().isAir())
            if (world.getBlockAt(location.add(0, 2, 0)).getType().isAir())
                if (world.getBlockAt(location.add(0, 3, 0)).getType().isAir())
                    if (!block.isLiquid() && !block.getType().toString().contains("LEAVES"))
                        return true;
        return false;
    }
}
