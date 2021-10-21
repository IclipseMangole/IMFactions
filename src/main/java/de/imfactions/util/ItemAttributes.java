package de.imfactions.util;

import java.util.Iterator;

import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.bukkit.Material;

public class ItemAttributes {
    public static double getDamage(Material material) {
        if (material == Material.BOW || material == Material.ARROW || material == Material.CROSSBOW)
            return 9.0D;
        Item item = null;
        try {
            item = (Item) Items.class.getField(material.name()).get(new String());
        } catch (NoSuchFieldException|IllegalAccessException e) {
            e.printStackTrace();
        }
        Iterator iterator = item.a(EnumItemSlot.a).get(GenericAttributes.f).iterator();
        Object object = new Object();
        while (iterator.hasNext())
            object = iterator.next();
        return ((AttributeModifier)object).getAmount();
    }

    public static double getAttackSpeed(Material material) {
        if (material == Material.BOW || material == Material.ARROW)
            return 1.0D;
        if (material == Material.CROSSBOW)
            return 1.25D;
        Item item = null;
        try {
            item = (Item)Items.class.getField(material.name()).get(new String());
        } catch (NoSuchFieldException|IllegalAccessException e) {
            e.printStackTrace();
        }
        Iterator iterator = item.a(EnumItemSlot.a).get(GenericAttributes.h).iterator();
        Object object = new Object();
        while (iterator.hasNext())
            object = iterator.next();
        double attackspeed = 4.0D + ((AttributeModifier)object).getAmount();
        return Math.round(attackspeed * 100.0D) / 100.0D;
    }
}