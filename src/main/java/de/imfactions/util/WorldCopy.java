package de.imfactions.util;

//  ╔══════════════════════════════════════╗
//  ║      ___       ___                   ║
//  ║     /  /___   /  /(_)____ ____  __   ║
//  ║    /  // __/ /  // // ) // ___// )\  ║                                  
//  ║   /  // /__ /  // //  _/(__  )/ __/  ║                                                                         
//  ║  /__/ \___//__//_//_/  /____/ \___/  ║                                              
//  ╚══════════════════════════════════════╝

import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by Iclipse on 04.06.2021
 */
public class WorldCopy {
    private WorldCopy(){}

    public static void copy(File from, File to){
        try {
            if(to.exists()) {
                FileUtils.deleteDirectory(to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File regionFrom = new File(from.getAbsolutePath() + "/region");
        File regionTo = new File(to.getAbsolutePath() + "/region");
        
        File poiFrom = new File(from.getAbsolutePath() + "/poi");
        File poiTo = new File(to.getAbsolutePath() + "/poi");

        File dataFrom = new File(from.getAbsolutePath() + "/data");
        File dataTo = new File(to.getAbsolutePath() + "/data");

        try {
            FileUtils.copyDirectory(regionFrom, regionTo);
            FileUtils.copyDirectory(poiFrom, poiTo);
            FileUtils.copyDirectory(dataFrom, dataTo);
            Files.copy(new File(from.getAbsolutePath() + "/level.dat").toPath(), new File(to.getAbsolutePath() + "/level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
            new File(to.getAbsolutePath() + "/data").mkdir();
            new File(to.getAbsolutePath() + "/playerdata").mkdir();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
