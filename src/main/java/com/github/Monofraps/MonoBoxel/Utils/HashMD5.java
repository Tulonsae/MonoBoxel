package com.github.Monofraps.MonoBoxel.Utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class just allows to quickly hash a string to a MD5 sum
 * 
 * @author Monofraps
 * 
 */
public class HashMD5 {
	
	public static String Hash(String str)
	{
		MessageDigest md5 = null;
		StringBuffer sbMD5sum = new StringBuffer();
		byte[] digest = null;
		
		try {
			md5 = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
		
		md5.reset();
		md5.update(str.getBytes());
		
		digest = md5.digest();
		
		for (byte b : digest)
		{
			sbMD5sum.append(Integer.toHexString((b & 0xFF) | 0x100)
					.toLowerCase().substring(1, 3));
		}
		
		return sbMD5sum.toString();
	}
}
