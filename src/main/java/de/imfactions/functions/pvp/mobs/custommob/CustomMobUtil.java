package de.imfactions.functions.pvp.mobs.custommob;

import de.imfactions.functions.pvp.mobs.Orc;
import de.imfactions.functions.pvp.mobs.Undead;
import de.imfactions.functions.pvp.mobs.WarBat;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;

public class CustomMobUtil {

    private static final ArrayList<CustomMobInsentient> customMobs = new ArrayList<>();

    public CustomMobUtil() {
    }

    public void createOrc(Location location) {
        customMobs.add(new Orc(location));
    }

    public void createUndead(Location location) {
        customMobs.add(new Undead(location));
    }

    public void createWarBat(Location location) {
        customMobs.add(new WarBat(location));
    }

    public static boolean isEntityCustom(Entity entity) {
        for (CustomMobInsentient customMobInsentient : customMobs) {
            if (customMobInsentient.getEntity().equals(entity))
                return true;
        }
        return false;
    }

    public static CustomMobInsentient getCustomMob(Entity entity) {
        for (CustomMobInsentient customMobInsentient : customMobs) {
            if (customMobInsentient.getEntity().equals(entity))
                return customMobInsentient;
        }
        return null;
    }

    public void deleteCustomMobs() {
        ArrayList<CustomMobInsentient> customMobInsentients = new ArrayList<>();
        customMobInsentients.addAll(customMobs);
        for (CustomMobInsentient customMobInsentient : customMobInsentients)
            deleteCustomMob(customMobInsentient);
    }

    private void deleteCustomMob(CustomMobInsentient customMob) {
        customMob.kill();
        customMobs.remove(customMob);
    }
}
