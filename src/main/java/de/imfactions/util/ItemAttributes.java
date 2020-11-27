package de.imfactions.util;

//  ╔══════════════════════════════════════╗
//  ║      ___       ___                   ║
//  ║     /  /___   /  /(_)____ ____  __   ║
//  ║    /  // __/ /  // // ) // ___// )\  ║                                  
//  ║   /  // /__ /  // //  _/(__  )/ __/  ║                                                                         
//  ║  /__/ \___//__//_//_/  /____/ \___/  ║                                              
//  ╚══════════════════════════════════════╝

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Material;

import java.util.Iterator;

/**
 * Created by Iclipse on 20.07.2020
 */
public class ItemAttributes {
    private ItemAttributes(){}

    public static double getDamage(Material material){
        if(material == Material.BOW || material == Material.ARROW || material == Material.CROSSBOW){
            return 9.0;
        }
        Item item = null;
        try {
            item = (Item) Items.class.getField(material.name()).get(new String());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Iterator iterator = item.a(EnumItemSlot.MAINHAND).get(GenericAttributes.ATTACK_DAMAGE).iterator();
        Object object = new Object();
        while (iterator.hasNext()){
            object = iterator.next();
        }
        return ((AttributeModifier) object).getAmount();
    }

    public static double getAttackSpeed(Material material){
        if(material == Material.BOW || material == Material.ARROW || material == Material.CROSSBOW){
            return 1;
        }else if(material == Material.CROSSBOW){
            return 1.25;
        }
        Item item = null;
        try {
            item = (Item) Items.class.getField(material.name()).get(new String());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Iterator iterator = item.a(EnumItemSlot.MAINHAND).get(GenericAttributes.ATTACK_SPEED).iterator();
        Object object = new Object();
        while (iterator.hasNext()){
            object = iterator.next();
        }
        double attackspeed = 4 + ((AttributeModifier) object).getAmount();
        return Math.round(attackspeed * 100) / 100.0;
    }
}
