package de.imfactions.functions.pvp.mobs;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import org.bukkit.Location;

public class Orc extends CustomMob {

    public Orc(String name, Location location) {
        super(EntityTypes.be, location.getWorld(), name, location);
        setAttributes();
        initPathfinder();
    }

    private void setAttributes() {
        customAttributes.setSize(2.0F, 2.0F);
    }

    @Override
    protected void initPathfinder() {
        this.bP.a(0, new PathfinderGoalRandomStrollLand(this, 1.0));
    }
}
