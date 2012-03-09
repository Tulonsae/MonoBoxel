package com.Monofraps.MonoBoxel.Utils;


import org.bukkit.entity.Player;

import com.Monofraps.MonoBoxel.MonoBoxel;


/**
 * Some more or less generic utilities.
 * 
 * @author Monofraps
 * 
 */
public class GenUtils {
	
	/**
	 * Will insert the Boxel prefix, if it was not present in the string.
	 * 
	 * @param boxelName
	 * @param plugin
	 * @return The Boxel name with the prefix.
	 */
	public static String boxelizeName(String boxelName, MonoBoxel plugin) {
	
		if (!boxelName.startsWith(plugin.getBoxelPrefix()))
			boxelName = plugin.getBoxelPrefix() + boxelName;
		
		return boxelName;
	}
	
	/**
	 * Will insert the Boxel prefix, if it was not present in the string.
	 * 
	 * @param boxelName
	 * @param plugin
	 * @return The Boxel name with the prefix.
	 */
	public static String groupboxelizeName(String boxelName, MonoBoxel plugin) {
	
		if (!boxelName.startsWith(plugin.getBoxelGroupPrefix()))
			boxelName = plugin.getBoxelGroupPrefix() + boxelName;
		
		return boxelName;
	}
	
	/**
	 * Removes the Boxel prefix from the name.
	 * 
	 * @param boxelName
	 * @param plugin
	 * @return The Boxel name without the prefix.
	 */
	public static String deboxelizeName(String boxelName, MonoBoxel plugin) {
	
		if (boxelName.startsWith(plugin.getBoxelPrefix()))
			boxelName = boxelName.substring(plugin.getBoxelPrefix().length());
		
		if (boxelName.startsWith(plugin.getBoxelGroupPrefix()))
			boxelName = boxelName.substring(plugin.getBoxelGroupPrefix().length());
		
		return boxelName;
	}
	
	/**
	 * Checks if [boxelName] belongs to [player].
	 * 
	 * @param player
	 * @param boxelName
	 * @param plugin
	 * @return true if [player] is the owner of [boxelName], otherwise false
	 */
	public static boolean checkBoxelAffiliation(Player player, String boxelName, MonoBoxel plugin) {
	
		boxelName = deboxelizeName(boxelName, plugin);
		
		if (boxelName.equals(player.getName()))
			return true;
		
		return false;
	}
}
