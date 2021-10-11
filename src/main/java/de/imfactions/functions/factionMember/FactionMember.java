package de.imfactions.functions.factionMember;

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
        switch (rank) {
            case 0:
                return "§2Member";
            case 1:
                return "§9Knight";
            case 2:
                return "§5Veteran";
            default:
                return "§4King";
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
        if (this.rank > rank) {
            return true;
        } else {
            return false;
        }
    }
}
