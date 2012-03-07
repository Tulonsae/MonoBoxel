package com.Monofraps.MonoBoxel;


import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * Class for internationalization.
 * 
 * @author Monofraps
 * 
 */
public class MBMessages {
	
	private static final String			BUNDLE_NAME		= "messages";
	
	private static final ResourceBundle	RESOURCE_BUNDLE	= ResourceBundle.getBundle(BUNDLE_NAME);
	
	private MBMessages() {
	
	}
	
	/**
	 * Returns the message for key [key].
	 * 
	 * @param key
	 * @return The message as string.
	 */
	public static String getMessage(String key) {
	
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
