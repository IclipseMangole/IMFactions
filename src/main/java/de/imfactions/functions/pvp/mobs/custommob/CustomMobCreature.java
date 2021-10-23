package de.imfactions.functions.pvp.mobs.custommob;

import net.minecraft.world.entity.EntityCreature;
import org.bukkit.Location;

public class CustomMobCreature extends CustomMobInsentient {

    EntityCreature entityCreature;

    public CustomMobCreature(EntityCreature type, Location location, String name) {
        super(type, location, name);
        entityCreature = type;
    }
}
