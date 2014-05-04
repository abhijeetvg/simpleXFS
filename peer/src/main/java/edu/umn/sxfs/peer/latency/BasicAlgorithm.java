package edu.umn.sxfs.peer.latency;

import java.util.Map;
import java.util.Set;

import edu.umn.sxfs.common.server.PeerInfo;

/**
 * This is basic algorithm that returns the node with lowest latency.
 * @author prashant
 *
 */
public final class BasicAlgorithm extends BaseAlgorithm {

	public BasicAlgorithm(PeerInfo peerInfo, Set<PeerInfo> peerInfos) {
		super(peerInfo, peerInfos);
	}

	@Override
	public PeerInfo getDestinationPeerInfo() {
		Set<PeerInfo> availablePeerInfos = getAvailablePeerInfos();
		Map<PeerInfo, Long> latencies = PeerPeerLatencyStore.getInstance().getLatencies(getPeerInfo());
		Long lowestLatency = Long.MAX_VALUE;
		PeerInfo minLatencyPeerInfo = null;
		for (PeerInfo peerInfo : availablePeerInfos) {
			Long currentLatency = latencies.get(peerInfo);
			if(currentLatency < lowestLatency) {
				lowestLatency = currentLatency;
				minLatencyPeerInfo = peerInfo;
			}
		}
		return minLatencyPeerInfo;
	}

}
