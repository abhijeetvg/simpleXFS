package edu.umn.sxfs.common.util;


/**
 * General util methods for string manipulation
 * @author prashant
 * @author Abhijeet
 *
 */
public final class StringUtil {
	private StringUtil() {
		// Util class, cannot be instantiated
	}
	
	/**
	 * Along with {@code isEmpty()} check from {@link String} class also checks if the string is {@literal null}
	 * @return
	 */
	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty();
	}
	
	/**
	 * Get the command prefix. Uniquely identifies the command.
	 * 
	 * @param string
	 * @return
	 */
	public static String getCmdPrefix(String string) {
		return (null != string) ? string.split(" ")[0] : null;
	}
}
