package de.imfactions.functions.pvp.mobs;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class Orc extends CustomMob {


    public Orc(Location location) {
        super(EntityTypes.be, location, ChatColor.DARK_GREEN + "Orc");
        level = customMobLevel.getRandomLevel();
        legendary = customMobLevel.getRandomLegendary();
        setAttributes();
        initPathfinder();
        this.setName(getHealth());
    }

    private void setAttributes() {
        customAttributes.setMaxHealth(8.2898 * (Math.pow(Math.E, 0.2983 * level)));
        if (legendary)
            customAttributes.setMaxHealth(getMaxHealth() + 30);
    }

    @Override
    protected void initPathfinder() {
        this.bP.a(0, new PathfinderGoalRandomStrollLand(this, 1.0));
        // this.bP.a(1, new PathfinderGoal);
    }
}
