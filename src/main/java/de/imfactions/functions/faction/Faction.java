package de.imfactions.functions.faction;

import java.time.Instant;
import java.util.Date;

public class Faction {

    private int factionID;
    private int memberAmount;
    private String name;
    private String shortcut;
    private Date foundingDate;
    private long raidProtection;
    private boolean gettingRaided;
    // maxmimale Energie: 10; RaidKosten: 5; +1 Energie pro 2 Stunden
    private int raidEnergy;

    public Faction(int factionID, String name, String shortcut, int memberAmount, Date foundingDate, long raidProtection, int raidEnergy, boolean gettingRaided) {
        this.factionID = factionID;
        this.name = name;
        this.shortcut = shortcut;
        this.memberAmount = memberAmount;
        this.foundingDate = foundingDate;
        this.raidProtection = raidProtection;
        this.raidEnergy = raidEnergy;
        this.gettingRaided = gettingRaided;
    }

    public Faction(int factionID, String name, String shortcut) {
        this.factionID = factionID;
        this.name = name;
        this.shortcut = shortcut;
        memberAmount = 1;
        foundingDate = Date.from(Instant.now());
        raidProtection = System.currentTimeMillis() +  12*60*60*1000;
        raidEnergy = 10;
        gettingRaided = false;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return factionID;
    }

    public int getMemberAmount() {
        return memberAmount;
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
        if(System.currentTimeMillis() >= raidProtection && !isGettingRaided())
            return true;
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

    public void setMemberAmount(int memberAmount) {
        this.memberAmount = memberAmount;
    }

    public int getRaidEnergy() {
        return raidEnergy;
    }

    public void setRaidEnergy(int raidEnergy) {
        this.raidEnergy = raidEnergy;
    }

    public void memberJoin(){
        memberAmount += 1;
    }

    public void memberLeave(){
        memberAmount -= 1;
    }

    public boolean canRaid(){
        return raidEnergy >= 5;
    }

    public void raid(){
        raidEnergy -= 5;
    }

    public boolean isGettingRaided(){
        return gettingRaided;
    }

    public void setGettingRaided(boolean gettingRaided){
        this.gettingRaided = gettingRaided;
    }
}
