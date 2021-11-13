package de.imfactions.functions.pvp.mobs.attributes;

import de.imfactions.functions.pvp.PVPZone;
import de.imfactions.functions.pvp.mobs.custommob.CustomMobInsentient;
import org.bukkit.ChatColor;

import java.util.Random;

public class CustomMobLevel {

    private final CustomMobInsentient customMobInsentient;
    private final Random random = new Random();

    public CustomMobLevel(CustomMobInsentient customMobInsentient) {
        this.customMobInsentient = customMobInsentient;
    }

    public int getRandomLevel() {
        int r = random.nextInt(100);
        if (r < 19)
            return 1;
        if (r < 36)
            return 2;
        if (r < 51)
            return 3;
        if (r < 64)
            return 4;
        if (r < 75)
            return 5;
        if (r < 84)
            return 6;
        if (r < 91)
            return 7;
        if (r < 96)
            return 8;
        if (r < 99)
            return 9;
        return 10;
    }

    public boolean getRandomLegendary() {
        int r = random.nextInt(100);
        return r < 1;
    }

    public int getZoneLevel(PVPZone pvpZone) {
        Random random = new Random();
        switch (pvpZone) {
            case SAFE:
            case PEACEFULLY:
                return 1 + random.nextInt(2);
            case EASY:
                return 2 + random.nextInt(2);
            case MEDIUM:
                return 3 + random.nextInt(2);
            case HARD:
                return 4 + random.nextInt(3);
            case EPIC:
                return 6 + random.nextInt(3);
            default:
                return 8 + random.nextInt(3);
        }
    }

    public String getLevelString() {
        String levelString;
        String legendaryString = ChatColor.BOLD + "" + ChatColor.DARK_RED + " Legendary";
        switch (customMobInsentient.level) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                levelString = ChatColor.GREEN + " Level " + customMobInsentient.level;
                break;
            case 6:
            case 7:
                levelString = ChatColor.BLUE + " Level " + customMobInsentient.level;
                break;
            case 8:
            case 9:
                levelString = ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + " Level " + customMobInsentient.level;
                break;
            default:
                levelString = ChatColor.BOLD + "" + ChatColor.GOLD + " Level " + customMobInsentient.level;
        }
        if (customMobInsentient.legendary)
            return legendaryString + levelString + " ";
        return levelString;
    }
}
