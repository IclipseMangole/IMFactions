package de.imfactions.functions.pvp.mobs;

import de.imfactions.functions.pvp.mobs.custommob.CustomMobMonster;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBowShoot;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRestrictSun;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

public class Undead extends CustomMobMonster {

    EntitySkeleton skeleton;

    public Undead(Location location) {
        super(new EntitySkeleton(EntityTypes.aB, ((CraftWorld) location.getWorld()).getHandle()), location, ChatColor.WHITE + "Undead");
        skeleton = (EntitySkeleton) entityMonster;
        level = customMobLevel.getRandomLevel();
        legendary = customMobLevel.getRandomLegendary();
        setAttributes();
        setDrops();
        initPathfinder();
        this.setName(getHealth());
        net.minecraft.world.level.World worldServer = ((CraftWorld) location.getWorld()).getHandle();
        worldServer.addEntity(skeleton);
    }

    private void setDrops() {

    }

    private void setAttributes() {
        customAttributes.setMaxHealth(6 * (Math.pow(Math.E, 0.2983 * level)));
        if (legendary)
            customAttributes.setMaxHealth(getMaxHealth() + 30);
        customAttributes.setEquip(Material.LEATHER_HELMET);
        customAttributes.setEquip(Material.BOW);
    }

    private void initPathfinder() {
        skeleton.bP.a();
        skeleton.bQ.a();
        skeleton.bP.a(0, new PathfinderGoalBowShoot<>(skeleton, 1.3D, 5, 10.0F));
        skeleton.bP.a(1, new PathfinderGoalRestrictSun(skeleton));
        skeleton.bP.a(3, new PathfinderGoalRandomStrollLand(skeleton, 1.0));
        skeleton.bP.a(4, new PathfinderGoalRandomLookaround(skeleton));
        skeleton.bQ.a(0, new PathfinderGoalHurtByTarget(skeleton));
        skeleton.bQ.a(1, new PathfinderGoalNearestAttackableTarget<>(skeleton, EntityHuman.class, true));
        skeleton.bQ.a(1, new PathfinderGoalNearestAttackableTarget<>(skeleton, EntityZombie.class, true));
    }
}
