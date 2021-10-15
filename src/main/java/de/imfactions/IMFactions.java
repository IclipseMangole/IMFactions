package de.imfactions;

import de.imfactions.commands.Ether;
import de.imfactions.commands.spawn.Spawn;
import de.imfactions.functions.WorldManager;
import de.imfactions.functions.faction.FactionCommand;
import de.imfactions.functions.factionPlot.FactionPlotCommand;
import de.imfactions.functions.factionPlot.PlotListener;
import de.imfactions.functions.lobby.LobbyListener;
import de.imfactions.functions.pvp.PVPListener;
import de.imfactions.functions.pvp.mobs.abilities.CustomMobListener;
import de.imfactions.functions.raid.RaidCommand;
import de.imfactions.functions.raid.RaidListener;
import de.imfactions.listener.JoinListener;
import de.imfactions.listener.QuitListener;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class IMFactions extends JavaPlugin {

    private Data data;
    private WorldManager worldManager;

    @Override
    public void onLoad() {
        worldManager = new WorldManager(this);
        if (Bukkit.getWorlds().size() == 0) {
            worldManager.loadLobby();
            worldManager.loadPVP();
            worldManager.loadPlots();
        }
    }

    @Override
    public void onEnable() {
        updateGamerules();
        data = new Data(this);
        data.createUtils();
        data.loadWorlds();
        data.loadScheduler();
        data.loadScoreboards();
        worldManager.loadManagers();
        registerCommands();
        registerListener();
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("Der Server restartet/reloadet");
        }
        data.getUserUtil().saveUsers();
        data.getMySQL().close();
        data.getScheduler().stopSchedulers();
        data.saveUtils();
        worldManager.savePlots();
    }

    public Data getData() {
        return data;
    }


    public String getServerName() {
        return this.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getName();
    }

    public void registerCommands() {
        data.getRegistration().register(new Ether(this), this);
        data.getRegistration().register(new FactionCommand(this), this);
        data.getRegistration().register(new de.imfactions.commands.World(this), this);
        data.getRegistration().register(new Spawn(this), this);
        data.getRegistration().register(new FactionPlotCommand(this), this);
        data.getRegistration().register(new RaidCommand(this), this);
    }

    public void registerListener() {
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LobbyListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PVPListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlotListener(this), this);
        Bukkit.getPluginManager().registerEvents(data.getFactionUtil().getFactionHomeScheduler(), this);
        Bukkit.getPluginManager().registerEvents(data.getSpawnScheduler(), this);
        Bukkit.getPluginManager().registerEvents(new RaidListener(this), this);
        Bukkit.getPluginManager().registerEvents(data.getNpcUtil(), this);
        Bukkit.getPluginManager().registerEvents(new CustomMobListener(this), this);
    }

    public void updateGamerules() {
        Bukkit.getWorlds().forEach(world -> world.setDifficulty(Difficulty.HARD));
        Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false));
        Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true));
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }
}
