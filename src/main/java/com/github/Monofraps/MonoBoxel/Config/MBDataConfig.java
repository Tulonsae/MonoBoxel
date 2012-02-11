package com.github.Monofraps.MonoBoxel.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.Monofraps.MonoBoxel.MonoBoxel;

/**
 * Handles special date as a config file.
 * Handles the backport locations for players.
 * @author MikeMatrix
 *
 */
public class MBDataConfig {

	private MonoBoxel master = null;

	private FileConfiguration dataConfig = null;
	private File dataConfigFile = null;

	public MBDataConfig(MonoBoxel plugin) {
		master = plugin;
	}

	public void reloadDataConfig() {
		if (dataConfigFile == null) {
			dataConfigFile = new File(master.getDataFolder(), "data.yml");
		}
		dataConfig = YamlConfiguration.loadConfiguration(dataConfigFile);

		InputStream defDataConfigStream = master.getResource("data.yml");
		if (defDataConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defDataConfigStream);
			dataConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration getDataConfig() {
		if (dataConfig == null) {
			reloadDataConfig();
		}
		return dataConfig;
	}

	public void saveDataConfig() {
		if (dataConfig == null || dataConfigFile == null)
			return;

		try {
			dataConfig.save(dataConfigFile);
		} catch (IOException e) {
			master.getLogManager().severe(
					"Could not save Data Configuration File to "
							+ dataConfigFile);
		}
	}
}
