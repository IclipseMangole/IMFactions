package de.imfactions.functions.items.api.modifiers;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import de.imfactions.util.RomanNumber;
import net.md_5.bungee.api.ChatColor;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Iclipse on 20.06.2020
 */
public class ItemModifierType<T> {
    private static Map<String, ItemModifierType<?>> itemModifierTypes = new HashMap<>();
    public static final ItemModifierType<Double> DAMAGE = new ItemModifierType("damage", "Damage", ChatColor.of("#b02323"), Double.class);
    public static final ItemModifierType<Double> ATTACK_SPEED = new ItemModifierType("attackSpeed", "Attack Speed", ChatColor.of("#4DFFE1"), Double.class);
    public static final ItemModifierType<Integer> FIRE_ASPECT = new ItemModifierType("fireAspect", "Burning", ChatColor.of("#e25822"), Integer.class, true);
    public static final ItemModifierType<Integer> POISON = new ItemModifierType("poison", "Poison", ChatColor.of("#1eb02d"), Integer.class, true);
    public static final ItemModifierType<Integer> CONFUSION = new ItemModifierType("confusion", "Confusion", ChatColor.of("#006644"), Integer.class, true);
    public static final ItemModifierType<Integer> SLOWNESS = new ItemModifierType("slowness", "Slowness", ChatColor.of("#005566"), Integer.class, true);
    public static final ItemModifierType<Integer> BLINDNESS = new ItemModifierType("blindness", "Blindness", ChatColor.of("#575757"), Integer.class, true);
    public static final ItemModifierType<Integer> WITHER = new ItemModifierType("wither", "Wither", ChatColor.of("#363636"), Integer.class, true);
    public static final ItemModifierType<Double> LIFESTEAL = new ItemModifierType("lifesteal", "Lifesteal", ChatColor.of("#C20000"), Double.class, true);

    private final String name;
    private final String displayName;
    private final ChatColor chatColor;
    private final Class<T> type;
    private final boolean roman;
    private final boolean percent;

    public ItemModifierType(String name, String displayName, ChatColor chatColor, Class<T> type, boolean special) {
        this.name = name;
        this.displayName = displayName;
        this.chatColor = chatColor;
        this.type = type;
        if (special) {
            if (type == Double.class) {
                roman = false;
                percent = true;
            } else if (type == Integer.class) {
                roman = true;
                percent = false;
            } else {
                roman = false;
                percent = false;
            }
        } else {
            roman = false;
            percent = false;
        }
        itemModifierTypes.put(name, this);
    }

    public ItemModifierType(String name, String displayName, ChatColor chatColor, Class<T> type) {
        this(name, displayName, chatColor, type, false);
    }


    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public String toString(T value){
        String valueString = value.toString();
        if(roman){
            valueString = RomanNumber.toRoman((int) value);
        }
        if(percent){
            DecimalFormat format = new DecimalFormat("#.#");
            valueString = format.format(((double) value * 100)) + "%";
        }
        return chatColor + displayName + " " + valueString;
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
