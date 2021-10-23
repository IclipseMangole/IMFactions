package de.imfactions.commands;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.user.User;
import de.imfactions.functions.user.UserUtil;
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
    private final IMFactions imFactions;
    private final Data data;
    private final UserUtil userUtil;

    public Ether(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        userUtil = data.getUserUtil();
    }


    @IMCommand(
            name = "ether",
            usage = "/ether",
            description = "ether.description",
            permissions = "im.imFactions.ether"
    )
    public void execute(CommandSender sender) {
        if (sender.hasPermission("im.imFactions.ether.*")) {
            builder = new StringBuilder();
            builder.append(imFactions.getData().getPrefix() + "§7Overview" + "\n");
            add("ether add <Name> <Amount>", "Adds Ether to a User");
            add("ether remove <Name> <Amount>", "Removes Ether from a User");
            add("ether set <Name> <Amount>", "Sets Ether of a User");
            add("ether get (Name)", "Gets Ether of a User");
            sender.sendMessage(builder.toString());
        } else {
            if (sender instanceof Player) {
                sender.sendMessage(imFactions.getData().getPrefix() + "You have §e" + userUtil.getUser(UUIDFetcher.getUUID(sender.getName())).getEther() + " Ether");
            } else {
                sender.sendMessage(imFactions.getData().getNoConsole());
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
            permissions = "im.imFactions.ether.add"
    )
    public void addEther(CommandSender sender, String name, int ether) {
        if (userUtil.isUserExists(name)) {
            User user = userUtil.getUser(name);
            user.addEther(ether);
            sender.sendMessage("§e" + ether + "§7 Ether has been added to §e" + name + "´s§7 account!");
        } else {
            sender.sendMessage(imFactions.getData().getPrefix() + "§cThe player §e" + name + "§c doesn´t exist!");
        }
    }

    @IMCommand(
            name = "remove",
            usage = "/ether remove <Name> <Amount>",
            description = "Removes Ether from a User",
            maxArgs = 2,
            minArgs = 2,
            parent = "ether",
            permissions = "im.imFactions.ether.remove"
    )
    public void remove(CommandSender sender, String name, int ether) {
        if (userUtil.isUserExists(name)) {
            userUtil.getUser(name).removeEther(ether);
            sender.sendMessage("§e" + ether + "§7 Ether has been removed from §e" + name + "´s§7 account!");
        } else {
            sender.sendMessage(imFactions.getData().getPrefix() + "§cThe player §e" + name + "§c doesn´t exist!");
        }
    }

    @IMCommand(
            name = "set",
            usage = "/ether set <Name> <Amount>",
            description = "Sets Ether of a User",
            maxArgs = 2,
            minArgs = 2,
            parent = "ether",
            permissions = "im.imFactions.ether.set"
    )
    public void set(CommandSender sender, String name, int ether) {
        if (userUtil.isUserExists(name)) {
            userUtil.getUser(name).setEther(ether);
            sender.sendMessage("§e" + name + "§7 has now §e" + ether + "§7 Ether");
        } else {
            sender.sendMessage(imFactions.getData().getPrefix() + "§cThe player §e" + name + "§c doesn´t exist!");
        }
    }

    @IMCommand(
            name = "get",
            usage = "/ether get (Name)",
            description = "Gets Ether of a User",
            maxArgs = 1,
            minArgs = 0,
            parent = "ether",
            permissions = "im.imFactions.ether.get"
    )
    public void get(CommandSender sender, String name) {
        if (name == null) {
            if (sender instanceof Player) {
                sender.sendMessage(imFactions.getData().getPrefix() + "You have §e" + userUtil.getUser(sender.getName()).getEther() + " Ether");
            } else {
                sender.sendMessage(imFactions.getData().getNoConsole());
            }
        } else {
            if (userUtil.isUserExists(name)) {
                sender.sendMessage(imFactions.getData().getPrefix() + "§e" + name + "§7 has §e" + userUtil.getUser(name).getEther() + " Ether");
            } else {
                sender.sendMessage(imFactions.getData().getPrefix() + "§cThe player §e" + name + "§c doesn´t exist!");
            }
        }
    }


    private void add(String usage, String description) {
        builder.append("\n" + imFactions.getData().getSymbol() + "§e/" + usage + "§8: §7 " + description + ChatColor.RESET);
    }

}
