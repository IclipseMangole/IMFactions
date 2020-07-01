package de.imfactions.commands;

import de.imfactions.IMFactions;
import de.imfactions.database.faction.FactionManager;
import de.imfactions.database.faction.FactionPlotManager;
import de.imfactions.database.faction.FactionUserManager;
import de.imfactions.database.UserManager;
import de.imfactions.functions.WorldLoader;
import de.imfactions.functions.plots.FactionPlot;
import de.imfactions.util.Command.IMCommand;
import de.imfactions.util.UUIDFetcher;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.*;

public class Faction {

    StringBuilder builder;
    FactionManager factionManager;
    FactionUserManager factionUserManager;
    UserManager userManager;
    FactionPlotManager factionPlotManager;

    public Faction() {
        this.factionManager = IMFactions.getInstance().getData().getFactionManager();
        this.factionUserManager = IMFactions.getInstance().getData().getFactionUserManager();
        this.userManager = IMFactions.getInstance().getData().getUserManager();
        this.factionPlotManager = IMFactions.getInstance().getData().getFactionPlotManager();
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
            builder.append(IMFactions.getInstance().getData().getPrefix() + "§7Overview" + "\n");
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
                if (name.length() < 31) {
                    if (shortcut.length() == 3) {
                        int factionId = factionManager.getHighestFactionId() + 1;
                        int position = factionPlotManager.getNextPosition();
                        Location edgeDownFrontRight = factionPlotManager.getLocationFromPosition(position, "edgeDownFrontRight");
                        Location edgeUpBackLeft = factionPlotManager.getLocationFromPosition(position, "edgeUpBackLeft");
                        Location home = edgeDownFrontRight;
                        home.setY(17);
                        factionManager.createFaction(factionId, name, shortcut);
                        factionUserManager.createFactionUser(UUIDFetcher.getUUID(player), factionId, 3, true);
                        factionPlotManager.createFactionPlot(factionId, edgeDownFrontRight, edgeUpBackLeft, home, System.currentTimeMillis() + 1000 * 60, position);
                        player.sendMessage("§aYou founded the Faction '§e" + name + "§a'");
                    } else {
                        player.sendMessage("§cA shortcut consists of 3 characters");
                    }
                } else {
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
                if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)) != null && factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()) != null) {
                    FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player));
                    FactionManager.Faction faction = factionManager.getFaction(factionUser.getFactionId());
                    if (faction.getUserAmount() == 1) {
                        faction.deleteFaction();
                        factionUser.delete();
                        player.sendMessage("§aYou left the Faction. The Faction isn't existing anymore.");
                        //factionplots müssen noch gelöscht werden
                        factionPlotManager.getFactionPlot(factionUser.getFactionId()).deleteFactionPlot();
                    } else {
                        int factionId = factionUser.getFactionId();
                        if (factionUser.getRank() == 3) {
                            factionUser.delete();
                            ArrayList<FactionUserManager.FactionUser> highestUser = factionUserManager.getHighestFactionUsers(factionId);
                            Random random = new Random();
                            highestUser.get(random.nextInt(highestUser.size())).setRank(3);
                            faction.setUserAmount(faction.getUserAmount() - 1);
                            player.sendMessage("§aYou left the Faction");

                            FactionUserManager.FactionUser[] king = new FactionUserManager.FactionUser[1];
                            highestUser.forEach(user -> {
                                if (user.getRank() == 3) {
                                    king[0] = user;
                                }
                            });
                            factionUserManager.getFactionUsers(factionId).forEach(user -> {
                                Bukkit.getPlayer(UUIDFetcher.getName(user.getUuid())).sendMessage("§e" + player.getName() + "§a left the Faction");
                                Bukkit.getPlayer(UUIDFetcher.getName(user.getUuid())).sendMessage("§e" + UUIDFetcher.getName(king[0].getUuid()) + "§a is now the new §4KING");
                            });
                        } else {
                            factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).delete();
                            factionManager.getFaction(factionId).setUserAmount(factionManager.getFaction(factionId).getUserAmount() - 1);
                            player.sendMessage("§aYou left the Faction");
                            factionUserManager.getFactionUsers(factionId).forEach(user -> {
                                Bukkit.getPlayer(UUIDFetcher.getName(user.getUuid())).sendMessage("§e" + player.getName() + "§a left the Faction");
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

        if (UUIDFetcher.getUUID(name) != null) {
            UUID uuidInvite = UUIDFetcher.getUUID(name);
            if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
                if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getRank() > 0) {
                    if (!factionUserManager.isFactionUserInFaction(uuidInvite)) {
                        FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player));
                        FactionUserManager.FactionUser invite = factionUserManager.getFactionUser(UUIDFetcher.getUUID(name));
                        FactionManager.Faction faction = factionManager.getFaction(factionUser.getFactionId());
                        if (!factionUserManager.getFactionUsers(factionUser.getFactionId()).contains(invite)) {
                            factionUserManager.createFactionUser(uuidInvite, factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId(), -1, true);
                            player.sendMessage("§aYou invited §e" + name + "§a to join your Faction");

                            TextComponent command = new TextComponent();
                            command.setText("/faction accept " + faction.getName());
                            command.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                            command.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction accept " + faction.getName()));
                            command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eClick to join").create()));
                            if(Bukkit.getPlayer(name).isOnline()) {
                                Bukkit.getPlayer(name).spigot().sendMessage(new ComponentBuilder("You got invited by the Faction ").color(net.md_5.bungee.api.ChatColor.GREEN).append(faction.getName()).color(net.md_5.bungee.api.ChatColor.YELLOW).append(". Do ").color(net.md_5.bungee.api.ChatColor.GREEN).append(command).append(" to join the Faction").color(net.md_5.bungee.api.ChatColor.GREEN).create());
                            }
                        } else {
                            player.sendMessage("§cThis player is already invited to your Faction");
                        }
                    } else {
                        FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player));
                        FactionUserManager.FactionUser invite = factionUserManager.getFactionUser(UUIDFetcher.getUUID(name));
                        if(factionUser.equals(invite)){
                            player.sendMessage("§cDon't try it Anakin!");
                        }else {
                            player.sendMessage("§cThis Player is already member of a Faction");
                        }
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
                    FactionManager.Faction faction = factionManager.getFaction(name);
                    for (FactionUserManager.FactionUser user : factionUserManager.getFactionInvites(UUIDFetcher.getUUID(player))) {
                        user.delete();
                    }
                    faction.setUserAmount(faction.getUserAmount() + 1);
                    factionUserManager.createFactionUser(UUIDFetcher.getUUID(player), factionManager.getFaction(name).getId(), 0, true);
                    player.sendMessage("§aYou joined the Faction '§e" + name + "§a'");
                    factionUserManager.getFactionUsers(factionManager.getFactionId(name)).forEach(user -> {
                        if(Bukkit.getPlayer(UUIDFetcher.getName(user.getUuid())).isOnline()) {
                            Bukkit.getPlayer(UUIDFetcher.getName(user.getUuid())).sendMessage("§e" + player.getName() + "§a joined the Faction");
                        }
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

        if (UUIDFetcher.getUUID(kick) != null) {
            UUID uuidKick = UUIDFetcher.getUUID(kick);
            if (factionUserManager.isFactionUserExists(UUIDFetcher.getUUID(player))) {
                if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
                    if (factionUserManager.isFactionUserExists(uuidKick) && factionUserManager.getFactionUser(UUIDFetcher.getUUID(kick)) != null) {
                        FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player));
                        FactionUserManager.FactionUser kickUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(kick));
                        if (factionUserManager.isFactionUserInFaction(uuidKick)) {
                            if (kickUser.getFactionId() == factionUser.getFactionId()) {
                                if (!kickUser.equals(factionUser)) {
                                    if (factionUser.isHigherRank(kickUser.getRank())) {
                                        kickUser.delete();
                                        player.sendMessage("§aYou kicked §e" + kick + " §aout of the Faction");
                                        if(Bukkit.getPlayer(kick).isOnline()) {
                                            Bukkit.getPlayer(kick).sendMessage("§e" + player.getName() + "§a kicked you out of the Faction");
                                        }
                                        factionUserManager.getFactionUsers(factionUser.getFactionId()).forEach(user -> {
                                            if(Bukkit.getPlayer(UUIDFetcher.getName(user.getUuid())).isOnline()) {
                                                Bukkit.getPlayer(UUIDFetcher.getName(user.getUuid())).sendMessage("§e" + kick + "§a was kicked out of the Faction");
                                            }
                                        });
                                    } else {
                                        player.sendMessage("§cYou have to be a higher Rank to kick this Player");
                                    }
                                } else {
                                    player.sendMessage("§cWhy fucking kick yourself? Use the command §e/faction leave §cto leave your Faction");
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

        if (factionUserManager.isFactionUserExists(UUIDFetcher.getUUID(promote))) {
            UUID uuidPromote = UUIDFetcher.getUUID(promote);
            if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
                if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(promote)) != null && factionUserManager.getFactionUser(UUIDFetcher.getUUID(promote)).getFactionId() == factionUserManager.getFactionUser(UUIDFetcher.getUUID(promote)).getFactionId()) {
                    FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(promote));
                    FactionUserManager.FactionUser promoteUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(promote));
                    if (!promoteUser.equals(factionUser)) {
                        if (factionUser.isHigherRank(promoteUser.getRank())) {
                            if (promoteUser.getRank() < 2) {
                                promoteUser.setRank(promoteUser.getRank() + 1);
                                player.sendMessage("§aYou promoted §e" + UUIDFetcher.getName(uuidPromote) + "§a to the Rank '" + promoteUser.getRankname() + "§a'");
                                if(Bukkit.getPlayer(promote).isOnline()) {
                                    Bukkit.getPlayer(promote).sendMessage("§aYou got promoted to the Rank '" + promoteUser.getRankname() + "§a'");
                                }
                            } else {
                                promoteUser.setRank(promoteUser.getRank() + 1);
                                factionUser.setRank(factionUser.getRank() - 1);
                                player.sendMessage("§aYou promoted §e" + UUIDFetcher.getName(uuidPromote) + "§a to the Rank '" + promoteUser.getRankname() + "§a'");
                                player.sendMessage("§aYou have now the Rank '" + factionUser.getRankname() + "§a'");
                                if(Bukkit.getPlayer(promote).isOnline()) {
                                    Bukkit.getPlayer(promote).sendMessage("§aYou got promoted to the Rank '" + promoteUser.getRankname() + "§a'");
                                }
                            }
                        } else {
                            player.sendMessage("§cYou have to be higher Rank to promote this Player");
                        }
                    } else {
                        player.sendMessage("§cPlease...You really think I'm stupid?");
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

        if (UUIDFetcher.getUUID(demote) != null) {
            UUID uuidDemote = UUIDFetcher.getUUID(demote);
            if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
                if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(demote)) != null && factionUserManager.getFactionUser(UUIDFetcher.getUUID(demote)).getFactionId() == factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()) {
                    FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player));
                    FactionUserManager.FactionUser demoteUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(demote));
                    if (!demoteUser.equals(factionUser)) {
                        if (factionUser.isHigherRank(demoteUser.getRank())) {
                            if (demoteUser.getRank() > 0) {
                                demoteUser.setRank(demoteUser.getRank() - 1);
                                player.sendMessage("§aYou demoted §e" + demote + "§a to the Rank '" + demoteUser.getRankname() + "§a'");
                                if(Bukkit.getPlayer(demote).isOnline()) {
                                    Bukkit.getPlayer(demote).sendMessage("§aYou got demoted to the Rank '" + demoteUser.getRankname() + "§a'");
                                }
                            } else {
                                player.sendMessage("§cThis player has already the lowest Rank");
                            }
                        } else {
                            player.sendMessage("§cYou have to be a higher Rank to demote this Player");
                        }
                    } else {
                        player.sendMessage("§cYou can't demote yourself. I can't imagine why somebody would like that");
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
                if (factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()) != null) {
                    FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player));
                    FactionManager.Faction faction = factionManager.getFaction(factionUser.getFactionId());
                    player.sendMessage("§7---------------§eFactionInfos§7---------------");
                    //Name
                    player.sendMessage("§7Name: §e" + faction.getName() + "§7[§e" + faction.getShortcut() + "§7]");
                    //RaidProtection
                    int totalSecs = (int) ((int) faction.getRaidProtection() - System.currentTimeMillis()) / 1000;
                    int hours = totalSecs / 3600;
                    int minutes = (totalSecs % 3600) / 60;
                    int seconds = totalSecs % 60;
                    String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    player.sendMessage("§7RaidProtection: §e" + timeString);
                    //Founding Date
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    player.sendMessage("§7FoundingDate: §e" + simpleDateFormat.format(faction.getFoundingDate()));
                    //Member Amount
                    TextComponent textComponent = new TextComponent("§7[§eClick Here§7]");
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction members"));
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eSee a List of all Members").create()));
                    player.spigot().sendMessage(new ComponentBuilder("Members: ").color(net.md_5.bungee.api.ChatColor.GRAY).append(faction.getUserAmount() + " ").color(net.md_5.bungee.api.ChatColor.YELLOW).append(textComponent).create());
                }
            } else {
                player.sendMessage("§7Invites from Factions:");
                ComponentBuilder factions = new ComponentBuilder();
                for (FactionUserManager.FactionUser user : factionUserManager.getFactionInvites(UUIDFetcher.getUUID(player))) {
                    FactionManager.Faction faction1 = factionManager.getFaction(user.getFactionId());
                    TextComponent textComponent = new TextComponent();
                    ComponentBuilder componentBuilder = new ComponentBuilder();
                    componentBuilder.append(faction1.getName()).color(net.md_5.bungee.api.ChatColor.YELLOW).append("[").color(net.md_5.bungee.api.ChatColor.GRAY).append(faction1.getShortcut()).color(net.md_5.bungee.api.ChatColor.YELLOW).append("], ").color(net.md_5.bungee.api.ChatColor.GRAY);
                    textComponent.setExtra(componentBuilder.getParts());
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction accept " + faction1.getName()));
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join ").color(net.md_5.bungee.api.ChatColor.GRAY).append(faction1.getName()).color(net.md_5.bungee.api.ChatColor.YELLOW).create()));
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
                if (factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()) != null) {
                    FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player));
                    FactionManager.Faction faction = factionManager.getFaction(factionUser.getFactionId());
                    //factionUser
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

        if(factionUserManager.isFactionUserExists(UUIDFetcher.getUUID(player))){
            if(factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))){
                FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player));
                FactionPlotManager.FactionPlot factionPlot = factionPlotManager.getFactionPlot(factionUser.getFactionId());
                if(factionPlot.getReachable() - System.currentTimeMillis() <= 0){
                    factionPlot.setReachable(0);
                    if(player.getWorld().getName().equalsIgnoreCase("world")){
                        player.teleport(factionPlot.getHome());
                    }else if(player.getWorld().getName().equalsIgnoreCase("FactionPlots_world")) {
                        player.teleport(factionPlot.getHome());
                    }else if(player.getWorld().getName().equalsIgnoreCase("FactionPVP_world")){

                    }
                }else{
                    player.sendMessage("§cYou have to wait " + (factionPlot.getReachable() - System.currentTimeMillis())/1000 + " seconds until your Plot is loaded");
                }
            }else{
                player.sendMessage("§cYou aren't Member of a Faction");
            }
        }else{
            player.sendMessage("§cYou aren't Member of a Faction");
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

        if(factionUserManager.isFactionUserExists(UUIDFetcher.getUUID(player))){
            if(factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))){
                FactionPlot factionPlotFunctions = IMFactions.getInstance().getData().getFactionPlotFunctions();
                FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player));
                FactionPlotManager.FactionPlot factionPlot = factionPlotManager.getFactionPlot(factionUser.getFactionId());
                if(factionPlotFunctions.isPlayerInPlot(factionPlot, player)){
                    factionPlot.setHome(player.getLocation());
                }else{
                    player.sendMessage("You have to be in your FactionPlot to change the Spawnpoint");
                }
            }else{
                player.sendMessage("You aren't Member of a Faction");
            }
        }else{
            player.sendMessage("You aren't Member of a Faction");
        }
    }

    @IMCommand(
            name = "test",
            usage = "faction test",
            description = "faction.test.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.factions.faction.test"
    )
    public void test(CommandSender sender, String name) {
        Player player = (Player) sender;

        WorldLoader.loadTest(name, player.getLocation());
    }

    private void add(String usage, String description) {
        builder.append("\n" + IMFactions.getInstance().getData().getSymbol() + "§e" + usage + "§8: §7 " + description + ChatColor.RESET);
    }
}
