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
}
