package de.imfactions.functions.items;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Iclipse on 20.06.2020
 */
public class ItemModifierType<T> {
    private static Map<String, ItemModifierType<?>> itemModifierTypes = new HashMap<>();
    public static final ItemModifierType<Double> DAMAGE = new ItemModifierType("damage", "Schaden", ChatColor.of("#b02323"), Double.class);
    public static final ItemModifierType<Double> ATTACK_SPEED = new ItemModifierType("attackSpeed", "Angriffsgeschwindigkeit", ChatColor.of("#4DFFE1"), Double.class);
    public static final ItemModifierType<Double> KNOCKBACK_RESISTANCE = new ItemModifierType("knockbackResistance", "Standfestigkeit", ChatColor.of("#000000"), Double.class);
    public static final ItemModifierType<Integer> SPEED = new ItemModifierType("speed", "Schnelligkeit", ChatColor.of("#4DFFFF"), Integer.class);
    public static final ItemModifierType<Integer> POISON = new ItemModifierType("poison", "Vergiftung", ChatColor.of("#1eb02d"), Integer.class);
    public static final ItemModifierType<Integer> FIRE_ASPECT = new ItemModifierType("fireAspect", "Verbrennung", ChatColor.of("#e25822"), Integer.class);

    private final String name;
    private final String displayName;
    private final ChatColor chatColor;
    private final Class<T> type;

    public ItemModifierType(String name, String displayName, ChatColor chatColor, Class<T> type) {
        this.name = name;
        this.displayName = displayName;
        this.chatColor = chatColor;
        this.type = type;
        itemModifierTypes.put(name, this);
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ItemModifierType{key=" + this.name + ",type=" + this.type + '}';
    }

    public static ItemModifierType getByName(String name) {
        return (ItemModifierType) itemModifierTypes.get(name);
    }

    public static ItemModifierType<?>[] values() {
        return (ItemModifierType[]) itemModifierTypes.values().toArray();
    }
}
