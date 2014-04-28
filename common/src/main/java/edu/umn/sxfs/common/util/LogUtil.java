package edu.umn.sxfs.common.util;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Methods for logging purposes.
 * The methods will sys out the messages along with different attributes like timestamp.
 * 
 * @author prashant
 *
 */
public final class LogUtil {
	private final static SimpleDateFormat simpleDateFomat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private LogUtil() {
		// Util class, cannot be instantiated
	}
	
	private static String currentTime() {
		return simpleDateFomat.format(new Date());
	}
	
	public static void log(String method, String message) {
		System.out.printf("%s\t%-35s\t%s\n",currentTime(),method,message);
	}
	
	public static void info(String message) {
		System.out.println(message);
	}
	
	public static void error(String method, String message) {
		System.out.print("ERROR: ");
		LogUtil.log(method, message);
	}

	public static void catchedRemoteException(RemoteException e) {
		if (null == e.getCause()) {
			LogUtil.error("Remote Error: ", e.getMessage());
		} else {
			LogUtil.error("Remote Error: ", e.getCause().getMessage());
		}

	}
}
