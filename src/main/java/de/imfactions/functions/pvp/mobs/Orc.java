package de.imfactions.functions.pvp.mobs;

import de.imfactions.functions.items.drops.MobDrops;
import de.imfactions.functions.pvp.mobs.custommob.CustomMobMonster;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityPig;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class Orc extends CustomMobMonster {

    EntityZombie zombie;

    public Orc(Location location) {
        super(new EntityZombie(((CraftWorld) location.getWorld()).getHandle()), location, ChatColor.DARK_GREEN + "Orc");
        zombie = (EntityZombie) entityMonster;
        level = customMobLevel.getRandomLevel();
        legendary = customMobLevel.getRandomLegendary();
        setAttributes();
        setDrops();
        initPathfinder();
        this.setName(getHealth());
        net.minecraft.world.level.World worldServer = ((CraftWorld) location.getWorld()).getHandle();
        worldServer.addEntity(zombie);
    }

    private void setDrops() {
        Random random = new Random();
        int l = level;
        if (legendary)
            l += 2;
        ArrayList<ItemStack> items;

        if (l < 4) {
            items = MobDrops.getCommonDrops();
            drops.put(items.get(random.nextInt(items.size())), 100);
            return;
        }
        if (l < 6) {
            items = MobDrops.getUncommonDrops();
            drops.put(items.get(random.nextInt(items.size())), 100);
            return;
        }
        if (l < 8) {
            items = MobDrops.getRareDrops();
            drops.put(items.get(random.nextInt(items.size())), 100);
            return;
        }
        if (l < 10) {
            items = MobDrops.getEpicDrops();
            drops.put(items.get(random.nextInt(items.size())), 100);
            return;
        }
        items = MobDrops.getLegendaryDrops();
        drops.put(items.get(random.nextInt(items.size())), 100);
    }

    private void setAttributes() {
        customAttributes.setMaxHealth(8.2898 * (Math.pow(Math.E, 0.2983 * level)));
        if (legendary)
            customAttributes.setMaxHealth(getMaxHealth() + 30);
        customAttributes.setEquip(Material.IRON_HELMET);
    }

    private void initPathfinder() {
        zombie.bP.a();
        zombie.bQ.a();
        zombie.bP.a(0, new PathfinderGoalZombieAttack(zombie, 1.7, true));
        zombie.bP.a(1, new PathfinderGoalRestrictSun(zombie));
        zombie.bP.a(2, new PathfinderGoalFleeSun(zombie, 1.2));
        zombie.bP.a(3, new PathfinderGoalRandomStrollLand(zombie, 1.0));
        zombie.bP.a(4, new PathfinderGoalRandomLookaround(zombie));
        zombie.bQ.a(0, new PathfinderGoalHurtByTarget(zombie));
        zombie.bQ.a(1, new PathfinderGoalNearestAttackableTarget<>(zombie, EntityHuman.class, true));
        zombie.bQ.a(2, new PathfinderGoalNearestAttackableTarget<>(zombie, EntityPig.class, true));
    }
}
