package de.imfactions.functions.pvp.mobs;

import de.imfactions.functions.items.FactionItem;
import de.imfactions.functions.items.FactionItemStack;
import de.imfactions.functions.pvp.mobs.custommob.CustomMobMonster;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

public class Orc extends CustomMobMonster {

    EntityZombie zombie;

    public Orc(Location location) {
        super(new EntityZombie(((CraftWorld) location.getWorld()).getHandle()), location, ChatColor.DARK_GREEN + "Orc");
        System.out.println("new Custom Orc");
        zombie = (EntityZombie) entityMonster;
        level = customMobLevel.getRandomLevel();
        legendary = customMobLevel.getRandomLegendary();
        setAttributes();
        setDrops();
        initPathfinder();
        this.setName(getHealth());
        net.minecraft.world.level.World worldServer = ((CraftWorld) location.getWorld()).getHandle();
        worldServer.addEntity(zombie);
        System.out.println("new Custom Orc auf World");
    }

    private void setDrops() {
        drops.put(new FactionItemStack(FactionItem.get("Stone Club"), 1).toItemStack(), 100);
    }

    private void setAttributes() {
        customAttributes.setMaxHealth(8.2898 * (Math.pow(Math.E, 0.2983 * level)));
        if (legendary)
            customAttributes.setMaxHealth(getMaxHealth() + 30);
    }

    private void initPathfinder() {
        zombie.bP.a(0, new PathfinderGoalZombieAttack(zombie, 1.5, true));
        zombie.bP.a(1, new PathfinderGoalRestrictSun(zombie));
        zombie.bP.a(2, new PathfinderGoalFleeSun(zombie, 1.2));
        zombie.bP.a(3, new PathfinderGoalRandomStrollLand(zombie, 1.0));
        zombie.bP.a(4, new PathfinderGoalRandomLookaround(zombie));
        zombie.bQ.a(0, new PathfinderGoalHurtByTarget(zombie));
        zombie.bQ.a(1, new PathfinderGoalNearestAttackableTarget<>(zombie, EntityHuman.class, true));
    }
}
