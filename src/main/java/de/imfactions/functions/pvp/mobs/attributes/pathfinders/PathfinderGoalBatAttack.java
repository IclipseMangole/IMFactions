package de.imfactions.functions.pvp.mobs.attributes.pathfinders;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ambient.EntityBat;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.EnumSet;

public class PathfinderGoalBatAttack extends PathfinderGoal {

    EntityBat entityBat;
    EntityLiving target;
    float damage;
    int cooldown = 0;

    public PathfinderGoalBatAttack(EntityBat entityBat, float damage) {
        this.entityBat = entityBat;
        this.damage = damage;
        this.a(EnumSet.of(Type.a));
    }

    @Override
    public boolean a() {
        cooldown--;
        if (cooldown > 0) {
            return false;
        }
        target = entityBat.getGoalTarget();
        Location bat = entityBat.getBukkitEntity().getLocation();
        if (target == null) {
            return false;
        }
        Location t = target.getBukkitEntity().getLocation();
        if (bat.distance(t) < 1.05)
            return true;
        Vector vector = t.add(0, 1, 0).subtract(bat).toVector().multiply(0.5);
        entityBat.move(EnumMoveType.a, new Vec3D(vector.getX(), vector.getY(), vector.getZ()));
        return false;
    }

    @Override
    public void c() {
        cooldown = 40;
        target.damageEntity(DamageSource.mobAttack(entityBat), damage);
        World world = entityBat.getBukkitEntity().getWorld();
        world.spawnParticle(Particle.DAMAGE_INDICATOR, target.getBukkitEntity().getLocation(), 10);
        world.playSound(target.getBukkitEntity().getLocation(), Sound.ENTITY_BAT_HURT, 1.0F, 1.0F);
    }

    @Override
    public boolean b() {
        return cooldown < 0;
    }
}
