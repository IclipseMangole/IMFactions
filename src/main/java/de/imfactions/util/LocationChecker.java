package de.imfactions.util;

import org.bukkit.Location;

public class LocationChecker {

    public static boolean isLocationInsideCube(Location location, Location edgeDownFrontLeft, Location edgeUpBackRight) {
        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();

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
