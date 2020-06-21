package de.imfactions.database;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import de.imfactions.IMFactions;
import de.imfactions.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Iclipse on 16.06.2020
 */

public class UserManager {

    private ArrayList<User> users;

    public UserManager() {
        IMFactions.getInstance().getData().getMySQL().update("CREATE TABLE IF NOT EXISTS user (uuid VARCHAR(60), ether INT(10), onlinetime BIGINT, firstJoin DATETIME, lastSeen BIGINT, PRIMARY KEY(uuid))");
        users = new ArrayList<>();
        loadUsers();
        Bukkit.getScheduler().runTaskTimerAsynchronously(IMFactions.getInstance(), new Runnable() {
            @Override
            public void run() {
                saveUsers();
            }
        }, 0, 10 * 60 * 20);
    }

    public User getUser(UUID uuid) {
        for (User user : users) {
            if (user.getUUID().equals(uuid)) {
                return user;
            }
        }
        return null;
    }

    public User getUser(String name) {
        return getUser(UUIDFetcher.getUUID(name));
    }

    public User getUser(Player p) {
        return getUser(p.getName());
    }

    public User createUser(UUID uuid) {
        return new User(uuid);
    }

    public boolean isUserExists(UUID uuid) {
        for (User user : users) {
            if (user.getUUID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public boolean isUserExists(String name) {
        return isUserExists(UUIDFetcher.getUUID(name));
    }


    public void loadUsers() {
        try {
            ResultSet rs = IMFactions.getInstance().getData().getMySQL().querry("SELECT uuid, ether, onlinetime, firstJoin, lastSeen FROM `user` WHERE 1");
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                User user = new User(uuid, rs.getInt("ether"), rs.getLong("onlinetime"), rs.getDate("firstJoin"), rs.getLong("lastSeen"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveUsers() {
        System.out.println("Save Users");
        for (User user : users) {
            user.save();
        }
    }

    public class User {
        private UUID uuid;
        private int ether;
        private long onlinetime;
        private Date firstJoin;
        private long lastSeen;

        private User(UUID uuid, int ether, long onlinetime, Date firstJoin, long lastSeen) {
            this.uuid = uuid;
            this.ether = ether;
            this.onlinetime = onlinetime;
            this.lastSeen = lastSeen;
            this.firstJoin = firstJoin;
        }

        private User(UUID uuid) {
            this(uuid, 0, 0, Date.from(Instant.now()), -1);
            create();
        }

        public void create() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            IMFactions.getInstance().getData().getMySQL().update("INSERT INTO user VALUES ('" + uuid + "', " + ether + ", " + onlinetime + ", '" + sdf.format(firstJoin) + "', " + lastSeen + ")");
            users.add(this);
        }


        public void delete() {
            IMFactions.getInstance().getData().getMySQL().update("DELETE FROM user WHERE uuid = '" + uuid + "'");
        }

        public void save() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            IMFactions.getInstance().getData().getMySQL().update("UPDATE user SET ether = " + ether + ", onlinetime = " + onlinetime + ", firstJoin = '" + sdf.format(firstJoin) + "', lastSeen = " + lastSeen + " WHERE uuid = '" + uuid + "'");
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
}
