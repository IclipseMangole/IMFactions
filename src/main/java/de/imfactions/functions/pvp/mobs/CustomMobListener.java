package de.imfactions.functions.pvp.mobs;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Random;

public class CustomMobListener implements Listener {

    private IMFactions imFactions;
    private Data data;

    public CustomMobListener(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
    }

    @EventHandler
    public void onSpawnEgg(CreatureSpawnEvent event) {
        if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG))
            return;
        if (!(event.getEntity() instanceof Zombie)) {
            new Orc(event.getLocation());
            event.setCancelled(true);
        }
        if (!(event.getEntity() instanceof Skeleton)) {
            new Undead(event.getLocation());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onNameChange(EntityDamageEvent event) {
        if (!(((CraftEntity) event.getEntity()).getHandle() instanceof CustomMob))
            return;
        CustomMob customMob = (CustomMob) ((CraftEntity) event.getEntity()).getHandle();
        customMob.setName((float) (customMob.getHealth() - event.getDamage()));
    }

    @EventHandler
    public void onDropLoot(EntityDeathEvent event) {
        if (!(((CraftEntity) event.getEntity()).getHandle() instanceof CustomMob))
            return;
        CustomMob customMob = (CustomMob) ((CraftEntity) event.getEntity()).getHandle();
        World world = event.getEntity().getWorld();
        Random random = new Random();
        for (Map.Entry<ItemStack, Integer> drops : customMob.drops.entrySet()) {
            if (random.nextInt(100) < drops.getValue())
                world.dropItemNaturally(event.getEntity().getLocation(), drops.getKey());
        }
    }
}
