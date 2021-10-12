package de.imfactions.functions.raid;

import java.time.Instant;
import java.util.Date;

public class Raid {
    int raidID;
    RaidState raidState;
    int factionIdAttackers;
    int factionIdDefenders;
    Date start;
    long time;

    public Raid(int raidID, RaidState raidState, int factionIdAttackers, int factionIdDefenders, Date start, long time) {
        this.raidID = raidID;
        this.raidState = raidState;
        this.factionIdAttackers = factionIdAttackers;
        this.factionIdDefenders = factionIdDefenders;
        this.start = start;
        this.time = time;
    }

    public Raid(int raidID, int factionIdAttackers, int factionIdDefenders) {
        this.raidID = raidID;
        this.factionIdAttackers = factionIdAttackers;
        this.factionIdDefenders = factionIdDefenders;
        start = Date.from(Instant.now());
        time = 0;
    }

    public int getFactionIdAttackers() {
        return factionIdAttackers;
    }

    public void setFactionIdAttackers(int factionIdAttackers) {
        this.factionIdAttackers = factionIdAttackers;
    }

    public int getFactionIdDefenders() {
        return factionIdDefenders;
    }

    public void setFactionIdDefenders(int factionIdDefenders) {
        this.factionIdDefenders = factionIdDefenders;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public RaidState getRaidState(){
        return raidState;
    }

    public void setRaidState(RaidState raidState){
        this.raidState = raidState;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRaidID() {
        return raidID;
    }

    public void setRaidID(int raidID) {
        this.raidID = raidID;
    }
}
