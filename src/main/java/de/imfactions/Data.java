package de.imfactions;

import de.imfactions.database.UserManager;
import de.imfactions.database.UserSettingsTable;
import de.imfactions.functions.UserSettingsManager;
import de.imfactions.util.Command.CommandRegistration;
import de.imfactions.util.MySQL;

public class Data {

    private final CommandRegistration registration;
    private final MySQL mysql;
    private final UserSettingsManager userSettingsManager;

    //Tables
    private UserManager userManager;
    private UserSettingsTable userSettingsTable;


    public Data() {
        registration = new CommandRegistration();
        mysql = new MySQL();
        userSettingsManager = new UserSettingsManager();

        //Tables
        userManager = new UserManager();
        userSettingsTable = new UserSettingsTable();
    }


    public String getSymbol() {
        String symbol = "§8 » §7";
        return symbol;
    }

    public String getPrefix() {
        String prefix = "§5IM§fFactions" + getSymbol();
        return prefix;
    }

    public String getNoperm() {
        String noperm = "§4No permissions!";
        return noperm;
    }

    public String getNoConsole() {
        String noConsole = "§4No Console!";
        return noConsole;
    }

    public String getConsoleOnly() {
        String consoleOnly = "§4Console only!";
        return consoleOnly;
    }


    public CommandRegistration getRegistration() {
        return registration;
    }

    public MySQL getMySQL() {
        return mysql;
    }

    public UserSettingsManager getUserSettingsManager() {
        return userSettingsManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public UserSettingsTable getUserSettingsTable() {
        return userSettingsTable;
    }
}
