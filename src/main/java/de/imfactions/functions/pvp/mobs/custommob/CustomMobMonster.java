package de.imfactions.functions.pvp.mobs.custommob;

import net.minecraft.world.entity.monster.EntityMonster;
import org.bukkit.Location;

public class CustomMobMonster extends CustomMobCreature {

    public EntityMonster entityMonster;

    public CustomMobMonster(EntityMonster type, Location location, String name) {
        super(type, location, name);
        entityMonster = type;
    }
}
