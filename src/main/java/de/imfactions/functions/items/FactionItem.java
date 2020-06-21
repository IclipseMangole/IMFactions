package de.imfactions.functions.items;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Iclipse on 21.06.2020
 */
public class FactionItem {
    private static ArrayList<FactionItem> factionItems = new ArrayList<>();

    private String displayName;
    private String subtitle;
    private Material material;
    private ItemRarity rarity;
    private HashMap<Integer, ArrayList<ItemModifier>> modifiersPerLevel;
    private String description;

    public FactionItem(String displayName, String subtitle, Material material, ItemRarity rarity, HashMap<Integer, ArrayList<ItemModifier>> modifiersPerLevel, String description) {
        this.displayName = displayName;
        this.subtitle = subtitle;
        this.material = material;
        this.rarity = rarity;
        this.modifiersPerLevel = modifiersPerLevel;
        this.description = description;
        factionItems.add(this);
    }

    public String getDisplayName() {
        return displayName;
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

    public HashMap<Integer, ArrayList<ItemModifier>> getModifiersPerLevel() {
        return modifiersPerLevel;
    }

    public ArrayList<ItemModifier> getModifiers(int level) {
        return modifiersPerLevel.get(level);
    }

    public String getDescription() {
        return description;
    }

    public static ArrayList<FactionItem> getFactionItems() {
        return factionItems;
    }

    public static FactionItem of(ItemStack itemStack, ItemRarity rarity) {
        for (FactionItem entry : factionItems) {
            if (entry.getMaterial().equals(itemStack.getType()) && entry.getRarity() == rarity) {
                return entry;
            }
        }
        return null;
    }
}
