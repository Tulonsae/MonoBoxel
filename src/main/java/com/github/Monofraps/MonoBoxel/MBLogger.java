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
	private boolean logStackTrace = false;

	/**
	 * 
	 * @param name Name of the Logger Object
	 * @param plugin Plugin reference
	 */
	protected MBLogger(String name, JavaPlugin plugin) {
		log = Logger.getLogger(name);
		pdFile = plugin.getDescription();
		logStackTrace = plugin.getConfig().getBoolean("log-stack-trace", false);
		LogPrefix = "[" + pdFile.getName() + " " + pdFile.getVersion() + "] ";
	}

	/**
	 * Logs an informative Message
	 * @param msg
	 */
	public void info(String msg) {
		if(logStackTrace)
		{
			msg = msg + "\n With stack: \n";
			
			for(StackTraceElement s : Thread.currentThread().getStackTrace())
			 msg += s.toString() + "\n";
			
			msg += "\n====================";
		}
		log.info(LogPrefix + msg);
	}

	/**
	 * Logs a severe Error message
	 * @param msg
	 */
	public void severe(String msg) {
		if(logStackTrace)
		{
			msg = msg + "\n With stack: \n";
			
			for(StackTraceElement s : Thread.currentThread().getStackTrace())
			 msg += s.toString() + "\n";
			
			msg += "\n====================";
		}
		log.severe(LogPrefix + msg);
	}

	/**
	 * Logs a Warning Message
	 * @param msg
	 */
	public void warning(String msg) {
		if(logStackTrace)
		{
			msg = msg + "\n With stack: \n";
			
			for(StackTraceElement s : Thread.currentThread().getStackTrace())
			 msg += s.toString() + "\n";
			
			msg += "\n====================";
		}
		log.warning(LogPrefix + msg);
	}
}
