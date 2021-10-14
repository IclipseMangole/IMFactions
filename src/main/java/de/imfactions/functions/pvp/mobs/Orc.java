package de.imfactions.functions.pvp.mobs;

import net.minecraft.world.entity.EntityTypes;
import org.bukkit.Location;

public class Orc extends CustomMob {

    public Orc(String name, Location location) {
        super(EntityTypes.be, location.getWorld(), name, location);
        setAttributes();
    }

    private void setAttributes() {
        customAttributes.setSize(10.0F, 10.0F);
    }
}
