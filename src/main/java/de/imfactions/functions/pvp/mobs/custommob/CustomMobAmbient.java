package de.imfactions.functions.pvp.mobs.custommob;

import net.minecraft.world.entity.ambient.EntityAmbient;
import org.bukkit.Location;

public class CustomMobAmbient extends CustomMobInsentient {

    public EntityAmbient entityAmbient;

    public CustomMobAmbient(EntityAmbient type, Location location, String name) {
        super(type, location, name);
        entityAmbient = type;
    }
}
