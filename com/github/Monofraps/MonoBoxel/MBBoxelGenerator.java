package com.github.Monofraps.MonoBoxel;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

// This chunk generator will just generate a flat grass surface (on layer 7)
public class MBBoxelGenerator extends ChunkGenerator {
	
	byte[] flatChunk;
	
	public MBBoxelGenerator () {
		
		flatChunk = new byte[32768];
		int y = 6;
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				flatChunk[xyzToByte(x, y, z)] = (byte) Material.GRASS.getId();
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
		byte[] result = new byte[32768];
		int y = 6;
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				result[xyzToByte(x, y, z)] = (byte) Material.GRASS.getId();
			}
		}
		
		return flatChunk;
	}

}
