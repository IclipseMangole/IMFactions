package de.imfactions.functions.raid;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.Scoreboard;
import de.imfactions.functions.faction.Faction;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMember;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.functions.factionPlot.FactionPlot;
import de.imfactions.functions.factionPlot.FactionPlotUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public class RaidScheduler implements Listener {

    private final Data data;
    private final IMFactions imFactions;
    private final RaidUtil raidUtil;
    private final FactionUtil factionUtil;
    private final FactionPlotUtil factionPlotUtil;
    private final FactionMemberUtil factionMemberUtil;
    private final Scoreboard scoreboard;
    private final HashMap<Integer, BukkitTask> preparingRaids = new HashMap<>();
    private final HashMap<Integer, BukkitTask> scoutingRaids = new HashMap<>();
    private final HashMap<Integer, BukkitTask> raidingRaids = new HashMap<>();

    public RaidScheduler(Data data) {
        this.data = data;
        imFactions = data.getImFactions();
        raidUtil = data.getRaidUtil();
        factionUtil = data.getFactionUtil();
        factionPlotUtil = data.getFactionPlotUtil();
        factionMemberUtil = data.getFactionMemberUtil();
        scoreboard = data.getScoreboard();
    }

    public void startPreparingRaid(int raidID, int seconds){
        if (preparingRaids.containsKey(raidID))
            return;

        Raid raid = raidUtil.getRaid(raidID);
        raid.setRaidState(RaidState.PREPARING);

        preparingRaids.put(raidID, Bukkit.getScheduler().runTaskTimer(imFactions, new Runnable() {

            int timer = seconds;
            final int ID = raidID;
            final Raid raid = raidUtil.getRaid(ID);

            @Override
            public void run() {
                ArrayList<Player> raidTeam = new ArrayList<>();
                for (FactionMember factionMember : raidUtil.getRaidTeam(ID)) {
                    raidTeam.add(factionMemberUtil.getPlayer(factionMember.getUuid()));
                }
                if (timer <= 0) {
                    if (raidUtil.getScoutableFactions(raid).size() == 0) {
                        for (Player player : raidTeam)
                            player.sendMessage(ChatColor.RED + "Error. Found no Faction to raid");
                        cancelPreparingRaid(ID);
                        return;
                    }
                    raidUtil.scoutNextFaction(raid);
                    return;
                }
                if (timer == 10) {
                    for (Player player : raidTeam) {
                        player.sendMessage(ChatColor.GREEN + "The Scouting Phase will begin in " + timer + " seconds");
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 1.0F);
                    }
                }
                if (timer <= 5) {
                    for (Player player : raidTeam) {
                        player.sendMessage(ChatColor.GREEN + "The Scouting Phase will begin in " + timer + " seconds");
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 1.0F);
                    }
                }
                for (Player player : raidTeam)
                    setActionBarTimer(player, timer, "Preparing: ");
                timer--;
            }
        }, 0, 20));
    }

    public void startScoutingRaid(int raidID, int defendersID, int seconds) {
        if (scoutingRaids.containsKey(raidID))
            return;

        Raid raid = raidUtil.getRaid(raidID);
        raid.setRaidState(RaidState.SCOUTING);
        Faction defenders = factionUtil.getFaction(defendersID);
        FactionPlot defendersPlot = factionPlotUtil.getFactionPlot(defendersID);
        raid.setFactionIdDefenders(defenders.getId());
        defenders.setGettingRaided(true);
        ArrayList<Player> raidTeam = new ArrayList<>();
        for (FactionMember factionMember : raidUtil.getRaidTeam(raidID)) {
            raidTeam.add(factionMemberUtil.getPlayer(factionMember.getUuid()));
        }
        for (Player player : raidTeam) {
            player.teleport(defendersPlot.getRaidSpawn());
            player.sendTitle(ChatColor.YELLOW + defenders.getName(), "Scouting", 5, 20, 5);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 1.0F);
        }

        scoutingRaids.put(raidID, Bukkit.getScheduler().runTaskTimer(imFactions, new Runnable() {

            int timer = seconds;
            final ArrayList<Player> members = raidTeam;

            @Override
            public void run() {
                if (timer <= 0) {
                    raidUtil.raidFaction(raidUtil.getRaid(raidID));
                    return;
                }
                if (timer == 10) {
                    for (Player player : members) {
                        player.sendMessage(ChatColor.GREEN + "The Raiding Phase will begin in " + timer + " seconds");
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 1.0F);
                    }
                }
                if (timer <= 5) {
                    for (Player player : members) {
                        player.sendMessage(ChatColor.GREEN + "The Raiding Phase will begin in " + timer + " seconds");
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 1.0F);
                    }
                }
                for (Player player : members)
                    setActionBarTimer(player, timer, "Scouting: ");
                timer--;
            }
        }, 0, 20));
    }

    public void startRaidingRaid(int raidID, int seconds) {
        if (raidingRaids.containsKey(raidID))
            return;

        Raid raid = raidUtil.getRaid(raidID);
        FactionPlot defendersPlot = factionPlotUtil.getFactionPlot(raid.getFactionIdDefenders());
        Faction defenders = factionUtil.getFaction(raid.getFactionIdDefenders());
        ArrayList<Player> raidTeam = new ArrayList<>();
        for (FactionMember factionMember : raidUtil.getRaidTeam(raidID)) {
            raidTeam.add(factionMemberUtil.getPlayer(factionMember.getUuid()));
        }
        for (Player player : raidTeam) {
            player.teleport(defendersPlot.getRaidSpawn());
            player.sendTitle(ChatColor.YELLOW + defenders.getName(), "Raiding", 5, 20, 5);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
        }

        raidingRaids.put(raidID, Bukkit.getScheduler().runTaskTimer(imFactions, new Runnable() {
            int timer = seconds;
            final int ID = raidID;
            final ArrayList<Player> members = raidTeam;

            @Override
            public void run() {
                if (timer <= 0) {
                    Raid raid = raidUtil.getRaid(ID);
                    raidUtil.endRaid(raid);
                    return;
                }
                for (FactionMember factionMember : raidUtil.getRaidTeam(ID)) {
                    Player player = Bukkit.getPlayer(factionMember.getUuid());
                    setActionBarTimer(player, timer, "Raiding: ");
                }
                if (timer == 60) {
                    for (Player player : members) {
                        player.sendMessage(ChatColor.GREEN + "The Raiding Phase will end in " + timer + " seconds");
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 1.0F);
                    }
                }
                if (timer == 10) {
                    for (Player player : members) {
                        player.sendMessage(ChatColor.GREEN + "The Raiding Phase will end in " + timer + " seconds");
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 1.0F);
                    }
                }
                if (timer <= 5) {
                    for (Player player : members) {
                        player.sendMessage(ChatColor.GREEN + "The Raiding Phase will end in " + timer + " seconds");
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 1.0F);
                    }
                }
                timer--;
            }
        }, 0, 20));
    }

    public void cancelPreparingRaid(int raidID) {
        if (preparingRaids.containsKey(raidID)) {
            preparingRaids.get(raidID).cancel();
            preparingRaids.remove(raidID);
        }
    }

    public void cancelScoutingRaid(int raidID) {
        if (scoutingRaids.containsKey(raidID)) {
            scoutingRaids.get(raidID).cancel();
            scoutingRaids.remove(raidID);
        }
    }

    public void cancelRaidingRaid(int raidID) {
        if (raidingRaids.containsKey(raidID)) {
            raidingRaids.get(raidID).cancel();
            raidingRaids.remove(raidID);
        }
        setScoreboard(raidID);
    }

    private void setScoreboard(int raidID) {
        for (FactionMember factionMember : raidUtil.getRaidTeam(raidID)) {
            Player player = factionMemberUtil.getPlayer(factionMember.getUuid());
            scoreboard.setScoreboard(player);
        }
    }

    public void setActionBarTimer(Player player, int timer, String prefix) {
        int minutes = timer / 60;
        int seconds = timer % 60;
        String time = ChatColor.GRAY + prefix + ChatColor.GOLD + ChatColor.BOLD + String.format("%02d", minutes) + ":" +
                String.format("%02d", seconds);
        sendActionBar(player, new StringBuilder(time));
    }

    private void sendActionBar(Player player, StringBuilder message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message.toString()));
    }

    public HashMap<Integer, BukkitTask> getPreparingRaids() {
        return preparingRaids;
    }

    public HashMap<Integer, BukkitTask> getScoutingRaids() {
        return scoutingRaids;
    }

    public HashMap<Integer, BukkitTask> getRaidingRaids() {
        return raidingRaids;
    }
}
