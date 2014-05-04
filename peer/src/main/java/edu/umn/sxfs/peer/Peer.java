package edu.umn.sxfs.peer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import edu.umn.sxfs.common.constants.RMIConstants;
import edu.umn.sxfs.common.exception.IllegalIPException;
import edu.umn.sxfs.common.exception.WrongPropertiesFileFormatException;
import edu.umn.sxfs.common.rmi.TrackingServer;
import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.common.validator.ContentValidator;
import edu.umn.sxfs.peer.file.FileStore;
import edu.umn.sxfs.peer.latency.PeerPeerLatencyStore;
import edu.umn.sxfs.peer.latency.WrongPeerLatencyConfigFileException;

/**
 * The starting point for the peer server. 
 * Takes the following arguments :
 * <br>
 * <ul>
 * 		<li>Current Peer Ip.</li>
 * 		<li>Current Peer Port.</li>
 *      <li>Tracking server ip.</li>
 *      <li>Tracking server RMI port.</li>
 * </ul>
 * 
 * @author prashant
 *
 */
public class Peer {
	private static final String CLASS_NAME = Peer.class.getSimpleName();
	private static PeerInfo currentPeerInfo = null;
	private static TrackingServer trackingServerRMIObjectHandle = null;
	
	private Peer() {
		throw new IllegalStateException("Cannot instantiate peer class"); 
	}

	/**
	 * Retursn false if the peer didnt start correctly.
	 * @param args
	 * @return
	 */
	public static boolean start(String[] args) {
		final String method = CLASS_NAME + ".start()";
		
		if(args.length != 3) {
			LogUtil.log(method, "Usage peer <current peer ip> <current peer port> <properties config file>");
			return false;
		}

		try {
			PeerConfig.loadProperties(args[2]);
		} catch (FileNotFoundException e2) {
			LogUtil.log(method, "Got exception " + e2.getMessage()
					+ ". Exiting.");
			System.exit(1);
		} catch (WrongPropertiesFileFormatException e2) {
			LogUtil.log(method, "Got exception " + e2.getMessage()
					+ ". Exiting.");
			System.exit(1);
		}
		
		// Current ip and port
		if(!ContentValidator.isValidIp(args[0])) {
			LogUtil.log(method, "Invalid ip:" + args[0]);
			return false;
		}
		if(!ContentValidator.isValidPort(args[1])) {
			LogUtil.log(method, "Invalid port:" + args[1]);
			return false;
		}
		try {
			currentPeerInfo = new PeerInfo(args[0], Integer.parseInt(args[1]));
		} catch (NumberFormatException e2) {
			LogUtil.log(method, "Invalid port:" + args[1]);
			return false;
		} catch (IllegalIPException e2) {
			LogUtil.log(method, "Invalid ip:" + args[0]);
			return false;
		}
		
		String fileStoreDirectory = PeerConfig.getFileStoreDirectory();
		LogUtil.log(method, "Initializing fileStore: " + fileStoreDirectory);
		FileStore.getInstance().initialize(fileStoreDirectory);
		FileStore.getInstance().printStore();
		LogUtil.log(method, "DONE Initializing fileStore");
		
		System.setProperty("java.rmi.server.hostname", currentPeerInfo.getIp());
		
		int trackingServerPort = PeerConfig.getTrackingServerPort();
		String trackingServerIp = PeerConfig.getTrackingServerIp();
		LogUtil.log(method, "Getting the Tracking server object at " + trackingServerIp + ":" + trackingServerPort);
		
		try {
				trackingServerRMIObjectHandle = (TrackingServer) Naming
						.lookup("rmi://"
								+ trackingServerIp + ":"
								+ trackingServerPort
								+ "/" + RMIConstants.TRACKING_SERVER_SERVICE);
		} catch (MalformedURLException e) {
			LogUtil.log(method, "Got exception " + e.getMessage()
					+ ". Exiting.");
			System.exit(1);
		} catch (RemoteException e) {
			LogUtil.log(method, "Got exception " + e.getMessage()
					+ ". Exiting.");
			System.exit(1);
		} catch (NotBoundException e) {
			LogUtil.log(method, "Got exception " + e.getMessage()
					+ ". Exiting.");
			System.exit(1);
		}
		LogUtil.log(method, "DONE getting the trackingServerObject");
		
		LogUtil.log(method, "Updating initial file list on the server");
		try {
			trackingServerRMIObjectHandle.updateFiles(currentPeerInfo, FileStore.getInstance()
					.getFilenames());
		} catch (RemoteException e1) {
			LogUtil.log(method, "Got exception " + e1.getMessage()
					+ ". Exiting.");
			System.exit(1);
		} 
		LogUtil.log(method, "DONE Updating initial file list on the server");
		
		String peerPeerLatencyFile = PeerConfig.getPeerPeerLatencyFile();
		LogUtil.log(method, "Initializing Peer-peer latency store from file : " + peerPeerLatencyFile);
		try {
			PeerPeerLatencyStore.getInstance().initialize(peerPeerLatencyFile);
		} catch (IOException e1) {
			LogUtil.log(method, "Got exception while initializing PeerPeerLatencyStore : " + e1.getMessage() + " Exiting.");
			System.exit(1);
		} catch (WrongPeerLatencyConfigFileException e1) {
			LogUtil.log(method, "Got exception while initializing PeerPeerLatencyStore : " + e1.getMessage() + " Exiting.");
			System.exit(1);
		}
		LogUtil.log(method, "DONE Initializing Peer-peer latency store from file : " + args[5]);
		
		LogUtil.log(method, "Binding the current peer to RMI at " + currentPeerInfo);
		try {
			LocateRegistry.createRegistry(currentPeerInfo.getPort());
			Naming.rebind(RMIConstants.PEER_SERVICE,
					PeerRMIInterfaceImpl.getInstance());
		} catch (RemoteException e) {
			LogUtil.log(method, "Got exception " + e.getMessage()
					+ ". Exiting.");
			System.exit(1);
		} catch (MalformedURLException e) {
			LogUtil.log(method, "Got exception " + e.getMessage()
					+ ". Exiting.");
			System.exit(1);
		}
		LogUtil.log(method, "DONE Binding " + RMIConstants.PEER_SERVICE);
		return true;
	}
	
	public static TrackingServer getTrackingServerRMIObjectHandler() {
		return trackingServerRMIObjectHandle;
	}
	
	public static PeerInfo getCurrentPeerInfo(){
		return currentPeerInfo;
	}
}
