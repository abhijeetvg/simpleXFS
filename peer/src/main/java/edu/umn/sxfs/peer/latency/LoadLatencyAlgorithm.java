package edu.umn.sxfs.peer.latency;

import edu.umn.sxfs.common.exception.PeerNotConnectedException;
import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.peer.PeerConfig;
import edu.umn.sxfs.peer.client.PeerClient;

import java.util.Map;
import java.util.Set;

/**
 * Implements peer selection algorithm!
 *
 * This algorithm considers load on the communicating peer apart from latency. We define a common threshold load beyond
 * which a request is not sent to the peer. It is assumed that each peer has the same threshold or all machines are
 * homogeneous.
 *
 * As far as the node is downloading and uploading below the threshold, least latency peer is selected. In
 * this case latency is given preference.
 *
 * If all machines are executing above threshold, least load peer is selected. In this case load is given preference.
 *
 * The ideal algorithm would consider:
 *  - Time taken by peer to transfer the file (latency + data transfer time). Only considering the latency makes an
 *    assumption that each upload (download) to (from) a peer takes the same time (latency in out case).
 *
 * Currently there is no way of knowing the network transfer rate between two peers so cannot implement the ideal case.
 *
 * Created by Abhijeet on 5/5/2014.
 */
public class LoadLatencyAlgorithm extends BaseAlgorithm {

    public LoadLatencyAlgorithm(PeerInfo peerInfo, Set<PeerInfo> peerInfos) {
        super(peerInfo, peerInfos);
    }

    @Override
    public PeerInfo getDestinationPeerInfo() {
        PeerInfo currentPeerInfo = getPeerInfo();
        if(currentPeerInfo == null) {
        	return null;
        }
		PeerClient.printOnShell("Selecting peer for : " + currentPeerInfo + "to download file.");
        Set<PeerInfo> availablePeerInfos = getAvailablePeerInfos();
        if(availablePeerInfos == null || availablePeerInfos.isEmpty()) {
        	return null;
        }
        Map<PeerInfo, Long> latencies = PeerPeerLatencyStore.getInstance().getLatencies(currentPeerInfo);
        if(latencies == null) {
        	return null;
        }
        Long lowestLatency = Long.MAX_VALUE;
        PeerInfo minLatencyPeerInfo = null, minLoadedPeer = null;
        int minLoad = Integer.MAX_VALUE;
        for (PeerInfo peerInfo : availablePeerInfos) {
            Long currentLatency = latencies.get(peerInfo);
            if(currentLatency == null) {
            	continue;
            }
            try {

                int load = PeerClient.getInstance().getClient().getLoad(peerInfo);
                if (load < minLoad) {
                    minLoad = load;
                    minLoadedPeer = peerInfo;
                }

                if(currentLatency < lowestLatency && load < PeerConfig.getLoadThreshold()) {
                        lowestLatency = currentLatency;
                        minLatencyPeerInfo = peerInfo;
                }
            } catch (PeerNotConnectedException e) {
                e.printStackTrace();
            }
        }

        PeerClient.printOnShell("Selected peerInfo : " + minLatencyPeerInfo);
        return null != minLatencyPeerInfo ? minLatencyPeerInfo : minLoadedPeer;
    }
}
