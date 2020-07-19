package de.imfactions.database.faction;

import de.imfactions.IMFactions;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class FactionUserManager {

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

    public FactionUser getFactionUser(UUID uuid) {
        for (FactionUser factionUser : factionUsers) {
            if (factionUser.getUuid().equals(uuid)) {
                return factionUser;
            }
        }
        return null;
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
            if (factionUser.getFactionId() == factionId) {
                factionIdUsers.add(factionUser);
            }
        }
        return factionIdUsers;
    }

    public ArrayList<FactionUser> getHighestFactionUsers(int factionId) {
        ArrayList<FactionUser> highestUsers = new ArrayList<>();
        int highest = 0;
        for(FactionUser factionUser : factionUsers){
            if(factionUser.getFactionId() == factionId) {
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

    public class FactionUser {

        UUID uuid;
        int factionId;
        int rank;
        //King 3, Veteran 2, Knight 1, Member 0, Invited -1

        private FactionUser(UUID uuid, int factionId, int rank) {
            this.factionId = factionId;
            this.uuid = uuid;
            this.rank = rank;
        }

        public FactionUser(UUID uuid, int factionId, int rank, boolean save) {
            this.uuid = uuid;
            this.factionId = factionId;
            this.rank = rank;
            createFactionUser(uuid, factionId, rank);
            factionUsers.add(this);
            save();
        }

        public int getFactionId() {
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
                case -1:
                    return "§7Invited";
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

        public void save() {
            factions.getData().getMySQL().update("UPDATE factionUser SET `factionId` = '" + this.factionId + "', `rank` = '" + rank + "' WHERE `uuid` = '" + this.uuid.toString() + "'");
        }

        public void delete() {
            factions.getData().getMySQL().update("DELETE FROM factionUser WHERE `uuid` = '" + this.uuid.toString() + "'");
            factionUsers.remove(this);
        }
    }
}
