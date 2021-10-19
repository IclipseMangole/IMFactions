package de.imfactions.functions.items.api;

import de.imfactions.IMFactions;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemDamageEvent;


public class Listener implements org.bukkit.event.Listener {
    IMFactions factions;

    public Listener(IMFactions factions) {
        this.factions = factions;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.ANVIL || event.getInventory().getType() == InventoryType.ENCHANTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        if (Item.isItem(event.getItem()))
            Bukkit.getScheduler().runTaskLater(this.factions, () -> {
                Item item = Item.of(event.getItem());
                item.updateLore(event.getItem());
            }, 5L);
    }
}

