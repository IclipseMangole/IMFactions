package de.imfactions.commands;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.database.faction.FactionManager;
import de.imfactions.database.faction.FactionPlotManager;
import de.imfactions.database.faction.FactionUserManager;
import de.imfactions.functions.WorldLoader;
import de.imfactions.util.Command.IMCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionPlot {

    private IMFactions imFactions;
    private FactionUserManager factionUserManager;
    private FactionManager factionManager;
    private FactionPlotManager factionPlotManager;
    private StringBuilder stringBuilder;
    private Data data;
    private WorldLoader worldLoader;

    public FactionPlot(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionManager = data.getFactionManager();
        factionPlotManager = data.getFactionPlotManager();
        factionUserManager = data.getFactionUserManager();
        worldLoader = imFactions.getWorldLoader();
    }

    @IMCommand(
            name = "factionplot",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.factions.factionplot",
            usage = "/factionplot",
            description = "See all factionplot commands"
    )
    public void factionplot(CommandSender sender) {
        stringBuilder = new StringBuilder();
        add("/factionplot", "Shows a Commmand-Overview");
        add("/factionplot create", "Creates a FactionPlot on your location");
        add("/factionplot delete", "Deletes a FactionPLot on your location");

        Player player = (Player) sender;
        player.sendMessage(stringBuilder.toString());
    }

    @IMCommand(
            name = "create",
            parent = "factionplot",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.factions.factionplot.create",
            usage = "/factionplot create",
            description = "Creates a FactionPlot on your location"
    )
    public void create(CommandSender sender) {
        Player player = (Player) sender;

        worldLoader.loadMap("FactionPlot", player.getLocation());
        player.sendMessage("§aThe FactionPlot is loading");
    }

    @IMCommand(
            name = "delete",
            parent = "factionplot",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.factions.factionplot.delete",
            usage = "/factionplot delete",
            description = "Deletes a FactionPlot on your location"
    )
    public void delete(CommandSender sender) {
            Player player = (Player) sender;

            worldLoader.deleteMap(player.getLocation());
            player.sendMessage("§aThe FactionPlot will be uninstalled soon");
    }

    private void add(String usage, String description) {
        stringBuilder.append("\n" + imFactions.getData().getSymbol() + "§e" + usage + "§8: §7 " + description + ChatColor.RESET);
    }
}
