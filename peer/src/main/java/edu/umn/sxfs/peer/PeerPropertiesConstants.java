package edu.umn.sxfs.peer;


public final class PeerPropertiesConstants {
	private PeerPropertiesConstants() {
		throw new IllegalStateException("Cannot instantiate a constants class");
	}
	
	public static final String PEER_ALGORITHM = "Peer Algorithm";
	public static final String LOAD_THRESHOLD = "Load Threshold";
	public static final String TRACKING_SERVER_IP = "Tracking Server Ip";
	public static final String TRACKING_SERVER_PORT = "Tracking Server Port";
	public static final String FILE_STORE_DIRECTORY = "File Store Directory";
	public static final String PEER_PEER_LATENCY_FILE = "Peer Peer Latency File";
	
	
	public static final int DEFAULT_LOAD = 2;
	public static final String BASE_ALGORITHM = "baseAlgorithm";
	public static final String DEFAULT_PEER_ALGORITHM = BASE_ALGORITHM;
}
