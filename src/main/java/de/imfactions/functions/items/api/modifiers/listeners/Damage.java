package de.imfactions.functions.items.api.modifiers.listeners;

//  ╔══════════════════════════════════════╗
//  ║      ___       ___                   ║
//  ║     /  /___   /  /(_)____ ____  __   ║
//  ║    /  // __/ /  // // ) // ___// )\  ║                                  
//  ║   /  // /__ /  // //  _/(__  )/ __/  ║                                                                         
//  ║  /__/ \___//__//_//_/  /____/ \___/  ║                                              
//  ╚══════════════════════════════════════╝

import de.imfactions.functions.items.api.Item;
import de.imfactions.functions.items.api.modifiers.ItemModifierType;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Iclipse on 17.07.2020
 */
public class Damage implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamage(EntityDamageByEntityEvent event) {
        System.out.println("EntityDamage by Entity");
        if (event.getDamager() instanceof Player) {
            ItemStack itemStack = ((Player) event.getDamager()).getInventory().getItemInMainHand();
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                if (Item.isItem(itemStack)) {
                    Item item = Item.of(itemStack);
                    if (item.getItemModifiers().containsKey(ItemModifierType.DAMAGE)) {
                        event.setDamage(event.getDamage() + (double) item.getItemModifiers().get(ItemModifierType.DAMAGE).value * 2);
                    }
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onShoot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof AbstractArrow) {
            AbstractArrow projectile = (AbstractArrow) event.getEntity();
            if (projectile.getShooter() instanceof Player) {
                Player player = (Player) projectile.getShooter();

                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    if (Item.isItem(itemStack)) {
                        Item item = Item.of(itemStack);
                        if (item.getItemModifiers().containsKey(ItemModifierType.DAMAGE)) {
                            projectile.setDamage(projectile.getDamage() + (double) item.getItemModifiers().get(ItemModifierType.DAMAGE).value * 2);
                            projectile.setBounce(true);
                        }
                    }
                }
            }
        }
    }
}
