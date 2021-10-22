package de.imfactions.functions.pvp.mobs;

import de.imfactions.functions.pvp.mobs.attributes.CustomAttributes;
import de.imfactions.functions.pvp.mobs.attributes.CustomMobLevel;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CustomMob extends EntityCreature {

    public String name;
    public int level;
    public boolean legendary;
    public HashMap<ItemStack, Integer> drops = new HashMap<>();
    public CustomAttributes customAttributes = new CustomAttributes(this);
    public CustomMobLevel customMobLevel = new CustomMobLevel(this);

    public CustomMob(EntityTypes<? extends EntityCreature> type, Location location, String name) {
        super(type, ((CraftWorld) location.getWorld()).getHandle());
        this.setInvisible(false);
        setLocation(location);
        this.name = name;
    }

    public void setName(float health) {
        int h = (int) health;
        if (h < 0)
            h = 0;
        int maxHealth = (int) getMaxHealth();
        String levelString = customMobLevel.getLevelString();

        ChatComponentText fullName = new ChatComponentText(name + levelString + " " + ChatColor.RED + h + "/" + maxHealth);
        this.setCustomName(fullName);
        this.setCustomNameVisible(true);
    }

    public void setLocation(Location location) {
        this.setPosition(location.getX(), location.getY(), location.getZ());
    }
}
