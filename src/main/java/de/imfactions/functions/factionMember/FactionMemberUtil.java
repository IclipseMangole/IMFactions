package de.imfactions.functions.factionMember;

import de.imfactions.IMFactions;
import de.imfactions.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class FactionMemberUtil {

    private IMFactions factions;
    private ArrayList<FactionUser> factionUsers;

    public FactionUserManager(IMFactions factions) {
        this.factions = factions;
        factions.getData().getMySQL().update("CREATE TABLE IF NOT EXISTS `factionUser` (`uuid` VARCHAR(64), `factionId` INT(10), `rank` INT(10), PRIMARY KEY(`uuid`))");
        factionUsers = new ArrayList<>();
        loadFactionUser();
        Bukkit.getScheduler().runTaskTimerAsynchronously(factions, new Runnable() {
            @Override
            public void run() {
                saveFactionUsers();
            }
        }, 0, 20 * 60 * 10);

    }

    public void createFactionUser(UUID uuid, int factionId, int rank, boolean save) {
        new FactionUser(uuid, factionId, rank, save);
    }

    public void createFactionUser(UUID uuid, int factionId, int rank) {
        if (!isFactionUserExists(uuid)) {
            factions.getData().getMySQL().update("INSERT INTO factionUser (`uuid`, `factionId`, `rank`) VALUES ('" + uuid + "', '" + factionId + "', '" + rank + "')");
        }
    }

    private void loadFactionUser() {
        try {
            ResultSet rs = factions.getData().getMySQL().querry("SELECT `uuid`, `factionId`, `rank` FROM factionUser WHERE 1");
            while (rs.next()) {
                factionUsers.add(new FactionUser(UUID.fromString(rs.getString("uuid")), rs.getInt("factionId"), rs.getInt("rank")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveFactionUsers() {
        for (FactionUser factionUser : factionUsers) {
            factionUser.save();
        }
    }

    public boolean isFactionUserExists(UUID uuid) {
        for (FactionUser factionUser : factionUsers) {
            if (factionUser.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFactionUserInFaction(UUID uuid) {
        for (FactionUser factionUser : factionUsers) {
            if (factionUser.getUuid().equals(uuid)) {
                if (factionUser.getRank() != -1) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public Player getPlayer(UUID uuid){
        for(FactionUser factionUser : factionUsers){
            if(factionUser.getUuid() == uuid){
                return Bukkit.getPlayer(uuid);
            }
        }
        return null;
    }

    public FactionUser getFactionUser(UUID uuid) {
        for (FactionUser factionUser : factionUsers) {
            if (factionUser.getUuid().equals(uuid)) {
                return factionUser;
            }
        }
        return null;
    }

    public ArrayList<Player> getOnlineMembers(int factionID){
        ArrayList<Player> online = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = UUIDFetcher.getUUID(player);
            int factionIDPlayer = getFactionUser(uuid).getFactionID();
            if(factionIDPlayer == factionID){
                online.add(player);
            }
        }
        return online;
    }

    public int getOnlineMembersAmount(int factionID){
        return getOnlineMembers(factionID).size();
    }

    public ArrayList<FactionUser> getFactionInvites(UUID uuid) {
        ArrayList<FactionUser> factionInvites = new ArrayList<>();
        for (FactionUser factionUser : factionUsers) {
            if (factionUser.getUuid().equals(uuid)) {
                factionInvites.add(factionUser);
            }
        }
        return factionInvites;
    }


    public ArrayList<FactionUser> getFactionUsers(int factionId) {
        ArrayList<FactionUser> factionIdUsers = new ArrayList<>();
        for (FactionUser factionUser : factionUsers) {
            if (factionUser.getFactionID() == factionId) {
                factionIdUsers.add(factionUser);
            }
        }
        return factionIdUsers;
    }

    public ArrayList<FactionUser> getHighestFactionUsers(int factionId) {
        ArrayList<FactionUser> highestUsers = new ArrayList<>();
        int highest = 0;
        for(FactionUser factionUser : factionUsers){
            if(factionUser.getFactionID() == factionId) {
                if (factionUser.getRank() > highest) {
                    highestUsers.clear();
                    highestUsers.add(factionUser);
                    highest = factionUser.getRank();
                } else if (factionUser.getRank() == highest) {
                    highestUsers.add(factionUser);
                }
            }
        }
        return  highestUsers;
    }
}
