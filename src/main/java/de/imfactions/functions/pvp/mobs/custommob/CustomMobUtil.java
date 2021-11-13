package de.imfactions.functions.pvp.mobs.custommob;

import de.imfactions.IMFactions;
import de.imfactions.functions.pvp.PVPZone;
import de.imfactions.functions.pvp.mobs.Orc;
import de.imfactions.functions.pvp.mobs.Undead;
import de.imfactions.functions.pvp.mobs.WarBat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CustomMobUtil {

    private static final HashMap<CustomMobInsentient, PVPZone> customMobs = new HashMap<>();
    private static final Integer MAX_MOBS_PER_ZONE = 50;

    public CustomMobUtil(IMFactions imFactions) {
        Bukkit.getScheduler().runTaskTimer(imFactions, new Runnable() {
            @Override
            public void run() {
                for (PVPZone pvpZone : PVPZone.getZonesWithPlayers()) {
                    if (getMobsInZoneAmount(pvpZone) >= MAX_MOBS_PER_ZONE)
                        continue;
                    Random random = new Random();
                    for (int i = 0; i < 10 + random.nextInt(6); i++) {
                        createRandomMob(PVPZone.getRandomLocation(pvpZone), pvpZone);
                    }
                }
            }
        }, 20 * 10, 20 * 10);
    }

    public void createOrc(Location location, PVPZone pvpZone) {
        customMobs.put(new Orc(location, pvpZone), pvpZone);
    }

    public void createUndead(Location location, PVPZone pvpZone) {
        customMobs.put(new Undead(location, pvpZone), pvpZone);
    }

    public void createWarBat(Location location, PVPZone pvpZone) {
        customMobs.put(new WarBat(location, pvpZone), pvpZone);
    }

    public void createRandomMob(Location location, PVPZone pvpZone) {
        Random random = new Random();
        int r = random.nextInt(3);
        switch (r) {
            case 0:
                createOrc(location, pvpZone);
                break;
            case 1:
                createUndead(location, pvpZone);
                break;
            default:
                createWarBat(location, pvpZone);
                break;
        }
    }

    public static boolean isEntityCustom(Entity entity) {
        for (CustomMobInsentient customMobInsentient : customMobs.keySet()) {
            if (customMobInsentient.getEntity().equals(entity))
                return true;
        }
        return false;
    }

    private ArrayList<CustomMobInsentient> getMobsInZone(PVPZone pvpZone) {
        ArrayList<CustomMobInsentient> mobs = new ArrayList<>();
        for (CustomMobInsentient customMobInsentient : customMobs.keySet()) {
            if (customMobs.get(customMobInsentient) == pvpZone)
                mobs.add(customMobInsentient);
        }
        return mobs;
    }

    private int getMobsInZoneAmount(PVPZone pvpZone) {
        return getMobsInZone(pvpZone).size();
    }


    public static CustomMobInsentient getCustomMob(Entity entity) {
        for (CustomMobInsentient customMobInsentient : customMobs.keySet()) {
            if (customMobInsentient.getEntity().equals(entity))
                return customMobInsentient;
        }
        return null;
    }

    public void deleteCustomMobs() {
        ArrayList<CustomMobInsentient> customMobInsentients = new ArrayList<>(customMobs.keySet());
        for (CustomMobInsentient customMobInsentient : customMobInsentients)
            deleteCustomMob(customMobInsentient);
    }

    private void deleteCustomMob(CustomMobInsentient customMob) {
        customMob.kill();
        customMobs.remove(customMob);
    }
}
