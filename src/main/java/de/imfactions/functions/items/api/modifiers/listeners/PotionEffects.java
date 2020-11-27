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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

/**
 * Created by Iclipse on 24.07.2020
 */
public class PotionEffects implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player) {
            ItemStack itemStack = ((Player) event.getDamager()).getInventory().getItemInMainHand();
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                if (Item.isItem(itemStack)) {
                    Item item = Item.of(itemStack);
                    if(event.getEntity() instanceof LivingEntity) {
                        HashMap<ItemModifierType, ItemModifierValue> modifiers = item.getItemModifiers();
                        if (modifiers.containsKey(ItemModifierType.POISON)) {
                            ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, (int) modifiers.get(ItemModifierType.POISON).value * 40, 0));
                        }
                        if (modifiers.containsKey(ItemModifierType.WITHER)) {
                            ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WITHER,  100, (int) modifiers.get(ItemModifierType.WITHER).value));
                        }
                        if (modifiers.containsKey(ItemModifierType.CONFUSION)) {
                            ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,  (int) modifiers.get(ItemModifierType.CONFUSION).value * 20, 1));
                        }
                        if (modifiers.containsKey(ItemModifierType.BLINDNESS)) {
                            ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ((int) modifiers.get(ItemModifierType.BLINDNESS).value * 30), 1));
                        }
                        if (modifiers.containsKey(ItemModifierType.SLOWNESS)) {
                            ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, (int) modifiers.get(ItemModifierType.SLOWNESS).value));
                        }
                    }
                }
            }
        }

    }
}
