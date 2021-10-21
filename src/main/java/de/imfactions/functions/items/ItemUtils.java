package de.imfactions.functions.items;

import de.imfactions.IMFactions;
import de.imfactions.functions.items.modifiers.listeners.Damage;
import de.imfactions.functions.items.modifiers.listeners.FireAspect;
import de.imfactions.functions.items.modifiers.listeners.LifeSteal;
import de.imfactions.functions.items.modifiers.listeners.PotionEffects;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.plugin.Plugin;


public class ItemUtils implements Listener{
    IMFactions factions;


    public ItemUtils(IMFactions factions) {
        this.factions = factions;
        registerListener();
        new Items();
    }


    public void registerListener() {
        Bukkit.getPluginManager().registerEvents(this, factions);
        Bukkit.getPluginManager().registerEvents(new Damage(), factions);
        Bukkit.getPluginManager().registerEvents(new FireAspect(), factions);
        Bukkit.getPluginManager().registerEvents(new PotionEffects(), factions);
        Bukkit.getPluginManager().registerEvents(new LifeSteal(), factions);
    }


    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.ANVIL || event.getInventory().getType() == InventoryType.ENCHANTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        if (FactionItemStack.isItem(event.getItem()))
            Bukkit.getScheduler().runTaskLater(this.factions, () -> {
                FactionItemStack factionItemStack = FactionItemStack.of(event.getItem());
                factionItemStack.updateLore(event.getItem());
            }, 5L);
    }

}


