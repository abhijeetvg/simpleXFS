package edu.umn.sxfs.common.validator;

import java.util.regex.Pattern;

/**
 * Validation supports.
 */
public final class ContentValidator {

	private ContentValidator() {
		// cannot instantiate, only static methods
	}
	
	/**
	 * Checks if the IP is a valid IP.
	 * 
	 * @param ipString
	 * @return
	 */
	public static boolean isValidIp(String ipString) {
		if(ipString == null) {
			return false;
		}
		String ipPattern = 
		        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		return Pattern.compile(ipPattern).matcher(ipString).matches();    
	}
	
	/**
	 * Checks if the port is a valid positive number.
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isValidPort(String string) {
		try{
			int parseInt = Integer.parseInt(string);
			if(parseInt < 0) {
				return false;
			}
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
}
