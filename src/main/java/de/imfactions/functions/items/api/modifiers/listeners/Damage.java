package de.imfactions.functions.items.api.modifiers.listeners;

import de.imfactions.functions.items.api.Item;
import de.imfactions.functions.items.api.modifiers.ItemModifierType;
import de.imfactions.functions.items.api.modifiers.ItemModifierValue;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;


public class Damage implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamage(EntityDamageByEntityEvent event) {
        System.out.println("EntityDamage by Entity");
        if (event.getDamager() instanceof Player) {
            ItemStack itemStack = ((Player) event.getDamager()).getInventory().getItemInMainHand();
            if (itemStack.getType() != Material.AIR && Item.isItem(itemStack)) {
                Item item = Item.of(itemStack);
                if (item.getItemModifiers().containsKey(ItemModifierType.DAMAGE)) {
                    event.setDamage(event.getDamage() + (Double) item.getItemModifiers().get(ItemModifierType.DAMAGE).value * 2.0D);
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
                if (itemStack.getType() != Material.AIR && Item.isItem(itemStack)) {
                    Item item = Item.of(itemStack);
                    if (item.getItemModifiers().containsKey(ItemModifierType.DAMAGE)) {
                        projectile.setDamage(projectile.getDamage() + (Double) item.getItemModifiers().get(ItemModifierType.DAMAGE).value * 2.0D);
                        projectile.setBounce(true);
                    }
                }
            }
        }
    }
}
