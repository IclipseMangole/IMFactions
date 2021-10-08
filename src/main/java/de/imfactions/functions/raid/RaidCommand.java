package de.imfactions.functions.raid;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.Scheduler;
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

    private IMFactions imFactions;
    private Data data;
    private FactionUserManager factionUserManager;
    private FactionManager factionManager;
    private FactionPlotManager factionPlotManager;
    private RaidManager raidManager;
    private StringBuilder builder;
    private Scheduler scheduler;

    public Raid(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionManager = data.getFactionManager();
        factionPlotManager = data.getFactionPlotManager();
        factionUserManager = data.getFactionUserManager();
        raidManager = data.getRaidManager();
        scheduler = data.getScheduler();
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
            parent = "raid"
    )
    public void start(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if (factionUserManager.isFactionUserInFaction(uuid)) {
            int factionID = factionUserManager.getFactionUser(uuid).getFactionID();
            if (factionUserManager.getFactionUser(uuid).getRank() == 3) {
                if (!raidManager.isFactionRaiding(factionID)) {
                    if(factionManager.getFaction(factionID).getRaidEnergy() >= 5) {
                        if (factionManager.getRaidableFactions().size() > 1) {
                            if(!player.getWorld().getName().equals("FactionPVP_world")) {

                                //neuer Raid
                                int raidID = raidManager.getHighestRaidID() + 1;
                                raidManager.createStartingRaid(raidID, factionID);
                                RaidManager.Raid raid = raidManager.getRaid(raidID);
                                raidManager.getRaidTeams().put(raid, factionUserManager.getFactionUser(uuid));
                                //raidEnergy abziehen
                                FactionManager.Faction faction = factionManager.getFaction(factionID);
                                faction.setRaidEnergy(faction.getRaidEnergy() - 5);
                                //Teleport vorbereiten
                                FactionManager.Faction enemy = factionManager.getRandomFactionForRaid(factionID);
                                FactionPlotManager.FactionPlot enemyPlot = factionPlotManager.getFactionPlot(enemy.getId());
                                Location raidTeleport = enemyPlot.getRaidSpawn();
                                scheduler.getRaids().put(player, raidTeleport);
                                scheduler.getCountdowns().put(player, 60);
                                //Faction Mitglieder einladen
                                factionUserManager.getOnlineMembers(factionID).forEach(member -> {
                                    TextComponent textComponent = new TextComponent("[HERE]");
                                    textComponent.setColor(net.md_5.bungee.api.ChatColor.of("990000"));
                                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join the Raid").create()));
                                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/raid join"));
                                    ComponentBuilder componentBuilder = new ComponentBuilder();
                                    componentBuilder.append("The King has started a new Raid. You have 60 seconds to join. Click ").color(net.md_5.bungee.api.ChatColor.of("CC4400"));
                                    textComponent.setExtra(componentBuilder.getParts());
                                    member.spigot().sendMessage(textComponent);
                                });
                            }else{
                                player.sendMessage("§cYou can't start a Raid while being in the PVP-World");
                            }
                        } else {
                            player.sendMessage("§cYou can't start a Raid. Either there are no other Factions or they all have Raid-Protection");
                        }
                    }else{
                        player.sendMessage("§cYour Faction needs at least 5⚡ Raid-Energy to start a Raid");
                    }
                } else {
                    player.sendMessage("§cYou have already started a Raid");
                }
            } else {
                player.sendMessage("Only the King can start Raids");
            }
        } else {
            player.sendMessage("§cYou aren't in a Faction");
        }
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

        if (factionUserManager.isFactionUserInFaction(uuid)) {
            int factionID = factionUserManager.getFactionUser(uuid).getFactionID();
            if (raidManager.isFactionRaiding(factionID)) {
                int raidID = raidManager.getActiveRaidID(factionID);
                if (raidManager.getRaid(raidID).getRaidState().equals("starting")) {
                    if (!player.getWorld().getName().equals("FactionPVP_world")) {
                        RaidManager.Raid raid = raidManager.getRaid(raidID);
                        raidManager.getRaidTeams().put(raid, factionUserManager.getFactionUser(uuid));

                        int secondsLeft = raidManager.getStartingSecondsLeft(raidID);
                        Location raidSpawn = raidManager.getRaidSpawn(raidID);

                        if (secondsLeft > -1) {
                            scheduler.getCountdowns().put(player, secondsLeft);
                            scheduler.getLocations().put(player, raidSpawn);
                        }
                    } else {
                        player.sendMessage("§cYou can't join a Raid while being in the PVP_Zone");
                    }
                } else {
                    player.sendMessage("§cIt's to late. The Raid Team is already scouting");
                }
            } else {
                player.sendMessage("§cThere is no active Raid to join");
            }
        } else {
            player.sendMessage("§cYou aren't in a Faction");
        }

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

        if(factionUserManager.isFactionUserInFaction(uuid)){
            FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(uuid);
            int factionID = factionUser.getFactionID();
            if(raidManager.isFactionRaiding(factionID)){
                if(raidManager.isFactionUserUserJoinedRaid(factionUserManager.getFactionUser(uuid))){
                    int raidID = raidManager.getActiveRaidID(factionID);
                    RaidManager.Raid raid = raidManager.getRaid(raidID);
                    if(raid.getRaidState().equals("scouting")){
                        FactionPlotManager.FactionPlot scouting = factionPlotManager.getFactionPlot(player.getLocation());
                        FactionManager.Faction newFaction = raidManager.getFactionForScout(raidID, scouting.getFactionID());
                        FactionPlotManager.FactionPlot newFactionPlot = factionPlotManager.getFactionPlot(newFaction.getId());
                        raidManager.getRaidTeam(raidID).forEach(member -> {
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

        if(factionUserManager.isFactionUserInFaction(uuid)){
            FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(uuid);
            int factionID = factionUser.getFactionID();
            if(raidManager.isFactionRaiding(factionID)){
                if(raidManager.isFactionUserUserJoinedRaid(factionUser)){
                    int raidID = raidManager.getActiveRaidID(factionID);
                    RaidManager.Raid raid = raidManager.getRaid(raidID);
                    if(raid.getRaidState().equals("scouting")){
                        FactionPlotManager.FactionPlot factionPlot = factionPlotManager.getFactionPlot(player.getLocation());
                        FactionManager.Faction enemy = factionManager.getFaction(factionPlot.getFactionID());
                        raidManager.updateRaidToActive(raidID, enemy.getId());
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

        if (factionUserManager.isFactionUserInFaction(uuid)) {
            FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(uuid);
            if (raidManager.isFactionRaiding(factionUser.getFactionID())) {
                int raidID = raidManager.getActiveRaidID(factionUser.getFactionID());
                RaidManager.Raid raid = raidManager.getRaid(raidID);
                ArrayList<FactionUserManager.FactionUser> team = raidManager.getRaidTeam(raidID);
                if (raidManager.isFactionUserUserJoinedRaid(factionUser)) {
                    if (team.size() == 1) {
                        raidManager.getRaids().remove(raid);
                        raidManager.getRaidTeams().remove(raid);

                        scheduler.getCountdowns().remove(player);
                        scheduler.getRaids().remove(player);

                        player.sendMessage("§aYou left the Raid. It isn't existing anymore");
                    } else {
                        scheduler.getRaids().remove(player);
                        scheduler.getCountdowns().remove(player);

                        raidManager.getRaidTeams().remove(raid, factionUser);

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
