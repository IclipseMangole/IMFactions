package de.imfactions.functions.pvp.mobs;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

public class Orc extends CustomMob {


    public Orc(Location location) {
        super(EntityTypes.be, location, ChatColor.DARK_GREEN + "Orc");
        level = customMobLevel.getRandomLevel();
        legendary = customMobLevel.getRandomLegendary();
        setAttributes();
        setDrops();
        initPathfinder();
        this.setName(getHealth());
        net.minecraft.world.level.World worldServer = ((CraftWorld) location.getWorld()).getHandle();
        worldServer.addEntity(this);
    }

    private void setDrops() {

    }

    private void setAttributes() {
        customAttributes.setMaxHealth(8.2898 * (Math.pow(Math.E, 0.2983 * level)));
        if (legendary)
            customAttributes.setMaxHealth(getMaxHealth() + 30);
    }

    @Override
    protected void initPathfinder() {
        this.bP.a(0, new PathfinderGoalMeleeAttack(this, 1.5, true));
        this.bP.a(1, new PathfinderGoalRestrictSun(this));
        this.bP.a(2, new PathfinderGoalFleeSun(this, 1.2));
        this.bP.a(3, new PathfinderGoalRandomStrollLand(this, 1.0));
        this.bP.a(4, new PathfinderGoalRandomLookaround(this));
        this.bQ.a(0, new PathfinderGoalHurtByTarget(this));
        this.bQ.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
    }
}
