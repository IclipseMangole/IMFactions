package de.imfactions.functions.items.api.modifiers.listeners;

import de.imfactions.functions.items.api.Item;
import de.imfactions.functions.items.api.modifiers.ItemModifierType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;


public class FireAspect
        implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            ItemStack itemStack = ((Player) event.getDamager()).getInventory().getItemInMainHand();
            if (itemStack.getType() != Material.AIR &&
                    Item.isItem(itemStack)) {
                Item item = Item.of(itemStack);
                if (item.getItemModifiers().containsKey(ItemModifierType.FIRE_ASPECT)) {
                    int ticks = (Integer) item.getItemModifiers().get(ItemModifierType.FIRE_ASPECT).value * 40;
                    if (event.getEntity().getFireTicks() < ticks)
                        event.getEntity().setFireTicks(ticks);
                }
            }
        }
    }
}


