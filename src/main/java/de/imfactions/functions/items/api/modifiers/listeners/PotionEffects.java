 package de.imfactions.functions.items.api.modifiers.listeners;
 
 import de.imfactions.functions.items.api.Item;
 import de.imfactions.functions.items.api.modifiers.ItemModifierType;
 import de.imfactions.functions.items.api.modifiers.ItemModifierValue;
 import java.util.HashMap;
 import org.bukkit.Material;
 import org.bukkit.entity.LivingEntity;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.entity.EntityDamageByEntityEvent;
 import org.bukkit.inventory.ItemStack;
 import org.bukkit.potion.PotionEffect;
 import org.bukkit.potion.PotionEffectType;
 
 
 
 
 
 
 
 
 
 
 
 
 public class PotionEffects
   implements Listener
 {
   @EventHandler
   public void onDamage(EntityDamageByEntityEvent event) {     if (event.getDamager() instanceof Player) {       ItemStack itemStack = ((Player)event.getDamager()).getInventory().getItemInMainHand();       if (itemStack != null && itemStack.getType() != Material.AIR &&
Item.isItem(itemStack)) {         Item item = Item.of(itemStack);         if (event.getEntity() instanceof LivingEntity) {           HashMap<ItemModifierType, ItemModifierValue> modifiers = item.getItemModifiers();           if (modifiers.containsKey(ItemModifierType.POISON)) {             ((LivingEntity)event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, ((Integer)((ItemModifierValue)modifiers.get(ItemModifierType.POISON)).value).intValue() * 40, 0));
           }           if (modifiers.containsKey(ItemModifierType.WITHER)) {             ((LivingEntity)event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, ((Integer)((ItemModifierValue)modifiers.get(ItemModifierType.WITHER)).value).intValue()));
           }           if (modifiers.containsKey(ItemModifierType.CONFUSION)) {
             ((LivingEntity)event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, ((Integer)((ItemModifierValue)modifiers.get(ItemModifierType.CONFUSION)).value).intValue() * 20, 1));
           }
           if (modifiers.containsKey(ItemModifierType.BLINDNESS)) {
             ((LivingEntity)event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ((Integer)((ItemModifierValue)modifiers.get(ItemModifierType.BLINDNESS)).value).intValue() * 30, 1));
           }
          if (modifiers.containsKey(ItemModifierType.SLOWNESS))
             ((LivingEntity)event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, ((Integer)((ItemModifierValue)modifiers.get(ItemModifierType.SLOWNESS)).value).intValue()));
         } 
       } 
     } 
   }
 }


