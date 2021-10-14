package de.imfactions.functions.pvp.mobs.abilities;

import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.monster.EntityMonster;

public class CustomAttributes {

    private EntityMonster entityMonster;

    public CustomAttributes(EntityMonster entityMonster) {
        this.entityMonster = entityMonster;
    }

    public void setSize(float hoehe, float breite) {

    }

    public void setMaxHealth(double health) {
        entityMonster.getAttributeInstance(GenericAttributes.a).setValue(health);
        entityMonster.setHealth(entityMonster.getMaxHealth());
    }

    public void setFollowRange(double range) {
        entityMonster.getAttributeInstance(GenericAttributes.b).setValue(range);
    }

    public void setKnockbackResistance(double resistance) {
        entityMonster.getAttributeInstance(GenericAttributes.c).setValue(resistance);
    }

    public void setSpeed(double speed) {
        entityMonster.getAttributeInstance(GenericAttributes.d).setValue(speed);
    }

    public void setAttackDamage(double damage) {
        entityMonster.getAttributeInstance(GenericAttributes.f).setValue(damage);
    }

    public void setAttackKnockback(double knockback) {
        entityMonster.getAttributeInstance(GenericAttributes.g).setValue(knockback);
    }

    public void setAttackSpeed(double attackSpeed) {
        entityMonster.getAttributeInstance(GenericAttributes.h).setValue(attackSpeed);
    }

    public void setArmor(double armor) {
        entityMonster.getAttributeInstance(GenericAttributes.i).setValue(armor);
    }

    public void setArmorToughness(double armorToughness) {
        entityMonster.getAttributeInstance(GenericAttributes.j).setValue(armorToughness);
    }
}
