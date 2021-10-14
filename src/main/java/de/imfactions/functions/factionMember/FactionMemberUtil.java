package de.imfactions.functions.factionMember;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.functions.faction.Faction;
import de.imfactions.functions.faction.FactionUtil;
import de.imfactions.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class FactionMemberUtil {

    private final IMFactions imFactions;
    private final Data data;
    private ArrayList<FactionMember> factionMembers;
    private FactionMemberTable factionMemberTable;
    private FactionUtil factionUtil;

    public FactionMemberUtil(Data data) {
        this.data = data;
        imFactions = data.getImFactions();
        Bukkit.getScheduler().runTaskTimerAsynchronously(imFactions, new Runnable() {
            @Override
            public void run() {
                saveFactionMembers();
            }
        }, 20 * 60, 20 * 60 * 10);
    }

    public void loadUtils(){
        factionMemberTable = new FactionMemberTable(this, data);
        factionUtil = data.getFactionUtil();
        factionMembers = factionMemberTable.getFactionMembers();
    }

    public void createFactionMember(UUID uuid, int factionId, int rank) {
        FactionMember factionMember = new FactionMember(uuid, factionId, rank);
        factionMembers.add(factionMember);
        factionMemberTable.createFactionMember(uuid,factionId,rank);
    }

    public void saveFactionMembers() {
        for (FactionMember factionMember : factionMembers) {
            factionMemberTable.saveFactionMember(factionMember);
        }
    }

    public boolean isFactionMemberExists(UUID uuid) {
        for (FactionMember FactionMember : factionMembers) {
            if (FactionMember.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFactionMemberInFaction(FactionMember factionMember, Faction faction){
        return factionMember.getFactionID() == faction.getId();
    }

    public Player getPlayer(UUID uuid){
        for(FactionMember FactionMember : factionMembers){
            if(FactionMember.getUuid() == uuid){
                return Bukkit.getPlayer(uuid);
            }
        }
        return null;
    }

    public FactionMember getFactionMember(UUID uuid) {
        for (FactionMember FactionMember : factionMembers) {
            if (FactionMember.getUuid().equals(uuid)) {
                return FactionMember;
            }
        }
        return null;
    }

    public ArrayList<Player> getOnlineMembers(int factionID){
        ArrayList<Player> online = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = UUIDFetcher.getUUID(player);
            int factionIDPlayer = getFactionMember(uuid).getFactionID();
            if(factionIDPlayer == factionID){
                online.add(player);
            }
        }
        return online;
    }

    public int getOnlineMembersAmount(int factionID){
        return getOnlineMembers(factionID).size();
    }

    public ArrayList<FactionMember> getFactionInvites(UUID uuid) {
        ArrayList<FactionMember> factionInvites = new ArrayList<>();
        for (FactionMember FactionMember : factionMembers) {
            if (FactionMember.getUuid().equals(uuid)) {
                factionInvites.add(FactionMember);
            }
        }
        return factionInvites;
    }


    public ArrayList<FactionMember> getFactionMembers(int factionId) {
        ArrayList<FactionMember> factionIdUsers = new ArrayList<>();
        for (FactionMember FactionMember : factionMembers) {
            if (FactionMember.getFactionID() == factionId) {
                factionIdUsers.add(FactionMember);
            }
        }
        return factionIdUsers;
    }

    public ArrayList<FactionMember> getHighestFactionMembers(int factionId) {
        ArrayList<FactionMember> highestUsers = new ArrayList<>();
        int highest = 0;
        for(FactionMember FactionMember : factionMembers){
            if(FactionMember.getFactionID() == factionId) {
                if (FactionMember.getRank() > highest) {
                    highestUsers.clear();
                    highestUsers.add(FactionMember);
                    highest = FactionMember.getRank();
                } else if (FactionMember.getRank() == highest) {
                    highestUsers.add(FactionMember);
                }
            }
        }
        return  highestUsers;
    }

    public void leaveFaction(FactionMember factionMember){
        Faction faction = factionUtil.getFaction(factionMember.factionId);
        faction.memberLeave();
        deleteFactionMember(factionMember);
    }

    public void deleteFactionMember(FactionMember factionMember){
        factionMemberTable.deleteFactionMember(factionMember);
        factionMembers.remove(factionMember);
    }
}
