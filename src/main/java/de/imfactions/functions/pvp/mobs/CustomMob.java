package de.imfactions.functions.pvp.mobs;

import de.imfactions.functions.pvp.mobs.abilities.CustomAttributes;
import de.imfactions.functions.pvp.mobs.abilities.CustomPathfinder;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityMonster;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

public class CustomMob extends EntityMonster {

    public String name;
    public CustomAttributes customAttributes = new CustomAttributes(this);
    public CustomPathfinder customPathfinder;

    public CustomMob(EntityTypes<? extends EntityMonster> type, World world, String name, Location location) {
        super(type, ((CraftWorld) world).getHandle());
        this.name = name;
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
}
