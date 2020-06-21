package de.imfactions.commands;

import de.imfactions.IMFactions;
import de.imfactions.database.faction.FactionManager;
import de.imfactions.database.faction.FactionUserManager;
import de.imfactions.database.UserManager;
import de.imfactions.util.Command.IMCommand;
import de.imfactions.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

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
            usage = "faction.usage",
            description = "faction.description",
            permissions = "im.factions.faction"
    )
    public void execute(CommandSender sender) {
        if (sender.hasPermission("im.factions.faction.*")) {
            builder = new StringBuilder();
            builder.append("Overview" + "\n");
            add("/faction found <Name>", "Makes the Player King of a new Faction");
            add("/faction leave", "The Player leaves a Faction");
            add("/faction invite <Player>", "Invites a new Player to the Faction");
            add("/faction accept <Name>", "Accepts a Faction Invite");
            add("/faction kick <Player>", "Kicks a Player from the Faction");
            add("/faction promote <Player>", "The Player gets a higher Rank");
            add("/faction demote <Player>", "The Player gets a lower Rank");
            add("/faction info", "Lists all informations about your Faction or Invites from Factions");
            add("/faction home", "The player gets teleported to the Faction´s Plot");
            add("/faction sethome", "Sets the Home-Spawnpoint for the Faction");
            sender.sendMessage(String.valueOf(builder));
        }
    }

    @IMCommand(
            name = "found",
            usage = "faction found <Name>",
            description = "faction.found.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.factions.faction.found"
    )
    public void found(CommandSender sender, String name) {
        Player player = (Player) sender;

        if (!factionManager.isFactionExists(name)) {
            if (!factionUserManager.isFactionUserExists(UUIDFetcher.getUUID(player)) || factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getRank() == -1) {
                int factionId = factionManager.getHighestFactionId();
                factionManager.createFaction(factionId, name);
                factionUserManager.createFactionUser(UUIDFetcher.getUUID(player), factionId, 3, true);
                player.sendMessage("§aYou founded the Faction '§e" + name + "§a'");
            } else {
                player.sendMessage("§cYou are already in a Faction");
            }
        } else {
            player.sendMessage("§cThis Faction already exists");
        }
    }

    @IMCommand(
            name = "leave",
            usage = "faction leave",
            description = "faction.leave.description",
            minArgs = 0,
            maxArgs = 0,
            parent = "faction",
            permissions = "im.factions.faction.leave"
    )
    public void leave(CommandSender sender) {
        Player player = (Player) sender;

        if (factionUserManager.isFactionUserExists(UUIDFetcher.getUUID(player))) {
            if (factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).getUserAmount() == 1) {
                factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).deleteFaction();
                factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).delete();
                player.sendMessage("§aYou left the Faction. The Faction isn't existing anymore.");
                //factionplots müssen noch gelöscht werden
            } else {
                if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getRank() == 3) {
                    int factionId = factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId();
                    factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).delete();
                    ArrayList<FactionUserManager.FactionUser> highestUser = factionUserManager.getHighestFactionUsers(factionId);
                    Random random = new Random();
                    highestUser.get(random.nextInt(highestUser.size() - 1)).setRank(3);
                    player.sendMessage("§aYou left the Faction");
                } else {
                    factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).delete();
                    player.sendMessage("§aYou left the Faction");
                }
            }
        } else {
            player.sendMessage("§cYou aren't a member of a Faction");
        }
    }

    @IMCommand(
            name = "invite",
            usage = "faction invite <Player>",
            description = "faction.invite.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.factions.faction.invite"
    )
    public void invite(CommandSender sender, String name) {
        Player player = (Player) sender;
        UUID uuidInvite = UUIDFetcher.getUUID(name);

        if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
            if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getRank() > 0) {
                if (!factionUserManager.isFactionUserInFaction(uuidInvite)) {
                    factionUserManager.createFactionUser(uuidInvite, factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId(), -1, true);
                    player.sendMessage("§aYou invited §e" + name + "§a to join your Faction");
                    Bukkit.getPlayer(uuidInvite).sendMessage("§e" + player.getName() + "§a has invited you to his Faction. Do §e/faction accept " + name + " §ato join the Faction");
                } else {
                    player.sendMessage("§cThis Player is already member of a Faction");
                }
            } else {
                player.sendMessage("§cYou don't have the permission to invite Players");
            }
        } else {
            player.sendMessage("§cYou can't invite Players without being in a Faction");
        }
    }

    @IMCommand(
            name = "accept",
            usage = "faction accept <Name>",
            description = "faction.accept.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.factions.faction.accept"
    )
    public void accept(CommandSender sender, String name) {
        Player player = (Player) sender;

        if (factionUserManager.isFactionUserExists(UUIDFetcher.getUUID(player))) {
            if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
                if (factionManager.isFactionExists(name)) {
                    boolean invited = false;
                    for (FactionUserManager.FactionUser user : factionUserManager.getFactionInvites(UUIDFetcher.getUUID(player))) {
                        if (user.getFactionId() == factionManager.getFaction(name).getId()) {
                            invited = true;
                        }
                    }
                    if (invited) {
                        ArrayList<FactionUserManager.FactionUser> factionUsers = factionUserManager.getFactionUsers(factionManager.getFaction(name).getId());
                        factionUsers.forEach(factionUser -> {
                            if (factionUser.getFactionId() != factionManager.getFaction(name).getId()) {
                                factionUserManager.getFactionUser(factionUser.getUuid()).delete();
                            }
                        });

                        factionUserManager.createFactionUser(UUIDFetcher.getUUID(player), factionManager.getFaction(name).getId(), 0, true);
                        player.sendMessage("§aYou joined the Faction '§e" + name + "§a'");
                    }
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
            usage = "faction kick <Player>",
            description = "faction.kick.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.factions.faction.kick"
    )
    public void kick(CommandSender sender, String kick) {
        Player player = (Player) sender;
        UUID uuidKick = UUIDFetcher.getUUID(kick);

        if (factionUserManager.isFactionUserExists(UUIDFetcher.getUUID(player))) {
            if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
                if (factionUserManager.getFactionUsers(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).contains(factionUserManager.getFactionUser(uuidKick))) {
                    if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).isHigherRank(factionUserManager.getFactionUser(uuidKick).getRank())) {
                        factionUserManager.getFactionUser(uuidKick).delete();
                        player.sendMessage("§aYou kicked §e" + kick + " §aout of the Faction");
                    } else {
                        player.sendMessage("§cYou have to be a higher Rank to kick this Player");
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
    }

    @IMCommand(
            name = "promote",
            usage = "faction promote <Player>",
            description = "faction.promote.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.factions.faction.promote"
    )
    public void promote(CommandSender sender, String promote) {
        Player player = (Player) sender;
        UUID uuidPromote = UUIDFetcher.getUUID(promote);

        if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
            if (factionUserManager.getFactionUser(uuidPromote).getFactionId() == factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()) {
                if (factionUserManager.getFactionUser(uuidPromote).equals(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)))) {
                    if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).isHigherRank(factionUserManager.getFactionUser(uuidPromote).getRank())) {
                        if (factionUserManager.getFactionUser(uuidPromote).getRank() < 2) {
                            factionUserManager.getFactionUser(uuidPromote).setRank(factionUserManager.getFactionUser(uuidPromote).getRank() + 1);
                            player.sendMessage("§aYou promoted §e" + UUIDFetcher.getName(uuidPromote) + "§a to the Rank '" + factionUserManager.getFactionUser(uuidPromote).getRankname() + "§a'");
                            Bukkit.getPlayer(uuidPromote).sendMessage("§aYou got promoted to the Rank '" + factionUserManager.getFactionUser(uuidPromote).getRankname() + "§a'");
                        } else {
                            factionUserManager.getFactionUser(uuidPromote).setRank(factionUserManager.getFactionUser(uuidPromote).getRank() + 1);
                            factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).setRank(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getRank() - 1);
                            player.sendMessage("§aYou promoted §e" + UUIDFetcher.getName(uuidPromote) + "§a to the Rank '" + factionUserManager.getFactionUser(uuidPromote).getRankname() + "§a'");
                            player.sendMessage("§aYou have now the Rank '" + factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getRankname() + "§a'");
                            Bukkit.getPlayer(uuidPromote).sendMessage("§aYou got promoted to the Rank '" + factionUserManager.getFactionUser(uuidPromote).getRankname() + "§a'");
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
    }

    @IMCommand(
            name = "demote",
            usage = "faction demote <Player>",
            description = "faction.demote.description",
            minArgs = 1,
            maxArgs = 1,
            parent = "faction",
            permissions = "im.factions.faction.demote"
    )
    public void demote(CommandSender sender, String demote) {
        Player player = (Player) sender;
        UUID uuidDemote = UUIDFetcher.getUUID(demote);

        if (factionUserManager.isFactionUserInFaction(UUIDFetcher.getUUID(player))) {
            if (factionUserManager.getFactionUser(uuidDemote).getFactionId() == (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player))).getFactionId()) {
                if (factionUserManager.getFactionUser(uuidDemote).equals(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)))) {
                    if (factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).isHigherRank(factionUserManager.getFactionUser(uuidDemote).getRank())) {
                        if (factionUserManager.getFactionUser(uuidDemote).getRank() > 0) {
                            factionUserManager.getFactionUser(uuidDemote).setRank(factionUserManager.getFactionUser(uuidDemote).getRank() - 1);
                            player.sendMessage("§aYou demoted §e" + demote + "§a to the Rank '" + factionUserManager.getFactionUser(uuidDemote).getRankname() + "§a'");
                            Bukkit.getPlayer(uuidDemote).sendMessage("§aYou got demoted to the Rank '" + factionUserManager.getFactionUser(uuidDemote).getRankname() + "§a'");
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
    }

    @IMCommand(
            name = "info",
            usage = "faction info",
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
                player.sendMessage("§aInfos about your Faction:");
                //Name
                player.sendMessage("§aName: §e" + factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).getName());
                //Members
                StringBuilder members = new StringBuilder();
                members.append("§aMembers: ");
                for (FactionUserManager.FactionUser factionUser : factionUserManager.getFactionUsers(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId())) {
                    members.append("§7[").append(factionUser.getRankname()).append("§7] §e" + UUIDFetcher.getName(factionUser.getUuid()) + "§7, ");
                }
                player.sendMessage(members.toString());
                //RaidProtection
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd HH:mm:ss");
                player.sendMessage("§aRaidProtection: " + simpleDateFormat.format(factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).getRaidProtection()));
                //Founding Date
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                player.sendMessage("§aFoundingDate: §e" + simpleDateFormat2.format(factionManager.getFaction(factionUserManager.getFactionUser(UUIDFetcher.getUUID(player)).getFactionId()).getFoundingDate()));

            } else {
                player.sendMessage("§aInvites from Factions:");
                StringBuilder invites = new StringBuilder();
                for (FactionUserManager.FactionUser factionUser : factionUserManager.getFactionInvites(UUIDFetcher.getUUID(player))) {
                    invites.append("§e").append(factionManager.getFaction(factionUser.getFactionId())).append("§a, ");
                }
                player.sendMessage(invites.toString());
            }
        } else {
            player.sendMessage("§cThere are no Invites");
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
