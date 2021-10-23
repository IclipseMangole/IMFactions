package de.imfactions.functions.items;

import de.imfactions.functions.items.modifiers.ItemModifierType;
import de.imfactions.functions.items.modifiers.ItemModifierValue;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;


public class FactionItem {
    private static final ArrayList<FactionItem> factionItems = new ArrayList<>();

    private final String name;
    private final String displayName;
    private final String subtitle;
    private final Material material;
    private final ItemRarity rarity;
    private final HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> modifiersPerLevel;

    public FactionItem(String name, String displayName, String subtitle, Material material, ItemRarity rarity, HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> modifiersPerLevel) {
        this.name = name;
        this.displayName = displayName;
        this.subtitle = subtitle;
        this.material = material;
        this.rarity = rarity;
        this.modifiersPerLevel = modifiersPerLevel;
        factionItems.add(this);
    }

    public FactionItem(String name, ChatColor prefix, String subtitle, Material material, ItemRarity rarity, HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> modifiersPerLevel) {
        this(name, prefix + name, subtitle, material, rarity, modifiersPerLevel);
    }

    public FactionItem(String name, String subtitle, Material material, ItemRarity rarity, HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> modifiersPerLevel) {
        this(name, rarity.getColor() + name, subtitle, material, rarity, modifiersPerLevel);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getSubtitle() {
        return this.subtitle;
    }

    public Material getMaterial() {
        return this.material;
    }

    public ItemRarity getRarity() {
        return this.rarity;
    }

    public HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> getModifiersPerLevel() {
        return this.modifiersPerLevel;
    }

    public HashMap<ItemModifierType, ItemModifierValue> getModifiers(int level) {
        return this.modifiersPerLevel.get(Integer.valueOf(level));
    }

    public static ArrayList<FactionItem> getFactionItems() {
        return factionItems;
    }

    public static FactionItem get(String name) {
        for (FactionItem factionItem : factionItems) {
            if (factionItem.getName().equals(name)) {
                return factionItem;
            }
        }
        throw new NullPointerException("Item " + name + " not existing");
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


