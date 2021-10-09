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
    private IMFactions imFactions;
    private Data data;
    private FactionUtil factionUtil;
    private FactionMemberUtil factionMemberUtil;
    private UserUtil userUtil;
    private FactionPlotUtil factionPlotUtil;
    private WorldLoader worldLoader;
    private Scheduler scheduler;

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
            builder.append(data.getPrefix() + "§eOverview§8:" + "\n");
            add("/faction found <Name> <Shortcut>", "Makes the Player King of a new Faction");
            add("/faction leave", "The Player leaves a Faction");
            add("/faction invite <Player>", "Invites a new Player to the Faction");
            add("/faction accept <Name>", "Accepts a Faction Invite");
            add("/faction kick <Player>", "Kicks a Player from the Faction");
            add("/faction promote <Player>", "The Player gets a higher Rank");
            add("/faction demote <Player>", "The Player gets a lower Rank");
            add("/faction info", "Infos about your Faction OR Invites from Factions");
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
            permissions = "im.imFactions.faction.found"
    )
    public void found(CommandSender sender, String name, String shortcut) {
        Player player = (Player) sender;

        if (!factionUtil.isFactionExists(name)) {
            if (!factionMemberUtil.isFactionMemberExists(UUIDFetcher.getUUID(player)) || factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getRank() == -1) {
                if(name.length() < 31) {
                    if(shortcut.length() == 3) {
                        if(factionPlotUtil.getLoadingFactionPlots() < 3) {
                            int factionId = factionUtil.getHighestFactionID() + 1;
                            factionUtil.createFaction(factionId, name, shortcut);
                            factionMemberUtil.createFactionMember(UUIDFetcher.getUUID(player), factionId, 3);
                            //FactionPlot
                            int position = factionPlotUtil.getFreePosition();
                            Location edgeDownFrontLeft = factionPlotUtil.getEdgeDownFrontLeft(position);
                            Location edgeUpBackRight = factionPlotUtil.getEdgeUpBackRight(edgeDownFrontLeft);
                            Location home = new Location(edgeDownFrontLeft.getWorld(), edgeDownFrontLeft.getX(), edgeDownFrontLeft.getY() + 17, edgeDownFrontLeft.getZ());
                            factionPlotUtil.createFactionPlot(factionId, edgeDownFrontLeft, edgeUpBackRight, home, System.currentTimeMillis() + 30000, position);

                            worldLoader.loadMap("FactionPlot", home);

                            player.sendMessage("§aYou founded the Faction '§e" + name + "§a'");
                        }else {
                            player.sendMessage("§cSorry, but there are too many new Factions and FactionPlots loading. Wait a short time");
                        }
                    }else{
                        player.sendMessage("§cA shortcut consists of 3 characters");
                    }
                }else{
                    player.sendMessage("§cThe Name of your Faction is too long. The Maximum is 30");
                }
            } else {
                player.sendMessage("§cYou are already in a Faction");
            }
        } else {
            player.sendMessage("§cThis Faction already exists");
        }
    }

    @IMCommand(
            name = "leave",
            usage = "§c/faction leave",
            description = "faction.leave.description",
            minArgs = 0,
            maxArgs = 0,
            parent = "faction",
            permissions = "im.imFactions.faction.leave"
    )
    public void leave(CommandSender sender) {
        Player player = (Player) sender;

        if (factionMemberUtil.isFactionMemberExists(UUIDFetcher.getUUID(player))) {
            if (factionMemberUtil.isFactionMemberInFaction(UUIDFetcher.getUUID(player))) {
                if (factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)) != null && factionUtil.getFaction(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID()) != null) {
                    if (factionUtil.getFaction(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID()).getUserAmount() == 1) {
                        //FactionPlots
                        if(player.getWorld().getName().equals("FactionPlots_world")) {
                            player.teleport(data.getWorldSpawn());
                        }
                        int factionId = factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID();
                        int position = factionPlotUtil.getFactionPlot(factionId).getPosition();
                        worldLoader.deleteMap(factionPlotUtil.getEdgeDownFrontLeft(position));
                        factionPlotUtil.deleteFactionPlot(factionPlotUtil.getFactionPlot(factionId));

                        factionUtil.deleteFaction(factionUtil.getFaction(factionId));
                        factionMemberUtil.deleteFactionMember(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)));
                        player.sendMessage("§aYou left the Faction. The Faction isn't existing anymore.");

                    } else {
                        player.teleport(data.getWorldSpawn());
                        int factionId = factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID();
                        if (factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getRank() == 3) {
                            factionMemberUtil.deleteFactionMember(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)));
                            ArrayList<FactionMember> highestUser = factionMemberUtil.getHighestFactionMembers(factionId);
                            Random random = new Random();
                            highestUser.get(random.nextInt(highestUser.size())).setRank(3);
                            factionUtil.getFaction(factionId).setUserAmount(factionUtil.getFaction(factionId).getUserAmount() - 1);
                            player.sendMessage("§aYou left the Faction");

                            FactionMember[] king = new FactionMember[1];
                            highestUser.forEach(factionMember -> {
                                if(factionMember.getRank() == 3){
                                    king[0] = factionMember;
                                }
                            });
                            factionMemberUtil.getFactionMembers(factionId).forEach(factionMember -> {
                                Bukkit.getPlayer(UUIDFetcher.getName(factionMember.getUuid())).sendMessage("§e" + player.getName() + "§a left the Faction");
                                Bukkit.getPlayer(UUIDFetcher.getName(factionMember.getUuid())).sendMessage("§e" + UUIDFetcher.getName(king[0].getUuid()) + "§a is now the new §4KING");
                            });
                        } else {
                            factionMemberUtil.deleteFactionMember(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)));
                            factionUtil.getFaction(factionId).setUserAmount(factionUtil.getFaction(factionId).getUserAmount() - 1);
                            player.sendMessage("§aYou left the Faction");
                            factionMemberUtil.getFactionMembers(factionId).forEach(factionMember -> {
                                Bukkit.getPlayer(UUIDFetcher.getName(factionMember.getUuid())).sendMessage("§e" + player.getName() + "§a left the Faction");
                            });
                        }
                    }
                } else {
                    player.sendMessage("§cYou aren't a member of a Faction");
                }
            } else {
                player.sendMessage("§cYou aren't a member of a Faction");
            }
        } else {
            player.sendMessage("§cYou aren't a member of a Faction");
        }
    }

    @IMCommand(
            name = "invite",
            usage = "§c/faction invite <Player>",
            description = "faction.invite.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.imFactions.faction.invite"
    )
    public void invite(CommandSender sender, String name) {
        Player player = (Player) sender;

        if (userUtil.isUserExists(name)) {
            UUID uuidInvite = UUIDFetcher.getUUID(name);
            if (factionMemberUtil.isFactionMemberInFaction(UUIDFetcher.getUUID(player))) {
                if (factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getRank() > 0) {
                    if (!factionMemberUtil.isFactionMemberInFaction(uuidInvite) && factionMemberUtil.getFactionMember(uuidInvite) == null) {
                        factionMemberUtil.createFactionMember(uuidInvite, factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID(), -1);
                        player.sendMessage("§aYou invited §e" + name + "§a to join your Faction");

                        TextComponent command = new TextComponent();
                        command.setText("/faction accept " + factionUtil.getFactionName(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID()));
                        command.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                        command.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction accept " + factionUtil.getFactionName(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID())));
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eClick to join").create()));

                        Bukkit.getPlayer(name).spigot().sendMessage(new ComponentBuilder("You got invited by the Faction ").color(net.md_5.bungee.api.ChatColor.GREEN).append(factionUtil.getFaction(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID()).getName()).color(net.md_5.bungee.api.ChatColor.YELLOW).append(". Do ").color(net.md_5.bungee.api.ChatColor.GREEN).append(command).append(" to join the Faction").color(net.md_5.bungee.api.ChatColor.GREEN).create());
                    } else {
                        player.sendMessage("§cThis Player is already member of a Faction");
                    }
                } else {
                    player.sendMessage("§cYou don't have the permission to invite Players");
                }
            } else {
                player.sendMessage("§cYou can't invite Players without being in a Faction");
            }
        } else {
            player.sendMessage("§cThe player doesn't exist");
        }
    }

    @IMCommand(
            name = "accept",
            usage = "§c/faction accept <Name>",
            description = "faction.accept.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.imFactions.faction.accept"
    )
    public void accept(CommandSender sender, String name) {
        Player player = (Player) sender;

        if (factionMemberUtil.isFactionMemberExists(UUIDFetcher.getUUID(player))) {
            if (!factionMemberUtil.isFactionMemberInFaction(UUIDFetcher.getUUID(player))) {
                if (factionUtil.isFactionExists(name)) {
                    for (FactionMember factionMember : factionMemberUtil.getFactionInvites(UUIDFetcher.getUUID(player))) {
                        factionMemberUtil.deleteFactionMember(factionMember);
                    }
                    factionUtil.getFaction(name).setUserAmount(factionUtil.getFaction(name).getUserAmount() + 1);
                    factionMemberUtil.createFactionMember(UUIDFetcher.getUUID(player), factionUtil.getFaction(name).getId(), 0);
                    player.sendMessage("§aYou joined the Faction '§e" + name + "§a'");
                    factionMemberUtil.getFactionMembers(factionUtil.getFactionID(name)).forEach(factionMember -> {
                        Bukkit.getPlayer(UUIDFetcher.getName(factionMember.getUuid())).sendMessage("§e" + player.getName() + "§a joined the Faction");
                    });
                } else {
                    player.sendMessage("§cThe Faction '§e" + name + "§c' doesn't exist");
                }
            } else {
                player.sendMessage("§cYou are already in a Faction");
            }
        } else {
            player.sendMessage("§cYou have no Invites to join a Faction");
        }
    }

    @IMCommand(
            name = "kick",
            usage = "§c/faction kick <Player>",
            description = "faction.kick.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.imFactions.faction.kick"
    )
    public void kick(CommandSender sender, String kick) {
        Player player = (Player) sender;

        if (userUtil.isUserExists(kick)) {
            UUID uuidKick = UUIDFetcher.getUUID(kick);
            if (factionMemberUtil.isFactionMemberExists(UUIDFetcher.getUUID(player))) {
                if (factionMemberUtil.isFactionMemberInFaction(UUIDFetcher.getUUID(player))) {
                    if (factionMemberUtil.isFactionMemberExists(uuidKick) && factionMemberUtil.getFactionMember(uuidKick) != null) {
                        if (factionMemberUtil.isFactionMemberInFaction(uuidKick)) {
                            if (factionMemberUtil.getFactionMember(uuidKick).getFactionID() == factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID()) {
                                if (!factionMemberUtil.getFactionMember(uuidKick).equals(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)))) {
                                    if (factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).isHigherRank(factionMemberUtil.getFactionMember(uuidKick).getRank())) {
                                        factionMemberUtil.deleteFactionMember(factionMemberUtil.getFactionMember(uuidKick));
                                        player.sendMessage("§aYou kicked §e" + kick + " §aout of the Faction");
                                        if(Bukkit.getPlayer(kick).getWorld().getName().equals("FactionPlots_world")){
                                            Bukkit.getPlayer(kick).teleport(data.getWorldSpawn());
                                        }
                                        Bukkit.getPlayer(kick).sendMessage("§e" + player.getName() + "§a kicked you out of the Faction");
                                        factionMemberUtil.getFactionMembers(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID()).forEach(factionMember -> {
                                            Bukkit.getPlayer(UUIDFetcher.getName(factionMember.getUuid())).sendMessage("§e" + kick + "§a was kicked out of the Faction");
                                        });
                                        Faction faction = factionUtil.getFaction(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID());
                                        faction.setUserAmount(faction.getUserAmount() - 1);
                                    } else {
                                        player.sendMessage("§cYou have to be a higher Rank to kick this Player");
                                    }
                                } else {
                                    player.sendMessage("§cWhy kick yourself? Use the command §e/faction leave §ato leave your Faction");
                                }
                            } else {
                                player.sendMessage("§cThis player doesn't exist in your Faction");
                            }
                        } else {
                            player.sendMessage("§cThis player doesn't exist in your Faction");
                        }
                    } else {
                        player.sendMessage("§cThis player doesn't exist in your Faction");
                    }
                } else {
                    player.sendMessage("§cYou aren't even in a Faction");
                }
            } else {
                player.sendMessage("§cYou aren't even in a Faction");
            }
        } else {
            player.sendMessage("§cThe player doesn't exist");
        }
    }

    @IMCommand(
            name = "promote",
            usage = "§c/faction promote <Player>",
            description = "faction.promote.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.imFactions.faction.promote"
    )
    public void promote(CommandSender sender, String promote) {
        Player player = (Player) sender;

        if (userUtil.isUserExists(promote)) {
            UUID uuidPromote = UUIDFetcher.getUUID(promote);
            if (factionMemberUtil.isFactionMemberInFaction(UUIDFetcher.getUUID(player))) {
                if (factionMemberUtil.getFactionMember(uuidPromote) != null && factionMemberUtil.getFactionMember(uuidPromote).getFactionID() == factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID()) {
                    if (!factionMemberUtil.getFactionMember(uuidPromote).equals(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)))) {
                        if (factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).isHigherRank(factionMemberUtil.getFactionMember(uuidPromote).getRank())) {
                            if (factionMemberUtil.getFactionMember(uuidPromote).getRank() < 2) {
                                factionMemberUtil.getFactionMember(uuidPromote).setRank(factionMemberUtil.getFactionMember(uuidPromote).getRank() + 1);
                                player.sendMessage("§aYou promoted §e" + UUIDFetcher.getName(uuidPromote) + "§a to the Rank '" + factionMemberUtil.getFactionMember(uuidPromote).getRankname() + "§a'");
                                Bukkit.getPlayer(promote).sendMessage("§aYou got promoted to the Rank '" + factionMemberUtil.getFactionMember(uuidPromote).getRankname() + "§a'");
                            } else {
                                factionMemberUtil.getFactionMember(uuidPromote).setRank(factionMemberUtil.getFactionMember(uuidPromote).getRank() + 1);
                                factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).setRank(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getRank() - 1);
                                player.sendMessage("§aYou promoted §e" + UUIDFetcher.getName(uuidPromote) + "§a to the Rank '" + factionMemberUtil.getFactionMember(uuidPromote).getRankname() + "§a'");
                                player.sendMessage("§aYou have now the Rank '" + factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getRankname() + "§a'");
                                Bukkit.getPlayer(UUIDFetcher.getName(uuidPromote)).sendMessage("§aYou got promoted to the Rank '" + factionMemberUtil.getFactionMember(uuidPromote).getRankname() + "§a'");
                            }
                        } else {
                            player.sendMessage("§cYou have to be higher Rank to promote this Player");
                        }
                    } else {
                        player.sendMessage("§cYou can't promote yourself");
                    }
                } else {
                    player.sendMessage("§cThis player isn't member of your Faction");
                }
            } else {
                player.sendMessage("§cYou aren't in a Faction");
            }
        } else {
            player.sendMessage("§cThe player doesn't exist");
        }
    }

    @IMCommand(
            name = "demote",
            usage = "§c/faction demote <Player>",
            description = "faction.demote.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.imFactions.faction.demote"
    )
    public void demote(CommandSender sender, String demote) {
        Player player = (Player) sender;

        if (userUtil.isUserExists(demote)) {
            UUID uuidDemote = UUIDFetcher.getUUID(demote);
            if (factionMemberUtil.isFactionMemberInFaction(UUIDFetcher.getUUID(player))) {
                if (factionMemberUtil.getFactionMember(uuidDemote) != null && factionMemberUtil.getFactionMember(uuidDemote).getFactionID() == (factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player))).getFactionID()) {
                    if (!factionMemberUtil.getFactionMember(uuidDemote).equals(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)))) {
                        if (factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).isHigherRank(factionMemberUtil.getFactionMember(uuidDemote).getRank())) {
                            if (factionMemberUtil.getFactionMember(uuidDemote).getRank() > 0) {
                                factionMemberUtil.getFactionMember(uuidDemote).setRank(factionMemberUtil.getFactionMember(uuidDemote).getRank() - 1);
                                player.sendMessage("§aYou demoted §e" + demote + "§a to the Rank '" + factionMemberUtil.getFactionMember(uuidDemote).getRankname() + "§a'");
                                Bukkit.getPlayer(UUIDFetcher.getName(uuidDemote)).sendMessage("§aYou got demoted to the Rank '" + factionMemberUtil.getFactionMember(uuidDemote).getRankname() + "§a'");
                            } else {
                                player.sendMessage("§cThis player has already the lowest Rank");
                            }
                        } else {
                            player.sendMessage("§cYou have to be a higher Rank to demote this Player");
                        }
                    } else {
                        player.sendMessage("§cYou can't demote yourself");
                    }
                } else {
                    player.sendMessage("§cThis player isn't member of your Faction");
                }
            } else {
                player.sendMessage("§cYou aren't in a Faction");
            }
        } else {
            player.sendMessage("§cThe player doesn't exist");
        }
    }

    @IMCommand(
            name = "info",
            usage = "§c/faction info",
            description = "faction.info.description",
            minArgs = 0,
            maxArgs = 0,
            parent = "faction",
            permissions = "im.imFactions.faction.info"
    )
    public void info(CommandSender sender) {
        Player player = (Player) sender;

        if (factionMemberUtil.isFactionMemberExists(UUIDFetcher.getUUID(player))) {
            if (factionMemberUtil.isFactionMemberInFaction(UUIDFetcher.getUUID(player))) {
                if (factionUtil.getFaction(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID()) != null) {
                    player.sendMessage("§7---------------§eFactionInfos§7---------------");
                    //Name
                    player.sendMessage("§7Name: §e" + factionUtil.getFaction(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID()).getName() + "§7[§e" + factionUtil.getFaction(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID()).getShortcut() + "§7]");
                    //RaidProtection
                    int totalSecs = (int) ((int) factionUtil.getFaction(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID()).getRaidProtection() - System.currentTimeMillis()) / 1000;
                    int hours = totalSecs / 3600;
                    int minutes = (totalSecs % 3600) / 60;
                    int seconds = totalSecs % 60;
                    String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    player.sendMessage("§7RaidProtection: §e" + timeString);
                    //Founding Date
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    player.sendMessage("§7FoundingDate: §e" + simpleDateFormat.format(factionUtil.getFaction(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID()).getFoundingDate()));
                    //Member Amount
                    TextComponent textComponent = new TextComponent("§7[§eClick Here§7]");
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction members"));
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eSee a List of all Members").create()));
                    player.spigot().sendMessage(new ComponentBuilder("Members: ").color(net.md_5.bungee.api.ChatColor.GRAY).append(factionUtil.getFaction(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID()).getUserAmount() + " ").color(net.md_5.bungee.api.ChatColor.YELLOW).append(textComponent).create());
                }
            } else {
                player.sendMessage("§7Invites from Factions:");
                ComponentBuilder imFactions = new ComponentBuilder();
                for (FactionMember factionMember : factionMemberUtil.getFactionInvites(UUIDFetcher.getUUID(player))) {
                    TextComponent textComponent = new TextComponent();
                    ComponentBuilder componentBuilder = new ComponentBuilder();
                    componentBuilder.append(factionUtil.getFactionName(factionMember.getFactionID())).color(net.md_5.bungee.api.ChatColor.YELLOW).append("[").color(net.md_5.bungee.api.ChatColor.GRAY).append(factionUtil.getFaction(factionMember.getFactionID()).getShortcut()).color(net.md_5.bungee.api.ChatColor.YELLOW).append("], ").color(net.md_5.bungee.api.ChatColor.GRAY);
                    textComponent.setExtra(componentBuilder.getParts());
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction accept " + factionUtil.getFactionName(factionMember.getFactionID())));
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join ").color(net.md_5.bungee.api.ChatColor.GRAY).append(factionUtil.getFactionName(factionMember.getFactionID())).color(net.md_5.bungee.api.ChatColor.YELLOW).create()));
                    imFactions.append(textComponent);
                }
                player.spigot().sendMessage(imFactions.create());
            }
        } else {
            player.sendMessage("§cThere are no Invites");
        }
    }

    @IMCommand(
            name = "members",
            usage = "§c/faction members",
            description = "faction.members.description",
            minArgs = 0,
            maxArgs = 0,
            parent = "faction",
            permissions = "im.imFactions.faction.members"
    )
    public void infoMembers(CommandSender sender) {
        Player player = (Player) sender;

        if (factionMemberUtil.isFactionMemberExists(UUIDFetcher.getUUID(player))) {
            if (factionMemberUtil.isFactionMemberInFaction(UUIDFetcher.getUUID(player))) {
                if (factionUtil.getFaction(factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player)).getFactionID()) != null) {
                    //factionMember
                    FactionMember factionMember = factionMemberUtil.getFactionMember(UUIDFetcher.getUUID(player));
                    Faction faction = factionUtil.getFaction(factionMember.getFactionID());
                    ArrayList<FactionMember> ranks = factionMemberUtil.getFactionMembers(faction.getId());
                    //Stringbuilders
                    StringBuilder air = new StringBuilder();
                    StringBuilder king = new StringBuilder();
                    StringBuilder veteran = new StringBuilder();
                    StringBuilder knight = new StringBuilder();
                    StringBuilder member = new StringBuilder();
                    StringBuilder invited = new StringBuilder();
                    //messages
                    //king
                    player.sendMessage("§7---------------------§4KING§7---------------------");
                    ranks.forEach(kingUser -> {
                        if (kingUser.getRank() == 3) {
                            king.append("§4" + UUIDFetcher.getName(kingUser.getUuid()) + "§7, ");
                        }
                    });
                    for (int i = 0; i < ((70 / 2) - (king.length() / 2)); i++) {
                        air.append(" ");
                    }
                    air.append(king);
                    player.sendMessage(String.valueOf(air));
                    air.delete(0, air.length());
                    //veteran
                    player.sendMessage("§7-------------------§5VETERAN§7--------------------");
                    ranks.forEach(veteranUser -> {
                        if (veteranUser.getRank() == 2) {
                            veteran.append("§5" + UUIDFetcher.getName(veteranUser.getUuid()) + "§7, ");
                        }
                    });
                    for (int i = 0; i < 70 / 2 - veteran.length() / 2; i++) {
                        air.append(" ");
                    }
                    air.append(veteran);
                    player.sendMessage(String.valueOf(air));
                    air.delete(0, air.length());
                    //knight
                    player.sendMessage("§7--------------------§1KNIGHT§7--------------------");
                    ranks.forEach(knightUser -> {
                        if (knightUser.getRank() == 1) {
                            knight.append("§1" + UUIDFetcher.getName(knightUser.getUuid()) + "§7, ");
                        }
                    });
                    for (int i = 0; i < 72 / 2 - knight.length() / 2; i++) {
                        air.append(" ");
                    }
                    air.append(knight);
                    player.sendMessage(String.valueOf(air));
                    air.delete(0, air.length());
                    //member
                    player.sendMessage("§7--------------------§2MEMBER§7--------------------");
                    ranks.forEach(memberUser -> {
                        if (memberUser.getRank() == 0) {
                            member.append("§2" + UUIDFetcher.getName(memberUser.getUuid()) + "§7, ");
                        }
                    });
                    for (int i = 0; i < 68 / 2 - member.length() / 2; i++) {
                        air.append(" ");
                    }
                    air.append(member);
                    player.sendMessage(String.valueOf(air));
                    air.delete(0, air.length());
                    //invited
                    player.sendMessage("§7--------------------§8INVITED§7--------------------");
                    ranks.forEach(invitedUser -> {
                        if (invitedUser.getRank() == -1) {
                            invited.append("§8" + UUIDFetcher.getName(invitedUser.getUuid()) + "§7, ");
                        }
                    });
                    for (int i = 0; i < 68 / 2 - invited.length() / 2; i++) {
                        air.append(" ");
                    }
                    air.append(invited);
                    player.sendMessage(String.valueOf(air));
                    air.delete(0, air.length());

                    TextComponent textComponent = new TextComponent();
                    textComponent.setText("§7[§eClick Here§7]");
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to see the FactionInfo").create()));
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction info"));
                    player.spigot().sendMessage(textComponent);
                } else {
                    player.sendMessage("§cThere is no Faction");
                }
            } else {
                player.sendMessage("§cYou aren't in a Faction");
            }
        } else {
            player.sendMessage("§cYou aren't in a Faction");
        }
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

        if(factionMemberUtil.isFactionMemberExists(uuid)){
            if(factionMemberUtil.isFactionMemberInFaction(uuid)){
                int factionId = factionMemberUtil.getFactionMember(uuid).getFactionID();
                Faction faction = factionUtil.getFaction(factionId);
                FactionPlot factionPlot = factionPlotUtil.getFactionPlot(factionId);
                if(factionPlot.getReachable() < System.currentTimeMillis()){
                    if(!scheduler.getCountdowns().containsKey(player)) {

                        String world = player.getWorld().getName();

                        if (world.equals("world")) {
                            scheduler.getCountdowns().put(player, 5);
                        } else if(world.equals("FactionPlots_world")){
                            scheduler.getCountdowns().put(player, 5);
                        }else {
                            scheduler.getCountdowns().put(player, 20);
                        }

                        scheduler.getLocations().put(player, factionPlot.getHome());

                    }else{
                        player.sendMessage("§cYou are already teleporting");
                    }
                }else {
                    player.sendMessage("§cYour FactionPlot is loading. Wait " + (factionPlot.getReachable() - System.currentTimeMillis()));
                }
            }else{
                player.sendMessage("§cYou aren't in a Faction");
            }
        }else{
            player.sendMessage("§cYou aren't in a Faction");
        }
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
        if(factionMemberUtil.isFactionMemberExists(uuid)) {
            if(factionMemberUtil.isFactionMemberInFaction(uuid)) {
                FactionMember factionMember = factionMemberUtil.getFactionMember(uuid);
                FactionPlot factionPlot = factionPlotUtil.getFactionPlot(factionMember.getFactionID());
                Location edgeDownFrontLeft = factionPlot.getEdgeDownFrontLeft();
                Location edgeUpBackRight = factionPlot.getEdgeUpBackRight();
                String world = player.getWorld().getName();
                if(world.equals("FactionPlots_world")) {
                    if(LocationChecker.isLocationInsideCube(player.getLocation(), edgeDownFrontLeft, edgeUpBackRight)) {
                        factionPlot.setHome(player.getLocation());
                        player.sendMessage("§aSet home to " + LocationBuilder.getString(player.getLocation()));
                    }else{
                        player.sendMessage("§cSet your home inside your FactionPlot");
                    }
                }else {
                    player.sendMessage("§cYou can only change your home at your FactionPlot");
                }
            }else{
                player.sendMessage("§cYou aren't in a Faction");
            }
        }else{
            player.sendMessage("§cYou aren't in a Faction");
        }
    }

    private void add(String usage, String description) {
        builder.append("\n" + data.getSymbol() + "§e" + usage + "§8: §7 " + description + ChatColor.RESET);
    }
}
