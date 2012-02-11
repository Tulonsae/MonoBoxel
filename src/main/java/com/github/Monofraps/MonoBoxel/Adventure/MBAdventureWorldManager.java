package com.github.Monofraps.MonoBoxel.Adventure;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

import com.github.Monofraps.MonoBoxel.MonoBoxel;


public class MBAdventureWorldManager {
	
	private List<MBAdventureWorld>	adventureWorlds	= null;
	private MonoBoxel				master			= null;
	
	public MBAdventureWorldManager(MonoBoxel plugin)
	{
		master = plugin;
		adventureWorlds = new ArrayList<MBAdventureWorld>();
	}
	
	public boolean CreateAdventureWorldFromWorld(World world)
	{
		return false;
	}
}
