package de.imfactions.functions.items;

import de.imfactions.functions.items.api.ItemStructure;
import de.imfactions.functions.items.api.modifiers.ItemModifierType;
import de.imfactions.functions.items.api.modifiers.ItemModifierValue;

import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;


public class Items {
    public Items() {
        createExcalibur();
        createBow();
    }

    public void createExcalibur() {
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        defaultLevel.put(ItemModifierType.DAMAGE, new ItemModifierValue(Double.valueOf(0.5D)));
        defaultLevel.put(ItemModifierType.ATTACK_SPEED, new ItemModifierValue(Double.valueOf(1.3D)));
        defaultLevel.put(ItemModifierType.FIRE_ASPECT, new ItemModifierValue(Integer.valueOf(1)));
        defaultLevel.put(ItemModifierType.LIFESTEAL, new ItemModifierValue(Double.valueOf(0.2D)));
        for (int i = 0; i < 6; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            level.replace(ItemModifierType.FIRE_ASPECT, new ItemModifierValue(Integer.valueOf(i + 1)));
            level.replace(ItemModifierType.LIFESTEAL, new ItemModifierValue(Double.valueOf((i + 1) * 0.2D)));
            map.put(Integer.valueOf(i), level);
        }
        new ItemStructure("Excalibur", ChatColor.of("#66FFFF"), "A perfectly balanced blade", Material.DIAMOND_SWORD, ItemRarity.RARE, map);
    }

    public void createBow() {
        System.out.println("Create Bow");
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        defaultLevel.put(ItemModifierType.DAMAGE, new ItemModifierValue(Double.valueOf(0.5D)));
        for (int i = 0; i < 6; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            defaultLevel.put(ItemModifierType.DAMAGE, new ItemModifierValue(Double.valueOf(i + 0.5D)));
            map.put(Integer.valueOf(i), level);
        }
        new ItemStructure("Yew arch", ChatColor.of("#36FFFF"), "Created by Elves", Material.BOW, ItemRarity.EPIC, map);
    }
}


