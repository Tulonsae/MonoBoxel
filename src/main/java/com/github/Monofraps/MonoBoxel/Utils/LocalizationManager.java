package com.github.Monofraps.MonoBoxel.Utils;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.github.Monofraps.MonoBoxel.MonoBoxel;
import com.github.Monofraps.MonoBoxel.Config.MBLocalizationConfig;


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
		 */
		public void setMessageVariable(String handle, String value) {
			messageVariables.put(handle, value);
		}
		
		@Override
		public String toString() {
			String returnString = messageTemplate;
			Iterator<Map.Entry<String, String>> varIt = messageVariables
					.entrySet().iterator();
			while (varIt.hasNext()) {
				Map.Entry<String, String> entry = varIt.next();
				returnString = returnString.replaceAll(entry.getKey(),
						entry.getValue());
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
	private MBLocalizationConfig					localizationConfig	= null;
	
	/**
	 * @param node
	 * @return
	 */
	public LocalizationMessage getMessage(String node) {
		return messages.containsKey(node) ? messages.get(node).clone() : null;
	}
	
	public LocalizationManager(MonoBoxel plugin) {
		master = plugin;
		localizationConfig = new MBLocalizationConfig(master);
		localizationConfig.reloadConfig();
		this.reloadLocalization();
	}
	
	public void reloadLocalization() {
		localizationConfig.reloadConfig();
		Set<String> temp = localizationConfig.getConfig().getKeys(false);
		for (String string : temp) {
			master.getLogManager().debugLog(Level.INFO, string);
			messages.put(string, new LocalizationMessage("hallo"));
		}
	}
	
	public void SaveLocalization() {
		localizationConfig.saveConfig();
	}
}
