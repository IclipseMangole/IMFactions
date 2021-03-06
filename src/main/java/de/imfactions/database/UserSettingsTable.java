package de.imfactions.database;

import de.imfactions.IMFactions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserSettingsTable {

    private IMFactions factions;

    public UserSettingsTable(IMFactions factions) {
        this.factions = factions;
        factions.getData().getMySQL().update("CREATE TABLE IF NOT EXISTS usersettings (id MEDIUMINT NOT NULL AUTO_INCREMENT, uuid VARCHAR(60), `key` VARCHAR(256), `value` VARCHAR(256), PRIMARY KEY (id))");
    }

    public void createUserSetting(UUID uuid, String key, String value) {
        if (!isSettingExists(uuid, key)) {
            factions.getData().getMySQL().update("INSERT INTO usersettings (uuid, `key`, `value`) VALUES ('" + uuid + "', '" + key + "', '" + value + "')");
        }
    }

    public void createUserSetting(UUID uuid, String key, int value) {
        if (!isSettingExists(uuid, key)) {
            factions.getData().getMySQL().update("INSERT INTO usersettings (uuid, `key`, `value`) VALUES ('" + uuid + "', '" + key + "', '" + value + "')");
        }
    }

    public void createUserSetting(UUID uuid, String key, boolean value) {
        if (!isSettingExists(uuid, key)) {
            factions.getData().getMySQL().update("INSERT INTO usersettings (uuid, `key`, `value`) VALUES ('" + uuid + "', '" + key + "', '" + value + "')");
        }
    }


    public void createUserSetting(UUID uuid, String key, Object value) {
        if (value instanceof Integer) {
            createUserSetting(uuid, key, (int) value);
        } else if (value instanceof Boolean) {
            createUserSetting(uuid, key, (boolean) value);
        } else {
            createUserSetting(uuid, key, value.toString());
        }
    }

    public void deleteUserSetting(UUID uuid, String key) {
        factions.getData().getMySQL().update("DELETE usersettings WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
    }

    public int getId(UUID uuid, String key) {
        try {
            ResultSet rs = factions.getData().getMySQL().querry("SELECT id FROM usersettings WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
            while (rs.next()) {
                return Integer.parseInt(rs.getString("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean isSettingExists(UUID uuid, String key) {
        try {
            ResultSet rs = factions.getData().getMySQL().querry("SELECT id FROM usersettings WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getString(UUID uuid, String key) {
        try {
            ResultSet rs = factions.getData().getMySQL().querry("SELECT `value` FROM usersettings WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
            while (rs.next()) {
                return rs.getString("value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getInt(UUID uuid, String key) {
        try {
            ResultSet rs = factions.getData().getMySQL().querry("SELECT `value` FROM usersettings WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
            while (rs.next()) {
                return Integer.parseInt(rs.getString("value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean getBoolean(UUID uuid, String key) {
        try {
            ResultSet rs = factions.getData().getMySQL().querry("SELECT `value` FROM usersettings WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
            while (rs.next()) {
                return Boolean.parseBoolean(rs.getString("value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setString(UUID uuid, String key, String value) {
        factions.getData().getMySQL().update("UPDATE usersettings SET `value` = '" + value + "' WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
    }

    public void setInt(UUID uuid, String key, int value) {
        factions.getData().getMySQL().update("UPDATE usersettings SET `value` = '" + value + "' WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
    }

    public void setBoolean(UUID uuid, String key, boolean value) {
        factions.getData().getMySQL().update("UPDATE usersettings SET `value` = '" + value + "' WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
    }


}
