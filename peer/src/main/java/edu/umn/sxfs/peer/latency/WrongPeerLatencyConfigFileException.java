package edu.umn.sxfs.peer.latency;

/**
 * This exception is thrown when the Latency config file is having wrong format.
 * @author prashant
 *
 */
public final class WrongPeerLatencyConfigFileException extends Exception {

	private static final long serialVersionUID = -2080542245940630765L;

	public WrongPeerLatencyConfigFileException(String msg) {
		super(msg);
	}
}
