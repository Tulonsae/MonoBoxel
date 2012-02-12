package com.github.Monofraps.MonoBoxel;


import java.util.Arrays;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;


/**
 * This chunk generator will generate a flat chunk.
 * Chunk layers:
 * 0 - Bedrock
 * 1 - 5 - dirt
 * 6 - grass
 * 
 * @version 0.4
 * @author Monofraps
 */
public class MBBoxelGenerator extends ChunkGenerator {
	
	private static final int	CHUNK_WIDTH		= 16;
	private static final int	CHUNK_LENGHT	= 16;
	private static final int	CHUNK_HEIGHT	= 128;
	private static final int	CHUNK_SIZE		= CHUNK_WIDTH * CHUNK_LENGHT
														* CHUNK_HEIGHT;
	
	private byte[]				flatChunk;
	private byte[]				borderChunk;
	private long				maxBoxelSize	= 16;
	private int					landHeight		= 6;
	
	public MBBoxelGenerator(long maxBoxelSize) {
		
		flatChunk = new byte[CHUNK_SIZE];
		borderChunk = new byte[CHUNK_SIZE];
		
		this.maxBoxelSize = maxBoxelSize;
		
		for (int x = 0; x < CHUNK_WIDTH; x++) {
			for (int z = 0; z < CHUNK_LENGHT; z++) {
				flatChunk[xyzToByte(x, 0, z)] = (byte) Material.BEDROCK.getId();
			}
		}
		
		for (int x = 0; x < CHUNK_WIDTH; x++) {
			for (int z = 0; z < CHUNK_LENGHT; z++) {
				for (int y = 1; y < landHeight; y++) {
					flatChunk[xyzToByte(x, y, z)] = (byte) Material.DIRT
							.getId();
				}
			}
		}
		
		for (int x = 0; x < CHUNK_WIDTH; x++) {
			for (int z = 0; z < CHUNK_LENGHT; z++) {
				flatChunk[xyzToByte(x, landHeight, z)] = (byte) Material.GRASS
						.getId();
			}
		}
		
		for (int x = 0; x < CHUNK_WIDTH; x++) {
			for (int z = 0; z < CHUNK_LENGHT; z++) {
				for (int y = 0; y < CHUNK_HEIGHT; y++) {
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
	
	/**
	 * Converts relative Chunk locations to 1 dimensional chunk index.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return 1 Dimensional Index for Chunk array's
	 */
	public int xyzToByte(int x, int y, int z) {
		return (x * CHUNK_WIDTH + z) * CHUNK_HEIGHT + y;
	}
	
	@Override
	public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
		
		// max-boxel-size set to 0 will result in non-limited worlds
		if (maxBoxelSize != 0) {
			if (chunkx > maxBoxelSize / 2) return Arrays.copyOf(borderChunk,
					borderChunk.length);
			if (chunkz > maxBoxelSize / 2) return Arrays.copyOf(borderChunk,
					borderChunk.length);
			if (chunkx < -maxBoxelSize / 2) return Arrays.copyOf(borderChunk,
					borderChunk.length);
			if (chunkz < -maxBoxelSize / 2) return Arrays.copyOf(borderChunk,
					borderChunk.length);
		}
		
		return Arrays.copyOf(flatChunk, flatChunk.length);
	}
	
}
