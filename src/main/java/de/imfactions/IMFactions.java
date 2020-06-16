package de.imfactions;

import de.imfactions.util.Command.CommandRegistration;
import de.imfactions.util.MySQL;
import org.bukkit.plugin.java.JavaPlugin;

public class IMFactions extends JavaPlugin {

    private static IMFactions instance;

    public static IMFactions getInstance() {
        return instance;
    }


    private Data data;
    private CommandRegistration registration;
    private MySQL mysql;

    @Override
    public void onLoad() {
        instance = this;
        data = new Data();
        mysql = new MySQL();
        registration = new CommandRegistration();
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        mysql.close();
    }

    public Data getData() {
        return data;
    }

    public MySQL getMySQL() {
        return mysql;
    }

    public CommandRegistration getRegistration() {
        return registration;
    }


    public String getServerName() {
        return instance.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getName();
    }

    public void registerCommands() {

    }

    public void registerListener() {

    }

    public void createTables() {

    }


}
