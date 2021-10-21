package de.imfactions.functions.pvp.mobs.abilities;

import de.imfactions.functions.pvp.mobs.CustomMob;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;

public class CustomAttributes {

    private CustomMob customMob;

    public CustomAttributes(CustomMob customMob) {
        this.customMob = customMob;
    }

    public void setMaxHealth(double health) {
        customMob.getAttributeInstance(GenericAttributes.a).setValue(health);
        customMob.setHealth(customMob.getMaxHealth());
    }

    public void setFollowRange(double range) {
        customMob.getAttributeInstance(GenericAttributes.b).setValue(range);
    }

    public void setKnockbackResistance(double resistance) {
        customMob.getAttributeInstance(GenericAttributes.c).setValue(resistance);
    }

    public void setSpeed(double speed) {
        customMob.getAttributeInstance(GenericAttributes.d).setValue(speed);
    }

    public void setAttackDamage(double damage) {
        customMob.getAttributeInstance(GenericAttributes.f).setValue(damage);
    }

    public void setAttackKnockback(double knockback) {
        customMob.getAttributeInstance(GenericAttributes.g).setValue(knockback);
    }

    public void setAttackSpeed(double attackSpeed) {
        customMob.getAttributeInstance(GenericAttributes.h).setValue(attackSpeed);
    }

    public void setArmor(double armor) {
        customMob.getAttributeInstance(GenericAttributes.i).setValue(armor);
    }

    public void setArmorToughness(double armorToughness) {
        customMob.getAttributeInstance(GenericAttributes.j).setValue(armorToughness);
    }
}
