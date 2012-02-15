package com.github.Monofraps.MonoBoxel.Utils;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.github.Monofraps.MonoBoxel.MonoBoxel;
import com.github.Monofraps.MonoBoxel.Config.MBConfiguration;


/**
 * Localization Manager.
 * 
 * @author MikeMatrix
 */
public class LocalizationManager {
	
	/**
	 * @author MikeMatrix
	 * 
	 */
	public class LocalizationMessage {
		
		private String					messageTemplate		= null;
		private HashMap<String, String>	messageVariables	= new HashMap<String, String>();
		
		/**
		 * @param messageTemplate
		 */
		public LocalizationMessage(String messageTemplate) {
			this.messageTemplate = messageTemplate;
		}
		
		/**
		 * @param messageTemplate
		 * @param messageVariables
		 */
		private LocalizationMessage(String messageTemplate,
				HashMap<String, String> messageVariables) {
			super();
			this.messageTemplate = messageTemplate;
			this.messageVariables = messageVariables;
		}
		
		/**
		 * @param handle
		 * @param value
		 * @return this
		 */
		public LocalizationMessage setMessageVariable(String handle,
				String value) {
			messageVariables.put(handle, value);
			return this;
		}
		
		@Override
		public String toString() {
			String returnString = messageTemplate;
			Iterator<Map.Entry<String, String>> varIt = messageVariables
					.entrySet().iterator();
			while (varIt.hasNext()) {
				Map.Entry<String, String> entry = varIt.next();
				returnString = returnString.replaceAll("%" + entry.getKey()
						+ "%", entry.getValue());
			}
			
			return returnString;
		}
		
		@Override
		protected LocalizationMessage clone() {
			return new LocalizationMessage(this.messageTemplate,
					this.messageVariables);
		}
	}
	
	private MonoBoxel								master				= null;
	
	private HashMap<String, LocalizationMessage>	messages			= new HashMap<String, LocalizationMessage>();
	private MBConfiguration							localizationConfig	= null;
	
	/**
	 * @param node
	 * @return New cloned instance of the LocalizationMessage queried, if no such node exists it
	 *         will return a LocalizationMessage with the Message "No Message Found.".
	 */
	public LocalizationMessage getMessage(String node) {
		return messages.containsKey(node) ? messages.get(node).clone()
				: new LocalizationMessage("No Message Found.");
	}
	
	/**
	 * @param node
	 * @return true if Message is available, false if not
	 */
	public boolean hasMessage(String node) {
		return messages.containsKey(node);
	}
	
	public LocalizationManager(MonoBoxel plugin) {
		master = plugin;
		localizationConfig = new MBConfiguration(master, "localization.yml");
		localizationConfig.reloadConfig();
		this.reloadLocalization();
	}
	
	/**
	 * Reload the Localizations.
	 */
	public void reloadLocalization() {
		localizationConfig.reloadConfig();
		Set<String> messageIds = localizationConfig.getConfig().getKeys(false);
		for (String messageId : messageIds) {
			master.getLogManager().debugLog(Level.INFO, messageId);
			LocalizationMessage localizationMessage = new LocalizationMessage(
					localizationConfig.getConfig().getString(
							messageId + ".template-message"));
			List<String> localizationValueKeys = localizationConfig.getConfig()
					.getStringList(messageId + ".variables");
			for (String localizationValueKey : localizationValueKeys) {
				localizationMessage
						.setMessageVariable(localizationValueKey, "");
			}
			messages.put(messageId, localizationMessage);
		}
	}
	
	/**
	 * Save the Localizations.
	 * A wrapper around the Configuration Manager.
	 */
	public void SaveLocalization() {
		localizationConfig.saveConfig();
	}
}
