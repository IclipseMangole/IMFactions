package de.imfactions.functions.items.modifiers.listeners;

import de.imfactions.functions.items.FactionItemStack;
import de.imfactions.functions.items.modifiers.ItemModifierType;
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
    public void onShoot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof AbstractArrow) {
            AbstractArrow projectile = (AbstractArrow) event.getEntity();
            if (projectile.getShooter() instanceof Player) {
                Player player = (Player) projectile.getShooter();
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if (itemStack.getType() != Material.AIR && FactionItemStack.isItem(itemStack)) {
                    FactionItemStack factionItemStack = FactionItemStack.of(itemStack);
                    if (factionItemStack.getItemModifiers().containsKey(ItemModifierType.DAMAGE)) {
                        projectile.setDamage(projectile.getDamage() + (Double) factionItemStack.getItemModifiers().get(ItemModifierType.DAMAGE).value * 2.0D);
                        projectile.setBounce(true);
                    }
                }
            }
        }
    }
}
