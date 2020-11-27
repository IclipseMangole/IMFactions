package de.imfactions.functions.items.api;

//  ╔══════════════════════════════════════╗
//  ║      ___       ___                   ║
//  ║     /  /___   /  /(_)____ ____  __   ║
//  ║    /  // __/ /  // // ) // ___// )\  ║                                  
//  ║   /  // /__ /  // //  _/(__  )/ __/  ║                                                                         
//  ║  /__/ \___//__//_//_/  /____/ \___/  ║                                              
//  ╚══════════════════════════════════════╝

import de.imfactions.IMFactions;
import de.imfactions.functions.items.api.modifiers.listeners.Damage;
import de.imfactions.functions.items.api.modifiers.listeners.FireAspect;
import de.imfactions.functions.items.api.modifiers.listeners.LifeSteal;
import de.imfactions.functions.items.api.modifiers.listeners.PotionEffects;
import org.bukkit.Bukkit;

/**
 * Created by Iclipse on 17.07.2020
 */
public class ItemAPI {
    IMFactions factions;
    public ItemAPI(IMFactions factions){
        this.factions = factions;
        registerListener();
    }

    public void registerListener(){
        Bukkit.getPluginManager().registerEvents(new Listener(factions), factions);
        Bukkit.getPluginManager().registerEvents(new Damage(), factions);
        Bukkit.getPluginManager().registerEvents(new FireAspect(), factions);
        Bukkit.getPluginManager().registerEvents(new PotionEffects(), factions);
        Bukkit.getPluginManager().registerEvents(new LifeSteal(), factions);
    }
}
