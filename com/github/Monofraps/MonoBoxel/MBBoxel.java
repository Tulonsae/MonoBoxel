package com.github.Monofraps.MonoBoxel;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class MBBoxel {
	
	MonoBoxel master = null;
	
	World correspondingWorld = null;
	String correspondingWorldName = "";
	boolean worldLoaded = false;


	public MBBoxel(MonoBoxel plugin, String worldName)
	{
		master = plugin;
		correspondingWorldName = worldName;
	}
	
	public boolean Create(Player player)
	{
		// first check if the Boxel does not exist already
		if(Exists())
		{
			master.log.info("MB was supposed to create Boxel " + correspondingWorldName + " but it already exists. Will load it...");
			if(!Load())
			{
				// something went wrong
				master.log.info("Could not load Boxel.");
				return false;
			}
			
			// the world exists, but we have no reference to it
			if(correspondingWorld == null)
			{
				correspondingWorld = master.GetMVCore().getMVWorldManager().getMVWorld(correspondingWorldName).getCBWorld();
			}
			
			return true;
		}
		
		// create the boxel
		master.worldManager.CreateWorld(correspondingWorldName, player);
		
		return false;
	}
	
	public boolean Load()
	{
		if(!Exists())
		{
			master.log.info("Tried to load a not existsing Boxel.");
			return false;
		}
		
		if(IsLoaded())
		{
			master.log.info("Boxel is already loaded.");
			return true;
		}
		
		if(!master.GetMVCore().getMVWorldManager().loadWorld(correspondingWorldName))
		{
			// failed to load Boxel
			master.log.info("Failed to load Boxel.");
			return false;
		}
		
		correspondingWorld = master.GetMVCore().getMVWorldManager().getMVWorld(correspondingWorldName).getCBWorld();
		return true;
	}
	
	public boolean Exists()
	{
		return master.worldManager.IsBoxel(correspondingWorldName)[0];
	}	
	public boolean IsLoaded()
	{
		return master.worldManager.IsBoxel(correspondingWorldName)[1];
	}
	
	public boolean Join(Player player)
	{
		// only port the player if the world exists and is laoded
		if(Exists() && IsLoaded())
		{
			return player.teleport(new Location(correspondingWorld, 0, 7, 0));
		}
		
		// the Boxel exists, but is not loaded
		if(Exists() && !IsLoaded())
		{
			if(Load())
			{
				return player.teleport(new Location(correspondingWorld, 0, 7, 0));
			}
			
			master.log.info("Failed to load Boxel to join");
			return false;
		}
		
		// the Boxel does not exist, create it
		if(!Exists())
		{
			
		}
		
		return false;
	}
}
