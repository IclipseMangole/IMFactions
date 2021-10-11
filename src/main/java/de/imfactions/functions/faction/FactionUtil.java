package de.imfactions.functions.faction;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.factionMember.FactionMember;
import de.imfactions.functions.factionMember.FactionMemberUtil;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class FactionUtil {

    private ArrayList<Faction> factions;
    private final IMFactions imFactions;
    private final Data data;
    private final FactionTable factionTable;
    private final FactionMemberUtil factionMemberUtil;
    private int raidEnergyCooldown;

    public FactionUtil(Data data) {
        this.data = data;
        imFactions = data.getImFactions();
        factionTable = new FactionTable(this, data);
        factionMemberUtil = data.getFactionMemberUtil();
        factions = factionTable.getFactions();
        raidEnergyCooldown = 0;
        Bukkit.getScheduler().runTaskTimerAsynchronously(imFactions, new Runnable() {
            @Override
            public void run() {
                saveFactions();

                for(Faction faction : factions){
                    if(faction.getRaidEnergy() < 20){
                        if(raidEnergyCooldown == 6){
                            faction.setRaidEnergy(faction.getRaidEnergy() + 1);
                            raidEnergyCooldown = raidEnergyCooldown - 6;
                        }else{
                            raidEnergyCooldown = raidEnergyCooldown + 1;
                        }
                    }
                }

            }
        }, 0, 10 * 60 * 20);
    }

    public ArrayList<Faction> getRaidableFactions(){
        ArrayList<Faction> raidableFactions = new ArrayList<>();
        for(Faction faction : factions){
            if(faction.isRaidable()){
                raidableFactions.add(faction);
            }
        }
        return raidableFactions;
    }

    public void createFaction(int factionID, String name, String shortcut) {
        new Faction(factionID, name, shortcut);
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
        return factionTable.getHighestFactionID();
    }

    public Faction getRandomFactionForRaid(int factionID){
        Random random = new Random();

        ArrayList<Faction> raidableFactions = getRaidableFactions();
        Faction faction = raidableFactions.get(random.nextInt(factions.size()));
        while(faction.getId() == factionID){
            faction = factions.get(random.nextInt(factions.size()));
        }
        return faction;
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
}
