package de.imfactions.functions.items;

import net.md_5.bungee.api.ChatColor;

public enum ItemRarity {
    COMMON(0, ChatColor.of("#5C5C5C")), UNCOMMON(1, ChatColor.of("#154400")), RARE(2, ChatColor.of("#000B47")), EPIC(3, ChatColor.of("#49007F")), LEGENDARY(4, ChatColor.of("#A10000"));

    private ChatColor color;
    private int id;
   
    ItemRarity(int id, ChatColor color) {
        this.id = id;
        this.color = color;
    }

    public int getId() {
        return this.id;
    }

    public ChatColor getColor() {
        return this.color;
    }
}