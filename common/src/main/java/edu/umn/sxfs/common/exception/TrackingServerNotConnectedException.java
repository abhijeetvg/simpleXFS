package edu.umn.sxfs.common.exception;

/**
 * Throw this exception when the there is exception while connecting to the tracking server.
 * @author prashant
 *
 */
public final class TrackingServerNotConnectedException extends Exception {

	private static final long serialVersionUID = -1438470644991302270L;
	
	public TrackingServerNotConnectedException(String msg) {
		super(msg);
	}
	
}
