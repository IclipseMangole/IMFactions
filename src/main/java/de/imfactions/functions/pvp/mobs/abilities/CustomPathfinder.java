package de.imfactions.functions.pvp.mobs.abilities;

import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderAbstract;

public class CustomPathfinder extends Pathfinder {

    private EntityMonster entityMonster;

    public CustomPathfinder(PathfinderAbstract var0, int var1, EntityMonster entityMonster) {
        super(var0, var1);
        this.entityMonster = entityMonster;
    }


}
