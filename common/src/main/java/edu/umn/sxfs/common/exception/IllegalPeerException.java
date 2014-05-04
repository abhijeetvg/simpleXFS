package edu.umn.sxfs.common.exception;

/**
 * Thrown when illegal peer.
 * @author prashant
 *
 */
public class IllegalPeerException extends Exception {

	private static final long serialVersionUID = 618476407454525907L;

	public IllegalPeerException() {
		super();
	}
	
	public IllegalPeerException(String s) {
		super(s);
	}
}
