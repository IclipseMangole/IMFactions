package de.imfactions.functions.pvp.mobs.custommob;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import org.bukkit.World;
import org.bukkit.entity.Bat;
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

    private final IMFactions imFactions;
    private final Data data;
    private final CustomMobUtil customMobUtil;

    public CustomMobListener(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        customMobUtil = data.getCustomMobUtil();
    }

    @EventHandler
    public void onSpawnEgg(CreatureSpawnEvent event) {
        if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG))
            return;
        if (event.getEntity() instanceof Zombie) {
            event.setCancelled(true);
            customMobUtil.createOrc(event.getLocation());
            return;
        }
        if (event.getEntity() instanceof Skeleton) {
            event.setCancelled(true);
            customMobUtil.createUndead(event.getLocation());
            return;
        }
        if (event.getEntity() instanceof Bat) {
            event.setCancelled(true);
            customMobUtil.createWarBat(event.getLocation());
        }
    }

    @EventHandler
    public void onNameChange(EntityDamageEvent event) {
        if (!CustomMobUtil.isEntityCustom(event.getEntity()))
            return;
        CustomMobInsentient customMobInsentient = CustomMobUtil.getCustomMob(event.getEntity());
        customMobInsentient.setName((float) (customMobInsentient.getHealth() - event.getDamage()));
    }

    @EventHandler
    public void onDropLoot(EntityDeathEvent event) {
        if (!CustomMobUtil.isEntityCustom(event.getEntity()))
            return;
        event.getDrops().clear();
        CustomMobInsentient customMobInsentient = CustomMobUtil.getCustomMob(event.getEntity());
        World world = event.getEntity().getWorld();
        Random random = new Random();
        for (Map.Entry<ItemStack, Integer> drops : customMobInsentient.drops.entrySet()) {
            if (random.nextInt(100) < drops.getValue())
                world.dropItemNaturally(event.getEntity().getLocation(), drops.getKey());
        }
    }
}
