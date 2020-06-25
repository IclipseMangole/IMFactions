package de.imfactions;

import de.imfactions.commands.Ether;
import de.imfactions.commands.Faction;
import de.imfactions.functions.WorldLoader;
import de.imfactions.listener.JoinListener;
import de.imfactions.listener.QuitListener;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;

public class IMFactions extends JavaPlugin {

    private static IMFactions instance;

    public static IMFactions getInstance() {
        return instance;
    }

    private Data data;

    @Override
    public void onLoad() {
        instance = this;
        if (Bukkit.getWorlds().size() == 0) {
            WorldLoader.loadLobby();
            WorldLoader.loadPlots();
            WorldLoader.loadPVP();
        }
    }

    @Override
    public void onEnable() {
        data = new Data();
        data.getMySQL().connect();
        data.createTables();
        data.Functions();
        registerCommands();
        registerListener();
        updateGamerules();
    }

    @Override
    public void onDisable() {
        data.getUserManager().saveUsers();
        data.getFactionManager().saveFactions();
        data.getFactionUserManager().saveFactionUsers();
        data.getMySQL().close();
    }

    public Data getData() {
        return data;
    }


    public String getServerName() {
        return instance.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getName();
    }

    public void registerCommands() {
        data.getRegistration().register(new Ether(), this);
        data.getRegistration().register(new Faction(), this);
    }

    public void registerListener() {
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(), this);
    }

    public void updateGamerules() {
        Bukkit.getWorlds().forEach(world -> {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        });
    }


}
