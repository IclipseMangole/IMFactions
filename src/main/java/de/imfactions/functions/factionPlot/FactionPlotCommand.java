package de.imfactions.functions.factionPlot;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.WorldLoader;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.util.Command.IMCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionPlotCommand {

    private IMFactions imFactions;
    private FactionMemberUtil factionMemberUtil;
    private FactionUtil factionUtil;
    private FactionPlotUtil factionPlotUtil;
    private StringBuilder stringBuilder;
    private Data data;
    private WorldLoader worldLoader;

    public FactionPlotCommand(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionUtil = data.getFactionUtil();
        factionMemberUtil = data.getFactionMemberUtil();
        factionPlotUtil = data.getFactionPlotUtil();
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
