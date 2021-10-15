package de.imfactions.functions.pvp.mobs;

import de.imfactions.functions.pvp.mobs.abilities.CustomAttributes;
import de.imfactions.functions.pvp.mobs.abilities.pathfinders.CustomPathfinder;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityMonster;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CustomMob extends EntityMonster {

    public String name;
    public CustomAttributes customAttributes = new CustomAttributes(this);
    public CustomPathfinder customPathfinder = new CustomPathfinder(this);

    public CustomMob(EntityTypes<? extends EntityMonster> type, World world, String name, Location location) {
        super(type, ((CraftWorld) world).getHandle());
        this.name = name;
        this.setCustomName(new ChatComponentText(name));
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.setInvisible(false);
        net.minecraft.world.level.World worldServer = ((CraftWorld) world).getHandle();
        worldServer.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public void setName() {
        float maxHealth = getMaxHealth();
        float health = getHealth();
    }
}
