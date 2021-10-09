package de.imfactions.functions.user;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class User {
    private UUID uuid;
    private int ether;
    private long onlinetime;
    private Date firstJoin;
    private long lastSeen;

    public User(UUID uuid, int ether, long onlinetime, Date firstJoin, long lastSeen) {
        this.uuid = uuid;
        this.ether = ether;
        this.onlinetime = onlinetime;
        this.lastSeen = lastSeen;
        this.firstJoin = firstJoin;
    }

    public User(UUID uuid) {
        this(uuid, 0, 0, Date.from(Instant.now()), -1);
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getEther() {
        return ether;
    }

    public void setEther(int ether) {
        this.ether = ether;
    }

    public void addEther(int ether) {
        this.ether += ether;
    }

    public void removeEther(int ether) {
        this.ether = this.ether < ether ? 0 : this.ether - ether;
    }

    public long getOnlinetime() {
        return onlinetime;
    }

    public void setOnlinetime(long onlinetime) {
        this.onlinetime = onlinetime;
    }

    public void addOnlineTime(long onlinetime) {
        this.onlinetime += onlinetime;
    }

    public Date getFirstJoin() {
        return firstJoin;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }
}
