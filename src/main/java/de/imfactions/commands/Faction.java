package de.imfactions.commands;

import de.imfactions.IMFactions;
import de.imfactions.database.UserManager;
import de.imfactions.database.faction.FactionManager;
import de.imfactions.database.faction.FactionPlotManager;
import de.imfactions.database.faction.FactionUserManager;
import de.imfactions.functions.Scheduler;
import de.imfactions.functions.WorldLoader;
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

public class Faction {

    private StringBuilder builder;
    private IMFactions factions;
    private FactionManager factionManager;
    private FactionUserManager factionUserManager;
    private UserManager userManager;
    private FactionPlotManager factionPlotManager;
    private WorldLoader worldLoader;
    private Scheduler scheduler;

    public Faction(IMFactions factions) {
        this.factions = factions;
        this.factionManager = factions.getData().getFactionManager();
        this.factionUserManager = factions.getData().getFactionUserManager();
        this.userManager = factions.getData().getUserManager();
        this.factionPlotManager = factions.getData().getFactionPlotManager();
        this.worldLoader = new WorldLoader(factions);
        this.scheduler = factions.getData().getScheduler();
    }

    @IMCommand(
            name = "faction",
            usage = "§c/faction",
            description = "faction.description",
            permissions = "im.factions.faction"
    )
    public void execute(CommandSender sender) {
        if (sender.hasPermission("im.factions.faction.*")) {
            builder = new StringBuilder();
            builder.append(factions.getData().getPrefix() + "§eOverview§8:" + "\n");
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
            permissions = "im.factions.faction.found"
    )
    public void found(CommandSender sender, String name, String shortcut) {
        Player player = (Player) sender;

        if (!factionManager.isFactionExists(name)) {
            if (!factionUserManager.isFactionUserExists(UUIDFetcher.getUUID(player)) || factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getRank() == -1) {
                if(name.length() < 31) {
                    if(shortcut.length() == 3) {
                        if(factionPlotManager.getLoadingFactionPlots() < 3) {
                            int factionId = factionManager.getHighestFactionID() + 1;
                            factionManager.createFaction(factionId, name, shortcut);
                            factionUserManager.createFactionUser(UUIDFetcher.getUUID(player), factionId, 3, true);
                            //FactionPlot
                            int position = factionPlotManager.getFreePosition();
                            Location edgeDownFrontLeft = factionPlotManager.getEdgeDownFrontLeft(position);
                            Location edgeUpBackRight = factionPlotManager.getEdgeUpBackRight(edgeDownFrontLeft);
                            Location home = new Location(edgeDownFrontLeft.getWorld(), edgeDownFrontLeft.getX(), edgeDownFrontLeft.getY() + 17, edgeDownFrontLeft.getZ());
                            factionPlotManager.createFactionPlot(factionId, edgeDownFrontLeft, edgeUpBackRight, home, System.currentTimeMillis() + 30000, position);

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
            permissions = "im.factions.faction.leave"
    )
    public void leave(CommandSender sender) {
        Player player = (Player) sender;

        if (factionUserManager.isFactionUserExists(UUIDFetcher.getUUID(player))) {
            if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
                if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)) != null && factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()) != null) {
                    if (factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()).getUserAmount() == 1) {
                        //FactionPlots
                        if(player.getWorld().getName().equals("FactionPlots_world")) {
                            player.teleport(factions.getData().getWorldSpawn());
                        }
                        int factionId = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID();
                        int position = factionPlotManager.getFactionPlot(factionId).getPosition();
                        worldLoader.deleteMap(factionPlotManager.getEdgeDownFrontLeft(position));
                        factionPlotManager.getFactionPlot(factionId).deleteFactionPlot();

                        factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()).deleteFaction();
                        factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).delete();
                        player.sendMessage("§aYou left the Faction. The Faction isn't existing anymore.");

                    } else {
                        player.teleport(factions.getData().getWorldSpawn());
                        int factionId = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID();
                        if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getRank() == 3) {
                            factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).delete();
                            ArrayList<FactionUserManager.FactionUser> highestUser = factionUserManager.getHighestFactionUsers(factionId);
                            Random random = new Random();
                            highestUser.get(random.nextInt(highestUser.size())).setRank(3);
                            factionManager.getFaction(factionId).setUserAmount(factionManager.getFaction(factionId).getUserAmount() - 1);
                            player.sendMessage("§aYou left the Faction");

                            FactionUserManager.FactionUser[] king = new FactionUserManager.FactionUser[1];
                            highestUser.forEach(factionUser -> {
                                if(factionUser.getRank() == 3){
                                    king[0] = factionUser;
                                }
                            });
                            factionUserManager.getFactionUsers(factionId).forEach(factionUser -> {
                                Bukkit.getPlayer(UUIDFetcher.getName(factionUser.getUuid())).sendMessage("§e" + player.getName() + "§a left the Faction");
                                Bukkit.getPlayer(UUIDFetcher.getName(factionUser.getUuid())).sendMessage("§e" + UUIDFetcher.getName(king[0].getUuid()) + "§a is now the new §4KING");
                            });
                        } else {
                            factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).delete();
                            factionManager.getFaction(factionId).setUserAmount(factionManager.getFaction(factionId).getUserAmount() - 1);
                            player.sendMessage("§aYou left the Faction");
                            factionUserManager.getFactionUsers(factionId).forEach(factionUser -> {
                                Bukkit.getPlayer(UUIDFetcher.getName(factionUser.getUuid())).sendMessage("§e" + player.getName() + "§a left the Faction");
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
            permissions = "im.factions.faction.invite"
    )
    public void invite(CommandSender sender, String name) {
        Player player = (Player) sender;

        if (userManager.isUserExists(name)) {
            UUID uuidInvite = UUIDFetcher.getUUID(name);
            if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
                if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getRank() > 0) {
                    if (!factionUserManager.isFactionUserInFaction(uuidInvite) && factionUserManager.getFactionUser(uuidInvite) == null) {
                        factionUserManager.createFactionUser(uuidInvite, factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID(), -1, true);
                        player.sendMessage("§aYou invited §e" + name + "§a to join your Faction");

                        TextComponent command = new TextComponent();
                        command.setText("/faction accept " + factionManager.getFactionName(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()));
                        command.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                        command.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction accept " + factionManager.getFactionName(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID())));
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eClick to join").create()));

                        Bukkit.getPlayer(name).spigot().sendMessage(new ComponentBuilder("You got invited by the Faction ").color(net.md_5.bungee.api.ChatColor.GREEN).append(factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()).getName()).color(net.md_5.bungee.api.ChatColor.YELLOW).append(". Do ").color(net.md_5.bungee.api.ChatColor.GREEN).append(command).append(" to join the Faction").color(net.md_5.bungee.api.ChatColor.GREEN).create());
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
            permissions = "im.factions.faction.accept"
    )
    public void accept(CommandSender sender, String name) {
        Player player = (Player) sender;

        if (factionUserManager.isFactionUserExists(UUIDFetcher.getUUID(player))) {
            if (!factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
                if (factionManager.isFactionExists(name)) {
                    for (FactionUserManager.FactionUser factionUser : factionUserManager.getFactionInvites(UUIDFetcher.getUUID(player))) {
                        factionUser.delete();
                    }
                    factionManager.getFaction(name).setUserAmount(factionManager.getFaction(name).getUserAmount() + 1);
                    factionUserManager.createFactionUser(UUIDFetcher.getUUID(player), factionManager.getFaction(name).getId(), 0, true);
                    player.sendMessage("§aYou joined the Faction '§e" + name + "§a'");
                    factionUserManager.getFactionUsers(factionManager.getFactionID(name)).forEach(factionUser -> {
                        Bukkit.getPlayer(UUIDFetcher.getName(factionUser.getUuid())).sendMessage("§e" + player.getName() + "§a joined the Faction");
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
            permissions = "im.factions.faction.kick"
    )
    public void kick(CommandSender sender, String kick) {
        Player player = (Player) sender;

        if (userManager.isUserExists(kick)) {
            UUID uuidKick = UUIDFetcher.getUUID(kick);
            if (factionUserManager.isFactionUserExists(UUIDFetcher.getUUID(player))) {
                if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
                    if (factionUserManager.isFactionUserExists(uuidKick) && factionUserManager.getFactionUser(uuidKick) != null) {
                        if (factionUserManager.isFactionUserInFaction(uuidKick)) {
                            if (factionUserManager.getFactionUser(uuidKick).getFactionID() == factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()) {
                                if (!factionUserManager.getFactionUser(uuidKick).equals(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)))) {
                                    if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).isHigherRank(factionUserManager.getFactionUser(uuidKick).getRank())) {
                                        factionUserManager.getFactionUser(uuidKick).delete();
                                        player.sendMessage("§aYou kicked §e" + kick + " §aout of the Faction");
                                        if(Bukkit.getPlayer(kick).getWorld().getName().equals("FactionPlots_world")){
                                            Bukkit.getPlayer(kick).teleport(factions.getData().getWorldSpawn());
                                        }
                                        Bukkit.getPlayer(kick).sendMessage("§e" + player.getName() + "§a kicked you out of the Faction");
                                        factionUserManager.getFactionUsers(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()).forEach(factionUser -> {
                                            Bukkit.getPlayer(UUIDFetcher.getName(factionUser.getUuid())).sendMessage("§e" + kick + "§a was kicked out of the Faction");
                                        });
                                        FactionManager.Faction faction = factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID());
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
            permissions = "im.factions.faction.promote"
    )
    public void promote(CommandSender sender, String promote) {
        Player player = (Player) sender;

        if (userManager.isUserExists(promote)) {
            UUID uuidPromote = UUIDFetcher.getUUID(promote);
            if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
                if (factionUserManager.getFactionUser(uuidPromote) != null && factionUserManager.getFactionUser(uuidPromote).getFactionID() == factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()) {
                    if (!factionUserManager.getFactionUser(uuidPromote).equals(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)))) {
                        if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).isHigherRank(factionUserManager.getFactionUser(uuidPromote).getRank())) {
                            if (factionUserManager.getFactionUser(uuidPromote).getRank() < 2) {
                                factionUserManager.getFactionUser(uuidPromote).setRank(factionUserManager.getFactionUser(uuidPromote).getRank() + 1);
                                player.sendMessage("§aYou promoted §e" + UUIDFetcher.getName(uuidPromote) + "§a to the Rank '" + factionUserManager.getFactionUser(uuidPromote).getRankname() + "§a'");
                                Bukkit.getPlayer(promote).sendMessage("§aYou got promoted to the Rank '" + factionUserManager.getFactionUser(uuidPromote).getRankname() + "§a'");
                            } else {
                                factionUserManager.getFactionUser(uuidPromote).setRank(factionUserManager.getFactionUser(uuidPromote).getRank() + 1);
                                factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).setRank(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getRank() - 1);
                                player.sendMessage("§aYou promoted §e" + UUIDFetcher.getName(uuidPromote) + "§a to the Rank '" + factionUserManager.getFactionUser(uuidPromote).getRankname() + "§a'");
                                player.sendMessage("§aYou have now the Rank '" + factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getRankname() + "§a'");
                                Bukkit.getPlayer(UUIDFetcher.getName(uuidPromote)).sendMessage("§aYou got promoted to the Rank '" + factionUserManager.getFactionUser(uuidPromote).getRankname() + "§a'");
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
            permissions = "im.factions.faction.demote"
    )
    public void demote(CommandSender sender, String demote) {
        Player player = (Player) sender;

        if (userManager.isUserExists(demote)) {
            UUID uuidDemote = UUIDFetcher.getUUID(demote);
            if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
                if (factionUserManager.getFactionUser(uuidDemote) != null && factionUserManager.getFactionUser(uuidDemote).getFactionID() == (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player))).getFactionID()) {
                    if (!factionUserManager.getFactionUser(uuidDemote).equals(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)))) {
                        if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).isHigherRank(factionUserManager.getFactionUser(uuidDemote).getRank())) {
                            if (factionUserManager.getFactionUser(uuidDemote).getRank() > 0) {
                                factionUserManager.getFactionUser(uuidDemote).setRank(factionUserManager.getFactionUser(uuidDemote).getRank() - 1);
                                player.sendMessage("§aYou demoted §e" + demote + "§a to the Rank '" + factionUserManager.getFactionUser(uuidDemote).getRankname() + "§a'");
                                Bukkit.getPlayer(UUIDFetcher.getName(uuidDemote)).sendMessage("§aYou got demoted to the Rank '" + factionUserManager.getFactionUser(uuidDemote).getRankname() + "§a'");
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
            permissions = "im.factions.faction.info"
    )
    public void info(CommandSender sender) {
        Player player = (Player) sender;

        if (factionUserManager.isFactionUserExists(UUIDFetcher.getUUID(player))) {
            if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
                if (factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()) != null) {
                    player.sendMessage("§7---------------§eFactionInfos§7---------------");
                    //Name
                    player.sendMessage("§7Name: §e" + factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()).getName() + "§7[§e" + factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()).getShortcut() + "§7]");
                    //RaidProtection
                    int totalSecs = (int) ((int) factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()).getRaidProtection() - System.currentTimeMillis()) / 1000;
                    int hours = totalSecs / 3600;
                    int minutes = (totalSecs % 3600) / 60;
                    int seconds = totalSecs % 60;
                    String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    player.sendMessage("§7RaidProtection: §e" + timeString);
                    //Founding Date
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    player.sendMessage("§7FoundingDate: §e" + simpleDateFormat.format(factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()).getFoundingDate()));
                    //Member Amount
                    TextComponent textComponent = new TextComponent("§7[§eClick Here§7]");
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction members"));
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eSee a List of all Members").create()));
                    player.spigot().sendMessage(new ComponentBuilder("Members: ").color(net.md_5.bungee.api.ChatColor.GRAY).append(factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()).getUserAmount() + " ").color(net.md_5.bungee.api.ChatColor.YELLOW).append(textComponent).create());
                }
            } else {
                player.sendMessage("§7Invites from Factions:");
                ComponentBuilder factions = new ComponentBuilder();
                for (FactionUserManager.FactionUser factionUser : factionUserManager.getFactionInvites(UUIDFetcher.getUUID(player))) {
                    TextComponent textComponent = new TextComponent();
                    ComponentBuilder componentBuilder = new ComponentBuilder();
                    componentBuilder.append(factionManager.getFactionName(factionUser.getFactionID())).color(net.md_5.bungee.api.ChatColor.YELLOW).append("[").color(net.md_5.bungee.api.ChatColor.GRAY).append(factionManager.getFaction(factionUser.getFactionID()).getShortcut()).color(net.md_5.bungee.api.ChatColor.YELLOW).append("], ").color(net.md_5.bungee.api.ChatColor.GRAY);
                    textComponent.setExtra(componentBuilder.getParts());
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction accept " + factionManager.getFactionName(factionUser.getFactionID())));
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join ").color(net.md_5.bungee.api.ChatColor.GRAY).append(factionManager.getFactionName(factionUser.getFactionID())).color(net.md_5.bungee.api.ChatColor.YELLOW).create()));
                    factions.append(textComponent);
                }
                player.spigot().sendMessage(factions.create());
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
            permissions = "im.factions.faction.members"
    )
    public void infoMembers(CommandSender sender) {
        Player player = (Player) sender;

        if (factionUserManager.isFactionUserExists(UUIDFetcher.getUUID(player))) {
            if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
                if (factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionID()) != null) {
                    //factionUser
                    FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player));
                    FactionManager.Faction faction = factionManager.getFaction(factionUser.getFactionID());
                    ArrayList<FactionUserManager.FactionUser> ranks = factionUserManager.getFactionUsers(faction.getId());
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
            permissions = "im.factions.faction.home"
    )
    public void home(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);

        if(factionUserManager.isFactionUserExists(uuid)){
            if(factionUserManager.isFactionUserInFaction(uuid)){
                int factionId = factionUserManager.getFactionUser(uuid).getFactionID();
                FactionManager.Faction faction = factionManager.getFaction(factionId);
                FactionPlotManager.FactionPlot factionPlot = factionPlotManager.getFactionPlot(factionId);
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
            permissions = "im.factions.faction.sethome"
    )
    public void sethome(CommandSender sender) {
        Player player = (Player) sender;
        UUID uuid = UUIDFetcher.getUUID(player);
        if(factionUserManager.isFactionUserExists(uuid)) {
            if(factionUserManager.isFactionUserInFaction(uuid)) {
                FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(uuid);
                FactionPlotManager.FactionPlot factionPlot = factionPlotManager.getFactionPlot(factionUser.getFactionID());
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
        builder.append("\n" + factions.getData().getSymbol() + "§e" + usage + "§8: §7 " + description + ChatColor.RESET);
    }
}
