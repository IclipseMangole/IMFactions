package de.imfactions.functions;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.faction.Faction;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMember;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.functions.raid.Raid;
import de.imfactions.functions.raid.RaidState;
import de.imfactions.functions.raid.RaidUtil;
import de.imfactions.functions.user.UserUtil;
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

    private final IMFactions imFactions;
    private final Data data;
    private final FactionUtil factionUtil;
    private final FactionMemberUtil factionMemberUtil;
    private final RaidUtil raidUtil;
    private final UserUtil userUtil;
    private final HashMap<Player, org.bukkit.scoreboard.Scoreboard> scoreboards;

    public Scoreboard(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionUtil = data.getFactionUtil();
        factionMemberUtil = data.getFactionMemberUtil();
        userUtil = data.getUserUtil();
        raidUtil = data.getRaidUtil();
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

            if (factionMemberUtil.isFactionMemberExists(uuid)) {
                FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
                int factionID = factionMember.getFactionID();
                if (raidUtil.isFactionRaiding(factionID)) {
                    int raidID = raidUtil.getActiveRaidID(factionID);
                    Raid raid = raidUtil.getRaid(raidID);
                    getRaidScoreboard(player, scoreboard, raid);
                }
            }
            getNormalScoreboard(player, scoreboard);
        });
    }

    /**
     * Scoreboard while being in a Raid
     */

    private void getRaidScoreboard(Player player, org.bukkit.scoreboard.Scoreboard scoreboard, Raid raid) {
        UUID uuid = player.getUniqueId();
        int raidID = raid.getRaidID();
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());

        String raidState = RaidState.getStringFromState(raid.getRaidState());
        String defenders = "None";
        String timeLeft = "30:00";
        ArrayList<FactionMember> raidTeam = raidUtil.getRaidTeam(raidID);

        if (raidState.equals(RaidState.RAIDING)) {
            Faction factionDefenders = factionUtil.getFaction(raid.getFactionIdDefenders());
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
        objective.getScore(faction.getName() + " §6vs §f" + defenders).setScore(16);
        objective.getScore(ChatColor.of(Color.BLUE) + "").setScore(15);
        objective.getScore("§6§lTime Left:").setScore(14);
        objective.getScore(timeLeft).setScore(13);
        objective.getScore(ChatColor.of(Color.PINK) + "").setScore(12);
        objective.getScore("§6§lRaid Team:").setScore(11);
        for (int i = 10; i > 10 - raidTeam.size(); i--) {
            FactionMember member = raidTeam.get(-i + 10);
            Player player1 = factionMemberUtil.getPlayer(member.getUuid());
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
        String ether = "§f" + userUtil.getUser(player).getEther();

        if (factionMemberUtil.isFactionMemberExists(uuid)) {
            if (factionMemberUtil.isFactionMemberExists(uuid)) {
                int factionID = factionMemberUtil.getFactionMember(uuid).getFactionID();
                Faction faction = factionUtil.getFaction(factionID);
                factionName = "§f" + faction.getName() + "§e[" + faction.getShortcut() + "]";
                onlineMembers = "§f" + factionMemberUtil.getOnlineMembersAmount(factionID) + "/" + faction.getMemberAmount();
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
        String ether = "§f" + userUtil.getUser(player).getEther();

        if (factionMemberUtil.isFactionMemberExists(uuid)) {
            if (factionMemberUtil.isFactionMemberExists(uuid)) {
                int factionID = factionMemberUtil.getFactionMember(uuid).getFactionID();
                Faction faction = factionUtil.getFaction(factionID);
                factionName = "§f" + faction.getName() + "§e[" + faction.getShortcut() + "]";
                onlineMembers = "§f" + factionMemberUtil.getOnlineMembersAmount(factionID) + "/" + faction.getMemberAmount();
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
