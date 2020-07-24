package de.imfactions.functions;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.database.UserManager;
import de.imfactions.database.faction.FactionManager;
import de.imfactions.database.faction.FactionUserManager;
import de.imfactions.util.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.awt.*;
import java.util.UUID;

public class Scoreboard {

    private IMFactions imFactions;
    private Data data;
    private FactionManager factionManager;
    private FactionUserManager factionUserManager;
    private UserManager userManager;

    public Scoreboard(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionManager = data.getFactionManager();
        factionUserManager = data.getFactionUserManager();
        userManager = data.getUserManager();

        Bukkit.getScheduler().runTaskTimer(imFactions, new Runnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player ->{
                    setScoreboard(player);
                });
            }
        }, 0, 20);
    }

    public void setScoreboard(Player player) {
        player.setScoreboard(createScoreboard(player));
    }

    public void updateScoreboard(Player player){
        org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);

        String name = "§f" + player.getName();
        UUID uuid = UUIDFetcher.getUUID(player);
        String factionName = "§fNone";
        String onlineMembers = "§f0/0";
        String world = getWorld(player);
        String ether = "§f" + userManager.getUser(player).getEther();

        if (factionUserManager.isFactionUserExists(uuid)) {
            if (factionUserManager.isFactionUserInFaction(uuid)) {
                int factionID = factionUserManager.getFactionUser(uuid).getFactionID();
                FactionManager.Faction faction = factionManager.getFaction(factionID);
                factionName = "§f" + faction.getName() + "§e[" + faction.getShortcut() + "]";
                onlineMembers = "§f" + factionUserManager.getOnlineMembersAmount(factionID) + "/" + faction.getUserAmount();
            }
        }

        objective.setDisplayName("§6§nFactions");
        objective.getScore(ChatColor.of(Color.YELLOW) + "").setScore(14);
        objective.getScore("§6§lPlayer:").setScore(13);
        objective.getScore(name).setScore(12);
        objective.getScore(ChatColor.of(Color.GREEN) + "").setScore(11);
        objective.getScore("§6§lFaction:").setScore(10);
        objective.getScore(factionName).setScore(9);
        objective.getScore(ChatColor.of(Color.RED) + "").setScore(8);
        objective.getScore("§6§lOnline Members:").setScore(7);
        objective.getScore(onlineMembers).setScore(6);
        objective.getScore(ChatColor.of(Color.BLUE) + "").setScore(5);
        objective.getScore("§6§lWorld:").setScore(4);
        objective.getScore(world).setScore(3);
        objective.getScore(ChatColor.of(Color.PINK) + "").setScore(2);
        objective.getScore("§6§lEther").setScore(1);
        objective.getScore(ether).setScore(0);
    }

    private org.bukkit.scoreboard.Scoreboard createScoreboard(Player player) {
        UUID uuid = UUIDFetcher.getUUID(player);
        int factionID;
        String name = "§f" + player.getName();
        String factionName = "§fNone";
        String onlineMembers = "§f0/0";
        String world = getWorld(player);
        String ether = "§f" + userManager.getUser(player).getEther();

        if (factionUserManager.isFactionUserExists(uuid)) {
            if (factionUserManager.isFactionUserInFaction(uuid)) {
                factionID = factionUserManager.getFactionUser(uuid).getFactionID();
                FactionManager.Faction faction = factionManager.getFaction(factionID);
                factionName = "§f" + faction.getName() + "§e[" + faction.getShortcut() + "]";
                onlineMembers = "§f" + factionUserManager.getOnlineMembersAmount(factionID) + "/" + faction.getUserAmount();
            }
        }

        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("objective", "objective", "objective");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.setDisplayName("§6§nFactions");
        objective.getScore(ChatColor.of(Color.YELLOW) + "").setScore(14);
        objective.getScore("§6§lPlayer:").setScore(13);
        objective.getScore(name).setScore(12);
        objective.getScore(ChatColor.of(Color.GREEN) + "").setScore(11);
        objective.getScore("§6§lFaction:").setScore(10);
        objective.getScore(factionName).setScore(9);
        objective.getScore(ChatColor.of(Color.RED) + "").setScore(8);
        objective.getScore("§6§lOnline Members:").setScore(7);
        objective.getScore(onlineMembers).setScore(6);
        objective.getScore(ChatColor.of(Color.BLUE) + "").setScore(5);
        objective.getScore("§6§lWorld:").setScore(4);
        objective.getScore(world).setScore(3);
        objective.getScore(ChatColor.of(Color.PINK) + "").setScore(2);
        objective.getScore("§6§lEther").setScore(1);
        objective.getScore(ether).setScore(0);

        return scoreboard;
    }

    private String getWorld(Player player) {
        World world = player.getWorld();
        String worldName = world.getName();

        switch (worldName) {
            case "world":
                return ChatColor.of("#5813BF") + "Lobby";
            case "FactionPVP_world":
                return ChatColor.of("#851818") + "PVP";
            case "FactionPlots_world":
                return ChatColor.of("#E07A04") + "Plots";
        }
        return "";
    }

}
