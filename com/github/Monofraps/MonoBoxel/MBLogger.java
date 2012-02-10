package com.github.Monofraps.MonoBoxel;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Logging Class for MonoBoxel.
 * Holds the log instance and adds Plugin information to the beginning of a Logmessage.
 * @version 0.4
 * @author MikeMatrix
 */
public class MBLogger {

	private Logger log;
	private PluginDescriptionFile pdFile;
	private String LogPrefix = "";

	/**
	 * 
	 * @param name Name of the Logger Object
	 * @param plugin Plugin reference
	 */
	protected MBLogger(String name, JavaPlugin plugin) {
		log = Logger.getLogger(name);
		pdFile = plugin.getDescription();
		LogPrefix = "[" + pdFile.getName() + " " + pdFile.getVersion() + "] ";
	}

	/**
	 * Logs an informative Message
	 * @param msg
	 */
	public void info(String msg) {
		log.info(LogPrefix + msg);
	}

	/**
	 * Logs a severe Error message
	 * @param msg
	 */
	public void severe(String msg) {
		log.severe(LogPrefix + msg);
	}

	/**
	 * Logs a Warning Message
	 * @param msg
	 */
	public void warning(String msg) {
		log.warning(LogPrefix + msg);
	}
}
