package de.imfactions.functions.faction;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class Faction {

    private int factionID;
    private int userAmount;
    private String name;
    private String shortcut;
    private Date foundingDate;
    private long raidProtection;
    // maxmimale Energie: 10; RaidKosten: 5; +1 Energie pro 2 Stunden
    private int raidEnergy;

    public Faction(int factionID, String name, String shortcut, int userAmount, Date foundingDate, long raidProtection, int raidEnergy) {
        this.factionID = factionID;
        this.name = name;
        this.shortcut = shortcut;
        this.userAmount = userAmount;
        this.foundingDate = foundingDate;
        this.raidProtection = raidProtection;
        this.raidEnergy = raidEnergy;
    }

    public Faction(int factionID, String name, String shortcut) {
        this.factionID = factionID;
        this.name = name;
        this.shortcut = shortcut;
        userAmount = 1;
        foundingDate = Date.from(Instant.now());
        raidProtection = System.currentTimeMillis() +  12*60*60*1000;
        raidEnergy = 10;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return factionID;
    }

    public int getUserAmount() {
        return userAmount;
    }

    public Date getFoundingDate() {
        return foundingDate;
    }

    public long getRaidProtection() {
        return raidProtection;
    }

    public String getShortcut() {
        return shortcut;
    }

    public boolean isRaidable(){
        if(System.currentTimeMillis() >= raidProtection){
            return true;
        }
        return false;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public void addRaidProtection(long raidProtection) {
        this.raidProtection += raidProtection;
    }

    public void setRaidProtection(long raidProtection) {
        this.raidProtection = raidProtection;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFoundingDate(Date foundingDate) {
        this.foundingDate = foundingDate;
    }

    public void setId(int factionID) {
        this.factionID = factionID;
    }

    public void setUserAmount(int userAmount) {
        this.userAmount = userAmount;
    }

    public int getRaidEnergy() {
        return raidEnergy;
    }

    public void setRaidEnergy(int raidEnergy) {
        this.raidEnergy = raidEnergy;
    }
}
