package com.github.Monofraps.MonoBoxel;


import java.util.Collection;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.Monofraps.MonoBoxel.MBPermissionManager.MBPermission;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;


/**
 * This class will hold information of a specific Boxel Boxel name and the
 * corresponding world (if loaded) will be stored here. This class also provides
 * Create/Load/Join/Leave functions.
 * 
 * @author Monofraps
 */
public class MBBoxel {
	
	protected MonoBoxel	master					= null;
	
	protected World		correspondingWorld		= null;
	protected String	correspondingWorldName	= "";
	private boolean		worldLoaded				= false;
	private int			unloadTaskId			= -1;
	
	private String		boxelGenerator			= "MonoBoxel";
	private String		boxelSeed				= "ThisSeedIsCool";
	
	/**
	 * 
	 * @param plugin
	 *            The reference to the MonoBoxel plugin class
	 * @param worldName
	 *            The name of the Boxel
	 * @param generator
	 *            The generator to use for Boxel generation (default for the
	 *            default Minecraft/Bukkit world generator, empty for the
	 *            MonoBoxel generator)
	 * @param seed
	 *            The seed to use for Boxel generation
	 */
	public MBBoxel(MonoBoxel plugin, String worldName, String generator,
			String seed) {
		master = plugin;
		correspondingWorldName = worldName;
		
		if (master.GetMVCore().getMVWorldManager().getMVWorld(worldName) != null) {
			correspondingWorld = master.GetMVCore().getMVWorldManager()
					.getMVWorld(correspondingWorldName).getCBWorld();
			worldLoaded = true;
		}
		
		if (generator.isEmpty())
			boxelGenerator = "MonoBoxel";
		else
			boxelGenerator = generator;
		
		boxelSeed = seed;
	}
	
	/**
	 * Will create the Boxel. Check if the given player has the right
	 * permissions and the perform the create action.
	 * 
	 * @param player
	 *            The player that wants to perform the creation.
	 * @return true on success, otherwise false
	 */
	public boolean Create(Player player) {
		
		boolean isPlayersOwnBoxel = false;
		String boxelOwner = "";
		
		// should always be true
		if (correspondingWorldName.startsWith(master.getBoxelPrefix()))
			boxelOwner = correspondingWorldName.substring(master
					.getBoxelPrefix().length());
		
		if (boxelOwner.equals(player.getName()))
			isPlayersOwnBoxel = true;
		
		// first check if the Boxel does not exist already
		if (isExisting()) {
			master.getLogManager().debugLog(
					Level.INFO,
					"MB was supposed to create Boxel " + correspondingWorldName
							+ " but it already exists. Will load it...");
			if (!Load()) {
				// something went wrong
				master.getLogManager().debugLog(Level.INFO,
						"Could not load Boxel.");
				return false;
			}
			
			// the world exists, but we have no reference to it
			if (correspondingWorld == null) {
				correspondingWorld = master.GetMVCore().getMVWorldManager()
						.getMVWorld(correspondingWorldName).getCBWorld();
			}
		}
		
		if (isPlayersOwnBoxel) {
			
			if (master.getPermManager().hasPermission(player,
					MBPermission.CAN_CREATE_OWN)) {
				if (DoCreate(player)) {
					
					correspondingWorld = master.GetMVCore().getMVWorldManager()
							.getMVWorld(correspondingWorldName).getCBWorld();
					return true;
					
				}
			} else {
				player.sendMessage("You don't have permissions to create your own Boxel!");
				return false;
			}
			
		} else {
			if (master.getPermManager().hasPermission(player,
					MBPermission.CAN_CREATE_OTHERS)) {
				
				if (DoCreate(player)) {
					
					correspondingWorld = master.GetMVCore().getMVWorldManager()
							.getMVWorld(correspondingWorldName).getCBWorld();
					return true;
					
				} else {
					player.sendMessage("Failed to create Boxel.");
					master.getLogManager().severe(
							"Failed to create Boxel " + correspondingWorldName);
					master.getLogManager().debugLog(Level.INFO,
							"Failed to create Boxel " + correspondingWorldName);
					return false;
				}
				
			} else {
				player.sendMessage("You don't have permissions to create other Boxels!");
				return false;
			}
		}
		
		return false;
	}
	
	/**
	 * Does the actual Boxel/World creation.
	 * 
	 * @param sender
	 *            The sender (mostly a player) that will perform this action (I
	 *            am using CommandSender because so it is possible to create
	 *            Boxels for specific players from the console)
	 * @return true on success, otherwise false
	 */
	protected boolean DoCreate(CommandSender sender) {
		MVWorldManager wm = master.GetMVCore().getMVWorldManager();
		MultiverseWorld result = null;
		
		if (correspondingWorld != null)
			master.getLogManager()
					.debugLog(Level.INFO,
							"DoCreate was called, but correspondingWorld was already set.");
		
		if (wm.getMVWorld(correspondingWorldName) != null) {
			sender.sendMessage("Found your boxel. Will port you there now...");
			return true;
		} else {
			// now check unloaded worlds too
			Collection<String> uworlds = wm.getUnloadedWorlds();
			if (uworlds.contains(correspondingWorldName)) {
				sender.sendMessage("Found your boxel. Will have to load it and port you there...");
				Load();
				return true;
			}
		}
		
		if (master.getMBWorldManager().getNumBoxels() >= master.getConfig()
				.getInt("max-boxel-count", 20)) {
			sender.sendMessage("The maximum number of boxels on this server is reached. Please contact a server admin.");
			return false;
		}
		
		sender.sendMessage("You don't seem to have a boxel yet. Will create one for you now...");
		
		if (boxelGenerator.equals("default"))
			boxelGenerator = "";
		
		if (wm.addWorld(correspondingWorldName,
				World.Environment.valueOf("NORMAL"), boxelSeed,
				WorldType.valueOf("NORMAL"), false, boxelGenerator)) {
			
			result = wm.getMVWorld(correspondingWorldName);
			
			if (result != null)
				master.getLogManager().info(
						"Boxel " + correspondingWorldName + " created!");
			else
				return false;
			
			result.setAllowAnimalSpawn(false);
			result.setAllowMonsterSpawn(false);
			result.setEnableWeather(false);
			result.setGameMode("CREATIVE");
			result.setPVPMode(false);
			result.setAutoLoad(false);
			
			master.getLogManager().info(
					"Boxel created for Player: " + sender.getName());
			master.getLogManager().debugLog(Level.INFO,
					"Boxel created for Player: " + sender.getName());
			sender.sendMessage("Boxel created! Will port you there now...");
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Will load the world if it exists and is not loaded.
	 * 
	 * @return true on success, otherwise false
	 */
	public boolean Load() {
		if (!isExisting()) {
			master.getLogManager().debugLog(Level.INFO,
					"Tried to load a not existsing Boxel.");
			return false;
		}
		
		if (isLoaded()) {
			master.getLogManager().debugLog(Level.INFO,
					"Boxel " + correspondingWorldName + " is already loaded.");
			worldLoaded = true;
			return true;
		}
		
		if (!master.GetMVCore().getMVWorldManager()
				.loadWorld(correspondingWorldName)) {
			// failed to load Boxel
			master.getLogManager().severe("Failed to load Boxel.");
			return false;
		}
		
		// the world should be loaded now
		correspondingWorld = master.GetMVCore().getMVWorldManager()
				.getMVWorld(correspondingWorldName).getCBWorld();
		worldLoaded = true;
		
		return true;
	}
	
	/**
	 * Checks if the Boxel exists.
	 * 
	 * @return true if the Boxel exists, otherwise false
	 */
	public boolean isExisting() {
		return master.getMBWorldManager().isBoxel(correspondingWorldName)[0];
	}
	
	/**
	 * Checks if the Boxel is loaded.
	 * 
	 * @return true if the Boxel is loaded, otherwise false
	 */
	public boolean isLoaded() {
		return master.getMBWorldManager().isBoxel(correspondingWorldName)[1];
	}
	
	/**
	 * Teleports a specific player to the Boxel.
	 * Checks the players permissions and sends him to the Boxel
	 * 
	 * @param player
	 *            The player that should be ported
	 * @return true on success, otherwise false
	 */
	public boolean Join(Player player) {
		boolean isPlayersOwnBoxel = false;
		String boxelOwner = "";
		
		// should always be true
		if (correspondingWorldName.startsWith(master.getBoxelPrefix()))
			boxelOwner = correspondingWorldName.substring(master
					.getBoxelPrefix().length());
		
		if (boxelOwner.equals(player.getName()))
			isPlayersOwnBoxel = true;
		
		// before porting the player, save his location
		if (master.getConfig().getBoolean("save-exit-location", true)) {
			// do not save the return/entry location if the player is in a Boxel
			if (!master.getMBWorldManager()
					.isBoxel(player.getWorld().getName())[0]) {
				master.getDataConfig()
						.getDataConfig()
						.set("playeroloc." + player.getName() + ".world",
								player.getWorld().getName());
				master.getDataConfig()
						.getDataConfig()
						.set("playeroloc." + player.getName() + ".position",
								player.getLocation().toVector());
				master.getDataConfig()
						.getDataConfig()
						.set("playeroloc." + player.getName() + ".yaw",
								player.getLocation().getYaw());
				master.getDataConfig()
						.getDataConfig()
						.set("playeroloc." + player.getName() + ".pitch",
								player.getLocation().getPitch());
				master.getDataConfig().saveDataConfig();
			}
		}
		
		// only port the player if the Boxel exists and is loaded and the player
		// has permissions
		if (isExisting() && isLoaded()) {
			if (isPlayersOwnBoxel) {
				if (master.getPermManager().hasPermission(player,
						MBPermission.CAN_VISIT_OWN)) {
					return player.teleport(correspondingWorld
							.getSpawnLocation());
				} else {
					player.sendMessage("You don't have permissions to visit your own Boxel!");
					return false;
				}
			} else {
				if (master.getPermManager().canVisitOtherBoxel(player,
						boxelOwner)) {
					return player.teleport(correspondingWorld
							.getSpawnLocation());
				} else {
					player.sendMessage("You don't have permissions to visit this Boxel!");
					return false;
				}
			}
		}
		
		// the Boxel exists, but is not loaded
		if (isExisting() && !isLoaded()) {
			
			if (isPlayersOwnBoxel) {
				
				if (master.getPermManager().hasPermission(player,
						MBPermission.CAN_VISIT_OWN)) {
					
					if (Load()) {
						return player.teleport(correspondingWorld
								.getSpawnLocation());
					} else {
						master.getLogManager().info(
								"Failed to load Boxel "
										+ correspondingWorldName + " to join");
					}
					
				} else {
					player.sendMessage("You don't have permissions to visit your own Boxel!");
					return false;
				}
				
			} else {
				
				if (master.getPermManager().canVisitOtherBoxel(player,
						boxelOwner)) {
					if (Load()) {
						return player.teleport(correspondingWorld
								.getSpawnLocation());
					} else {
						
						master.getLogManager().warning(
								"Failed to load Boxel "
										+ correspondingWorldName + " to join");
					}
				} else {
					master.getPermManager().SendNotAllowedMessage(player);
					return false;
				}
				
			}
			
			return false;
		}
		
		// the Boxel does not exist, create it - or just return with error:
		// "This Boxel does not exists." ?
		if (!isExisting()) {
			
			master.getLogManager().debugLog(
					Level.INFO,
					"Boxel.Join was called but the Boxel "
							+ correspondingWorldName + " does not exist.");
			
			player.sendMessage("Boxel does not exists yet. I'll try to create one for you...");
			
			if (Create(player))
				return player.teleport(correspondingWorld.getSpawnLocation());
			
		}
		
		return false;
	}
	
	/**
	 * Teleports a player back to his original location.
	 * 
	 * @param player
	 *            The player to teleport
	 * @return true on success, otherwise false
	 */
	public boolean Leave(Player player) {
		MVWorldManager wm = master.GetMVCore().getMVWorldManager();
		MultiverseWorld entryWorld = null;
		
		if (master.getConfig().getBoolean("save-exit-location", true)) {
			
			String outWorld = master.getDataConfig().getDataConfig()
					.getString("playeroloc." + player.getName() + ".world", "");
			Vector outPosition = master
					.getDataConfig()
					.getDataConfig()
					.getVector("playeroloc." + player.getName() + ".position",
							new org.bukkit.util.Vector());
			double outPitch = master
					.getDataConfig()
					.getDataConfig()
					.getDouble("playeroloc." + player.getName() + ".pitch", 0.0);
			
			double outYaw = master.getDataConfig().getDataConfig()
					.getDouble("playeroloc." + player.getName() + ".yaw", 0.0);
			
			// the saved location could not be loaded correctly
			if (outWorld.isEmpty()) {
				master.getLogManager().debugLog(
						Level.INFO,
						"save-exit-location was set, but no entry location for player "
								+ player.getName() + " was found.");
				player.teleport(wm.getSpawnWorld().getSpawnLocation());
				return true;
			}
			
			// we have load the entry location, now see if the entry
			// world is loaded
			entryWorld = wm.getMVWorld(outWorld);
			if (entryWorld == null) {
				// Multiverse getMVWorld returned null, check the
				// unloaded worlds
				if (!wm.getUnloadedWorlds().contains(outWorld)) {
					// the saved world could not be found, so port the
					// player to the default spawn world
					master.getLogManager().debugLog(
							Level.INFO,
							"save-exit-location was set, but no entry world "
									+ outWorld + " for player "
									+ player.getName() + " was found.");
					player.teleport(wm.getSpawnWorld().getSpawnLocation());
					return true;
				} else {
					// the entry world of the player is in the
					// Multiverse config, but not loaded; load it!
					if (!wm.loadWorld(outWorld)) {
						master.getLogManager().debugLog(
								Level.INFO,
								"Failed to load entry world for player "
										+ player.getName());
						player.sendMessage("Failed to load entry world");
						player.teleport(wm.getSpawnWorld().getSpawnLocation());
						return true;
					} else {
						// Multiverse has load the world
						entryWorld = wm.getMVWorld(outWorld);
					}
				}
			}
			
			return player.teleport(new Location(entryWorld.getCBWorld(),
					outPosition.getX(), outPosition.getY(), outPosition.getZ(),
					(float) outYaw, (float) outPitch));
		}
		
		return false;
	}
	
	/**
	 * Unloads the Boxel if no player is inside.
	 * 
	 * @return true on success, otherwise false (will also return false if there
	 *         are players in this Boxel)
	 */
	public boolean Unload() {
		
		if (isEmpty() && isLoaded()) {
			if (master.GetMVCore().getMVWorldManager()
					.unloadWorld(correspondingWorldName)) {
				
				worldLoaded = false;
				master.getLogManager().info(
						"Unloaded world " + correspondingWorldName
								+ " due to inactivity.");
				return true;
			} else
				return false;
		}
		
		return false;
	}
	
	/**
	 * Checks if the Boxel is empty.
	 * 
	 * @return true if the Boxel is empty
	 */
	public boolean isEmpty() {
		if (correspondingWorld == null)
			return true;
		
		if (correspondingWorld.getPlayers().size() == 0)
			return true;
		else
			return false;
	}
	
	/**
	 * 
	 * @return the correspondingWorldName
	 */
	public String getCorrespondingWorldName() {
		return correspondingWorldName;
	}
	
	/**
	 * @return the unloadTaskId
	 */
	public int getUnloadTaskId() {
		return unloadTaskId;
	}
	
	/**
	 * @param unloadTaskId
	 *            the unloadTaskId to set
	 */
	public void setUnloadTaskId(int unloadTaskId) {
		this.unloadTaskId = unloadTaskId;
	}
	
	/**
	 * @return the correspondingWorld
	 */
	public World getCorrespondingWorld() {
		return correspondingWorld;
	}
	
	/**
	 * @return the worldLoaded
	 */
	public boolean isWorldLoaded() {
		return worldLoaded;
	}
	
	/**
	 * @return the boxelGenerator
	 */
	public String getBoxelGenerator() {
		return boxelGenerator;
	}
	
	/**
	 * @return the boxelSeed
	 */
	public String getBoxelSeed() {
		return boxelSeed;
	}
}
