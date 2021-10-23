package de.imfactions.functions.user;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class UserUtil {

    private final IMFactions imFactions;
    private final Data data;
    private final ArrayList<User> users;
    private final UserTable userTable;

    public UserUtil(Data data) {
        this.data = data;
        imFactions = data.getImFactions();
        userTable = new UserTable(this, data);
        users = userTable.getUsers();
        Bukkit.getScheduler().runTaskTimerAsynchronously(imFactions, new Runnable() {
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
        User user = new User(uuid);
        users.add(user);
        userTable.createUser(user.getUUID(), user.getEther(), user.getOnlinetime(), user.getFirstJoin(), user.getLastSeen());
        return user;
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
        UUID uuid;
        try {
            uuid = UUIDFetcher.getUUID(name);
        } catch (Exception e) {
            return false;
        }
        return isUserExists(uuid);
    }

    public void saveUsers() {
        for (User user : users) {
            userTable.saveUser(user);
        }
    }
}
