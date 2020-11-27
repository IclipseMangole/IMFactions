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
import de.imfactions.functions.items.api.modifiers.ItemModifierValue;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by Iclipse on 24.07.2020
 */
public class LifeSteal implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                if (Item.isItem(itemStack)) {
                    Item item = Item.of(itemStack);
                    HashMap<ItemModifierType, ItemModifierValue> modifiers = item.getItemModifiers();
                    if (modifiers.containsKey(ItemModifierType.LIFESTEAL)) {
                        if(player.getHealth() + event.getDamage() * (double) modifiers.get(ItemModifierType.LIFESTEAL).value > player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()){
                            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                        }else{
                            player.setHealth(player.getHealth() + event.getDamage() * (double) modifiers.get(ItemModifierType.LIFESTEAL).value);
                        }
                    }
                }
            }
        }

    }
}
