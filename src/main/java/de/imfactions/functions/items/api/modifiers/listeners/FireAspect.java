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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Iclipse on 21.07.2020
 */
public class FireAspect implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player) {
            ItemStack itemStack = ((Player) event.getDamager()).getInventory().getItemInMainHand();
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                if (Item.isItem(itemStack)) {
                    Item item = Item.of(itemStack);
                    if (item.getItemModifiers().containsKey(ItemModifierType.FIRE_ASPECT)) {
                        int ticks = (int) item.getItemModifiers().get(ItemModifierType.FIRE_ASPECT).value * 40;
                        if(event.getEntity().getFireTicks() < ticks){
                            event.getEntity().setFireTicks(ticks);
                        }
                    }
                }
            }
        }

    }
}
