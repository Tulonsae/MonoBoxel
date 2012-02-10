package com.github.Monofraps.MonoBoxel;

import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.Monofraps.MonoBoxel.CommandExecutors.MBBoxelCommandExecutor;
import com.github.Monofraps.MonoBoxel.CommandExecutors.MBBoxelinfoCommandExecutor;
import com.github.Monofraps.MonoBoxel.CommandExecutors.MBBoxellookupCommandExecutor;
import com.github.Monofraps.MonoBoxel.CommandExecutors.MBBoxelremoveCommandExecutor;
import com.github.Monofraps.MonoBoxel.EventHooks.MBEventListener;
import com.onarandombox.MultiverseCore.MultiverseCore;

/**
 * Main class of MonoBoxel plugin.
 * 
 * @version 0.4
 * @author Monofraps
 */
public class MonoBoxel extends JavaPlugin {

	MBLogger logger = null;
	MBBoxelCommandExecutor boxelCmdExecutor;
	MBBoxellookupCommandExecutor boxellookupCmdExecutor;
	MBBoxelremoveCommandExecutor boxelremoveCmdExecutor;
	MBBoxelinfoCommandExecutor boxelinfoCmdExecutor;

	private MBEventListener eventListener = null;

	MBWorldManager worldManager;

	private MultiverseCore mvCore = null;
	private String boxelPrefix = "BOXEL_";

	/**
	 * Hooks up the command executors and initializes the scheduled tasks
	 */
	public void onEnable() {
		this.logger = new MBLogger("Minecraft", this);

		// missuse the version to find out if this is the first run
		String versionInfo = getConfig().getString("version", "first run");
		if (versionInfo.equals("first run"))
			saveDefaultConfig();

		// we may use the version for incompatibility checks later
		reloadConfig();
		getConfig().set("version", getDescription().getVersion());

		boxelPrefix = getConfig().getString("boxel-prefix", "BOXEL_");

		boxelCmdExecutor = new MBBoxelCommandExecutor(this);
		boxellookupCmdExecutor = new MBBoxellookupCommandExecutor(this);
		boxelremoveCmdExecutor = new MBBoxelremoveCommandExecutor(this);
		boxelinfoCmdExecutor = new MBBoxelinfoCommandExecutor(this);
		getCommand("boxel").setExecutor(boxelCmdExecutor);
		getCommand("boxlookup").setExecutor(boxellookupCmdExecutor);
		getCommand("boxremove").setExecutor(boxelremoveCmdExecutor);
		getCommand("boxinfo").setExecutor(boxelinfoCmdExecutor);

		eventListener = new MBEventListener(this);

		worldManager = new MBWorldManager(this);

		logger.info("MonoBoxel enabled!");
	}

	/**
	 * Tries to get the MultiverseCore instance
	 * 
	 * @return The MultiverCore or null on failure.
	 */
	public MultiverseCore GetMVCore() {
		if (mvCore == null) {
			Plugin[] plugins = getServer().getPluginManager().getPlugins();
			Plugin mv = null;
			for (Plugin p : plugins) {
				if (p.toString().contains("Multiverse-Core")) {
					mv = p;
					logger.info("Multiverse Core found.");
				}
			}
			if (mv == null) {
				logger.info("Multiverse-Core *NOT* found! Is it installed and enabled?");
				return null;
			}

			mvCore = (MultiverseCore) mv;
		}

		return mvCore;
	}

	public void onDisable() {
		saveConfig();
		logger.info("MonoBoxel disabled!");
	}

	/**
	 * 
	 * @return The MonoBoxel chunk generator
	 */
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new MBBoxelGenerator(getConfig().getLong("max-boxel-size", 5));
	}

	/**
	 * Checks if the player has permissions to create his own Boxel
	 * 
	 * @param player
	 *            The player that want's to perform the creation.
	 * @return true if the player has permissions, otherwise false
	 */
	public boolean CheckPermCanCreateOwn(Player player) {
		return player.hasPermission("monoboxel.boxel.create.own");
	}

	/**
	 * Checks if the player has permissions to create other players Boxels
	 * 
	 * @param player
	 *            The player that want's to perform the creation.
	 * @return true if the player has permissions, otherwise false
	 */
	public boolean CheckPermCanCreateOther(Player player) {
		return player.hasPermission("monoboxel.boxel.create.other");
	}

	/**
	 * Checks if the player has permissions visit his own Boxel
	 * 
	 * @param player
	 *            The player that want's to perform the creation.
	 * @return true if the player has permissions, otherwise false
	 */
	public boolean CheckPermCanVisitOwn(Player player) {
		// if the player has permissions to create his own Boxel, he will also
		// be able to visit it
		if (player.hasPermission("monoboxel.boxel.create.own"))
			return true;

		if (player.hasPermission("monoboxel.boxel.visit.own"))
			return true;

		if (player.hasPermission("monoboxel.boxel.visit." + player.getName()))
			return true;

		return false;
	}

	/**
	 * Checks if the player has permissions to visit other players Boxels
	 * 
	 * @param player
	 *            The player that want's to perform the creation.
	 * @return true if the player has permissions, otherwise false
	 */
	public boolean CheckPermCanVisitOther(Player player, String boxelName) {
		// do not check for visit.BOXEL_<name> permissions but for visit.<name>
		if (boxelName.startsWith(getBoxelPrefix()))
			boxelName = boxelName.substring(getBoxelPrefix().length());

		if (getConfig().getBoolean("per-boxel-permissions", true))
			return player.hasPermission("monoboxel.boxel.visit." + boxelName);
		else
			return player.hasPermission("monoboxel.boxel.visit.other");
	}

	/**
	 * 
	 * @return The Boxel prefix
	 */
	public String getBoxelPrefix() {
		return boxelPrefix;
	}

	public MBWorldManager getMBWorldManager() {
		return worldManager;
	}

	public MBLogger getLogManager() {
		return logger;
	}
}
