package de.imfactions.functions.factionPlot;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.WorldManager;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.util.Command.IMCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionPlotCommand {

    private final IMFactions imFactions;
    private final FactionMemberUtil factionMemberUtil;
    private final FactionUtil factionUtil;
    private final FactionPlotUtil factionPlotUtil;
    private StringBuilder stringBuilder;
    private final Data data;
    private final WorldManager worldLoader;

    public FactionPlotCommand(IMFactions imFactions) {
        this.imFactions = imFactions;
        data = imFactions.getData();
        factionUtil = data.getFactionUtil();
        factionMemberUtil = data.getFactionMemberUtil();
        factionPlotUtil = data.getFactionPlotUtil();
        worldLoader = imFactions.getWorldManager();
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
        player.sendMessage(ChatColor.GREEN + "The FactionPlot is loading");
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

        worldLoader.loadMap("Luft", player.getLocation());
        player.sendMessage(ChatColor.GREEN + "The FactionPlot will be uninstalled soon");
    }

    private void add(String usage, String description) {
        stringBuilder.append("\n" + imFactions.getData().getSymbol() + "§e" + usage + "§8: §7 " + description + ChatColor.RESET);
    }
}
