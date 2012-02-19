package com.Monofraps.MonoBoxel;


import java.util.Collection;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.Monofraps.MonoBoxel.MBPermissionManager.MBPermission;
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
	
	/**
	 * Plugin main class reference.
	 */
	protected MonoBoxel	master					= null;
	
	/**
	 * Prefix of this Boxel.
	 */
	protected String	boxelPrefix				= "BOXEL_";
	
	/**
	 * Corresponding Boxel World.
	 */
	protected World		correspondingWorld		= null;
	/**
	 * Corresponding Boxel World Name.
	 */
	protected String	correspondingWorldName	= "";
	
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
		
		if (master.getMVCore().getMVWorldManager().getMVWorld(
				correspondingWorldName) != null)
			correspondingWorld = master.getMVCore().getMVWorldManager().getMVWorld(
					correspondingWorldName).getCBWorld();
		
		if (generator.isEmpty())
			boxelGenerator = "MonoBoxel";
		else
			boxelGenerator = generator;
		
		boxelSeed = seed;
		
		boxelPrefix = master.getBoxelPrefix();
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
		if (correspondingWorldName.startsWith(boxelPrefix))
			boxelOwner = correspondingWorldName.substring(boxelPrefix.length());
		
		if (boxelOwner.equals(player.getName()))
			isPlayersOwnBoxel = true;
		
		// check permissions
		if (isPlayersOwnBoxel
				&& !master.getPermissionManager().hasPermission(
						player,
						new MBPermission(MBPermission.CAN_CREATE_OWN))) {
			master.getPermissionManager().SendNotAllowedMessage(player);
			master.getLogManager().info("Failed at test 1");
			return false;
		}
		if (!isPlayersOwnBoxel
				&& !master.getPermissionManager().hasPermission(
						player,
						new MBPermission(MBPermission.ROOT_CAN_CREATE,
								boxelOwner))) {
			master.getPermissionManager().SendNotAllowedMessage(player);
			master.getLogManager().info("Failed at test 2");
			return false;
		}
		
		// first check if the Boxel does not already exist
		if (isExisting()) {
			
			master.getLogManager().debugLog(
					Level.INFO,
					master.getLocalizationManager().getMessage("loaded").setMessageVariable(
							"boxeltype", "Boxel").setMessageVariable(
							"boxelname", correspondingWorldName).toString());
			
			if (!Load()) {
				// something went wrong
				master.getLogManager().debugLog(
						Level.SEVERE,
						master.getLocalizationManager().getMessage(
								"failed-to-load").setMessageVariable(
								"boxeltype", "Boxel").setMessageVariable(
								"boxelname", correspondingWorldName).toString());
				return false;
			}
			
			// the world exists, but we have no reference to it
			if (correspondingWorld == null) {
				correspondingWorld = master.getMVCore().getMVWorldManager().getMVWorld(
						correspondingWorldName).getCBWorld();
				return true;
			}
		}
		
		// create the Boxel now
		if (DoCreate(player)) {
			correspondingWorld = master.getMVCore().getMVWorldManager().getMVWorld(
					correspondingWorldName).getCBWorld();
			
			return true;
		} else {
			master.getLogManager().debugLog(
					Level.WARNING,
					master.getLocalizationManager().getMessage(
							"unable-to-create-boxel").setMessageVariable(
							"boxelname", correspondingWorldName).toString());
			return false;
		}
	}
	
	/**
	 * Does the actual Boxel/World creation.
	 * 
	 * @param sender
	 *            The sender (mostly a player) that will perform this action (I
	 *            am using CommandSender because so it is possible to create
	 *            Boxel's for specific players from the console)
	 * @return true on success, otherwise false
	 */
	protected boolean DoCreate(CommandSender sender) {
	
		MVWorldManager wm = master.getMVCore().getMVWorldManager();
		MultiverseWorld result = null;
		
		if (correspondingWorld != null)
			master.getLogManager().debugLog(
					Level.INFO,
					master.getLocalizationManager().getMessage(
							"failed-to-create").setMessageVariable("boxeltype",
							"Boxel").setMessageVariable("boxelname",
							correspondingWorldName).toString());
		
		if (wm.getMVWorld(correspondingWorldName) != null) {
			sender.sendMessage("Found your boxel. Will port you there now...");
			return true;
		} else {
			// now check unloaded worlds too
			Collection<String> uworlds = wm.getUnloadedWorlds();
			if (uworlds.contains(correspondingWorldName)) {
				sender.sendMessage("Found your boxel. Will have to load it and port you there...");
				if (!Load()) {
					master.getLogManager().debugLog(
							Level.SEVERE,
							master.getLocalizationManager().getMessage(
									"failed-to-load").setMessageVariable(
									"boxeltype", "Boxel").setMessageVariable(
									"boxelname", correspondingWorldName).toString());
					sender.sendMessage("Failed to load Boxel.");
					return false;
				}
				return true;
			}
		}
		
		if (master.getMBWorldManager().getNumBoxels() >= master.getConfig().getInt(
				"max-boxel-count")) {
			sender.sendMessage(master.getLocalizationManager().getMessage(
					"maximum-reached").setMessageVariable(
					"maximum",
					String.valueOf(master.getConfig().getInt("max-boxel-count"))).setMessageVariable(
					"type", "Boxels").toString());
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
			result.setDifficulty("PEACEFUL");
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
			return true;
		}
		
		if (!master.getMVCore().getMVWorldManager().loadWorld(
				correspondingWorldName)) {
			// failed to load Boxel
			master.getLogManager().severe("Failed to load Boxel.");
			return false;
		}
		
		// the world should be loaded now
		correspondingWorld = master.getMVCore().getMVWorldManager().getMVWorld(
				correspondingWorldName).getCBWorld();
		
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
		
		if (!isExisting()) {
			master.getLogManager().severe(
					"The Boxel " + correspondingWorldName + " does not exist.");
			return false;
		}
		
		// should always be true
		if (correspondingWorldName.startsWith(master.getBoxelPrefix()))
			boxelOwner = correspondingWorldName.substring(master.getBoxelPrefix().length());
		
		if (boxelOwner.equals(player.getName()))
			isPlayersOwnBoxel = true;
		
		// check permissions
		if (isPlayersOwnBoxel
				&& !master.getPermissionManager().hasPermission(player,
						new MBPermission(MBPermission.CAN_VISIT_OWN))) {
			master.getPermissionManager().SendNotAllowedMessage(player);
			return false;
		}
		
		if (!isPlayersOwnBoxel
				&& !master.getPermissionManager().hasPermission(
						player,
						new MBPermission(MBPermission.ROOT_CAN_VISIT,
								boxelOwner))) {
			master.getPermissionManager().SendNotAllowedMessage(player);
			return false;
		}
		
		// before porting the player, save his location
		if (master.getConfig().getBoolean("save-exit-location")) {
			// do not save the return/entry location if the player is in a Boxel
			if (!master.getMBWorldManager().isBoxel(player.getWorld().getName())[0]) {
				master.getDataConfig().getConfig().set(
						"playeroloc." + player.getName() + ".world",
						player.getWorld().getName());
				master.getDataConfig().getConfig().set(
						"playeroloc." + player.getName() + ".position",
						player.getLocation().toVector());
				master.getDataConfig().getConfig().set(
						"playeroloc." + player.getName() + ".yaw",
						player.getLocation().getYaw());
				master.getDataConfig().getConfig().set(
						"playeroloc." + player.getName() + ".pitch",
						player.getLocation().getPitch());
				master.getDataConfig().saveConfig();
			}
		}
		
		if (!isLoaded()) {
			if (!Load()) {
				master.getLogManager().severe(
						"Could not load Boxel " + correspondingWorldName);
				return false;
			}
		}
		
		// port the player now
		return player.teleport(correspondingWorld.getSpawnLocation());
	}
	
	/**
	 * Teleports a player back to his original location.
	 * 
	 * @param player
	 *            The player to teleport
	 * @return true on success, otherwise false
	 */
	public boolean Leave(Player player) {
	
		MVWorldManager wm = master.getMVCore().getMVWorldManager();
		MultiverseWorld entryWorld = null;
		
		String outWorld = master.getDataConfig().getConfig().getString(
				"playeroloc." + player.getName() + ".world", "");
		Vector outPosition = master.getDataConfig().getConfig().getVector(
				"playeroloc." + player.getName() + ".position",
				new org.bukkit.util.Vector());
		double outPitch = master.getDataConfig().getConfig().getDouble(
				"playeroloc." + player.getName() + ".pitch", 0.0);
		
		double outYaw = master.getDataConfig().getConfig().getDouble(
				"playeroloc." + player.getName() + ".yaw", 0.0);
		
		// the saved location could not be loaded correctly
		if (outWorld.isEmpty()) {
			master.getLogManager().debugLog(
					Level.WARNING,
					"No entry location for player " + player.getName()
							+ " was found.");
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
						"Entry world " + outWorld + " for player "
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
	
	/**
	 * Unloads the Boxel if no player is inside.
	 * 
	 * @return true on success, otherwise false (will also return false if there
	 *         are players in this Boxel)
	 */
	public boolean Unload() {
	
		if (isEmpty() && isLoaded()) {
			if (master.getMVCore().getMVWorldManager().unloadWorld(
					correspondingWorldName)) {
				
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
