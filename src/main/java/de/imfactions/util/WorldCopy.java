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
    private WorldCopy() {
    }

    public static void copy(File from, File to, boolean playerdata) {
        try {
            if (to.exists()) {
                FileUtils.deleteDirectory(to);
            }
            to.mkdir();

            for (File file : from.listFiles()) {
                if (file.isDirectory()) {
                    if (!playerdata) {
                        if (file.getName().equals("playerdata") || file.getName().equals("advancements") || file.getName().equals("stats")) {
                            continue;
                        }
                    }
                    FileUtils.copyDirectory(file, new File(to.getAbsolutePath() + "/" + file.getName()));
                } else {
                    FileUtils.copyFile(file, new File(to.getAbsolutePath()  + "/" + file.getName()));
                }
            }
            if(!playerdata){
                new File(to.getAbsolutePath()  + "/" + "playerdata").mkdir();
                new File(to.getAbsolutePath()  + "/" + "advancements").mkdir();
                new File(to.getAbsolutePath()  + "/" + "stats").mkdir();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
