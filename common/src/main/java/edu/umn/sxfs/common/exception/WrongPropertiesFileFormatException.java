package edu.umn.sxfs.common.exception;

/**
 * Thrown when the properties file is not in proper format.
 * @author prashant
 *
 */
public final class WrongPropertiesFileFormatException extends Exception {

	private static final long serialVersionUID = 7184283454702545468L;
	
	public WrongPropertiesFileFormatException(String msg) {
		super(msg);
	}
}
