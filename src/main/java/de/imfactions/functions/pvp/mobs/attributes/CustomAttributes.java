package de.imfactions.functions.pvp.mobs.attributes;

import de.imfactions.functions.pvp.mobs.custommob.CustomMobInsentient;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;

public class CustomAttributes {

    private final CustomMobInsentient customMobInsentient;
    private final EntityInsentient insentient;

    public CustomAttributes(CustomMobInsentient customMobInsentient) {
        this.customMobInsentient = customMobInsentient;
        insentient = customMobInsentient.entityInsentient;
    }

    public void setMaxHealth(double health) {
        insentient.getAttributeInstance(GenericAttributes.a).setValue(health);
        insentient.setHealth(insentient.getMaxHealth());
    }

    public void setFollowRange(double range) {
        insentient.getAttributeInstance(GenericAttributes.b).setValue(range);
    }

    public void setKnockbackResistance(double resistance) {
        insentient.getAttributeInstance(GenericAttributes.c).setValue(resistance);
    }

    public void setSpeed(double speed) {
        insentient.getAttributeInstance(GenericAttributes.d).setValue(speed);
    }

    public void setFlySpeed(double flySpeed) {
        insentient.getAttributeInstance(GenericAttributes.e).setValue(flySpeed);
    }

    public void setAttackDamage(double damage) {
        insentient.getAttributeInstance(GenericAttributes.f).setValue(damage);
    }

    public void setAttackKnockback(double knockback) {
        insentient.getAttributeInstance(GenericAttributes.g).setValue(knockback);
    }

    public void setAttackSpeed(double attackSpeed) {
        insentient.getAttributeInstance(GenericAttributes.h).setValue(attackSpeed);
    }

    public void setArmor(double armor) {
        insentient.getAttributeInstance(GenericAttributes.i).setValue(armor);
    }

    public void setArmorToughness(double armorToughness) {
        insentient.getAttributeInstance(GenericAttributes.j).setValue(armorToughness);
    }

    public void setEquip(Material material) {
        if (!material.isItem())
            return;
        EnumItemSlot slot;
        switch (material.getEquipmentSlot()) {
            case HAND:
                slot = EnumItemSlot.a;
                break;
            case OFF_HAND:
                slot = EnumItemSlot.b;
                break;
            case FEET:
                slot = EnumItemSlot.c;
                break;
            case LEGS:
                slot = EnumItemSlot.d;
                break;
            case CHEST:
                slot = EnumItemSlot.e;
                break;
            default:
                slot = EnumItemSlot.f;
        }
        org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack(material);
        ItemStack equip = CraftItemStack.asNMSCopy(itemStack);
        insentient.setSlot(slot, equip);
    }
}
