package de.imfactions.functions.items.api;
import de.imfactions.functions.items.ItemRarity;
import de.imfactions.functions.items.api.modifiers.ItemModifierType;
import de.imfactions.functions.items.api.modifiers.ItemModifierValue;
import java.util.ArrayList;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;







 public class ItemStructure
         {
             private static ArrayList<ItemStructure> factionItems = new ArrayList<>();
    
       private String displayName;
       private ChatColor chatColor;
       private String subtitle;
       private Material material;
       private de.imfactions.functions.items.ItemRarity rarity;
       private HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> modifiersPerLevel;

    
    
    public ItemStructure(String displayName, ChatColor chatColor, String subtitle, Material material, de.imfactions.functions.items.ItemRarity rarity, HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> modifiersPerLevel) {
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
        return this.chatColor;

    }

    
    
    public String getSubtitle() {
        return this.subtitle;

    }

    
    
    public Material getMaterial() {
        return this.material;

    }

    
    
    public de.imfactions.functions.items.ItemRarity getRarity() {
        return this.rarity;

    }

    
    
    public HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> getModifiersPerLevel() {
        return this.modifiersPerLevel;

    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    public HashMap<ItemModifierType, ItemModifierValue> getModifiers(int level) {
        return this.modifiersPerLevel.get(Integer.valueOf(level));

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


