package de.imfactions.functions.user;

import de.imfactions.Data;
import de.imfactions.IMFactions;
import de.imfactions.util.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserSettingsTable {

    private final IMFactions imFactions;
    private final Data data;
    private final MySQL mySQL;
    private final UserSettingsUtil userSettingsUtil;

    public UserSettingsTable(UserSettingsUtil userSettingsUtil, Data data) {
        this.data = data;
        this.userSettingsUtil = userSettingsUtil;
        imFactions = data.getImFactions();
        mySQL = data.getMySQL();
        mySQL.update("CREATE TABLE IF NOT EXISTS usersettings (id MEDIUMINT NOT NULL AUTO_INCREMENT, uuid VARCHAR(60), `key` VARCHAR(256), `value` VARCHAR(256), PRIMARY KEY (id))");
    }

    public void createUserSetting(UUID uuid, String key, String value) {
        if (!isSettingExists(uuid, key)) {
            mySQL.update("INSERT INTO usersettings (uuid, `key`, `value`) VALUES ('" + uuid + "', '" + key + "', '" + value + "')");
        }
    }

    public void createUserSetting(UUID uuid, String key, int value) {
        if (!isSettingExists(uuid, key)) {
            mySQL.update("INSERT INTO usersettings (uuid, `key`, `value`) VALUES ('" + uuid + "', '" + key + "', '" + value + "')");
        }
    }

    public void createUserSetting(UUID uuid, String key, boolean value) {
        if (!isSettingExists(uuid, key)) {
            mySQL.update("INSERT INTO usersettings (uuid, `key`, `value`) VALUES ('" + uuid + "', '" + key + "', '" + value + "')");
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
        mySQL.update("DELETE usersettings WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
    }

    public int getId(UUID uuid, String key) {
        try {
            ResultSet rs = mySQL.querry("SELECT id FROM usersettings WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
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
            ResultSet rs = mySQL.querry("SELECT id FROM usersettings WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getString(UUID uuid, String key) {
        try {
            ResultSet rs = mySQL.querry("SELECT `value` FROM usersettings WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
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
            ResultSet rs = mySQL.querry("SELECT `value` FROM usersettings WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
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
            ResultSet rs = mySQL.querry("SELECT `value` FROM usersettings WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
            while (rs.next()) {
                return Boolean.parseBoolean(rs.getString("value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setString(UUID uuid, String key, String value) {
        mySQL.update("UPDATE usersettings SET `value` = '" + value + "' WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
    }

    public void setInt(UUID uuid, String key, int value) {
        mySQL.update("UPDATE usersettings SET `value` = '" + value + "' WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
    }

    public void setBoolean(UUID uuid, String key, boolean value) {
        mySQL.update("UPDATE usersettings SET `value` = '" + value + "' WHERE uuid = '" + uuid + "' AND `key` = '" + key + "'");
    }


}

