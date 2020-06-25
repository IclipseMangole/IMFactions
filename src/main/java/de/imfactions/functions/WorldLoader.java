package de.imfactions.functions;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import de.imfactions.IMFactions;
import de.imfactions.util.FileUtils;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static de.imfactions.util.FileUtils.copyFilesInDirectory;

/**
 * Created by Iclipse on 21.06.2020
 */
public class WorldLoader {
    public static void loadLobby() {
        //if (mapUpdate) {
        File from = new File("/home/IMNetzwerk/BuildServer/FactionLobby_world/region");
        File to = new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/world/region");

        if (to.exists()) {
            FileUtils.deleteDirectory(new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/world"));
        }
        //if (to.getTotalSpace() != from.getTotalSpace()) {

        try {
            copyFilesInDirectory(from, to);
            Files.copy(new File("/home/IMNetzwerk/BuildServer/FactionLobby_world/level.dat").toPath(), new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/world/level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
            new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/world/data").mkdir();
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

    public static void loadPVP(){
        File from = new File("/home/IMNetzwerk/BuildServer/FactionPVP_world/region");
        File to = new File(IMFactions.getInstance().getDataFolder().getParentFile().getParentFile().getAbsolutePath() + "/FactionPVP_world/region");

        if(to.exists()){
            FileUtils.deleteDirectory(new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPVP_world"));
        }

        try{
            copyFilesInDirectory(from,to);
            Files.copy(new File("/home/IMNetzwerk/BuildServer/FactionPVP_world/level.dat").toPath(), new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPVP_world/level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException oe){
            oe.printStackTrace();
        }
    }

    public static void loadPlots(){
        File from = new File("/home/IMNetzwerk/BuildServer/FactionPlots_world/region");
        File to = new File(IMFactions.getInstance().getDataFolder().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlots_world/region");

        if(to.exists()){
            FileUtils.deleteDirectory(new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlots_world"));
        }

        try{
            copyFilesInDirectory(from,to);
            Files.copy(new File("/home/IMNetzwerk/BuildServer/FactionPlots_world/level.dat").toPath(), new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlots_world/level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException oe){
            oe.printStackTrace();
        }
    }
}
