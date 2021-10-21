package de.imfactions.functions.items;

import java.util.ArrayList;
import java.util.HashMap;

import de.imfactions.functions.items.modifiers.ItemModifierType;
import de.imfactions.functions.items.modifiers.ItemModifierValue;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class FactionItem {
    private static ArrayList<FactionItem> factionItems = new ArrayList<>();

    private String displayName;
    private ChatColor chatColor;
    private String subtitle;
    private Material material;
    private ItemRarity rarity;
    private HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> modifiersPerLevel;

    public FactionItem(String displayName, ChatColor chatColor, String subtitle, Material material, ItemRarity rarity, HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> modifiersPerLevel) {
        this.displayName = displayName;
        this.chatColor = chatColor;
        this.subtitle = subtitle;
        this.material = material;
        this.rarity = rarity;
        this.modifiersPerLevel = modifiersPerLevel;
        factionItems.add(this);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public ChatColor getChatColor() {
        return chatColor;
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

    public static FactionItem get(String displayName) {
        for (FactionItem factionItem : factionItems) {
            if (factionItem.getDisplayName().equals(displayName)) {
                return factionItem;
            }
        }
        throw new NullPointerException("Item " + displayName + " not existing");
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


