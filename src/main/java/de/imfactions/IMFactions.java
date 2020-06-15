package de.imfactions;

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

    }

    @Override
    public void onDisable() {

    }

    public Data getData() {
        return data;
    }
}
