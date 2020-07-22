package de.imfactions.util;

import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class EmptyChunkGenerator extends ChunkGenerator implements Listener {
    @Override
    public ChunkGenerator.ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        return createChunkData(world);
    }

    /*
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        for (int x = e.getChunk().getX(); x < 16; x++) {
            for (int z = e.getChunk().getZ(); z < 16; z++) {
                e.getWorld().setBiome(x, z, Biome.BEACH);
            }
        }
    }
    */

}
