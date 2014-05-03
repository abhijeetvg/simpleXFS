package edu.umn.sxfs.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Util class for calculating the checksum of the files
 * @author prashant
 *
 */
public class MD5CheckSumUtil {
	
	private final static String MD5 = "MD5";
	
	private MD5CheckSumUtil() {
		throw new IllegalStateException("Util Class. Cannot instantiate");
	}

	/**
	 * This file reads the file and calculates the MD5 checksum.
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static byte[] createChecksum(String filename) throws IOException{
		InputStream fis = new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = null;
		try {
			complete = MessageDigest.getInstance(MD5);
		} catch (NoSuchAlgorithmException e) {
			// Should never reach here;
		}
		int reads;

		do {
			reads = fis.read(buffer);
			if (reads > 0) {
				complete.update(buffer, 0, reads);
			}
		} while (reads != -1);

		fis.close();
		return complete.digest();
	}

	public static boolean isEqualCheckSum(byte[] checksum1, byte[] checksum2) {
		return MessageDigest.isEqual(checksum1, checksum2);
	}
}