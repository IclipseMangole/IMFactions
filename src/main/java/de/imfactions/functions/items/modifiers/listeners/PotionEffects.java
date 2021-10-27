package de.imfactions.functions.items.modifiers.listeners;

import de.imfactions.functions.items.FactionItemStack;
import de.imfactions.functions.items.modifiers.ItemModifierType;
import de.imfactions.functions.items.modifiers.ItemModifierValue;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;


public class PotionEffects
        implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            ItemStack itemStack = ((Player) event.getDamager()).getInventory().getItemInMainHand();
            if (itemStack.getType() != Material.AIR && FactionItemStack.isItem(itemStack)) {
                FactionItemStack factionItemStack = FactionItemStack.of(itemStack);
                if (event.getEntity() instanceof LivingEntity) {
                    HashMap<ItemModifierType, ItemModifierValue> modifiers = factionItemStack.getItemModifiers();
                    if (modifiers.containsKey(ItemModifierType.POISON)) {
                        ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, ((Integer) modifiers.get(ItemModifierType.POISON).value).intValue() * 40, 0));
                    }
                    if (modifiers.containsKey(ItemModifierType.WITHER)) {
                        ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, ((Integer) modifiers.get(ItemModifierType.WITHER).value).intValue()));
                    }
                    if (modifiers.containsKey(ItemModifierType.CONFUSION)) {
                        ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, ((Integer) modifiers.get(ItemModifierType.CONFUSION).value).intValue() * 20, 1));
                    }
                    if (modifiers.containsKey(ItemModifierType.BLINDNESS)) {
                        ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ((Integer) modifiers.get(ItemModifierType.BLINDNESS).value).intValue() * 30, 1));
                    }
                    if (modifiers.containsKey(ItemModifierType.SLOWNESS))
                        ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, ((Integer) modifiers.get(ItemModifierType.SLOWNESS).value).intValue()));
                }
            }
        }
    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Arrow))
            return;

        Arrow arrow = (Arrow) event.getEntity();
        if (!(arrow.getShooter() instanceof Player))
            return;

        Player player = (Player) arrow.getShooter();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR || !FactionItemStack.isItem(itemStack))
            return;

        FactionItemStack factionItemStack = FactionItemStack.of(itemStack);
        HashMap<ItemModifierType, ItemModifierValue> modifiers = factionItemStack.getItemModifiers();

        if (modifiers.isEmpty())
            return;


        if (modifiers.containsKey(ItemModifierType.POISON)) {
            arrow.addCustomEffect(new PotionEffect(PotionEffectType.POISON, (Integer) modifiers.get(ItemModifierType.POISON).value * 40, 0), true);
        }
        if (modifiers.containsKey(ItemModifierType.WITHER)) {
            arrow.addCustomEffect(new PotionEffect(PotionEffectType.WITHER, 100, (Integer) modifiers.get(ItemModifierType.WITHER).value), true);
        }
        if (modifiers.containsKey(ItemModifierType.CONFUSION)) {
            arrow.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, (Integer) modifiers.get(ItemModifierType.CONFUSION).value * 20, 1), true);
        }
        if (modifiers.containsKey(ItemModifierType.BLINDNESS)) {
            arrow.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, (Integer) modifiers.get(ItemModifierType.BLINDNESS).value * 30, 1), true);
        }
        if (modifiers.containsKey(ItemModifierType.SLOWNESS))
            arrow.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 40, (Integer) modifiers.get(ItemModifierType.SLOWNESS).value), true);

    }
}


