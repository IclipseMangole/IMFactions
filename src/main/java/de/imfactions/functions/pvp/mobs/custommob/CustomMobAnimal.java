package de.imfactions.functions.pvp.mobs.custommob;

import net.minecraft.world.entity.animal.EntityAnimal;
import org.bukkit.Location;

public class CustomMobAnimal extends CustomMobCreature {

    EntityAnimal entityAnimal;

    public CustomMobAnimal(EntityAnimal type, Location location, String name) {
        super(type, location, name);
        entityAnimal = type;
    }
}
