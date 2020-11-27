package de.imfactions.functions.items;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---


import de.imfactions.functions.items.api.Item;
import de.imfactions.functions.items.api.ItemRarity;
import de.imfactions.functions.items.api.ItemStructure;
import de.imfactions.functions.items.api.modifiers.ItemModifierType;
import de.imfactions.functions.items.api.modifiers.ItemModifierValue;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.util.HashMap;

/**
 * Created by Iclipse on 20.06.2020
 */
public class    Items {

    public Items(){
        createExcalibur();
        createBow();
    }

    public void createExcalibur(){
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        defaultLevel.put(ItemModifierType.DAMAGE, new ItemModifierValue(0.5));
        defaultLevel.put(ItemModifierType.ATTACK_SPEED, new ItemModifierValue(1.3));
        defaultLevel.put(ItemModifierType.FIRE_ASPECT, new ItemModifierValue(1));
        defaultLevel.put(ItemModifierType.LIFESTEAL, new ItemModifierValue(0.2));
        for(int i = 0; i < 6; i++){
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            level.replace(ItemModifierType.FIRE_ASPECT, new ItemModifierValue(i + 1));
            level.replace(ItemModifierType.LIFESTEAL, new ItemModifierValue((i + 1) * 0.2));
            map.put(i, level);
        }

        new ItemStructure("Excalibur", ChatColor.of("#66FFFF"), "A perfectly balanced blade", Material.DIAMOND_SWORD, ItemRarity.RARE, map);
    }

    public void createBow(){
        System.out.println("Create Bow");
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        defaultLevel.put(ItemModifierType.DAMAGE, new ItemModifierValue(0.5));
        for(int i = 0; i < 6; i++){
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            defaultLevel.put(ItemModifierType.DAMAGE, new ItemModifierValue(i + 0.5));
            map.put(i, level);
        }

        new ItemStructure("Yew arch", ChatColor.of("#36FFFF"), "Created by Elves", Material.BOW, ItemRarity.EPIC, map);
    }
}
