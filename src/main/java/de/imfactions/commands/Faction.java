package de.imfactions.commands;

import de.imfactions.IMFactions;
import de.imfactions.database.faction.FactionManager;
import de.imfactions.database.faction.FactionUserManager;
import de.imfactions.database.UserManager;
import de.imfactions.util.Command.IMCommand;
import de.imfactions.util.UUIDFetcher;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    public Faction() {
        this.factionManager = IMFactions.getInstance().getData().getFactionManager();
        this.factionUserManager = IMFactions.getInstance().getData().getFactionUserManager();
        this.userManager = IMFactions.getInstance().getData().getUserManager();
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
            builder.append("Overview" + "\n");
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
                        int factionId = factionManager.getHighestFactionId() + 1;
                        factionManager.createFaction(factionId, name, shortcut);
                        factionUserManager.createFactionUser(UUIDFetcher.getUUID(player), factionId, 3, true);
                        player.sendMessage("§aYou founded the Faction '§e" + name + "§a'");
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
                if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)) != null && factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()) != null) {
                    if (factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).getUserAmount() == 1) {
                        factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).deleteFaction();
                        factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).delete();
                        player.sendMessage("§aYou left the Faction. The Faction isn't existing anymore.");
                        //factionplots müssen noch gelöscht werden
                    } else {
                        int factionId = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId();
                        if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getRank() == 3) {
                            factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).delete();
                            ArrayList<FactionUserManager.FactionUser> highestUser = factionUserManager.getHighestFactionUsers(factionId);
                            Random random = new Random();
                            highestUser.get(random.nextInt(highestUser.size())).setRank(3);
                            factionManager.getFaction(factionId).setUserAmount(factionManager.getFaction(factionId).getUserAmount() - 1);
                            player.sendMessage("§aYou left the Faction");

                            FactionUserManager.FactionUser[] king = new FactionUserManager.FactionUser[1];
                            int kingPlace;
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
                        factionUserManager.createFactionUser(uuidInvite, factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId(), -1, true);
                        player.sendMessage("§aYou invited §e" + name + "§a to join your Faction");

                        TextComponent command = new TextComponent();
                        command.setText("/faction accept " + factionManager.getFactionName(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()));
                        command.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                        command.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction accept " + factionManager.getFactionName(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId())));
                        command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eClick to join").create()));

                        Bukkit.getPlayer(name).spigot().sendMessage(new ComponentBuilder("You got invited by the Faction ").color(net.md_5.bungee.api.ChatColor.GREEN).append(factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).getName()).color(net.md_5.bungee.api.ChatColor.YELLOW).append(". Do ").color(net.md_5.bungee.api.ChatColor.GREEN).append(command).append(" to join the Faction").color(net.md_5.bungee.api.ChatColor.GREEN).create());
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
                    factionUserManager.getFactionUsers(factionManager.getFactionId(name)).forEach(factionUser -> {
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
                            if (factionUserManager.getFactionUser(uuidKick).getFactionId() == factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()) {
                                if (!factionUserManager.getFactionUser(uuidKick).equals(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)))) {
                                    if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).isHigherRank(factionUserManager.getFactionUser(uuidKick).getRank())) {
                                        factionUserManager.getFactionUser(uuidKick).delete();
                                        player.sendMessage("§aYou kicked §e" + kick + " §aout of the Faction");
                                        Bukkit.getPlayer(kick).sendMessage("§e" + player.getName() + "§a kicked you out of the Faction");
                                        factionUserManager.getFactionUsers(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).forEach(factionUser -> {
                                            Bukkit.getPlayer(UUIDFetcher.getName(factionUser.getUuid())).sendMessage("§e" + kick + "§a was kicked out of the Faction");
                                        });
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
                if (factionUserManager.getFactionUser(uuidPromote) != null && factionUserManager.getFactionUser(uuidPromote).getFactionId() == factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()) {
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
                if (factionUserManager.getFactionUser(uuidDemote) != null && factionUserManager.getFactionUser(uuidDemote).getFactionId() == (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player))).getFactionId()) {
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
                if (factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()) != null) {
                    player.sendMessage("§7---------------§eFactionInfos§7---------------");
                    //Name
                    player.sendMessage("§7Name: §e" + factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).getName() + "§7[§e" + factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).getShortcut() + "§7]");
                    //RaidProtection
                    int totalSecs = (int) ((int) factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).getRaidProtection() - System.currentTimeMillis()) / 1000;
                    int hours = totalSecs / 3600;
                    int minutes = (totalSecs % 3600) / 60;
                    int seconds = totalSecs % 60;
                    String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    player.sendMessage("§7RaidProtection: §e" + timeString);
                    //Founding Date
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    player.sendMessage("§7FoundingDate: §e" + simpleDateFormat.format(factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).getFoundingDate()));
                    //Member Amount
                    TextComponent textComponent = new TextComponent("§7[§eClick Here§7]");
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction members"));
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eSee a List of all Members").create()));
                    player.spigot().sendMessage(new ComponentBuilder("Members: ").color(net.md_5.bungee.api.ChatColor.GRAY).append(factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).getUserAmount() + " ").color(net.md_5.bungee.api.ChatColor.YELLOW).append(textComponent).create());
                }
            } else {
                player.sendMessage("§7Invites from Factions:");
                ComponentBuilder factions = new ComponentBuilder();
                for (FactionUserManager.FactionUser factionUser : factionUserManager.getFactionInvites(UUIDFetcher.getUUID(player))) {
                    TextComponent textComponent = new TextComponent();
                    ComponentBuilder componentBuilder = new ComponentBuilder();
                    componentBuilder.append(factionManager.getFactionName(factionUser.getFactionId())).color(net.md_5.bungee.api.ChatColor.YELLOW).append("[").color(net.md_5.bungee.api.ChatColor.GRAY).append(factionManager.getFaction(factionUser.getFactionId()).getShortcut()).color(net.md_5.bungee.api.ChatColor.YELLOW).append("], ").color(net.md_5.bungee.api.ChatColor.GRAY);
                    textComponent.setExtra(componentBuilder.getParts());
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction accept " + factionManager.getFactionName(factionUser.getFactionId())));
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join ").color(net.md_5.bungee.api.ChatColor.GRAY).append(factionManager.getFactionName(factionUser.getFactionId())).color(net.md_5.bungee.api.ChatColor.YELLOW).create()));
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
                    //factionUser
                    FactionUserManager.FactionUser factionUser = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player));
                    FactionManager.Faction faction = factionManager.getFaction(factionUser.getFactionId());
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

    }

    private void add(String usage, String description) {
        builder.append("\n" + IMFactions.getInstance().getData().getSymbol() + "§e" + usage + "§8: §7 " + description + ChatColor.RESET);
    }
}
