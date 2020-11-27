package de.imfactions.functions.items.api;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---


import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.Event;

/**
 * Created by Iclipse on 20.06.2020
 */
public enum ItemRarity {

    COMMON(0, ChatColor.of("#363636")), UNCOMMON(1, ChatColor.of("#154400")), RARE(2, ChatColor.of("#0044CC")), EPIC(3, ChatColor.of("#49007F")), LEGENDARY(4, ChatColor.of("#A10000"));

    private int id;
    private ChatColor color;

    ItemRarity(int id, ChatColor color) {
        this.id = id;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public ChatColor getColor() {
        return color;
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
