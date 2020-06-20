package de.imfactions;

import de.imfactions.commands.Ether;
import de.imfactions.commands.Faction;
import de.imfactions.listener.JoinListener;
import de.imfactions.listener.QuitListener;
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
        data = new Data();
    }

    @Override
    public void onEnable() {
        registerCommands();
        registerListener();
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
        data.getRegistration().register(new JoinListener(), this);
        data.getRegistration().register(new QuitListener(), this);
    }


}
