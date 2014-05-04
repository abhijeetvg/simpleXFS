package edu.umn.sxfs.peer.latency;

import java.util.Set;

import edu.umn.sxfs.common.server.PeerInfo;

/**
 * Factory that returns appropriate Algorithm object.
 * @author prashant
 *
 */
public final class AlgorithmFactory {
	
	public static final String BASIC_ALGORITHM = "basicAlgorithm"; 
	
	public static BaseAlgorithm getAlgorithm(String key, PeerInfo peerInfo, Set<PeerInfo> availablePeerInfos) {
		if(key == null || key.isEmpty()) {
			throw new IllegalArgumentException();
		}
		if(key.equals(BASIC_ALGORITHM)) {
			return new BasicAlgorithm(peerInfo, availablePeerInfos);
		}
		
		throw new IllegalArgumentException();
	}
}
