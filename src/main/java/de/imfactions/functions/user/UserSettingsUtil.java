package de.imfactions.functions.user;

import de.imfactions.Data;
import de.imfactions.IMFactions;

import java.util.HashMap;
import java.util.UUID;

public class UserSettingsUtil {

    private IMFactions imFactions;
    private Data data;
    private UserSettingsTable userSettingsTable;
    private HashMap<String, Object> userSettings;

    public UserSettingsUtil(Data data) {
        this.data = data;
        imFactions = data.getImFactions();
        userSettingsTable = new UserSettingsTable(this, data);
        userSettings = new HashMap<>();
    }

    public void createSettings(UUID uuid) {
        if (userSettings.size() > 0) {
            userSettings.forEach((key, defaultValue) -> {
                userSettingsTable.createUserSetting(uuid, key, defaultValue);
            });
        }
    }
}
