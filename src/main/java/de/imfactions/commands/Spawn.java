package de.imfactions.commands;

import de.imfactions.IMFactions;
import de.imfactions.database.UserManager;
import de.imfactions.database.faction.FactionManager;
import de.imfactions.database.faction.FactionPlotManager;
import de.imfactions.database.faction.FactionUserManager;
import de.imfactions.functions.Scheduler;
import de.imfactions.util.Command.IMCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn {

    private FactionManager factionManager;
    private IMFactions imFactions;
    private FactionUserManager factionUserManager;
    private FactionPlotManager factionPlotManager;
    private StringBuilder builder;
    private UserManager userManager;
    private Scheduler scheduler;

    public Spawn(IMFactions imFactions){
        this.imFactions = imFactions;
        factionManager = imFactions.getData().getFactionManager();
        factionPlotManager = imFactions.getData().getFactionPlotManager();
        factionUserManager = imFactions.getData().getFactionUserManager();
        userManager = imFactions.getData().getUserManager();
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
