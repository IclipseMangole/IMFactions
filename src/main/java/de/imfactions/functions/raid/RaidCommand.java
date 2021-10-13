package de.imfactions.functions.raid;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.Scheduler;
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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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
    private final Scheduler scheduler;
    private final RaidScheduler raidScheduler;

    public RaidCommand(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionUtil = data.getFactionUtil();
        factionPlotUtil = data.getFactionPlotUtil();
        factionMemberUtil = data.getFactionMemberUtil();
        raidUtil = data.getRaidUtil();
        scheduler = data.getScheduler();
        raidScheduler = raidUtil.getRaidScheduler();
    }

    @IMCommand(
            name = "raid",
            usage = "§c/raid",
            description = "Overview of all raid commands",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.factions.raid"
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
            permissions = "im.factions.raid.start",
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
        if(!faction.canRaid()){
            player.sendMessage(ChatColor.RED + "Your Faction needs at least 5⚡ Raid-Energy to start a Raid");
            return;
        }
        if(factionUtil.getRaidableFactions().size() < 2){
            player.sendMessage(ChatColor.RED + "There are currently no Factions that could be raided");
            return;
        }

        int raidID = raidUtil.getHighestRaidID() + 1;
        raidUtil.createPreparingRaid(raidID, faction.getId());
        Raid raid = raidUtil.getRaid(raidID);
        raidUtil.getRaidTeams().put(raid, factionMemberUtil.getFactionMember(uuid));
        faction.raid();
        raidScheduler.startPreparingRaid(raidID, 60);

        factionMemberUtil.getOnlineMembers(faction.getId()).forEach(member -> {
            TextComponent textComponent = new TextComponent("[HERE]");
            textComponent.setColor(net.md_5.bungee.api.ChatColor.of("990000"));
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join the Raid").create()));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/raid join"));
            ComponentBuilder componentBuilder = new ComponentBuilder();
            componentBuilder.append("A new Raid has been started. You have 60 seconds to join. Click ").color(net.md_5.bungee.api.ChatColor.of("CC4400"));
            textComponent.setExtra(componentBuilder.getParts());
            member.spigot().sendMessage(textComponent);
        });
    }

    @IMCommand(
            name = "join",
            usage = "§c/raid join",
            description = "Joins the active Raid",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.factions.raid.join",
            parent = "raid"
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
        if(!raidUtil.isFactionRaiding(faction.getId())){
            player.sendMessage(ChatColor.RED + "There is no active Raid to join");
            return;
        }
        int raidID = raidUtil.getActiveRaidID(faction.getId());
        Raid raid = raidUtil.getRaid(raidID);
        if(player.getWorld().getName().equalsIgnoreCase("FactionPVP_world")){
            player.sendMessage(ChatColor.RED + "You can't join a Raid while being in the PVP_Zone");
            return;
        }
        if(!raid.getRaidState().equals(RaidState.PREPARING)){
            player.sendMessage(ChatColor.RED + "The time to join the Raid is up");
            return;
        }

        for(FactionMember raidMember : raidUtil.getRaidTeam(raidID)){
            Player raidPlayer = Bukkit.getPlayer(raidMember.getUuid());
            raidPlayer.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " joined the Raid");
        }
        raidUtil.getRaidTeams().put(raid, factionMember);
        player.sendMessage(ChatColor.GREEN + "You joined the Raid");
    }

    @IMCommand(
            name = "scout",
            usage = "§c/raid scout",
            description = "The Team scouts a new random Faction",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.factions.raid.scout",
            parent = "raid"
    )
    public void scout(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if(!factionMemberUtil.isFactionMemberExists(uuid)){
            player.sendMessage(ChatColor.RED + "You aren't member of a Faction");
            return;
        }
        FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
        Faction faction = factionUtil.getFaction(factionMember.getFactionID());
        if(!raidUtil.isFactionRaiding(faction.getId())){

        }





        if(factionMemberUtil.isFactionMemberExists(uuid)){
            FactionMember factionUser = factionMemberUtil.getFactionMember(uuid);
            int factionID = factionUser.getFactionID();
            if(raidUtil.isFactionRaiding(factionID)){
                if(raidUtil.isFactionMemberJoinedRaid(factionMemberUtil.getFactionMember(uuid))){
                    int raidID = raidUtil.getActiveRaidID(factionID);
                    Raid raid = raidUtil.getRaid(raidID);
                    if(raid.getRaidState().equals("scouting")){
                        FactionPlot scouting = factionPlotUtil.getFactionPlot(player.getLocation());
                        Faction newFaction = raidUtil.getFactionForScout(raidID, scouting.getFactionID());
                        FactionPlot newFactionPlot = factionPlotUtil.getFactionPlot(newFaction.getId());
                        raidUtil.getRaidTeam(raidID).forEach(member -> {
                            Player player1 = Bukkit.getPlayer(member.getUuid());
                            player1.teleport(newFactionPlot.getRaidSpawn());
                        });
                    }else{
                        player.sendMessage("§cYour Faction can't scout right now");
                    }
                }else{
                    player.sendMessage("§cYou didn't join the Raid");
                }
            }else{
                player.sendMessage("§cYour Faction isn't raiding");
            }
        }else{
            player.sendMessage("§cYou aren't in a Faction");
        }

    }

    @IMCommand(
            name = "begin",
            usage = "§c/raid begin",
            description = "The Team begins to raid an Faction",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.factions.raid.begin",
            parent = "raid"
    )
    public void begin(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if(factionMemberUtil.isFactionMemberExists(uuid)){
            FactionMember factionUser = factionMemberUtil.getFactionMember(uuid);
            int factionID = factionUser.getFactionID();
            if(raidUtil.isFactionRaiding(factionID)){
                if(raidUtil.isFactionMemberJoinedRaid(factionUser)){
                    int raidID = raidUtil.getActiveRaidID(factionID);
                    Raid raid = raidUtil.getRaid(raidID);
                    if(raid.getRaidState().equals("scouting")){
                        FactionPlot factionPlot = factionPlotUtil.getFactionPlot(player.getLocation());
                        Faction enemy = factionUtil.getFaction(factionPlot.getFactionID());
                        raidUtil.updateRaidToRaiding(raidID, enemy.getId());
                    }else{
                        player.sendMessage("§cNo");
                    }
                }else{
                    player.sendMessage("§cYou didn't join the Raid");
                }
            }else{
                player.sendMessage("§cYour Faction isn't raiding");
            }
        }else{
            player.sendMessage("§cYou aren't in a Faction");
        }
    }

    @IMCommand(
            name = "leave",
            usage = "§c/raid leave",
            description = "The Player leaves the Raid",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.factions.raid.leave",
            parent = "raid"
    )
    public void leave(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if (factionMemberUtil.isFactionMemberExists(uuid)) {
            FactionMember factionUser = factionMemberUtil.getFactionMember(uuid);
            if (raidUtil.isFactionRaiding(factionUser.getFactionID())) {
                int raidID = raidUtil.getActiveRaidID(factionUser.getFactionID());
                Raid raid = raidUtil.getRaid(raidID);
                ArrayList<FactionMember> team = raidUtil.getRaidTeam(raidID);
                if (raidUtil.isFactionMemberJoinedRaid(factionUser)) {
                    if (team.size() == 1) {
                        raidUtil.getRaids().remove(raid);
                        raidUtil.getRaidTeams().remove(raid);

                        scheduler.getCountdowns().remove(player);
                        scheduler.getRaids().remove(player);

                        player.sendMessage("§aYou left the Raid. It isn't existing anymore");
                    } else {
                        scheduler.getRaids().remove(player);
                        scheduler.getCountdowns().remove(player);

                        raidUtil.getRaidTeams().remove(raid, factionUser);

                        player.sendMessage("§aYou left the Raid");
                    }
                } else {
                    player.sendMessage("§cYou can't leave because you haven't joined yet");
                }
            } else {
                player.sendMessage("§cThere is no raid you could leave");
            }
        } else {
            player.sendMessage("§cYou aren't in a Faction");
        }

    }

    @IMCommand(
            name = "infos",
            usage = "§c/raid infos",
            description = "Shows the last 9 Raids",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.factions.raid.infos",
            parent = "raid"
    )
    public void infos(CommandSender sender) {
        Player player = (Player) sender;

        Inventory inventory = Bukkit.createInventory(player, 1, "Raids");



    }

    private void add(String usage, String description) {
        builder.append("\n" + data.getSymbol() + "§e/" + usage + "§8: §7 " + description + ChatColor.RESET);
    }
}
