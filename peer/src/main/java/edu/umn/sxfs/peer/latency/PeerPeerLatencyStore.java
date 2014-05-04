package edu.umn.sxfs.peer.latency;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.umn.sxfs.common.exception.IllegalIPException;
import edu.umn.sxfs.common.exception.IllegalPeerException;
import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.common.util.LogUtil;

/**
 * Stores the peer-peer to latency.
 * The latencies are initialized from the config file that is passed as the 
 * command line argument to the peer.
 * @author prashant
 *
 */
public final class PeerPeerLatencyStore {

	private final Map<PeerInfo,Map<PeerInfo,Long>>peerPeerLatencyMap = new HashMap<PeerInfo, Map<PeerInfo,Long>>();
	
	private static PeerPeerLatencyStore instance = null;
	private final String SEPARATED = ",";
	
	private PeerPeerLatencyStore() {
		if(instance != null) {
			throw new IllegalStateException("Cannot instantiate the singleton class twice.");
		}
	}
	
	public static PeerPeerLatencyStore getInstance() {
		if(instance == null) {
			synchronized (PeerPeerLatencyStore.class) {
				if(instance == null) {
					instance = new PeerPeerLatencyStore();
				}
			}
		}
		return instance;
	}
	
	public synchronized boolean initialize(String filename) throws IOException, WrongPeerLatencyConfigFileException {
		BufferedReader br = null;
		String line = null;
		
		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			throw e;
		}
		try {
			while ((line = br.readLine()) != null) {
				if(line.isEmpty()) {
					continue;
				}
				String[] fields = line.split(SEPARATED);
				if(fields == null || fields.length != 3) {
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("Wrong peer latency file format (");
					stringBuilder.append(filename);
					stringBuilder.append(") on line:");
					stringBuilder.append(line);
					throw new WrongPeerLatencyConfigFileException(stringBuilder.toString());
				}
				PeerInfo peerInfo1 = null;
				PeerInfo peerInfo2 = null;
				try {
					peerInfo1 =  PeerInfo.createPeerInfo(fields[0]);
					peerInfo2 = PeerInfo.createPeerInfo(fields[1]);
				} catch (IllegalPeerException e) {
					throw new WrongPeerLatencyConfigFileException(e.getMessage());
				} catch (IllegalIPException e) {
					throw new WrongPeerLatencyConfigFileException(e.getMessage());
				}
				Long latency = Long.parseLong(fields[2]);
				updatePeerPeerLatencyMap(peerInfo1, peerInfo2, latency);
			}
		} catch (IOException e) {
			throw e;
		}finally{
			br.close();
		}
		return true;
	}

	private void updatePeerPeerLatencyMap(PeerInfo peerInfo1,
			PeerInfo peerInfo2, Long latency) {
		if(peerPeerLatencyMap.containsKey(peerInfo1)){
			peerPeerLatencyMap.get(peerInfo1).put(peerInfo2, latency);
		}else{
			HashMap<PeerInfo, Long> hashMap = new HashMap<PeerInfo, Long>();
			hashMap.put(peerInfo2, latency);
			peerPeerLatencyMap.put(peerInfo1, hashMap);
		}
		if(peerPeerLatencyMap.containsKey(peerInfo2)){
			peerPeerLatencyMap.get(peerInfo2).put(peerInfo1, latency);
		}else{
			HashMap<PeerInfo, Long> hashMap = new HashMap<PeerInfo, Long>();
			hashMap.put(peerInfo1, latency);
			peerPeerLatencyMap.put(peerInfo2, hashMap);
		}
	}
	
	/**
	 * Return the map of latencies for given peerInfo.
	 * @param peerInfo
	 * @return
	 */
	public synchronized Map<PeerInfo, Long> getLatencies(PeerInfo peerInfo) {
		return peerPeerLatencyMap.get(peerInfo);
	}
	
	/**
	 * Prints latencies as PeerInfo1, PeerInfo2, latency triplets.
	 * @param peerInfo
	 * @return
	 */
	public synchronized void printLatencies() {
		String method = "PeerPeerLatencyStore.printLatencies()";
		LogUtil.log(method, "PeerPeerLatencyStore:");
		for (PeerInfo peerInfo : peerPeerLatencyMap.keySet()) {
			Map<PeerInfo, Long> map = peerPeerLatencyMap.get(peerInfo);
			for (PeerInfo peerInfo2 : map.keySet()) {
				System.out.println(peerInfo + " # " + peerInfo2 + " # " + map.get(peerInfo2));
			}
		}
	}
}
