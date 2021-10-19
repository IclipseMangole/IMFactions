package de.imfactions.functions.items;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;


public class ItemModifierType<T> {
    private static final Map<String, ItemModifierType<?>> itemModifierTypes = new HashMap<>();
    public static final ItemModifierType<Double> DAMAGE = new ItemModifierType<>("damage", "Schaden", ChatColor.of("#b02323"), Double.class);
    public static final ItemModifierType<Double> ATTACK_SPEED = new ItemModifierType<Double>("attackSpeed", "Angriffsgeschwindigkeit", ChatColor.of("#4DFFE1"), Double.class);
    public static final ItemModifierType<Double> KNOCKBACK_RESISTANCE = new ItemModifierType<Double>("knockbackResistance", "Standfestigkeit", ChatColor.of("#000000"), Double.class);
    public static final ItemModifierType<Integer> SPEED = new ItemModifierType<Integer>("speed", "Schnelligkeit", ChatColor.of("#4DFFFF"), Integer.class);
    public static final ItemModifierType<Integer> POISON = new ItemModifierType<>("poison", "Vergiftung", ChatColor.of("#1eb02d"), Integer.class);
    public static final ItemModifierType<Integer> FRIRE_ASPECT = new ItemModifierType<Integer>("fireAspect", "Verbrennung", ChatColor.of("#e25822"), Integer.class);

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
        return this.name;
    }

    public Class<T> getType() {
        return this.type;
    }


    public String toString() {
        return "ItemModifierType{key=" + this.name + ",type=" + this.type + '}';
    }

    public static ItemModifierType getByName(String name) {
        return itemModifierTypes.get(name);
    }

    public static ItemModifierType<?>[] values() {
        return (ItemModifierType<?>[]) itemModifierTypes.values().toArray();
    }
}


