package de.imfactions.functions;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import de.imfactions.IMFactions;
import de.imfactions.functions.factionPlot.FactionPlotUtil;
import de.imfactions.util.WorldCopy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Iclipse on 21.06.2020
 */
public class WorldManager {
    private final IMFactions imFactions;
    private FactionPlotUtil factionPlotUtil;

    public WorldManager(IMFactions imFactions) {
        this.imFactions = imFactions;
    }

    public void loadManagers(){
        factionPlotUtil = imFactions.getData().getFactionPlotUtil();
    }

    public void loadLobby() {
        //if (mapUpdate) {
        File from = new File("/home/IMNetzwerk/Server/Bauserver/FactionLobby_world");
        File to = new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/world");

        WorldCopy.copy(from, to, false);
    }

    public void loadPVP() {
        File from = new File("/home/IMNetzwerk/Server/Bauserver/FactionPVP_world");
        File to = new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPVP_world");

        WorldCopy.copy(from, to, false);
    }


    public void loadPlots() {
        File file = new File(Bukkit.getWorldContainer().getAbsolutePath() + "/FactionPlots_world");
        if(file.exists()){
            Bukkit.getLogger().log(Level.INFO, "Directory FactionPlots_world exists, no new world will be loaded!");
            return;
        }
        File backupDirectory = new File("/home/IMNetzwerk/Welten/Factions/" + imFactions.getServerName() + "Backup");
        if(backupDirectory.listFiles().length == 0){
            Bukkit.getLogger().log(Level.INFO, "There is no backup available, a empty world will be created!");
            return;
        }
        File backupZip = Arrays.stream(backupDirectory.listFiles()).sorted().findFirst().get();
        try {
            unzip(backupZip, file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "A problem occured while loading the latest backup");
        }
    }


    public void startAutoSave(){
        Bukkit.getLogger().log(Level.INFO, "[Plots] Autosave gestartet!");
        Bukkit.getScheduler().runTaskTimer(imFactions, new Runnable() {
            @Override
            public void run() {
                Bukkit.getLogger().log(Level.INFO, "[Plots] Autosaving...");
                savePlots();
            }
        }, 200, 10*60*20);
    }

    public void savePlots() {
        World world = Bukkit.getWorld("FactionPlots_world");
        if(world == null){
            Bukkit.getLogger().log(Level.WARNING, "Die Welt FactionPlot_world ist nicht geladen!");
            return;
        }
        world.save();
        File file = zip(Bukkit.getWorldContainer().getAbsolutePath() + "/FactionPlots_world");
        File backupDirectory = new File("/home/IMNetzwerk/Welten/Factions/" + imFactions.getServerName() + "Backup");
        while(backupDirectory.listFiles().length >= 10){
            Arrays.stream(backupDirectory.listFiles()).sorted().findFirst().get().delete();
        }
        file.renameTo(new File(backupDirectory.getAbsolutePath() + "/" + LocalDateTime.now() + ".zip"));
    }

    public void loadMap(String map, Location location) {
        Clipboard clipboard = loadSchematic(map);
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(location.getWorld()), -1)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .ignoreAirBlocks(false)
                    .copyEntities(false)
                    .copyBiomes(false)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    public Clipboard loadSchematic(String name) {
        Clipboard clipboard = null;
        File file = getSchematicFile(name);

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clipboard;
    }

    public File getSchematicFile(String name) {
        return new File("/home/IMNetzwerk/Server/Bauserver/plugins/WorldEdit/schematics/" + name + ".schem");
    }

    public void deleteMap(Location edgeDownFrontLeft){
        Location loc1 = new Location(edgeDownFrontLeft.getWorld(), edgeDownFrontLeft.getX() - 55, 0, edgeDownFrontLeft.getZ() - 55);
        Location loc2 = new Location(edgeDownFrontLeft.getWorld(), edgeDownFrontLeft.getX() + 36 + 100, edgeDownFrontLeft.getY() + 129, edgeDownFrontLeft.getZ() + 36 + 100);

        for (int x = (int) loc1.getX(); x < loc2.getX(); x++) {
            for (int y = (int) loc1.getY(); y < loc2.getY(); y++) {
                for (int z = (int) loc1.getZ(); z < loc2.getZ(); z++) {
                    Block block = new Location(edgeDownFrontLeft.getWorld(), x, y, z).getBlock();
                    if (block.getType().isAir())
                        continue;
                    block.setType(Material.AIR);
                }
            }
        }
    }

    private File zip(String folder){
        File zipFile = new File(folder.concat(".zip"));
        try (FileOutputStream fos = new FileOutputStream(zipFile.getAbsolutePath());
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            Path sourcePath = Paths.get(folder);
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                    if(!sourcePath.equals(dir)){
                        zos.putNextEntry(new ZipEntry(sourcePath.relativize(dir) + "/"));
                        zos.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(sourcePath.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zipFile;
    }

    private void unzip(File fileZip, File to) throws IOException {
        File destDir = new File("src/main/resources/unzipTest");
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip.getAbsolutePath()));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
