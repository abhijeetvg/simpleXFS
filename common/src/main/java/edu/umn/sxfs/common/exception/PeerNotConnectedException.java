package edu.umn.sxfs.common.exception;

/**
 * Throw this exception when the there is exception while connecting to the peer.
 * @author prashant
 *
 */
public final class PeerNotConnectedException extends Exception {

	private static final long serialVersionUID = -1438470644991302270L;
	
	public PeerNotConnectedException(String msg) {
		super(msg);
	}
	
}
