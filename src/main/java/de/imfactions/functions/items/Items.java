package de.imfactions.functions.items;


import de.imfactions.functions.items.modifiers.ItemModifierType;
import de.imfactions.functions.items.modifiers.ItemModifierValue;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.util.HashMap;


public class Items {


    public Items() {
        //COMMON
        createStoneClub();
        createHeadCutter();
        createFaramirsBow();
        //UNCOMMON
        //RARE
        createExcalibur();
        //EPIC
        createBow();
        //LEGENDARY
    }

    //COMMON

    public void createStoneClub() {
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        defaultLevel.put(ItemModifierType.ATTACK_SPEED, new ItemModifierValue(Double.valueOf(1.0D)));
        for (int i = 0; i < 5; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            if (i != 4) {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(2.0D + i));
            } else {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(7.0D));
            }
            map.put(Integer.valueOf(i), level);
        }
        new FactionItem("Stone Club", ChatColor.of("#363636"), "Booga Booga", Material.STONE_HOE, ItemRarity.COMMON, map);
    }

    public void createHeadCutter() {
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        defaultLevel.put(ItemModifierType.DAMAGE, new ItemModifierValue(Double.valueOf(8.0D)));
        defaultLevel.put(ItemModifierType.ATTACK_SPEED, new ItemModifierValue(Double.valueOf(0.25D)));
        for (int i = 0; i < 5; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            if (i != 4) {
                level.put(ItemModifierType.ATTACK_SPEED, new ItemModifierValue(0.25D + i * 0.1875));
            } else {
                level.put(ItemModifierType.ATTACK_SPEED, new ItemModifierValue(0.875D));
            }
            map.put(Integer.valueOf(i), level);
        }
        new FactionItem("Head Cutter", ChatColor.of("#363636"), "On the neck!", Material.STONE_SWORD, ItemRarity.COMMON, map);
    }

    public void createFaramirsBow() {
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            if (i != 4) {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(2.0D + i));
            } else {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(7.0D));
            }
            map.put(Integer.valueOf(i), level);
        }
        new FactionItem("Faramir's Bow", ChatColor.of("#363636"), "Did you drink enough Zielwasser?", Material.BOW, ItemRarity.COMMON, map);
    }

    //UNCOMMON

    //RARE

    public void createExcalibur() {
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        defaultLevel.put(ItemModifierType.ATTACK_SPEED, new ItemModifierValue(Double.valueOf(1.0D)));
        for (int i = 0; i < 5; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            if (i != 4) {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(Double.valueOf(4.0D + i)));
                level.put(ItemModifierType.FIRE_ASPECT, new ItemModifierValue(Integer.valueOf(1)));
            } else {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(Double.valueOf(7.0D)));
                level.put(ItemModifierType.FIRE_ASPECT, new ItemModifierValue(Integer.valueOf(2)));
            }
            map.put(Integer.valueOf(i), level);
        }
        new FactionItem("Excalibur", ChatColor.of("#66FFFF"), "A perfectly balanced blade", Material.DIAMOND_SWORD, ItemRarity.RARE, map);
    }

    //EPIC

    public void createBow() {
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            if (i != 4) {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(Double.valueOf(8.0D + i)));
            } else {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(Double.valueOf(13.0D)));
            }
            map.put(Integer.valueOf(i), level);
        }
        new FactionItem("Yew arch", ChatColor.of("#36FFFF"), "Created by Elves", Material.BOW, ItemRarity.EPIC, map);
    }

    //LEGENDARY

}


