package de.imfactions.functions.factionMember;

import org.bukkit.ChatColor;

import java.util.UUID;

public class FactionMember {

    UUID uuid;
    int factionId;
    int rank;
    //King 3, Veteran 2, Knight 1, Member 0

    public FactionMember(UUID uuid, int factionId, int rank) {
        this.uuid = uuid;
        this.factionId = factionId;
        this.rank = rank;
    }

    public int getFactionID() {
        return factionId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getRank() {
        return rank;
    }

    public String getRankname() {
        return getRankColor() + getRankString();
    }

    public String getRankString(){
        switch (rank) {
            case 0:
                return "Member";
            case 1:
                return "Knight";
            case 2:
                return "Veteran";
            default:
                return "King";
        }
    }

    public ChatColor getRankColor(){
        switch (rank){
            case 0:
                return ChatColor.DARK_GREEN;
            case 1:
                return ChatColor.BLUE;
            case 2:
                return ChatColor.DARK_PURPLE;
            default:
                return ChatColor.DARK_RED;
        }
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setFactionId(int factionId) {
        this.factionId = factionId;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isHigherRank(int rank) {
        return this.rank > rank;
    }

    public void promote(){
        rank += 1;
    }

    public void demote(){
        rank -= 1;
    }
}
