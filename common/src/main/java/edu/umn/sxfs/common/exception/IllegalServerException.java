package edu.umn.sxfs.common.exception;

/**
 * Thrown when the server has not joined.
 * @author prashant
 *
 */
public class IllegalServerException extends Exception {

	private static final long serialVersionUID = 618476407454525907L;

	public IllegalServerException() {
		super();
	}
	
	public IllegalServerException(String s) {
		super(s);
	}
}
