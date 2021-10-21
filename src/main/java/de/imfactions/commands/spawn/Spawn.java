package de.imfactions.commands.spawn;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.util.Command.IMCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn {

    private final IMFactions imFactions;
    private final Data data;
    private final SpawnScheduler spawnScheduler;

    public Spawn(IMFactions imFactions){
        this.imFactions = imFactions;
        data = imFactions.getData();
        spawnScheduler = data.getSpawnScheduler();
    }

    @IMCommand(
            name = "spawn",
            description = "§cTeleports you to the Spawn",
            usage = "§c/spawn",
            minArgs = 0,
            maxArgs = 0,
            permissions = "im.imFactions.spawn",
            noConsole = true
    )
    public void spawn(CommandSender sender) {
        Player player = (Player) sender;
        String world = player.getWorld().getName();

        if(world.equalsIgnoreCase("FactionPVP_world")) {
            spawnScheduler.teleportSpawn(player, 10);
            return;
        }
        spawnScheduler.teleportSpawn(player, 5);
    }
}
