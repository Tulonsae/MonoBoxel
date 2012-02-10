package com.github.Monofraps.MonoBoxel;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

// This chunk generator will just generate a flat grass surface (on layer 7)
public class MBBoxelGenerator extends ChunkGenerator {
	
	byte[] flatChunk;
	
	public MBBoxelGenerator () {
		
		// does not seem to work with generating this once
		flatChunk = new byte[32768];
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				flatChunk[xyzToByte(x, 0, z)] = (byte) Material.BEDROCK.getId();
			}
		}
		
		for(int y = 1; y < 6; y++)
		{
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					flatChunk[xyzToByte(x, 0, z)] = (byte) Material.DIRT.getId();
				}
			}
		}
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				flatChunk[xyzToByte(x, 6, z)] = (byte) Material.DIRT.getId();
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
		
		return Arrays.copyOf(flatChunk, flatChunk.length);
	}

}
