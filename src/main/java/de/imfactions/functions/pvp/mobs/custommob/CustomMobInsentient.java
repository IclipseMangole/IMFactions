package de.imfactions.functions.pvp.mobs.custommob;

import de.imfactions.functions.pvp.mobs.attributes.CustomAttributes;
import de.imfactions.functions.pvp.mobs.attributes.CustomMobLevel;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CustomMobInsentient {

    public String name;
    public int level;
    public boolean legendary;
    public HashMap<ItemStack, Integer> drops = new HashMap<>();
    public CustomAttributes customAttributes;
    public CustomMobLevel customMobLevel;
    public EntityInsentient entityInsentient;

    public CustomMobInsentient(EntityInsentient type, Location location, String name) {
        entityInsentient = type;
        customMobLevel = new CustomMobLevel(this);
        customAttributes = new CustomAttributes(this);
        entityInsentient.setInvisible(false);
        setLocation(location);
        this.name = name;
    }

    public void setName(float health) {
        int h = (int) Math.ceil(health);
        if (h <= 0)
            h = 0;
        int maxHealth = (int) Math.ceil(entityInsentient.getMaxHealth());
        String levelString = customMobLevel.getLevelString();

        ChatComponentText fullName = new ChatComponentText(name + levelString + " " + ChatColor.RED + h + "/" + maxHealth);
        entityInsentient.setCustomName(fullName);
        entityInsentient.setCustomNameVisible(true);
    }

    public void setLocation(Location location) {
        entityInsentient.setPosition(location.getX(), location.getY(), location.getZ());
    }

    public void kill() {
        entityInsentient.killEntity();
    }

    public Entity getEntity() {
        return entityInsentient.getBukkitEntity();
    }

    public float getHealth() {
        return entityInsentient.getHealth();
    }

    public float getMaxHealth() {
        return entityInsentient.getMaxHealth();
    }
}
