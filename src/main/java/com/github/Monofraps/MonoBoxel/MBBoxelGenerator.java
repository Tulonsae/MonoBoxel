package com.github.Monofraps.MonoBoxel;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

/**
 * This chunk generator will generate a flat chunk
 * Chunk layers:
 * 		0 - Bedrock
 * 		1 - 5 - dirt
 * 		6 - grass
 * 
 * @version 0.4
 * @author Monofraps
 */
public class MBBoxelGenerator extends ChunkGenerator {

	byte[] flatChunk;
	byte[] borderChunk;
	long maxBoxelSize = 16;

	public MBBoxelGenerator(long maxBoxelSize) {

		flatChunk = new byte[32768];
		borderChunk = new byte[32768];

		this.maxBoxelSize = maxBoxelSize;

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				flatChunk[xyzToByte(x, 0, z)] = (byte) Material.BEDROCK.getId();
			}
		}

		for (int x = 0; x < 16; x++) {
			for (int y = 1; y < 6; y++) {
				for (int z = 0; z < 16; z++) {
					flatChunk[xyzToByte(x, y, z)] = (byte) Material.DIRT
							.getId();
				}
			}
		}

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				flatChunk[xyzToByte(x, 6, z)] = (byte) Material.GRASS.getId();
			}
		}

		for (int x = 0; x < 16; x++) {
			for (int y = 1; y < 127; y++) {
				for (int z = 0; z < 16; z++) {
					borderChunk[xyzToByte(x, y, z)] = (byte) Material.BEDROCK
							.getId();
				}
			}
		}
	}

	@Override
	public boolean canSpawn(World world, int x, int z) {
		return true;
	}

	// This converts relative chunk locations to bytes that can be written to
	// the chunk
	public int xyzToByte(int x, int y, int z) {
		return (x * 16 + z) * 128 + y;
	}

	@Override
	public byte[] generate(World world, Random rand, int chunkx, int chunkz) {

		// max-boxel-size set to 0 will result in non-limited worlds
		if (maxBoxelSize != 0) {
			if (chunkx >= maxBoxelSize)
				return Arrays.copyOf(borderChunk, borderChunk.length);
			if (chunkz >= maxBoxelSize)
				return Arrays.copyOf(borderChunk, borderChunk.length);
			if(chunkx <= -maxBoxelSize)
				return Arrays.copyOf(borderChunk, borderChunk.length);
			if(chunkz <= -maxBoxelSize)
				return Arrays.copyOf(borderChunk, borderChunk.length);
		}

		return Arrays.copyOf(flatChunk, flatChunk.length);
	}

}
