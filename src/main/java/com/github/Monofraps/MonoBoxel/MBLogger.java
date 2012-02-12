package com.github.Monofraps.MonoBoxel;


import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.utils.DebugLog;


/**
 * Logging Class for MonoBoxel.
 * Holds the log instance and adds Plugin information to the beginning of a Logmessage.
 * 
 * @version 0.4
 * @author MikeMatrix
 */
public class MBLogger {
	
	private Logger					log				= null;
	private DebugLog				debugLog		= null;
	private PluginDescriptionFile	pdFile			= null;
	private String					logPrefix		= "";
	private boolean					logStackTrace	= false;
	
	/**
	 * 
	 * @param name
	 *            Name of the Logger Object
	 * @param plugin
	 *            Plugin reference
	 */
	protected MBLogger(String name, JavaPlugin plugin) {
		log = Logger.getLogger(name);
		pdFile = plugin.getDescription();
		
		plugin.getDataFolder().mkdirs();
		File debugLogFile = new File(plugin.getDataFolder(), "debug.log");
		try {
			debugLogFile.createNewFile();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		debugLog = new DebugLog(pdFile.getName() + "-debug",
				plugin.getDataFolder() + File.separator + "debug.log");
		
		logStackTrace = plugin.getConfig().getBoolean("log-stack-trace", false);
		logPrefix = "[" + pdFile.getName() + " " + pdFile.getVersion() + "] ";
	}
	
	/**
	 * Logs a Message to a debug.log file in the Plugin Data directory.
	 * 
	 * @param level
	 * @param msg
	 */
	public void debugLog(Level level, String msg) {
		debugLog.log(level, logPrefix + msg);
	}
	
	/**
	 * Logs an informative Message.
	 * 
	 * @param msg
	 */
	public void info(String msg) {
		if (logStackTrace) {
			msg += "\n With stack: \n";
			
			for (StackTraceElement s : Thread.currentThread().getStackTrace())
				msg += s.toString() + "\n";
			
			msg += "\n====================";
		}
		log.info(logPrefix + msg);
	}
	
	/**
	 * Logs a severe Error message.
	 * 
	 * @param msg
	 */
	public void severe(String msg) {
		if (logStackTrace) {
			msg += "\n With stack: \n";
			
			for (StackTraceElement s : Thread.currentThread().getStackTrace())
				msg += s.toString() + "\n";
			
			msg += "\n====================";
		}
		log.severe(logPrefix + msg);
	}
	
	/**
	 * Logs a Warning Message.
	 * 
	 * @param msg
	 */
	public void warning(String msg) {
		if (logStackTrace) {
			msg += "\n With stack: \n";
			
			for (StackTraceElement s : Thread.currentThread().getStackTrace())
				msg += s.toString() + "\n";
			
			msg += "\n====================";
		}
		log.warning(logPrefix + msg);
	}
}
