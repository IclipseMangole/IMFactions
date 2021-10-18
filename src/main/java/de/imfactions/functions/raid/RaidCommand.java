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
import de.imfactions.util.Command.IMCommand;
import de.imfactions.util.UUIDFetcher;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class RaidCommand {

    private final IMFactions imFactions;
    private final Data data;
    private final FactionMemberUtil factionMemberUtil;
    private final FactionUtil factionUtil;
    private final FactionPlotUtil factionPlotUtil;
    private final RaidUtil raidUtil;
    private StringBuilder builder;
    private final RaidScheduler raidScheduler;
    private final Scoreboard scoreboard;

    public RaidCommand(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionUtil = data.getFactionUtil();
        factionPlotUtil = data.getFactionPlotUtil();
        factionMemberUtil = data.getFactionMemberUtil();
        raidUtil = data.getRaidUtil();
        raidScheduler = raidUtil.getRaidScheduler();
        scoreboard = data.getScoreboard();
    }

    @IMCommand(
            name = "raid",
            usage = "§c/raid",
            description = "Overview of all raid commands",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.imFactions.raid"
    )
    public void raid(CommandSender sender) {
        Player player = (Player) sender;
        builder = new StringBuilder();

        builder.append(data.getPrefix() + "§7Overview:" + "\n");
        add("raid", "Shows all raid commands");
        add("raid start", "Starts a new Raid and invites Faction Members");
        add("raid join", "Joins a started Raid");
        add("raid scout", "Scouts the next Faction");
        add("raid begin", "Begins to raid the scouting Faction");
        add("raid leave", "Leaves the Raid");
        add("raid infos", "Shows information about the last 9 Raids");

        player.sendMessage(builder.toString());

    }

    @IMCommand(
            name = "start",
            usage = "§c/raid start",
            description = "Invites the Faction members to join a new Raid",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.imFactions.raid.start",
            parent = "raid",
            noConsole = true
    )
    public void start(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if(!factionMemberUtil.isFactionMemberExists(uuid)){
            player.sendMessage(ChatColor.RED + "You aren't member of a Faction");
            return;
        }
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());
        if(factionMember.getRank() < 2){
            player.sendMessage(ChatColor.RED + "You have to be a higher rank to start a Raid");
            return;
        }
        if(player.getWorld().getName().equalsIgnoreCase("FactionPVP_world")){
            player.sendMessage(ChatColor.RED + "You can't start a Raid while being in the PVP-Zone");
            return;
        }
        if(raidUtil.isFactionRaiding(faction.getId())){
            player.sendMessage(ChatColor.RED + "Your Faction is already raiding");
            return;
        }
        if (!faction.canRaid()) {
            player.sendMessage(ChatColor.RED + "Your Faction needs at least 5⚡ Raid-Energy to start a Raid");
            return;
        }
        if (factionUtil.getRaidableFactions(faction.getId()).size() == 0) {
            player.sendMessage(ChatColor.RED + "There are currently no Factions that could be raided");
            return;
        }

        int raidID = raidUtil.getHighestRaidID() + 1;
        raidUtil.createPreparingRaid(raidID, faction.getId());
        Raid raid = raidUtil.getRaid(raidID);
        raidUtil.getRaidTeams().put(factionMember, raid);
        raidScheduler.startPreparingRaid(raidID, 60);

        for (Player member : factionMemberUtil.getOnlineMembers(faction.getId())) {
            if (member != player) {
                TextComponent join = new TextComponent(ChatColor.DARK_GREEN + "[Join]");
                join.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.DARK_GREEN + "Click to join the Raid").create()));
                join.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/raid join"));
                TextComponent message = new TextComponent(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " started a new Raid. You have 60 seconds to join. ");
                member.spigot().sendMessage(new ComponentBuilder(message).append(join).create());
            }
        }
        scoreboard.setRaidScoreboard(player, raid);
    }

    @IMCommand(
            name = "join",
            usage = "§c/raid join",
            description = "Joins the active Raid",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.imFactions.raid.join",
            parent = "raid",
            noConsole = true
    )
    public void join(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if(!factionMemberUtil.isFactionMemberExists(uuid)){
            player.sendMessage(ChatColor.RED + "You aren't member of a Faction");
            return;
        }
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());
        if (!raidUtil.isFactionRaiding(faction.getId())) {
            player.sendMessage(ChatColor.RED + "There is no active Raid to join");
            return;
        }
        int raidID = raidUtil.getActiveRaidID(faction.getId());
        Raid raid = raidUtil.getRaid(raidID);
        if (raidUtil.getRaidTeam(raidID).contains(factionMember)) {
            player.sendMessage(ChatColor.RED + "You joined the Raid already");
            return;
        }
        if (player.getWorld().getName().equalsIgnoreCase("FactionPVP_world")) {
            player.sendMessage(ChatColor.RED + "You can't join a Raid while being in the PVP_Zone");
            return;
        }
        if (!raid.getRaidState().equals(RaidState.PREPARING)) {
            player.sendMessage(ChatColor.RED + "The time to join the Raid is up");
            return;
        }

        for (FactionMember raidMember : raidUtil.getRaidTeam(raidID)) {
            Player raidPlayer = Bukkit.getPlayer(raidMember.getUuid());
            raidPlayer.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " joined the Raid");
        }
        raidUtil.getRaidTeams().put(factionMember, raid);
        player.sendMessage(ChatColor.GREEN + "You joined the Raid");
        scoreboard.setRaidScoreboard(player, raid);
    }

    @IMCommand(
            name = "scout",
            usage = "§c/raid scout",
            description = "The Team scouts a new random Faction",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.imFactions.raid.scout",
            parent = "raid",
            noConsole = true
    )
    public void scout(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            player.sendMessage(ChatColor.RED + "You aren't member of a Faction");
            return;
        }
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());
        if (!raidUtil.isFactionRaiding(faction.getId())) {
            player.sendMessage(ChatColor.RED + "Your Faction isn't raiding");
            return;
        }
        int raidID = raidUtil.getActiveRaidID(faction.getId());
        Raid raid = raidUtil.getRaid(raidID);
        if (!raidUtil.isFactionMemberJoinedRaid(factionMember)) {
            player.sendMessage("You aren't part of the Raidteam");
            return;
        }
        if (!raid.getRaidState().equals(RaidState.SCOUTING)) {
            player.sendMessage(ChatColor.RED + "You aren't in the Scouting Phase right now");
            return;
        }
        if (!factionPlotUtil.isLocationOnFactionPlot(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "Something went wrong");
            return;
        }
        FactionPlot scouted = factionPlotUtil.getFactionPlot(player.getLocation());
        int currentlyScouted = scouted.getFactionID();
        if (raidUtil.getScoutableFactions(raidID, currentlyScouted).size() == 0) {
            player.sendMessage(ChatColor.RED + "There is no other Faction to scout");
            return;
        }

        Faction newDefenders = raidUtil.getFactionForScout(raidID, scouted.getFactionID());
        raidScheduler.startScoutingRaid(raidID, newDefenders.getId(), 300);
    }

    @IMCommand(
            name = "begin",
            usage = "§c/raid begin",
            description = "The Team begins to raid an Faction",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.imFactions.raid.begin",
            parent = "raid",
            noConsole = true
    )
    public void begin(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            player.sendMessage(ChatColor.RED + "You aren't member of a Faction");
            return;
        }
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());
        if (!raidUtil.isFactionRaiding(faction.getId())) {
            player.sendMessage(ChatColor.RED + "You are cringe. You aren't raiding");
            return;
        }
        int raidID = raidUtil.getActiveRaidID(faction.getId());
        Raid raid = raidUtil.getRaid(raidID);
        if (!raidUtil.getRaidTeam(raidID).contains(factionMember)) {
            player.sendMessage(ChatColor.RED + "You aren't part of the Raidteam");
            return;
        }
        if (!raid.getRaidState().equals(RaidState.SCOUTING)) {
            player.sendMessage(ChatColor.RED + "You can only skip the Scouting Phase");
            return;
        }
        if (!factionPlotUtil.isLocationOnFactionPlot(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "Something went wrong");
            return;
        }

        FactionPlot factionPlot = factionPlotUtil.getFactionPlot(player.getLocation());
        Faction defenders = factionUtil.getFaction(factionPlot.getFactionID());
        raidUtil.updateRaidToRaiding(raidID, defenders.getId());
        raidUtil.getRaidScheduler().cancelScoutingRaid(raidID);
        raidUtil.getRaidScheduler().startRaidingRaid(raidID, 30 * 60);
    }

    @IMCommand(
            name = "leave",
            usage = "§c/raid leave",
            description = "The Player leaves the Raid",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.imFactions.raid.leave",
            parent = "raid",
            noConsole = true
    )
    public void leave(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            player.sendMessage(ChatColor.RED + "You aren't member of a Faction");
            return;
        }
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());
        if (!raidUtil.isFactionRaiding(faction.getId())) {
            player.sendMessage(ChatColor.RED + "There is no active Raid");
            return;
        }
        if (!raidUtil.isFactionMemberJoinedRaid(factionMember)) {
            player.sendMessage(ChatColor.RED + "To leave the Raid you have to join it first. Stupid Motherf*****");
            return;
        }

        int raidID = raidUtil.getActiveRaidID(faction.getId());
        Raid raid = raidUtil.getRaid(raidID);
        ArrayList<FactionMember> team = raidUtil.getRaidTeam(raidID);
        raidUtil.getRaidTeams().remove(raid, factionMember);

        if (team.size() == 1) {
            player.sendMessage(ChatColor.GREEN + "You left the Raid. It isn't active anymore");
            player.teleport(factionPlotUtil.getFactionPlot(faction.getId()).getHome());
            scoreboard.setScoreboard(player);
            if (raid.getRaidState().equals(RaidState.PREPARING)) {
                for (Player member : factionMemberUtil.getOnlineMembers(faction.getId())) {
                    member.sendMessage(ChatColor.RED + "The Raid got canceled");
                }
                raidUtil.getRaidScheduler().cancelPreparingRaid(raidID);
                raidUtil.deleteRaid(raid);
                return;
            }
            Faction raided = factionUtil.getFaction(raid.getFactionIdDefenders());
            raided.setGettingRaided(false);
            if (raid.getRaidState().equals(RaidState.SCOUTING)) {
                raidUtil.getRaidScheduler().cancelScoutingRaid(raidID);
                raid.setRaidState(RaidState.DONE);
                return;
            }
            if (raid.getRaidState().equals(RaidState.RAIDING)) {
                raidUtil.getRaidScheduler().cancelRaidingRaid(raidID);
                raid.setRaidState(RaidState.DONE);
                raided.setRaidProtection(System.currentTimeMillis() + 1000 * 60);
                return;
            }
        }
        player.sendMessage(ChatColor.GREEN + "You left the Raid");
        player.teleport(factionPlotUtil.getFactionPlot(faction.getId()).getHome());
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
        for (Player member : factionMemberUtil.getOnlineMembers(faction.getId())) {
            if (member != factionMember)
                member.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.RED + " left the Raid");
        }
        scoreboard.setScoreboard(player);
    }

    @IMCommand(
            name = "infos",
            usage = "§c/raid infos",
            description = "Shows the last 9 Raids",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.imFactions.raid.infos",
            parent = "raid"
    )
    public void infos(CommandSender sender) {
        Player player = (Player) sender;
    }

    private void add(String usage, String description) {
        builder.append("\n" + data.getSymbol() + "§e/" + usage + "§8: §7 " + description + ChatColor.RESET);
    }
}
