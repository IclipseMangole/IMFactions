package de.imfactions.functions.items.api.modifiers.listeners;

import de.imfactions.functions.items.api.Item;
import de.imfactions.functions.items.api.modifiers.ItemModifierType;
import de.imfactions.functions.items.api.modifiers.ItemModifierValue;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;


public class LifeSteal implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack.getType() != Material.AIR &&
                    Item.isItem(itemStack)) {
                Item item = Item.of(itemStack);
                HashMap<ItemModifierType, ItemModifierValue> modifiers = item.getItemModifiers();
                if (modifiers.containsKey(ItemModifierType.LIFESTEAL))
                    if (player.getHealth() + event.getDamage() * ((Double) ((ItemModifierValue) modifiers.get(ItemModifierType.LIFESTEAL)).value).doubleValue() > player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
                        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                    } else {
                        player.setHealth(player.getHealth() + event.getDamage() * ((Double) ((ItemModifierValue) modifiers.get(ItemModifierType.LIFESTEAL)).value).doubleValue());
                    }
            }
        }
    }
}
