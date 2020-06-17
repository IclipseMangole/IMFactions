package de.imfactions.functions;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import de.imfactions.IMFactions;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Iclipse on 16.06.2020
 */
public class UserSettingsManager {
    HashMap<String, Object> userSettings;

    public UserSettingsManager() {
        userSettings = new HashMap<>();


    }

    public void createSettings(UUID uuid) {
        if (userSettings.size() > 0) {
            userSettings.forEach((key, defaultValue) -> {
                IMFactions.getInstance().getData().getUserSettingsTable().createUserSetting(uuid, key, defaultValue);
            });
        }
    }


}
