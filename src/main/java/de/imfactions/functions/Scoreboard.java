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
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.UUID;

public class Scoreboard {

    private final IMFactions imFactions;
    private final Data data;
    private final FactionUtil factionUtil;
    private final FactionMemberUtil factionMemberUtil;
    private final RaidUtil raidUtil;
    private final UserUtil userUtil;

    public Scoreboard(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionUtil = data.getFactionUtil();
        factionMemberUtil = data.getFactionMemberUtil();
        userUtil = data.getUserUtil();
        raidUtil = data.getRaidUtil();

        Bukkit.getScheduler().runTaskTimer(imFactions, new Runnable() {
            @Override
            public void run() {
                updateScoreboard();
            }
        }, 20, 20);
    }

    public void setScoreboard(Player player) {
        createNormalScoreboard(player);
    }

    public void setRaidScoreboard(Player player, Raid raid) {
        createRaidScoreboard(player, raid);
    }

    public void updateScoreboard() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            UUID uuid = UUIDFetcher.getUUID(onlinePlayer);
            if (!factionMemberUtil.isFactionMemberExists(uuid)) {
                updateNormalScoreboard(onlinePlayer);
                return;
            }
            FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
            if (!raidUtil.isFactionRaiding(factionMember.getFactionID())) {
                updateNormalScoreboard(onlinePlayer);
                return;
            }
            int raidID = raidUtil.getActiveRaidID(factionMember.getFactionID());
            Raid raid = raidUtil.getRaid(raidID);
            if (!raidUtil.isFactionMemberJoinedRaid(factionMember)) {
                updateNormalScoreboard(onlinePlayer);
                return;
            }
            updateRaidScoreboard(onlinePlayer, raid);
        }
    }

    /**
     * Scoreboard while being in a Raid
     */

    private void createRaidScoreboard(Player player, Raid raid) {
        UUID uuid = player.getUniqueId();
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());

        String name = ChatColor.WHITE + player.getName();
        String raidState = ChatColor.WHITE + RaidState.getStringFromState(raid.getRaidState());
        String attackers = ChatColor.WHITE + faction.getName();
        String defenders = ChatColor.WHITE + "None";

        if (!raid.getRaidState().equals(RaidState.PREPARING)) {
            Faction factionDefenders = factionUtil.getFaction(raid.getFactionIdDefenders());
            defenders = ChatColor.WHITE + factionDefenders.getName();
        }

        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("raid", "raid", "raid");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Raid");
        objective.getScore(ChatColor.BOLD + "").setScore(20);
        objective.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Player:").setScore(19);

        Team playerScore = scoreboard.registerNewTeam("playerScore");
        playerScore.addEntry(ChatColor.BOLD + "" + ChatColor.RESET);
        playerScore.setPrefix(name);
        objective.getScore(ChatColor.BOLD + "" + ChatColor.RESET).setScore(18);

        objective.getScore(ChatColor.RED + "").setScore(17);
        objective.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "State:").setScore(16);

        Team stateScore = scoreboard.registerNewTeam("stateScore");
        stateScore.addEntry(ChatColor.RED + "" + ChatColor.BLUE);
        stateScore.setPrefix(raidState);
        objective.getScore(ChatColor.RED + "" + ChatColor.BLUE).setScore(15);

        objective.getScore(ChatColor.BLUE + "").setScore(14);
        objective.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Attackers:").setScore(13);

        Team attackerScore = scoreboard.registerNewTeam("attackerScore");
        attackerScore.addEntry(ChatColor.BLUE + "" + ChatColor.GREEN);
        attackerScore.setPrefix(attackers);
        objective.getScore(ChatColor.BLUE + "" + ChatColor.GREEN).setScore(12);

        objective.getScore(ChatColor.GREEN + "").setScore(11);
        objective.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Defenders:").setScore(10);

        Team defenderScore = scoreboard.registerNewTeam("defenderScore");
        defenderScore.addEntry(ChatColor.GREEN + "" + ChatColor.AQUA);
        defenderScore.setPrefix(defenders);
        objective.getScore(ChatColor.GREEN + "" + ChatColor.AQUA).setScore(9);

        objective.getScore(ChatColor.AQUA + "").setScore(8);
        objective.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "RaidTeam:").setScore(7);

        int score = 6;
        for (int rank = 3; rank >= 0; rank--) {
            ArrayList<FactionMember> raidTeam = factionUtil.getMembersWithRank(faction.getId(), rank);
            for (int i = raidTeam.size(); i > 0; i--) {
                FactionMember member = raidTeam.get(i - 1);
                if (raidUtil.isFactionMemberJoinedRaid(member)) {
                    String memberName = UUIDFetcher.getName(member.getUuid());
                    Team raidTeamScore = scoreboard.registerNewTeam(memberName);
                    raidTeamScore.addEntry(memberName);
                    raidTeamScore.setPrefix(member.getRankColor() + "");
                    objective.getScore(memberName).setScore(score);
                    score--;
                }
            }
        }

        player.setScoreboard(scoreboard);
    }


    private void updateRaidScoreboard(Player player, Raid raid) {
        UUID uuid = player.getUniqueId();
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());

        String name = ChatColor.WHITE + player.getName();
        String raidState = ChatColor.WHITE + RaidState.getStringFromState(raid.getRaidState());
        String attackers = ChatColor.WHITE + faction.getName();
        String defenders = ChatColor.WHITE + "None";

        if (!raid.getRaidState().equals(RaidState.PREPARING)) {
            Faction factionDefenders = factionUtil.getFaction(raid.getFactionIdDefenders());
            defenders = ChatColor.WHITE + factionDefenders.getName();
        }

        org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();
        scoreboard.getTeam("playerScore").setPrefix(name);
        scoreboard.getTeam("stateScore").setPrefix(raidState);
        scoreboard.getTeam("attackerScore").setPrefix(attackers);
        scoreboard.getTeam("defenderScore").setPrefix(defenders);

        for (int rank = 3; rank >= 0; rank--) {
            ArrayList<FactionMember> raidTeam = factionUtil.getMembersWithRank(faction.getId(), rank);
            for (int i = raidTeam.size(); i > 0; i--) {
                FactionMember member = raidTeam.get(i - 1);
                if (raidUtil.isFactionMemberJoinedRaid(member)) {
                    String memberName = UUIDFetcher.getName(member.getUuid());
                    scoreboard.getTeam(memberName).setPrefix(member.getRankColor() + "");
                }
            }
        }
    }


    private void createNormalScoreboard(Player player) {
        String name = "§f" + player.getName();
        UUID uuid = UUIDFetcher.getUUID(player);
        String factionName = "§fNone";
        String onlineMembers = "§f0/0";
        String worldName = getWorld(player);
        ChatColor worldColor = getWorldColor(worldName);
        String ether = "§f" + userUtil.getUser(player).getEther();

        if (factionMemberUtil.isFactionMemberExists(uuid)) {
            if (factionMemberUtil.isFactionMemberExists(uuid)) {
                int factionID = factionMemberUtil.getFactionMember(uuid).getFactionID();
                Faction faction = factionUtil.getFaction(factionID);
                factionName = "§f" + faction.getName() + "§e[" + faction.getShortcut() + "]";
                onlineMembers = "§f" + factionMemberUtil.getOnlineMembersAmount(factionID) + "/" + faction.getMemberAmount();
            }
        }

        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("123", "123", "123");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§6§nFactions");
        objective.getScore(ChatColor.BOLD + "").setScore(14);
        objective.getScore("§6§lPlayer:").setScore(13);

        Team playerScore = scoreboard.registerNewTeam("playerScore");
        playerScore.addEntry(ChatColor.BOLD + "" + ChatColor.RESET);
        playerScore.setPrefix(name);
        objective.getScore(ChatColor.BOLD + "" + ChatColor.RESET).setScore(12);

        objective.getScore(ChatColor.BLUE + "").setScore(11);
        objective.getScore("§6§lFaction:").setScore(10);

        Team factionScore = scoreboard.registerNewTeam("factionScore");
        factionScore.addEntry(ChatColor.BLUE + "" + ChatColor.RED);
        factionScore.setPrefix(factionName);
        objective.getScore(ChatColor.BLUE + "" + ChatColor.RED).setScore(9);

        objective.getScore(ChatColor.RED + "").setScore(8);
        objective.getScore("§6§lOnline Members:").setScore(7);

        Team membersScore = scoreboard.registerNewTeam("membersScore");
        membersScore.addEntry(ChatColor.RED + "" + ChatColor.YELLOW);
        membersScore.setPrefix(onlineMembers);
        objective.getScore(ChatColor.RED + "" + ChatColor.YELLOW).setScore(6);

        objective.getScore(ChatColor.GREEN + "").setScore(5);
        objective.getScore("§6§lWorld:").setScore(4);

        Team worldScore = scoreboard.registerNewTeam("worldScore");
        worldScore.addEntry(ChatColor.GREEN + "" + ChatColor.GRAY);
        worldScore.setPrefix(worldColor + worldName);
        objective.getScore(ChatColor.GREEN + "" + ChatColor.GRAY).setScore(3);

        objective.getScore(ChatColor.DARK_GRAY + "").setScore(2);
        objective.getScore("§6§lEther:").setScore(1);

        Team etherScore = scoreboard.registerNewTeam("etherScore");
        etherScore.addEntry(ChatColor.DARK_GRAY + "" + ChatColor.DARK_PURPLE);
        etherScore.setPrefix(ether);
        objective.getScore(ChatColor.DARK_GRAY + "" + ChatColor.DARK_PURPLE).setScore(0);

        player.setScoreboard(scoreboard);
    }

    private void updateNormalScoreboard(Player player) {
        String name = "§f" + player.getName();
        UUID uuid = UUIDFetcher.getUUID(player);
        String factionName = "§fNone";
        String onlineMembers = "§f0/0";
        String worldName = getWorld(player);
        ChatColor worldColor = getWorldColor(worldName);
        String ether = "§f" + userUtil.getUser(player).getEther();

        if (factionMemberUtil.isFactionMemberExists(uuid)) {
            if (factionMemberUtil.isFactionMemberExists(uuid)) {
                int factionID = factionMemberUtil.getFactionMember(uuid).getFactionID();
                Faction faction = factionUtil.getFaction(factionID);
                factionName = "§f" + faction.getName() + "§e[" + faction.getShortcut() + "]";
                onlineMembers = "§f" + factionMemberUtil.getOnlineMembersAmount(factionID) + "/" + faction.getMemberAmount();
            }
        }

        org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();
        scoreboard.getTeam("playerScore").setPrefix(name);
        scoreboard.getTeam("factionScore").setPrefix(factionName);
        scoreboard.getTeam("membersScore").setPrefix(onlineMembers);
        scoreboard.getTeam("worldScore").setPrefix(worldColor + worldName);
        scoreboard.getTeam("etherScore").setPrefix(ether);
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
            case "Lobby":
                return ChatColor.of("#5813BF");
            case "PVP":
                return ChatColor.of("#851818");
            case "Plots":
                return ChatColor.of("#E07A04");
        }
        return ChatColor.RESET;
    }

}
