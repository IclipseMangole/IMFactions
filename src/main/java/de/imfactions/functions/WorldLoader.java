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
import de.imfactions.util.EmptyChunkGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by Iclipse on 21.06.2020
 */
public class WorldLoader {
    private IMFactions imFactions;
    private FactionPlotUtil factionPlotUtil;

    public WorldLoader(IMFactions imFactions) {
        this.imFactions = imFactions;
    }

    public void loadManagers(){
        factionPlotUtil = imFactions.getData().getFactionPlotUtil();
    }

    public void loadLobby() {
        //if (mapUpdate) {
        File from = new File("/home/IMNetzwerk/BuildServer/FactionLobby_world/region");
        File to = new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/world/region");

        try {
            if (to.exists()) {
                FileUtils.deleteDirectory(new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/world"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //if (to.getTotalSpace() != from.getTotalSpace()) {

        try {
            FileUtils.copyDirectory(from, to);
            Files.copy(new File("/home/IMNetzwerk/BuildServer/FactionLobby_world/level.dat").toPath(), new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/world/level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
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

    public void loadPVP() {
        File from = new File("/home/IMNetzwerk/BuildServer/FactionPVP_world/region");
        File to = new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPVP_world/region");

        try {
            if (to.exists()) {
                FileUtils.deleteDirectory(new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPVP_world"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileUtils.copyDirectory(from, to);
            Files.copy(new File("/home/IMNetzwerk/BuildServer/FactionPVP_world/level.dat").toPath(), new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPVP_world/level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPlots() {
        if(!new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlotsSave_world/region").exists()){
            new WorldCreator("FactionPlotsSave_world").generator(new EmptyChunkGenerator()).createWorld();
        }
        File from = new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlotsSave_world/region");
        File to = new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlots_world/region");

        try {
            if (to.exists()) {
                FileUtils.deleteDirectory(new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlots_world"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileUtils.copyDirectory(from, to);
            Files.copy(new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlotsSave_world/level.dat").toPath(), new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlots_world/level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePlots() {
        File to = new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlotsSave_world/region");
        File from = new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlots_world/region");

        try {
            if (to.exists()) {
                FileUtils.deleteDirectory(new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlotsSave_world"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileUtils.copyDirectory(from, to);
            Files.copy(new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlots_world/level.dat").toPath(), new File(imFactions.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + "/FactionPlotsSave_world/level.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String map, Location location) {
        Clipboard clipboard = loadSchematic(map);
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(location.getWorld()), -1)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .ignoreAirBlocks(true)
                    .copyEntities(true)
                    .copyBiomes(true)
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
        return new File("/home/IMNetzwerk/BuildServer/plugins/WorldEdit/schematics/" + name + ".schem");
    }

    public void deleteMap(Location edgeDownFrontLeft){
        Location loc1 = new Location(edgeDownFrontLeft.getWorld(), edgeDownFrontLeft.getX() - 46, 0, edgeDownFrontLeft.getZ() - 46);
        Location loc2 = new Location(edgeDownFrontLeft.getWorld(), edgeDownFrontLeft.getX() + 45 + 100, edgeDownFrontLeft.getY() + 150, edgeDownFrontLeft.getZ() + 45 + 100);

        for(int x = (int) loc1.getX(); x < loc2.getX(); x++){
            for(int y = (int) loc1.getY(); y < loc2.getY(); y++){
                for(int z = (int) loc1.getZ(); z < loc2.getZ(); z++){
                    new Location(edgeDownFrontLeft.getWorld(), x, y, z).getBlock().setType(Material.AIR);
                }
            }
        }
    }
}
