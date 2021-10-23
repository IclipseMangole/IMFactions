package de.imfactions.commands;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.util.Command.IMCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class World {

    private final IMFactions imFactions;
    private StringBuilder builder;
    private final Data data;

    public World(IMFactions imFactions){
        this.imFactions = imFactions;
        data = imFactions.getData();
    }

    @IMCommand(
            name = "world",
            usage = "§c/world",
            description = "world.description",
            permissions = "im.factions.world"
    )
    public void execute(CommandSender sender){
        if(sender.hasPermission("im.factions.world.*")){
            builder = new StringBuilder();
            builder.append(data.getPrefix() + "§eOverview§8:");
            add("/world list","Shows a List of all worlds");
            add("world tp", "Tp's the player to a world");
            sender.sendMessage(String.valueOf(builder));
        }
    }

    @IMCommand(
            name = "list",
            usage = "§c/world list",
            description = "list.description",
            permissions = "im.factions.world.list",
            parent = "world"
    )
    public void list(CommandSender sender){
        Player player = (Player) sender;
        TextComponent base = new TextComponent();
        base.addExtra(data.getPrefix() + "§eWorlds§8: ");
        Bukkit.getWorlds().forEach(world -> {
            TextComponent textComponent = new TextComponent();
            textComponent.setText("§5" + world.getName());
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eClick to tp you to §5" + world.getName()).create()));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/world tp " + world.getName()));
            base.addExtra(textComponent);
            base.addExtra("§8, ");
        });
        player.spigot().sendMessage(base);
    }

    @IMCommand(
            name = "tp",
            usage = "§c/world tp <World>",
            description = "tp.description",
            permissions = "im.factions.world.tp",
            minArgs = 1,
            maxArgs = 1,
            parent = "world"
    )
    public void tp(CommandSender sender, String world){
        Player player = (Player) sender;
        org.bukkit.World w = Bukkit.getWorld(world);
        Location spawn = w.getSpawnLocation();
        Location location = new Location(w, spawn.getX(), spawn.getY(), spawn.getZ());
        player.teleport(location);
    }

    private void add(String usage, String description) {
        builder.append("\n" + imFactions.getData().getSymbol() + "§e" + usage + "§8: §7 " + description + ChatColor.RESET);
    }
}
