package edu.umn.sxfs.peer;

import edu.umn.sxfs.peer.latency.AlgorithmFactory;


public final class PeerPropertiesConstants {
	private PeerPropertiesConstants() {
		throw new IllegalStateException("Cannot instantiate a constants class");
	}
	
	public static final String PEER_ALGORITHM = "peerAlgorithm";
	public static final String LOAD_THRESHOLD = "loadThreshold";
	public static final String TRACKING_SERVER_IP = "trackingServerIp";
	public static final String TRACKING_SERVER_PORT = "trackingServerPort";
	public static final String FILE_STORE_DIRECTORY = "fileStoreDirectory";
	public static final String PEER_PEER_LATENCY_FILE = "peerPeerLatencyFile";
	
	
	public static final int DEFAULT_LOAD = 2;
	public static final String DEFAULT_PEER_ALGORITHM = AlgorithmFactory.BASIC_ALGORITHM;
}
