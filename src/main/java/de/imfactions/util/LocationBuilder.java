package de.imfactions.util;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationBuilder {
    public static String toString(Location location) {
        String loc = "";

        loc = loc + location.getWorld().getName();
        loc = loc + "," + location.getX();
        loc = loc + "," + location.getY();
        loc = loc + "," + location.getZ();
        if (location.getYaw() == 0 && location.getPitch() == 0) {
            loc = loc + "," + location.getYaw();
            loc = loc + "," + location.getPitch();
        }

        return loc;
    }

    public static String getString(Location location){
        String loc = "( ";

        loc = loc + Math.round(location.getX());
        loc = loc + " | ";
        loc = loc + Math.round(location.getY());
        loc = loc + " | ";
        loc = loc + Math.round(location.getZ());
        loc = loc + " ) ";

        return loc;
    }


    public static Location fromString(String location) {
        Location loc = null;
        String[] sloc = location.split(",");
        if (sloc.length == 4) {
            loc = new Location(Bukkit.getWorld(sloc[0]), Double.valueOf(sloc[1]), Double.valueOf(sloc[2]), Double.valueOf(sloc[3]));
        }
        if (sloc.length == 6) {
            loc = new Location(Bukkit.getWorld(sloc[0]), Double.valueOf(sloc[1]), Double.valueOf(sloc[2]), Double.valueOf(sloc[3]), Float.valueOf(sloc[4]), Float.valueOf(sloc[5]));
        }
        return loc;
    }

}
