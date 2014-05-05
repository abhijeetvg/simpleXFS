package edu.umn.sxfs.peer.latency;

import java.util.Map;
import java.util.Set;

import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.peer.client.PeerClient;

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
		PeerInfo currentPeerInfo = getPeerInfo();
		if(currentPeerInfo == null) {
			return null;
		}
		PeerClient.printOnShell("Selecting peerinfo for : " + currentPeerInfo);
		Set<PeerInfo> availablePeerInfos = getAvailablePeerInfos();
		if(availablePeerInfos == null || availablePeerInfos.isEmpty()) {
			return null;
		}
		Map<PeerInfo, Long> latencies = PeerPeerLatencyStore.getInstance().getLatencies(currentPeerInfo);
		if(latencies == null) {
			return null;
		}
		Long lowestLatency = Long.MAX_VALUE;
		PeerInfo minLatencyPeerInfo = null;
		for (PeerInfo peerInfo : availablePeerInfos) {
			Long latency = latencies.get(peerInfo);
			if(latency == null) {
				continue;
			}
			Long currentLatency = latency;
			if(currentLatency < lowestLatency) {
				lowestLatency = currentLatency;
				minLatencyPeerInfo = peerInfo;
			}
		}
		PeerClient.printOnShell("Selected peerInfo : " + minLatencyPeerInfo);
		return minLatencyPeerInfo;
	}

}
