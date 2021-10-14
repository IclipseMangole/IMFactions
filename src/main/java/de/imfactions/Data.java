package de.imfactions;

import de.imfactions.commands.spawn.SpawnScheduler;
import de.imfactions.functions.Scheduler;
import de.imfactions.functions.Scoreboard;
import de.imfactions.functions.Tablist;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.functions.factionPlot.FactionPlotUtil;
import de.imfactions.functions.lobby.lottery.LotteryUtil;
import de.imfactions.functions.raid.RaidUtil;
import de.imfactions.functions.texture.TextureUtil;
import de.imfactions.functions.user.UserSettingsUtil;
import de.imfactions.functions.user.UserUtil;
import de.imfactions.util.Command.CommandRegistration;
import de.imfactions.util.EmptyChunkGenerator;
import de.imfactions.util.MySQL;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;

public class Data {
    private IMFactions imFactions;

    private final CommandRegistration registration;
    private final MySQL mysql;
    private final Tablist tablist;

    //Worlds
    private World world;
    private World PVP_world;
    private World FactionPlots_world;
    private Location worldSpawn;
    private Location PVP_worldSpawn;
    private Location FactionPlots_worldSpawn;

    //Scheduler
    private Scheduler scheduler;
    private SpawnScheduler spawnScheduler;

    //Scoreboard
    private Scoreboard scoreboard;

    //Utils
    private TextureUtil textureUtil;
    private FactionUtil factionUtil;
    private FactionPlotUtil factionPlotUtil;
    private FactionMemberUtil factionMemberUtil;
    private RaidUtil raidUtil;
    private UserUtil userUtil;
    private UserSettingsUtil userSettingsUtil;
    private LotteryUtil lotteryUtil;

    public Data(IMFactions imFactions) {
        this.imFactions = imFactions;
        registration = new CommandRegistration(imFactions);
        mysql = new MySQL(imFactions);
        tablist = new Tablist(imFactions);
    }

    public void createUtils() {
        textureUtil = new TextureUtil(imFactions);
        factionUtil = new FactionUtil(this);
        factionPlotUtil = new FactionPlotUtil(this);
        factionMemberUtil = new FactionMemberUtil(this);
        raidUtil = new RaidUtil(this);
        userUtil = new UserUtil(this);
        userSettingsUtil = new UserSettingsUtil(this);
        //lotteryUtil = new LotteryUtil(imFactions);
        loadUtils();
    }

    private void loadUtils() {
        factionUtil.loadUtils();
        factionPlotUtil.loadUtils();
        factionMemberUtil.loadUtils();
        raidUtil.loadUtils();
    }

    public void saveUtils() {
        factionUtil.saveFactions();
        factionPlotUtil.saveFactionPlots();
        factionMemberUtil.saveFactionMembers();
        raidUtil.saveRaids();
        userUtil.saveUsers();
    }

    public void loadWorlds() {
        world = Bukkit.getWorld("world");
        PVP_world = Bukkit.createWorld(new WorldCreator("FactionPVP_world"));
        if (!new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlotsSave_world/").exists()) {
            FactionPlots_world = new WorldCreator("FactionPlotsSave_world").generator(new EmptyChunkGenerator()).createWorld();
        } else {
            FactionPlots_world = Bukkit.createWorld(new WorldCreator("FactionPlots_world"));
        }
        worldSpawn = new Location(world, 0.5, 31, -17.5);
        PVP_worldSpawn = new Location(PVP_world, 63.5, 80.5, 1265.5);
        FactionPlots_worldSpawn = new Location(FactionPlots_world, 0, 17, 0);
        world.setSpawnLocation(worldSpawn);
        PVP_world.setSpawnLocation(PVP_worldSpawn);
        FactionPlots_world.setSpawnLocation(FactionPlots_worldSpawn);
    }

    public void loadScheduler(){
        scheduler = new Scheduler(imFactions);
        spawnScheduler = new SpawnScheduler(imFactions);
    }

    public void loadScoreboards(){
        scoreboard = new Scoreboard(imFactions);
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

    public ChatColor getPurple() {
        return ChatColor.of("#a42eff");
    }

    public ChatColor getWhite() {
        return ChatColor.of("#ffffff");
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

    public Scoreboard getScoreboard() {
        return scoreboard;
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

    public Scheduler getScheduler() {
        return scheduler;
    }

    public SpawnScheduler getSpawnScheduler(){
        return spawnScheduler;
    }

    public IMFactions getImFactions(){
        return imFactions;
    }

    public TextureUtil getTextureUtil() {
        return textureUtil;
    }

    public FactionUtil getFactionUtil() {
        return factionUtil;
    }

    public FactionPlotUtil getFactionPlotUtil() {
        return factionPlotUtil;
    }

    public FactionMemberUtil getFactionMemberUtil() {
        return factionMemberUtil;
    }

    public RaidUtil getRaidUtil() {
        return raidUtil;
    }

    public UserUtil getUserUtil() {
        return userUtil;
    }

    public UserSettingsUtil getUserSettingsUtil() {
        return userSettingsUtil;
    }

    public LotteryUtil getLotteryUtil() {
        return lotteryUtil;
    }
}
