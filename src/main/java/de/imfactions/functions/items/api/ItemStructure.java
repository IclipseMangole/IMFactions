package de.imfactions.functions.items.api;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import de.imfactions.functions.items.api.modifiers.ItemModifierType;
import de.imfactions.functions.items.api.modifiers.ItemModifierValue;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Iclipse on 21.06.2020
 */
public class ItemStructure {
    private static ArrayList<ItemStructure> factionItems = new ArrayList<>();

    private String displayName;
    private ChatColor chatColor;
    private String subtitle;
    private Material material;
    private ItemRarity rarity;
    private HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> modifiersPerLevel;

    public ItemStructure(String displayName, ChatColor chatColor, String subtitle, Material material, ItemRarity rarity, HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> modifiersPerLevel) {
        this.displayName = displayName;
        this.chatColor = chatColor;
        this.subtitle = subtitle;
        this.material = material;
        this.rarity = rarity;
        this.modifiersPerLevel = modifiersPerLevel;
        factionItems.add(this);
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> getModifiersPerLevel() {
        return modifiersPerLevel;
    }

    public HashMap<ItemModifierType, ItemModifierValue> getModifiers(int level) {
        /*
        HashMap<ItemModifierType, ItemModifierValue> modifiers = modifiersPerLevel.get(level);
        for(int i = level - 1; i >= 0; i--){
            modifiersPerLevel.get(i).forEach((itemModifierType, itemModifierValue) -> {
                if(!modifiers.containsKey(itemModifierType)){
                    modifiers.put(itemModifierType, itemModifierValue);
                }
            });
        }
        return modifiers;
         */
        return modifiersPerLevel.get(level);
    }

    public static ArrayList<ItemStructure> getFactionItems() {
        return factionItems;
    }

    public static ItemStructure of(ItemStack itemStack, ItemRarity rarity) {
        for (ItemStructure entry : factionItems) {
            if (entry.getMaterial() == itemStack.getType() && entry.getRarity() == rarity) {
                return entry;
            }
        }
        return null;
    }
}
