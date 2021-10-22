package de.imfactions.functions.pvp.mobs.attributes;

import de.imfactions.functions.pvp.mobs.CustomMob;
import org.bukkit.ChatColor;

import java.util.Random;

public class CustomMobLevel {

    private final CustomMob customMob;
    private final Random random = new Random();

    public CustomMobLevel(CustomMob customMob) {
        this.customMob = customMob;
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

    public String getLevelString() {
        String levelString;
        String legendaryString = ChatColor.BOLD + "" + ChatColor.DARK_RED + " Legendary";
        switch (customMob.level) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                levelString = ChatColor.GREEN + " Level " + customMob.level;
                break;
            case 6:
            case 7:
                levelString = ChatColor.BLUE + " Level " + customMob.level;
                break;
            case 8:
            case 9:
                levelString = ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + " Level " + customMob.level;
                break;
            default:
                levelString = ChatColor.BOLD + "" + ChatColor.GOLD + " Level " + customMob.level;
        }
        if (customMob.legendary)
            return legendaryString + levelString + " ";
        return levelString;
    }
}
