package de.imfactions;

import de.imfactions.commands.spawn.SpawnScheduler;
import de.imfactions.functions.Scoreboard;
import de.imfactions.functions.Tablist;
import de.imfactions.functions.faction.FactionHomeScheduler;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.functions.factionPlot.FactionPlotUtil;
import de.imfactions.functions.items.ItemUtils;
import de.imfactions.functions.lobby.lottery.LotteryUtil;
import de.imfactions.functions.npc.NPCUtil;
import de.imfactions.functions.pvp.mobs.custommob.CustomMobUtil;
import de.imfactions.functions.raid.RaidScheduler;
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
    private final IMFactions imFactions;

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
    private SpawnScheduler spawnScheduler;
    private FactionHomeScheduler factionHomeScheduler;
    private RaidScheduler raidScheduler;

    //Scoreboard
    private Scoreboard scoreboard;

    //Utils
    private TextureUtil textureUtil;
    private NPCUtil npcUtil;
    private FactionUtil factionUtil;
    private FactionPlotUtil factionPlotUtil;
    private FactionMemberUtil factionMemberUtil;
    private RaidUtil raidUtil;
    private UserUtil userUtil;
    private UserSettingsUtil userSettingsUtil;
    private LotteryUtil lotteryUtil;
    private ItemUtils itemUtils;
    private CustomMobUtil customMobUtil;

    public Data(IMFactions imFactions) {
        this.imFactions = imFactions;
        registration = new CommandRegistration(imFactions);
        mysql = new MySQL(imFactions);
        tablist = new Tablist(imFactions);
    }

    public void createUtils() {
        textureUtil = new TextureUtil(imFactions);
        npcUtil = new NPCUtil(imFactions);
        factionUtil = new FactionUtil(this);
        factionPlotUtil = new FactionPlotUtil(this);
        factionMemberUtil = new FactionMemberUtil(this);
        raidUtil = new RaidUtil(this);
        userUtil = new UserUtil(this);
        userSettingsUtil = new UserSettingsUtil(this);
        lotteryUtil = new LotteryUtil(imFactions);
        itemUtils = new ItemUtils(imFactions);
        customMobUtil = new CustomMobUtil();
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
        customMobUtil.deleteCustomMobs();
    }

    public void loadWorlds() {
        world = Bukkit.getWorld("world");
        PVP_world = Bukkit.createWorld(new WorldCreator("FactionPVP_world"));
        if (!new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlotsSave_world/").exists()) {
            FactionPlots_world = new WorldCreator("FactionPlots_world").generator(new EmptyChunkGenerator()).createWorld();
        } else {
            FactionPlots_world = Bukkit.createWorld(new WorldCreator("FactionPlots_world"));
        }
        imFactions.getWorldManager().startAutoSave();
        worldSpawn = new Location(world, 0.5, 31, -17.5);
        PVP_worldSpawn = new Location(PVP_world, 54.0, 78.0, 372.0, 180, 0);
        FactionPlots_worldSpawn = new Location(FactionPlots_world, 0, 17, 0);
        world.setSpawnLocation(worldSpawn);
        PVP_world.setSpawnLocation(PVP_worldSpawn);
        FactionPlots_world.setSpawnLocation(FactionPlots_worldSpawn);
    }

    public void loadScheduler() {
        spawnScheduler = new SpawnScheduler(imFactions);
        factionHomeScheduler = new FactionHomeScheduler(this);
        raidScheduler = new RaidScheduler(this);
        factionUtil.loadScheduler();
        raidUtil.loadSchedulers();
    }

    public void loadScoreboards(){
        scoreboard = new Scoreboard(imFactions);
        raidUtil.loadScoreboards();
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

    public SpawnScheduler getSpawnScheduler(){
        return spawnScheduler;
    }

    public IMFactions getImFactions(){
        return imFactions;
    }

    public TextureUtil getTextureUtil() {
        return textureUtil;
    }

    public NPCUtil getNpcUtil() {
        return npcUtil;
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

    public ItemUtils getItemUtils() {
        return itemUtils;
    }

    public FactionHomeScheduler getFactionHomeScheduler() {
        return factionHomeScheduler;
    }

    public RaidScheduler getRaidScheduler() {
        return raidScheduler;
    }

    public CustomMobUtil getCustomMobUtil() {
        return customMobUtil;
    }
}
