package de.imfactions.commands;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import de.imfactions.IMFactions;
import de.imfactions.database.UserManager;
import de.imfactions.util.Command.IMCommand;
import de.imfactions.util.UUIDFetcher;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Iclipse on 16.06.2020
 */
public class Ether {
    private StringBuilder builder;
    private IMFactions factions;
    private UserManager manager;

    public Ether(IMFactions factions) {
        this.factions = factions;
        manager = factions.getData().getUserManager();
    }


    @IMCommand(
            name = "ether",
            usage = "/ether",
            description = "ether.description",
            permissions = "im.factions.ether"
    )
    public void execute(CommandSender sender) {
        if (sender.hasPermission("im.factions.ether.*")) {
            builder = new StringBuilder();
            builder.append(factions.getData().getPrefix() + "§7Overview" + "\n");
            add("ether add <Name> <Amount>", "Adds Ether to a User");
            add("ether remove <Name> <Amount>", "Removes Ether from a User");
            add("ether set <Name> <Amount>", "Sets Ether of a User");
            add("ether get (Name)", "Gets Ether of a User");
            sender.sendMessage(builder.toString());
        } else {
            if (sender instanceof Player) {
                sender.sendMessage(factions.getData().getPrefix() + "You have §e" + manager.getUser(UUIDFetcher.getUUID(sender.getName())).getEther() + " Ether");
            } else {
                sender.sendMessage(factions.getData().getNoConsole());
            }
        }
    }

    @IMCommand(
            name = "add",
            usage = "/ether add <Name> <Amount>",
            description = "ether.add.description",
            maxArgs = 2,
            minArgs = 2,
            parent = "ether",
            permissions = "im.factions.ether.add"
    )
    public void addEther(CommandSender sender, String name, int ether) {
        if (manager.isUserExists(name)) {
            UserManager.User user = manager.getUser(name);
            user.addEther(ether);
            sender.sendMessage("§e" + ether + "§7 Ether has been added to §e" + name + "´s§7 account!");
        } else {
            sender.sendMessage(factions.getData().getPrefix() + "§cThe player §e" + name + "§c doesn´t exist!");
        }
    }

    @IMCommand(
            name = "remove",
            usage = "/ether remove <Name> <Amount>",
            description = "Removes Ether from a User",
            maxArgs = 2,
            minArgs = 2,
            parent = "ether",
            permissions = "im.factions.ether.remove"
    )
    public void remove(CommandSender sender, String name, int ether) {
        if (manager.isUserExists(name)) {
            manager.getUser(name).removeEther(ether);
            sender.sendMessage("§e" + ether + "§7 Ether has been removed from §e" + name + "´s§7 account!");
        } else {
            sender.sendMessage(factions.getData().getPrefix() + "§cThe player §e" + name + "§c doesn´t exist!");
        }
    }

    @IMCommand(
            name = "set",
            usage = "/ether set <Name> <Amount>",
            description = "Sets Ether of a User",
            maxArgs = 2,
            minArgs = 2,
            parent = "ether",
            permissions = "im.factions.ether.set"
    )
    public void set(CommandSender sender, String name, int ether) {
        if (manager.isUserExists(name)) {
            manager.getUser(name).setEther(ether);
            sender.sendMessage("§e" + name + "§7 has now §e" + ether + "§7 Ether");
        } else {
            sender.sendMessage(factions.getData().getPrefix() + "§cThe player §e" + name + "§c doesn´t exist!");
        }
    }

    @IMCommand(
            name = "get",
            usage = "/ether get (Name)",
            description = "Gets Ether of a User",
            maxArgs = 1,
            minArgs = 0,
            parent = "ether",
            permissions = "im.factions.ether.get"
    )
    public void get(CommandSender sender, String name) {
        if (name == null) {
            if (sender instanceof Player) {
                sender.sendMessage(factions.getData().getPrefix() + "You have §e" + manager.getUser(sender.getName()).getEther() + " Ether");
            } else {
                sender.sendMessage(factions.getData().getNoConsole());
            }
        } else {
            if (manager.isUserExists(name)) {
                sender.sendMessage(factions.getData().getPrefix() + "§e" + name + "§7 has §e" + manager.getUser(name).getEther() + " Ether");
            } else {
                sender.sendMessage(factions.getData().getPrefix() + "§cThe player §e" + name + "§c doesn´t exist!");
            }
        }
    }


    private void add(String usage, String description) {
        builder.append("\n" + factions.getData().getSymbol() + "§e/" + usage + "§8: §7 " + description + ChatColor.RESET);
    }

}
