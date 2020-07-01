package de.imfactions.functions;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import de.imfactions.IMFactions;
import de.imfactions.util.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    /*
    public static void loadPVP(){
        File from = new File("/home/IMNetzwerk/BuildServer/FactionPVP_world/region");
        File to = new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPVP_world/region");

        if(to.exists()){
            FileUtils.deleteDirectory(new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPVP_world"));
        }

        try{
            copyFilesInDirectory(from,to);
            Files.copy(new File("/home/IMNetzwerk/BuildServer/FactionPVP_world/level.dat").toPath(), new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPVP_world/level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
            new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPVP_world/data").mkdir();
        }catch(IOException oe){
            oe.printStackTrace();
        }
    }

    public static void loadPlots(){
        File from = new File("/home/IMNetzwerk/BuildServer/FactionPlots_world/region");
        File to = new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlots_world/region");

        if(to.exists()){
            FileUtils.deleteDirectory(new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlots_world"));
        }

        try{
            copyFilesInDirectory(from,to);
            Files.copy(new File("/home/IMNetzwerk/BuildServer/FactionPlots_world/level.dat").toPath(), new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlots_world/level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
            new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlots_world/data").mkdir();
        }catch(IOException oe){
            oe.printStackTrace();
        }
    }

    public static void savePlots(){
        File from = new File("/home/IMNetzwerk/FactionsDev/FactionPlots_world/region");
        File to = new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlotsSave_world/region");

        if(to.exists()){
            FileUtils.deleteDirectory(new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlotsSave_world"));
        }

        try{
            copyFilesInDirectory(from,to);
            Files.copy(new File("/home/IMNetzwerk/FactionsDev/FactionPlots_world/level.dat").toPath(), new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlotsSave_world/level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
            new File(IMFactions.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlotsSave_world/data").mkdir();
        }catch(IOException oe){
            oe.printStackTrace();
        }
    }
*/
    public static void loadFactionPlot(String name, Location location) {
        Clipboard clipboard = loadSchematic(name);
        ClipboardHolder holder = new ClipboardHolder(clipboard);
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(Bukkit.getWorld("FactionPlots_world")), 1000000);
        PasteBuilder pasteBuilder = holder.createPaste(editSession);
        pasteBuilder.to(BukkitAdapter.asBlockVector(location));
        pasteBuilder.build();
    }

    public static void loadTest(String name, Location location) {
        Clipboard clipboard = loadSchematic(name);
        ClipboardHolder holder = new ClipboardHolder(clipboard);
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(Bukkit.getWorld("world")), 1000000);
        PasteBuilder pasteBuilder = holder.createPaste(editSession);
        pasteBuilder.to(BukkitAdapter.asBlockVector(location));
        pasteBuilder.build();
    }

    public static Clipboard loadSchematic(String name) {
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

    public static File getSchematicFile(String name) {
        return new File("/home/IMNetzwerk/BuildServer/plugins/WorldEdit/schematics/" + name);
    }
}
