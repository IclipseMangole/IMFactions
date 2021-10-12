package de.imfactions.functions.faction;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.Scheduler;
import de.imfactions.functions.WorldLoader;
import de.imfactions.functions.factionMember.FactionMember;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.functions.factionPlot.FactionPlot;
import de.imfactions.functions.factionPlot.FactionPlotUtil;
import de.imfactions.functions.user.UserUtil;
import de.imfactions.util.Command.IMCommand;
import de.imfactions.util.LocationBuilder;
import de.imfactions.util.LocationChecker;
import de.imfactions.util.UUIDFetcher;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class FactionCommand {

    private StringBuilder builder;
    private final IMFactions imFactions;
    private final Data data;
    private final FactionUtil factionUtil;
    private final FactionMemberUtil factionMemberUtil;
    private final UserUtil userUtil;
    private final FactionPlotUtil factionPlotUtil;
    private final WorldLoader worldLoader;
    private final Scheduler scheduler;

    public FactionCommand(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        this.userUtil = data.getUserUtil();
        factionUtil = data.getFactionUtil();
        factionMemberUtil = data.getFactionMemberUtil();
        factionPlotUtil = data.getFactionPlotUtil();
        this.worldLoader = new WorldLoader(imFactions);
        this.scheduler = data.getScheduler();
    }

    @IMCommand(
            name = "faction",
            usage = "§c/faction",
            description = "faction.description",
            permissions = "im.imFactions.faction"
    )
    public void execute(CommandSender sender) {
        if (sender.hasPermission("im.imFactions.faction.*")) {
            builder = new StringBuilder();
            builder.append(data.getPrefix()).append("§eOverview§8:").append("\n");
            add("/faction found <Name> <Shortcut>", "Makes the Player King of a new Faction");
            add("/faction leave", "The Player leaves a Faction");
            add("/faction invite <Player>", "Invites a new Player to the Faction");
            //add("/faction accept <Name>", "Accepts a Faction Invite");
            add("/faction kick <Player>", "Kicks a Player from the Faction");
            add("/faction promote <Player>", "The Player gets a higher Rank");
            add("/faction demote <Player>", "The Player gets a lower Rank");
            add("/faction info", "Infos about your Faction Or Invites from Factions");
            add("/faction members", "Shows all Members of your Faction");
            add("/faction home", "The player gets teleported to the Faction´s Plot");
            add("/faction sethome", "Sets the Home-Spawnpoint for the Faction");
            sender.sendMessage(String.valueOf(builder));
        }
    }

    @IMCommand(
            name = "found",
            usage = "§c/faction found <Name> <Shortcut>",
            description = "faction.found.description",
            minArgs = 2,
            maxArgs = 2,
            parent = "faction",
            permissions = "im.imFactions.faction.found",
            noConsole = true
    )
    public void found(CommandSender sender, String name, String shortcut) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if (factionMemberUtil.isFactionMemberExists(uuid)) {
            player.sendMessage(ChatColor.RED + "You are already member of a Faction");
            return;
        }
        if (factionUtil.isFactionExists(name)) {
            player.sendMessage(ChatColor.RED + "This Faction already exists");
            return;
        }
        if (name.length() > 31) {
            player.sendMessage(ChatColor.RED + "The name of your Faction is too long. The maximum is 31 :)");
            return;
        }
        if (shortcut.length() != 3) {
            player.sendMessage(ChatColor.RED + "The shortcut has to be out of 3 characters");
            return;
        }
        if (factionPlotUtil.getLoadingFactionPlots() >= 3) {
            player.sendMessage(ChatColor.RED + "Because there are so many people founding Factions, you have to wait a short time in order to prevent the Server kacking up");
            return;
        }

        //Faction
        int factionId = factionUtil.getHighestFactionID() + 1;
        factionUtil.createFaction(factionId, name, shortcut);
        factionMemberUtil.createFactionMember(uuid, factionId, 3);
        //FactionPlot
        factionPlotUtil.createFactionPlot(factionId);
        worldLoader.loadMap("FactionPlot", factionPlotUtil.getFactionPlot(factionId).getHome());
        player.sendMessage(ChatColor.GREEN + "You founded the Faction " + ChatColor.YELLOW + name);
    }

    @IMCommand(
            name = "leave",
            usage = "§c/faction leave",
            description = "faction.leave.description",
            minArgs = 0,
            maxArgs = 0,
            parent = "faction",
            permissions = "im.imFactions.faction.leave",
            noConsole = true
    )
    public void leave(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            player.sendMessage(ChatColor.RED + "You aren't member of a Faction");
            return;
        }

        int factionID = factionMemberUtil.getFactionMember(uuid).getFactionID();
        Faction faction = factionUtil.getFaction(factionID);
        FactionPlot factionPlot = factionPlotUtil.getFactionPlot(factionID);
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);

        if (player.getWorld().getName().equalsIgnoreCase("FactionPlots_world")) {
            player.teleport(data.getWorldSpawn());
        }
        factionMemberUtil.leaveFaction(factionMember);
        //Last one in Faction
        if (faction.getMemberAmount() == 1) {
            worldLoader.deleteMap(factionPlot.getEdgeDownFrontLeft());
            factionPlotUtil.deleteFactionPlot(factionPlot);
            factionUtil.deleteFaction(faction);
            player.sendMessage(ChatColor.GREEN + "You left the Faction. " + ChatColor.YELLOW + faction.getName() + ChatColor.GREEN + " isn't existing anymore");
            return;
        }
        for (FactionMember teamMember : factionMemberUtil.getFactionMembers(factionID))
            Bukkit.getPlayer(teamMember.getUuid()).sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " left the Faction");
        player.sendMessage(ChatColor.GREEN + "You left the Faction");
        //King leaves Faction
        if (factionMember.getRank() == 3) {
            ArrayList<FactionMember> highestMembers = factionMemberUtil.getHighestFactionMembers(factionID);
            Random random = new Random();
            FactionMember newKing = highestMembers.get(random.nextInt(highestMembers.size()));
            newKing.setRank(3);
            for (FactionMember teamMember : factionMemberUtil.getFactionMembers(factionID))
                Bukkit.getPlayer(teamMember.getUuid()).sendMessage(ChatColor.YELLOW + Bukkit.getPlayer(newKing.getUuid()).getName() + ChatColor.GREEN + " is the new King of the Faction");
        }
    }

    @IMCommand(
            name = "invite",
            usage = "§c/faction invite <Player>",
            description = "faction.invite.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.imFactions.faction.invite",
            noConsole = true
    )
    public void invite(CommandSender sender, String name) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            player.sendMessage(ChatColor.RED + "WTF! You aren't member of a Faction");
            return;
        }
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        if (factionMember.getRank() == 0) {
            player.sendMessage(ChatColor.RED + "Your rank doesn't grant you the permission to invite players");
            return;
        }
        if (!userUtil.isUserExists(name)) {
            player.sendMessage(ChatColor.RED + "This player doesn't exist on this Server");
            return;
        }
        Player invited = Bukkit.getPlayer(name);
        UUID uuidInvited = UUIDFetcher.getUUID(player);
        if (!invited.isOnline()) {
            player.sendMessage(ChatColor.RED + "The player isn't online");
            return;
        }
        if (factionMemberUtil.isFactionMemberExists(uuidInvited)) {
            player.sendMessage(ChatColor.RED + "This player is already member of a Faction");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "You invited " + ChatColor.YELLOW + name + ChatColor.GREEN + " to your Faction");

        int factionID = factionMember.getFactionID();
        Faction faction = factionUtil.getFaction(factionID);

        TextComponent message = new TextComponent(ChatColor.GREEN + "You got invited by the Faction " + ChatColor.YELLOW + faction.getName());
        TextComponent accept = new TextComponent(ChatColor.DARK_GREEN + " [Accept]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction accept " + faction.getName()));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.YELLOW + "Click to join").create()));
        invited.spigot().sendMessage(new ComponentBuilder(message + "" + accept).create());
    }

    @IMCommand(
            name = "accept",
            usage = "§c/faction accept <Name>",
            description = "faction.accept.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.imFactions.faction.accept",
            noConsole = true
    )
    public void accept(CommandSender sender, String name) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if (factionMemberUtil.isFactionMemberExists(uuid)) {
            player.sendMessage(ChatColor.RED + "You are already member of a Faction");
            return;
        }
        if (!factionUtil.isFactionExists(name)) {
            player.sendMessage(ChatColor.RED + "This Factions doesn't exist");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "You joined the Faction " + ChatColor.YELLOW + name);
        Faction faction = factionUtil.getFaction(name);
        factionMemberUtil.createFactionMember(uuid, faction.getId(), 0);
        faction.memberJoin();
        for (FactionMember factionMember : factionMemberUtil.getFactionMembers(faction.getId()))
            Bukkit.getPlayer(factionMember.getUuid()).sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " has joined the Faction");
    }

    @IMCommand(
            name = "kick",
            usage = "§c/faction kick <Player>",
            description = "faction.kick.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.imFactions.faction.kick",
            noConsole = true
    )
    public void kick(CommandSender sender, String kick) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            player.sendMessage(ChatColor.RED + "You aren't in a Faction you Hu*******");
            return;
        }
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());
        if (!userUtil.isUserExists(kick)) {
            player.sendMessage(ChatColor.RED + "This player doesn't exist");
            return;
        }
        UUID uuidKick = UUIDFetcher.getUUID(kick);
        if (!factionMemberUtil.isFactionMemberExists(uuidKick)) {
            player.sendMessage(ChatColor.RED + "This player isn't member of your Faction");
            return;
        }
        FactionMember factionMemberKick = factionMemberUtil.getFactionMember(uuidKick);
        if (!factionMemberUtil.isFactionMemberInFaction(factionMemberKick, faction)) {
            player.sendMessage(ChatColor.RED + "This player isn't member of your Faction");
            return;
        }
        if (factionMember == factionMemberKick) {
            player.sendMessage(ChatColor.RED + "You can't kick yourself. Use " + ChatColor.YELLOW + "/faction leave " + ChatColor.RED + "to leave your Faction");
            return;
        }
        if (factionMember.getRank() <= factionMemberKick.getRank()) {
            player.sendMessage(ChatColor.RED + "You have to be higher ranked to kick this member");
            return;
        }
        Player kicked = Bukkit.getPlayer(uuidKick);
        factionMemberUtil.leaveFaction(factionMemberKick);
        faction.memberLeave();
        player.sendMessage(ChatColor.GREEN + "You kicked " + ChatColor.YELLOW + kicked.getName() + ChatColor.GREEN + " out of the Faction");
        kicked.sendMessage(ChatColor.RED + "You got kicked out of th Faction");
        for (FactionMember factionMembers : factionMemberUtil.getFactionMembers(faction.getId()))
            if (factionMembers != factionMember)
                Bukkit.getPlayer(factionMembers.getUuid()).sendMessage(ChatColor.YELLOW + kicked.getName() + ChatColor.GREEN + " got kicked out of the Faction");
    }

    @IMCommand(
            name = "promote",
            usage = "§c/faction promote <Player>",
            description = "faction.promote.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.imFactions.faction.promote",
            noConsole = true
    )
    public void promote(CommandSender sender, String promote) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            player.sendMessage(ChatColor.RED + "You aren't member of a Faction");
            return;
        }
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());
        if (!userUtil.isUserExists(promote)) {
            player.sendMessage(ChatColor.RED + "This player doesn't exist");
            return;
        }
        Player promoted = Bukkit.getPlayer(promote);
        UUID uuidPromoted = UUIDFetcher.getUUID(promoted);
        if (!factionMemberUtil.isFactionMemberExists(uuidPromoted)) {
            player.sendMessage(ChatColor.RED + "This player isn't member of your Faction");
            return;
        }
        FactionMember factionMemberPromoted = factionMemberUtil.getFactionMember(uuidPromoted);
        if (!factionMemberUtil.isFactionMemberInFaction(factionMemberPromoted, faction)) {
            player.sendMessage(ChatColor.RED + "This player isn't member of your Faction");
            return;
        }
        if (factionMember == factionMemberPromoted) {
            player.kickPlayer(ChatColor.RED + "I'm not stupid fu***** bas****");
            return;
        }
        if (factionMember.getRank() <= factionMemberPromoted.getRank() || factionMember.getRank() < 2) {
            player.sendMessage(ChatColor.RED + "You have to be higher ranked to promote this player");
            return;
        }

        factionMemberPromoted.promote();
        promoted.sendMessage(ChatColor.GREEN + "You got promoted to " + factionMemberPromoted.getRankname());
        player.sendMessage(ChatColor.GREEN + "You promoted " + ChatColor.YELLOW + promote + ChatColor.GREEN + " to " + factionMemberPromoted.getRankname());
        if (factionMember.getRank() == 3 && factionMemberPromoted.getRank() == 2) {
            factionMember.demote();
            for (FactionMember factionMembers : factionMemberUtil.getFactionMembers(faction.getId()))
                if (factionMembers != factionMember && factionMembers != factionMemberPromoted)
                    Bukkit.getPlayer(factionMembers.getUuid()).sendMessage(ChatColor.YELLOW + promote + ChatColor.GREEN + " is the new " + factionMemberPromoted.getRankname());
        }
    }

    @IMCommand(
            name = "demote",
            usage = "§c/faction demote <Player>",
            description = "faction.demote.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.imFactions.faction.demote",
            noConsole = true
    )
    public void demote(CommandSender sender, String demote) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            player.sendMessage(ChatColor.RED + "You aren't member of a Faction");
            return;
        }
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());
        if (!userUtil.isUserExists(demote)) {
            player.sendMessage(ChatColor.RED + "This player doesn't exist");
            return;
        }
        Player demoted = Bukkit.getPlayer(demote);
        UUID uuidDemoted = UUIDFetcher.getUUID(demoted);
        if (!factionMemberUtil.isFactionMemberExists(uuidDemoted)) {
            player.sendMessage(ChatColor.RED + "This player isn't member of your Faction");
            return;
        }
        FactionMember factionMemberDemoted = factionMemberUtil.getFactionMember(uuidDemoted);
        if (!factionMemberUtil.isFactionMemberInFaction(factionMemberDemoted, faction)) {
            player.sendMessage(ChatColor.RED + "This player isn't member of your Faction");
            return;
        }
        if (factionMember == factionMemberDemoted) {
            player.kickPlayer(ChatColor.RED + "You can't demote yourself. This would be outrages");
            return;
        }
        if (factionMember.getRank() <= factionMemberDemoted.getRank() || factionMember.getRank() < 2) {
            player.sendMessage(ChatColor.RED + "You have to be higher ranked to demote this player");
            return;
        }
        if (factionMemberDemoted.getRank() == 0) {
            player.sendMessage(ChatColor.RED + "He has already the lowest rank");
            return;
        }

        factionMemberDemoted.demote();
    }

    @IMCommand(
            name = "info",
            usage = "§c/faction info",
            description = "faction.info.description",
            minArgs = 0,
            maxArgs = 0,
            parent = "faction",
            permissions = "im.imFactions.faction.info",
            noConsole = true
    )
    public void info(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            player.sendMessage(ChatColor.RED + "You aren't member of a Faction");
            return;
        }

        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());
        StringBuilder message = new StringBuilder(ChatColor.GRAY + "---------------" + ChatColor.YELLOW + "FactionInfos" + ChatColor.GRAY + "---------------\n");
        //name
        message.append(ChatColor.GRAY + "Name: " + ChatColor.YELLOW + faction.getName() + "\n");
        //shortcut
        message.append(ChatColor.GRAY + "Shortcut: " + ChatColor.YELLOW + faction.getShortcut() + "\n");
        //founding date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        message.append(ChatColor.GRAY + "Founding Date: " + ChatColor.YELLOW + simpleDateFormat.format(faction.getFoundingDate()) + "\n");
        //RaidProtection
        int totalSecs = (int) ((int) faction.getRaidProtection() - System.currentTimeMillis()) / 1000;
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        message.append(ChatColor.GRAY + "RaidProtection: " + ChatColor.YELLOW + timeString + "\n");
        //member amount
        message.append(ChatColor.GRAY + "Members: " + ChatColor.YELLOW + faction.getMemberAmount() + "\n");
        //members
        TextComponent textComponent = new TextComponent(ChatColor.GRAY + "[" + ChatColor.YELLOW + "Click Here" + ChatColor.GRAY + "]");
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction members"));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.YELLOW + "See a List of all Members").create()));
        player.spigot().sendMessage(textComponent);
    }

    @IMCommand(
            name = "members",
            usage = "§c/faction members",
            description = "faction.members.description",
            minArgs = 0,
            maxArgs = 0,
            parent = "faction",
            permissions = "im.imFactions.faction.members",
            noConsole = true
    )
    public void infoMembers(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            player.sendMessage(ChatColor.RED + "You aren't member of a Faction");
            return;
        }

        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());
        ArrayList<FactionMember> members = factionMemberUtil.getFactionMembers(faction.getId());
        StringBuilder message = new StringBuilder();
        for (int rank = 3; rank > 0; rank--) {
            switch (rank) {
                case 3:
                    message.append(ChatColor.GRAY + "---------------------" + ChatColor.DARK_RED + "KING" + ChatColor.GRAY + "---------------------\n");
                case 2:
                    message.append(ChatColor.GRAY + "---------------------" + ChatColor.DARK_PURPLE + "VETERAN" + ChatColor.GRAY + "---------------------\n");
                case 1:
                    message.append(ChatColor.GRAY + "---------------------" + ChatColor.BLUE + "KNIGHT" + ChatColor.GRAY + "---------------------\n");
                default:
                    message.append(ChatColor.GRAY + "---------------------" + ChatColor.DARK_GREEN + "MEMBER" + ChatColor.GRAY + "---------------------\n");
            }
            for (FactionMember member : members) {
                if (member.getRank() == rank) {
                    String name = UUIDFetcher.getName(uuid);
                    for (int i = 0; i < (35 - name.length() / 2); i++) {
                        message.append(" ");
                    }
                    message.append(member.getRankColor() + name + "\n");
                }
            }
        }
        player.sendMessage(message.toString());
    }

    @IMCommand(
            name = "home",
            usage = "faction home",
            description = "faction.home.description",
            minArgs = 0,
            maxArgs = 0,
            parent = "faction",
            permissions = "im.imFactions.faction.home"
    )
    public void home(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if (!factionMemberUtil.isFactionMemberExists(uuid)) {
            player.sendMessage(ChatColor.RED + "You aren't member of a Faction");
            return;
        }
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        if (factionPlotUtil.getFactionPlot(factionMember.getFactionID()).isLoading()) {
            player.sendMessage(ChatColor.RED + "Your FactionPlot is loading. Please wait");
            return;
        }
        factionUtil.teleportHome(factionMember);
    }

    @IMCommand(
            name = "sethome",
            usage = "faction sethome",
            description = "faction.sethome.description",
            minArgs = 0,
            maxArgs = 0,
            parent = "faction",
            permissions = "im.imFactions.faction.sethome"
    )
    public void sethome(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if (factionMemberUtil.isFactionMemberExists(uuid)) {
            player.sendMessage(ChatColor.RED + "You aren't member of a Faction");
            return;
        }
        if (!player.getLocation().getWorld().getName().equalsIgnoreCase("FactionPlots_world")) {
            player.sendMessage(ChatColor.RED + "You can only change your home at your FactionPlot");
            return;
        }
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        FactionPlot factionPlot = factionPlotUtil.getFactionPlot(factionMember.getFactionID());
        Location edgeDownFrontLeft = factionPlot.getEdgeDownFrontLeft();
        Location edgeUpBackRight = factionPlot.getEdgeUpBackRight();
        if (!LocationChecker.isLocationInsideCube(player.getLocation(), edgeDownFrontLeft, edgeUpBackRight)) {
            player.sendMessage(ChatColor.RED + "You have to set the home inside the FactionPlot");
            return;
        }

        factionPlot.setHome(player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Set home to " + ChatColor.YELLOW + LocationBuilder.getString(player.getLocation()));
    }

    private void add(String usage, String description) {
        builder.append("\n" + data.getSymbol() + "§e" + usage + "§8: §7 " + description + ChatColor.RESET);
    }
}
