package de.imfactions.functions.items.api;

import de.imfactions.IMFactions;
import de.imfactions.functions.items.api.modifiers.listeners.Damage;
import de.imfactions.functions.items.api.modifiers.listeners.FireAspect;
import de.imfactions.functions.items.api.modifiers.listeners.LifeSteal;
import de.imfactions.functions.items.api.modifiers.listeners.PotionEffects;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;


public class ItemAPI {
    IMFactions factions;


    public ItemAPI(IMFactions factions) {
        this.factions = factions;
        registerListener();

    }


    public void registerListener() {
        Bukkit.getPluginManager().registerEvents(new de.imfactions.functions.items.api.Listener(this.factions), (Plugin) this.factions);
        Bukkit.getPluginManager().registerEvents((Listener) new Damage(), (Plugin) this.factions);
        Bukkit.getPluginManager().registerEvents((Listener) new FireAspect(), (Plugin) this.factions);
        Bukkit.getPluginManager().registerEvents((Listener) new PotionEffects(), (Plugin) this.factions);
        Bukkit.getPluginManager().registerEvents((Listener) new LifeSteal(), (Plugin) this.factions);

    }

}


