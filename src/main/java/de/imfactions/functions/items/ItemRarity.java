package de.imfactions.functions.items;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---


import org.bukkit.ChatColor;

/**
 * Created by Iclipse on 20.06.2020
 */
public enum ItemRarity {

    COMMON(0, ChatColor.GRAY), UNCOMMON(1, ChatColor.GREEN), RARE(2, ChatColor.BLUE), EPIC(3, ChatColor.DARK_PURPLE), LEGENDARY(4, ChatColor.DARK_RED);

    ItemRarity(int id, ChatColor color) {
    }

    public static ItemRarity valueOf(int id) {
        switch (id) {
            case 0:
                return COMMON;
            case 1:
                return UNCOMMON;
            case 2:
                return RARE;
            case 3:
                return EPIC;
            case 4:
                return LEGENDARY;
            default:
                return null;
        }
    }
}
