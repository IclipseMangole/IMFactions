package de.imfactions.functions.faction;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.factionMember.FactionMember;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

public class FactionUtil {

    private ArrayList<Faction> factions;
    private final IMFactions imFactions;
    private final Data data;
    private FactionTable factionTable;
    private FactionMemberUtil factionMemberUtil;
    private FactionHomeScheduler factionHomeScheduler;
    private int raidEnergyCooldown;

    public FactionUtil(Data data) {
        this.data = data;
        imFactions = data.getImFactions();
        raidEnergyCooldown = 0;
        Bukkit.getScheduler().runTaskTimerAsynchronously(imFactions, new Runnable() {
            @Override
            public void run() {
                saveFactions();

                for(Faction faction : factions){
                    if(faction.getRaidEnergy() < 20){
                        if (raidEnergyCooldown == 12) {
                            faction.setRaidEnergy(faction.getRaidEnergy() + 1);
                            raidEnergyCooldown = raidEnergyCooldown - 6;
                        } else {
                            raidEnergyCooldown = raidEnergyCooldown + 1;
                        }
                    }
                }

            }
        }, 20 * 60, 10 * 60 * 20);
    }

    public void loadUtils(){
        factionTable = new FactionTable(this, data);
        factionMemberUtil = data.getFactionMemberUtil();
        factionHomeScheduler = new FactionHomeScheduler(data);
        factions = factionTable.getFactions();
    }

    public ArrayList<Faction> getRaidableFactions(int factionID) {
        ArrayList<Faction> raidableFactions = new ArrayList<>();
        for (Faction faction : factions) {
            if (faction.isRaidable() && faction.getId() != factionID) {
                raidableFactions.add(faction);
            }
        }
        return raidableFactions;
    }

    public void createFaction(int factionID, String name, String shortcut) {
        Faction faction = new Faction(factionID, name, shortcut);
        factions.add(faction);
        factionTable.createFaction(faction.getId(), faction.getName(), faction.getShortcut(), faction.getMemberAmount(), faction.getFoundingDate(), faction.getRaidProtection(), faction.getRaidEnergy(), faction.isGettingRaided());
    }

    public Faction getFaction(int factionID) {
        for (Faction faction : factions) {
            if (faction.getId() == factionID) {
                return faction;
            }
        }
        return null;
    }

    public Faction getFaction(String name) {
        for (Faction faction : factions) {
            if (faction.getName().equals(name)) {
                return faction;
            }
        }
        return null;
    }

    public String getFactionName(int factionID){
        for(Faction faction : factions){
            if(faction.getId() == factionID){
                return faction.getName();
            }
        }
        return "";
    }

    public int getFactionID(String name){
        for(Faction faction : factions){
            if(faction.getName().equals(name)){
                return faction.getId();
            }
        }
        return -1;
    }

    public int getHighestFactionID(){
        int highest = 0;

        for (Faction faction : factions) {
            if (faction.getId() > highest)
                highest = faction.getId();
        }
        return highest;
    }

    public Faction getRandomFactionForRaid(int factionID) {
        Random random = new Random();

        ArrayList<Faction> raidableFactions = getRaidableFactions(factionID);
        return raidableFactions.get(random.nextInt(factions.size()));
    }

    public boolean isFactionExists(int factionID) {
        for (Faction faction : factions) {
            if (faction.getId() == factionID) {
                return true;
            }
        }
        return false;
    }

    public boolean isFactionExists(String name) {
        for (Faction faction : factions) {
            if (faction.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void saveFactions() {
        for (Faction faction : factions) {
            factionTable.saveFaction(faction);
        }
    }

    public ArrayList<Faction> getFactions() {
        return factions;
    }

    public void deleteFaction(Faction faction){
        factionTable.deleteFaction(faction);
    }

    public FactionMember getKing(Faction faction){
        for(FactionMember factionMember : factionMemberUtil.getFactionMembers(faction.getId())){
            if(factionMember.getRank() == 3){
                return factionMember;
            }
        }
        return null;
    }

    public void teleportHome(FactionMember factionMember){
        Player player = Bukkit.getPlayer(factionMember.getUuid());
        if(player.getLocation().getWorld().getName().equalsIgnoreCase("FactionsPVP_world")){
            factionHomeScheduler.teleportHome(factionMember, 10);
        }else {
            factionHomeScheduler.teleportHome(factionMember, 5);
        }

    }

    public FactionHomeScheduler getFactionHomeScheduler() {
        return factionHomeScheduler;
    }
}
