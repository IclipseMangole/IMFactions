package de.imfactions.functions;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import de.imfactions.IMFactions;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by Iclipse on 21.06.2020
 */
public class WorldLoader {

    private IMFactions factions;

    public WorldLoader(IMFactions factions) {
        this.factions = factions;
    }

    public void loadLobby() {
        //if (mapUpdate) {
        File from = new File("/home/IMNetzwerk/BuildServer/FactionLobby_world/region");
        File to = new File(factions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/world/region");

        if (to.exists()) {
            try {
                FileUtils.deleteDirectory(new File(factions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/world"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //if (to.getTotalSpace() != from.getTotalSpace()) {

        try {
            FileUtils.copyDirectory(from, to);
            Files.copy(new File("/home/IMNetzwerk/BuildServer/FactionLobby_world/level.dat").toPath(), new File(factions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/world/level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
            new File(factions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/world/data").mkdir();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //}
        /*
            } else {
                try {
                    copyFilesInDirectory(from, to);
                    Files.copy(new File("/home/IMNetzwerk/BuildServer/IMLobby_world/level.dat").toPath(), new File(Data.instance.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/world/level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

         */
    }
}
