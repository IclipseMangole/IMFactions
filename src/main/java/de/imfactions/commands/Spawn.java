package de.imfactions.commands;

import de.imfactions.IMFactions;
import de.imfactions.functions.Scheduler;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import de.imfactions.functions.factionPlot.FactionPlotUtil;
import de.imfactions.functions.user.UserUtil;
import de.imfactions.util.Command.IMCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn {

    private FactionUtil factionUtil;
    private IMFactions imFactions;
    private FactionMemberUtil factionMemberUtil;
    private FactionPlotUtil factionPlotUtil;
    private StringBuilder builder;
    private UserUtil userUtil;
    private Scheduler scheduler;

    public Spawn(IMFactions imFactions){
        this.imFactions = imFactions;
        factionUtil = imFactions.getData().getFactionUtil();
        factionPlotUtil = imFactions.getData().getFactionPlotUtil();
        factionMemberUtil = imFactions.getData().getFactionMemberUtil();
        userUtil = imFactions.getData().getUserUtil();
        scheduler = imFactions.getData().getScheduler();
    }

    @IMCommand(
            name = "spawn",
            description = "§cTeleports you to the Spawn",
            usage = "§c/spawn",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.factions.spawn"
    )
    public void spawn(CommandSender sender) {
        Player player = (Player) sender;

        String world = player.getWorld().getName();

        if (!scheduler.getCountdowns().containsKey(player)) {
            if (world.equals("FactionPVP_world")) {
                scheduler.getCountdowns().put(player, 20);
            } else {
                scheduler.getCountdowns().put(player, 5);
            }
            Location location = imFactions.getData().getWorldSpawn();
            scheduler.getLocations().put(player, location);
        }else{
            player.sendMessage("§cYou are already teleporting");
        }
    }


}
