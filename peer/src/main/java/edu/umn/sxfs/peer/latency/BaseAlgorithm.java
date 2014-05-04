package edu.umn.sxfs.peer.latency;

import java.util.HashSet;
import java.util.Set;

import edu.umn.sxfs.common.server.PeerInfo;

/**
 * Base algorithm to be extended by different Peer selection algorithms.
 * @author prashant
 *
 */
public abstract class BaseAlgorithm {

	private PeerInfo peerInfo;
	private Set<PeerInfo> availablePeerInfos = new HashSet<PeerInfo>();
	
	public BaseAlgorithm(PeerInfo peerInfo, Set<PeerInfo> peerInfos) {
		this.peerInfo = peerInfo;
		this.availablePeerInfos = peerInfos;
	}
	
	protected PeerInfo getPeerInfo() {
		return peerInfo;
	}
	
	protected Set<PeerInfo> getAvailablePeerInfos() {
		return availablePeerInfos;
	}

	/**
	 * This method returns the required PeerInfo from which the file is can be downloaded.
	 */
	public abstract PeerInfo getDestinationPeerInfo();
}
