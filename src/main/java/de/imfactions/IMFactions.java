package de.imfactions;

import de.imfactions.commands.Ether;
import de.imfactions.commands.Faction;
import de.imfactions.listener.JoinListener;
import de.imfactions.listener.QuitListener;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;

public class IMFactions extends JavaPlugin {

    private Data data;

    @Override
    public void onLoad() {
        data = new Data(this);
        if (Bukkit.getWorlds().size() == 0) {
            data.getWorldLoader().loadLobby();
        }
    }

    @Override
    public void onEnable() {
        data.getMySQL().connect();
        data.createTables();
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
        return this.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getName();
    }

    public void registerCommands() {
        data.getRegistration().register(new Ether(this), this);
        data.getRegistration().register(new Faction(this), this);
    }

    public void registerListener() {
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(this), this);
    }

    public void updateGamerules() {
        Bukkit.getWorlds().forEach(world -> {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        });
    }


}
