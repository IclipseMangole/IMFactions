package de.imfactions.functions.pvp.mobs.attributes;

import de.imfactions.functions.pvp.mobs.custommob.CustomMobInsentient;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;

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
}
