package de.imfactions.functions.raid;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.faction.Faction;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.functions.factionMember.FactionMember;
import de.imfactions.functions.factionPlot.FactionPlot;
import de.imfactions.functions.factionPlot.FactionPlotUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;

public class RaidScheduler implements Listener {

    private final Data data;
    private final IMFactions imFactions;
    private final RaidUtil raidUtil;
    private final FactionUtil factionUtil;
    private final FactionPlotUtil factionPlotUtil;
    private HashMap<Integer, BukkitTask> preparingRaids = new HashMap<>();
    private HashMap<Integer, BukkitTask> scoutingRaids = new HashMap<>();
    private HashMap<Integer, BukkitTask> raidingRaids = new HashMap<>();

    public RaidScheduler (Data data){
        this.data = data;
        imFactions = data.getImFactions();
        raidUtil = data.getRaidUtil();
        factionUtil = data.getFactionUtil();
        factionPlotUtil = data.getFactionPlotUtil();
    }

    public void startPreparingRaid(int raidID, int seconds){
        if(preparingRaids.containsKey(raidID))
            return;
        preparingRaids.put(raidID, Bukkit.getScheduler().runTaskTimer(imFactions, new Runnable() {
            int timer = seconds;
            int ID = raidID;
            Raid raid = raidUtil.getRaid(ID);
            Faction attackers = factionUtil.getFaction(raid.getFactionIdAttackers());
            @Override
            public void run() {
                if(timer <=0){
                    for(FactionMember factionMember : raidUtil.getRaidTeam(ID)){
                        Player player = Bukkit.getPlayer(factionMember.getUuid());
                        if(factionUtil.getRaidableFactions().size() < 2){
                            player.sendMessage(ChatColor.RED + "Error. Found no Faction to raid");
                            cancelPreparingRaid(raidID);
                            return;
                        }
                        Faction defenders = factionUtil.getRandomFactionForRaid(attackers.getId());
                        FactionPlot factionPlot = factionPlotUtil.getFactionPlot(defenders.getId());
                        Location raidSpawn = factionPlot.getRaidSpawn();
                        player.teleport(raidSpawn);
                        player.sendTitle(ChatColor.YELLOW + defenders.getName(), "Scouting", 5, 20,5);
                    }
                    raidUtil.updateRaidToScouting(raidID);
                    //startScoutingRaid(raidID, 300);
                    cancelPreparingRaid(raidID);
                    return;
                }
                timer--;
            }
        }, 0, 20));
    }

    public void cancelPreparingRaid(int raidID){
        preparingRaids.get(raidID).cancel();
    }
}
