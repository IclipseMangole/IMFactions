package de.imfactions.functions.items;

import de.imfactions.IMFactions;
import de.imfactions.functions.items.drops.MobDrops;
import de.imfactions.functions.items.modifiers.listeners.Damage;
import de.imfactions.functions.items.modifiers.listeners.FireAspect;
import de.imfactions.functions.items.modifiers.listeners.LifeSteal;
import de.imfactions.functions.items.modifiers.listeners.PotionEffects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;


public class ItemUtils implements Listener{
    IMFactions factions;
    long lastSwing;

    public ItemUtils(IMFactions factions) {
        this.factions = factions;
        this.lastSwing = System.currentTimeMillis();
        registerListener();
        new Items();
        new MobDrops();
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
        if (FactionItemStack.isItem(event.getItem())) {
            Bukkit.getScheduler().runTaskLater(this.factions, () -> {
                FactionItemStack factionItemStack = FactionItemStack.of(event.getItem());
                factionItemStack.updateLore(event.getItem());
            }, 5L);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.getInventory().setItem(0, new FactionItemStack(FactionItem.get("Excalibur"), 16).toItemStack());
        player.getInventory().setItem(1, new FactionItemStack(FactionItem.get("Yew Arch"), 16).toItemStack());
        player.getInventory().setItem(2, new FactionItemStack(FactionItem.get("Stone Club"), 16).toItemStack());
        player.getInventory().setItem(3, new FactionItemStack(FactionItem.get("Head Cutter"), 16).toItemStack());
        player.getInventory().setItem(4, new FactionItemStack(FactionItem.get("Faramir's Bow"), 16).toItemStack());
        player.getInventory().setItem(5, new FactionItemStack(FactionItem.get("Tim's Crossbow"), 16).toItemStack());
        player.getInventory().setItem(6, new FactionItemStack(FactionItem.get("Dagger"), 16).toItemStack());
        player.getInventory().setItem(7, new FactionItemStack(FactionItem.get("Gimli's Axe"), 16).toItemStack());
    }

}


