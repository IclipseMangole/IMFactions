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

    private Data data;
    private WorldLoader worldLoader;

    @Override
    public void onLoad() {
        System.out.println(getFile().getAbsolutePath());
        System.out.println(Bukkit.getWorldContainer().getAbsolutePath());
        System.out.println(getDataFolder().getAbsolutePath());
        worldLoader = new WorldLoader(this);
        if (Bukkit.getWorlds().size() == 0) {
            worldLoader.loadLobby();
            worldLoader.loadPVP();
            worldLoader.loadPlots();
        }
    }

    @Override
    public void onEnable() {
        data = new Data(this);
        data.getMySQL().connect();
        data.createTables();
        data.loadWorlds();
        data.startScheduler();
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
        worldLoader.savePlots();
    }

    public Data getData() {
        return data;
    }


    public String getServerName() {
        return this.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getName();
    }

    public void registerCommands() {
        data.getRegistration().register(new Ether(this), this);
        data.getRegistration().register(new Faction(this), this);
        data.getRegistration().register(new de.imfactions.commands.World(this), this);
    }

    public void registerListener() {
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(this), this);
    }

    public void updateGamerules() {
        Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false));
    }


}
