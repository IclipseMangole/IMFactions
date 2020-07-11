package de.imfactions;

import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import de.imfactions.database.UserManager;
import de.imfactions.database.UserSettingsTable;
import de.imfactions.database.faction.FactionManager;
import de.imfactions.database.faction.FactionPlotManager;
import de.imfactions.database.faction.FactionUserManager;
import de.imfactions.functions.Tablist;
import de.imfactions.database.faction.RaidManager;
import de.imfactions.functions.UserSettingsManager;
import de.imfactions.util.Command.CommandRegistration;
import de.imfactions.util.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class Data {
    private IMFactions factions;

    private final CommandRegistration registration;
    private final MySQL mysql;
    private final UserSettingsManager userSettingsManager;
    private final Tablist tablist;

    //Tables
    private UserManager userManager;
    private UserSettingsTable userSettingsTable;
    private FactionManager factionManager;
    private FactionUserManager factionUserManager;
    private FactionPlotManager factionPlotManager;
    private RaidManager raidManager;

    //Worlds
    private World world;
    private World PVP_world;
    private World FactionPlots_world;
    private Location worldSpawn;
    private Location PVP_worldSpawn;
    private Location FactionPlots_worldSpawn;

    public Data(IMFactions factions) {
        this.factions = factions;
        registration = new CommandRegistration(factions);
        mysql = new MySQL(factions);
        userSettingsManager = new UserSettingsManager(factions);
        tablist = new Tablist(factions);
    }

    public void createTables() {
        userManager = new UserManager(factions);
        userSettingsTable = new UserSettingsTable(factions);
        factionManager = new FactionManager(factions);
        factionUserManager = new FactionUserManager(factions);
        factionPlotManager = new FactionPlotManager(factions);
        userManager = new UserManager(factions);
        userSettingsTable = new UserSettingsTable(factions);
        factionManager = new FactionManager(factions);
        factionUserManager = new FactionUserManager(factions);
        factionPlotManager = new FactionPlotManager(factions);
        raidManager = new RaidManager(factions);
    }

    public void loadWorlds(){
        world = Bukkit.getWorld("world");
        PVP_world = Bukkit.createWorld(new WorldCreator("/home/IMNetzwerk/FactionsDev01/FactionPVP_world"));
        FactionPlots_world = Bukkit.createWorld(new WorldCreator("/home/IMNetzwerk/FactionsDev01/FactionPlots_world"));
        worldSpawn = new Location(world, 0, 31, -18);
        PVP_worldSpawn = new Location(PVP_world, 0, 50 ,0);
        FactionPlots_worldSpawn = new Location(FactionPlots_world, 0 ,17 ,0);
        world.setSpawnLocation(worldSpawn);
        PVP_world.setSpawnLocation(PVP_worldSpawn);
        FactionPlots_world.setSpawnLocation(FactionPlots_worldSpawn);
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

    public Location getFactionPlots_worldSpawn() {
        return FactionPlots_worldSpawn;
    }

    public Location getPVP_worldSpawn() {
        return PVP_worldSpawn;
    }

    public Location getWorldSpawn() {
        return worldSpawn;
    }

    public World getWorld() {
        return world;
    }

    public World getFactionPlots_world() {
        return FactionPlots_world;
    }

    public World getPVP_world() {
        return PVP_world;
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

    public FactionManager getFactionManager(){
        return factionManager;
    }

    public FactionPlotManager getFactionPlotManager() {
        return factionPlotManager;
    }

    public FactionUserManager getFactionUserManager() {
        return factionUserManager;
    }

    public RaidManager getRaidManager() {
        return raidManager;
    }
}
