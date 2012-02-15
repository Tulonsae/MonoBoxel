package com.github.Monofraps.MonoBoxel.Config;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.Monofraps.MonoBoxel.MonoBoxel;


/**
 * Handles the Localization Configuration file.
 * 
 * @author MikeMatrix
 * 
 */
public class MBLocalizationConfig {
	
	private MonoBoxel			master					= null;
	
	private FileConfiguration	localizationConfig		= null;
	private File				localizationConfigFile	= null;
	
	public MBLocalizationConfig(MonoBoxel plugin) {
		master = plugin;
	}
	
	/**
	 * Reload Configuration from File.
	 */
	public void reloadConfig() {
		if (localizationConfigFile == null) {
			localizationConfigFile = new File(master.getDataFolder(),
					"localization.yml");
		}
		localizationConfig = YamlConfiguration
				.loadConfiguration(localizationConfigFile);
		
		InputStream defDataConfigStream = master
				.getResource("localization.yml");
		if (defDataConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defDataConfigStream);
			
			try {
				defConfig.save(localizationConfigFile);
				localizationConfig.load(localizationConfigFile);
				localizationConfig.save(localizationConfigFile);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @return the DataConfig
	 */
	public FileConfiguration getConfig() {
		if (localizationConfig == null) {
			reloadConfig();
		}
		return localizationConfig;
	}
	
	/**
	 * Save the Configuration to the Disk.
	 */
	public void saveConfig() {
		if (localizationConfig == null || localizationConfigFile == null)
			return;
		
		try {
			localizationConfig.save(localizationConfigFile);
		} catch (IOException e) {
			master.getLogManager().severe(
					"Could not save Data Configuration File to "
							+ localizationConfigFile);
		}
	}
}