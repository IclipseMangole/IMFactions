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
        createGimlisAxe();
        createDagger();
        createTimsCrossbow();
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
        for (int i = 1; i <= 5; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            if (i != 5) {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(2.0D + i - 1));
            } else {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(7.0D));
            }
            map.put(Integer.valueOf(i), level);
        }
        new FactionItem("Stone Club", "Booga Booga", Material.STONE_HOE, ItemRarity.COMMON, map);
    }

    public void createHeadCutter() {
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        defaultLevel.put(ItemModifierType.DAMAGE, new ItemModifierValue(Double.valueOf(8.0D)));
        for (int i = 1; i <= 5; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            if (i != 5) {
                level.put(ItemModifierType.ATTACK_SPEED, new ItemModifierValue(0.25D + (i - 1) * 0.1875));
            } else {
                level.put(ItemModifierType.ATTACK_SPEED, new ItemModifierValue(0.875D));
            }
            map.put(Integer.valueOf(i), level);
        }
        new FactionItem("Head Cutter", "On the neck!", Material.STONE_SWORD, ItemRarity.COMMON, map);
    }

    public void createFaramirsBow() {
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            if (i != 5) {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(2.0D + i - 1));
            } else {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(7.0D));
            }
            map.put(Integer.valueOf(i), level);
        }
        new FactionItem("Faramir's Bow", "Did you drink enough Zielwasser?", Material.BOW, ItemRarity.COMMON, map);
    }

    //UNCOMMON

    public void createGimlisAxe() {
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        defaultLevel.put(ItemModifierType.ATTACK_SPEED, new ItemModifierValue(Double.valueOf(0.5D)));
        for (int i = 1; i <= 5; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            if (i != 5) {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(6.0D + i * 2));
            } else {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(18.0D));
            }
            map.put(Integer.valueOf(i), level);
        }
        new FactionItem("Gimli's Axe", "Make Moria great again", Material.GOLDEN_AXE, ItemRarity.UNCOMMON, map);
    }

    public void createDagger() {
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        defaultLevel.put(ItemModifierType.ATTACK_SPEED, new ItemModifierValue(Double.valueOf(2.0D)));
        for (int i = 1; i <= 5; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            if (i != 5) {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(1.5D + i * 0.5));
            } else {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(4.5));
            }
            map.put(Integer.valueOf(i), level);
        }
        new FactionItem("Dagger", "Fast as Fuck", Material.IRON_SWORD, ItemRarity.UNCOMMON, map);
    }

    public void createTimsCrossbow() {
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        defaultLevel.put(ItemModifierType.ATTACK_SPEED, new ItemModifierValue(Double.valueOf(1.0D)));
        for (int i = 1; i <= 5; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            if (i != 5) {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(1.0D + i));
                level.put(ItemModifierType.POISON, new ItemModifierValue(Integer.valueOf(1)));
            } else {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(5.0D));
                level.put(ItemModifierType.POISON, new ItemModifierValue(Integer.valueOf(2)));
            }
            map.put(Integer.valueOf(i), level);
        }
        new FactionItem("Tim's Crossbow", "Shoots poison arrows", Material.CROSSBOW, ItemRarity.UNCOMMON, map);
    }

    //RARE

    public void createExcalibur() {
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        defaultLevel.put(ItemModifierType.ATTACK_SPEED, new ItemModifierValue(Double.valueOf(1.0D)));
        for (int i = 1; i <= 5; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            if (i != 5) {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(Double.valueOf(4.0D + i - 1)));
                level.put(ItemModifierType.FIRE_ASPECT, new ItemModifierValue(Integer.valueOf(1)));
            } else {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(Double.valueOf(7.0D)));
                level.put(ItemModifierType.FIRE_ASPECT, new ItemModifierValue(Integer.valueOf(2)));
            }
            map.put(Integer.valueOf(i), level);
        }
        new FactionItem("Excalibur", "A perfectly balanced blade", Material.DIAMOND_SWORD, ItemRarity.RARE, map);
    }

    //EPIC

    public void createBow() {
        HashMap<Integer, HashMap<ItemModifierType, ItemModifierValue>> map = new HashMap<>();
        HashMap<ItemModifierType, ItemModifierValue> defaultLevel = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            HashMap<ItemModifierType, ItemModifierValue> level = (HashMap<ItemModifierType, ItemModifierValue>) defaultLevel.clone();
            if (i != 5) {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(Double.valueOf(8.0D + i) - 1));
            } else {
                level.put(ItemModifierType.DAMAGE, new ItemModifierValue(Double.valueOf(13.0D)));
            }
            map.put(Integer.valueOf(i), level);
        }
        new FactionItem("Yew Arch", ChatColor.of("#8C19FF"), "Created by Elves", Material.BOW, ItemRarity.EPIC, map);
    }

    //LEGENDARY

}


