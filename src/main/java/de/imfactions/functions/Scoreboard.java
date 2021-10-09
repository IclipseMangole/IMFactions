package de.imfactions.functions;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.util.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Scoreboard {

    private IMFactions imFactions;
    private Data data;
    private FactionManager factionManager;
    private FactionUserManager factionUserManager;
    private RaidManager raidManager;
    private UserManager userManager;
    private HashMap<Player, org.bukkit.scoreboard.Scoreboard> scoreboards;

    public Scoreboard(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionManager = data.getFactionManager();
        factionUserManager = data.getFactionUserManager();
        userManager = data.getUserManager();
        raidManager = data.getRaidManager();
        scoreboards = new HashMap<>();

        Bukkit.getScheduler().runTaskTimer(imFactions, new Runnable() {
            @Override
            public void run() {
                updateScoreboard();
            }
        }, 20, 20);
    }

    public void setScoreboard(Player player) {
        createScoreboard(player);
    }

    public void updateScoreboard() {
        scoreboards.forEach((player, scoreboard) -> {
            UUID uuid = player.getUniqueId();

            if (factionUserManager.isFactionUserInFaction(uuid)) {
                FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(uuid);
                int factionID = factionUser.getFactionID();
                if (raidManager.isFactionRaiding(factionID)) {
                    int raidID = raidManager.getActiveRaidID(factionID);
                    RaidManager.Raid raid = raidManager.getRaid(raidID);
                    getRaidScoreboard(player, scoreboard, raid);
                }
            }
            getNormalScoreboard(player, scoreboard);
        });
    }

    /**
     * Scoreboard while being in a Raid
     */

    private void getRaidScoreboard(Player player, org.bukkit.scoreboard.Scoreboard scoreboard, RaidManager.Raid raid) {
        UUID uuid = player.getUniqueId();
        int raidID = raid.getRaidID();
        FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(uuid);
        FactionManager.Faction faction = factionManager.getFaction(factionUser.getFactionID());

        String raidState = raid.getRaidState();
        String defenders = "None";
        String timeLeft = "30:00";
        ArrayList<FactionUserManager.FactionUser> raidTeam = raidManager.getRaidTeam(raidID);

        if (raidState.equals("active")) {
            FactionManager.Faction factionDefenders = factionManager.getFaction(raid.getFactionIdDefenders());
            defenders = factionDefenders.getName();
            long time = ((raid.getStart().getTime() + 1000 * 60 * 30) - System.currentTimeMillis()) / 1000;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.format(time);
            timeLeft = "" + time;
        }

        Objective objective = scoreboard.getObjective("123") != null ? scoreboard.getObjective("123") : scoreboard.registerNewObjective("123", "123", "123");
        objective.setDisplayName("§6§nRaid");
        objective.getScore(ChatColor.of(Color.YELLOW) + "").setScore(20);
        objective.getScore("§6§lState:").setScore(19);
        objective.getScore(raidState).setScore(18);
        objective.getScore(ChatColor.of(Color.GREEN) + "").setScore(17);
        objective.getScore( faction.getName() + " §6vs §f" + defenders).setScore(16);
        objective.getScore(ChatColor.of(Color.BLUE) + "").setScore(15);
        objective.getScore("§6§lTime Left:").setScore(14);
        objective.getScore(timeLeft).setScore(13);
        objective.getScore(ChatColor.of(Color.PINK) + "").setScore(12);
        objective.getScore("§6§lRaid Team:").setScore(11);
        for (int i = 10; i > 10 - raidTeam.size(); i--) {
            FactionUserManager.FactionUser member = raidTeam.get(-i+10);
            Player player1 = factionUserManager.getPlayer(member.getUuid());
            objective.getScore(player1.getName()).setScore(i);
        }

    }


    /**
     * Normal Scoreboard while playing
     */

    private void getNormalScoreboard(Player player, org.bukkit.scoreboard.Scoreboard scoreboard) {
        Objective objective = scoreboard.getObjective("123") != null ? scoreboard.getObjective("123") : scoreboard.registerNewObjective("123", "123", "123");

        String name = "§f" + player.getName();
        UUID uuid = UUIDFetcher.getUUID(player);
        String factionName = "§fNone";
        String onlineMembers = "§f0/0";
        String world = getWorld(player);
        String worldName = player.getWorld().getName();
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
        objective.getScore("" + getWorldColor(worldName) + world).setScore(3);
        objective.getScore(ChatColor.of(Color.PINK) + "").setScore(2);
        objective.getScore("§6§lEther").setScore(1);
        objective.getScore(ether).setScore(0);

    }

    private void createScoreboard(Player player) {
        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.getObjective("123") != null ? scoreboard.getObjective("123") : scoreboard.registerNewObjective("123", "123", "123");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        String name = "§f" + player.getName();
        UUID uuid = UUIDFetcher.getUUID(player);
        String factionName = "§fNone";
        String onlineMembers = "§f0/0";
        String world = getWorld(player);
        String worldName = player.getWorld().getName();
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
        objective.getScore("" + getWorldColor(worldName) + world).setScore(3);
        objective.getScore(ChatColor.of(Color.PINK) + "").setScore(2);
        objective.getScore("§6§lEther").setScore(1);
        objective.getScore(ether).setScore(0);

        scoreboards.put(player, scoreboard);
        player.setScoreboard(scoreboard);
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

    private ChatColor getWorldColor(String worldName) {

        switch (worldName) {
            case "world":
                return ChatColor.of("#5813BF");
            case "FactionPVP_world":
                return ChatColor.of("#851818");
            case "FactionPlots_world":
                return ChatColor.of("#E07A04");
        }
        return ChatColor.RESET;
    }

}
