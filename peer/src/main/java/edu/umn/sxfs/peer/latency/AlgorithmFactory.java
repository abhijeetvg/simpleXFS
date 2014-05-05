package edu.umn.sxfs.peer.latency;

import java.util.Set;

import edu.umn.sxfs.common.server.PeerInfo;

/**
 * Factory that returns appropriate Algorithm object.
 * LoadLatencyAlgorithm is returned by default. If anything is specified
 *
 * @author prashant
 *
 */
public final class AlgorithmFactory {

	public static final String BASIC_ALGORITHM = "basicAlgorithm";
    public static final String LOAD_LATENCY_ALGORITHM = "loadLatencyAlgorithm";

    public static BaseAlgorithm getAlgorithm(String key, PeerInfo peerInfo, Set<PeerInfo> availablePeerInfos) {
		if(key == null || key.isEmpty()) {
			throw new IllegalArgumentException();
		}
		if(key.equals(BASIC_ALGORITHM)) {
			return new BasicAlgorithm(peerInfo, availablePeerInfos);
		} else if (key.equals(LOAD_LATENCY_ALGORITHM)) {
            return new LoadLatencyAlgorithm(peerInfo, availablePeerInfos);
        }

        //TODO: Client will end abruptly. throw ClientGeneric.
		throw new IllegalArgumentException();
	}
}
