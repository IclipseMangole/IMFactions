package de.imfactions.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileUtils {

    /**
     * Copys a Directory recursively to another Directory
     *
     * @param from The directory you copy from
     * @param to   The Directory you want to copy to
     * @throws IOException
     */
    public static void copyFilesInDirectory(File from, File to) throws IOException {
        if (!to.exists()) {
            to.mkdirs();
        }
        for (File file : from.listFiles()) {
            if (file.isDirectory()) {
                copyFilesInDirectory(file, new File(to.getAbsolutePath() + "/" + file.getName()));
            } else {
                File n = new File(to.getAbsolutePath() + "/" + file.getName());
                Files.copy(file.toPath(), n.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    /**
     * Deletes a directory recursively
     *
     * @param dir Directory to delete
     */
    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            for (int i = 0; i < dir.listFiles().length; i++) {
                deleteDirectory(dir.listFiles()[i]);
            }
        }
        dir.delete();
    }

}
