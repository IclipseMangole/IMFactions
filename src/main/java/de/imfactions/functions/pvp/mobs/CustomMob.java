package de.imfactions.functions.pvp.mobs;

import de.imfactions.functions.pvp.mobs.abilities.CustomAttributes;
import de.imfactions.functions.pvp.mobs.abilities.pathfinders.CustomPathfinder;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityMonster;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CustomMob extends EntityMonster {

    public String name;
    public int level;
    public boolean legendary;
    public CustomAttributes customAttributes = new CustomAttributes(this);
    public CustomPathfinder customPathfinder = new CustomPathfinder(this);
    public CustomMobLevel customMobLevel = new CustomMobLevel(this);

    public CustomMob(EntityTypes<? extends EntityMonster> type, Location location, String name) {
        super(type, ((CraftWorld) location.getWorld()).getHandle());
        this.setInvisible(false);
        setLocation(location);
        this.name = name;
        net.minecraft.world.level.World worldServer = ((CraftWorld) location.getWorld()).getHandle();
        worldServer.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public void setName(float health) {
        float h = health;
        if (h < 0)
            h = 0;
        float maxHealth = getMaxHealth();
        String levelString = customMobLevel.getLevelString();

        ChatComponentText fullName = new ChatComponentText(name + levelString + ChatColor.RED + h + "/" + maxHealth);
        this.setCustomName(fullName);
        this.setCustomNameVisible(true);
    }

    public void setLocation(Location location) {
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }
}
