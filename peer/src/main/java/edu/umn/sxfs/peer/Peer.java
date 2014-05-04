package edu.umn.sxfs.peer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import edu.umn.sxfs.common.constants.RMIConstants;
import edu.umn.sxfs.common.exception.IllegalIPException;
import edu.umn.sxfs.common.rmi.TrackingServer;
import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.common.validator.ContentValidator;
import edu.umn.sxfs.peer.file.FileStore;

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
	private static String trackingServerIp = null;
	private static int trackingServerRMIPort = RMIConstants.RMI_DEFAULT_PORT;
	private static String currentPeerIp = null;
	private static int currentPeerPort = RMIConstants.RMI_DEFAULT_PORT;
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
		
		if(args.length != 5) {
			LogUtil.log(method, "Usage peer <current peer ip> <current peer port> <tracking server ip> <tracking server port> <local file store>");
			return false;
		}

		// Current ip and port
		if(!ContentValidator.isValidIp(args[0])) {
			LogUtil.log(method, "Invalid ip:" + args[0]);
			return false;
		}
		currentPeerIp = args[0];
		if(!ContentValidator.isValidPort(args[1])) {
			LogUtil.log(method, "Invalid port:" + args[0]);
			return false;
		}
		currentPeerPort = Integer.parseInt(args[1]);
		
		// tracking server Ip and port
		if(!ContentValidator.isValidIp(args[2])) {
			LogUtil.log(method, "Invalid ip:" + args[2]);
			return false;
		}
		trackingServerIp = args[2];

		if(!ContentValidator.isValidPort(args[3])) {
			LogUtil.log(method, "Invalid port:" + args[3]);
			return false;
		}
		trackingServerRMIPort = Integer.parseInt(args[3]);
		
		LogUtil.log(method, "Initializing fileStore");
		FileStore.getInstance().initialize(args[4]);
		FileStore.getInstance().printStore();
		LogUtil.log(method, "DONE Initializing fileStore");
		
		System.setProperty("java.rmi.server.hostname", currentPeerIp);
		
		LogUtil.log(method, "Getting the Tracking server object at " + trackingServerIp + ":" + trackingServerRMIPort);
		
		try {
				trackingServerRMIObjectHandle = (TrackingServer) Naming
						.lookup("rmi://"
								+ trackingServerIp + ":"
								+ trackingServerRMIPort
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
			trackingServerRMIObjectHandle.updateFiles(new PeerInfo(
					currentPeerIp, currentPeerPort), FileStore.getInstance()
					.getFilenames());
		} catch (RemoteException e1) {
			LogUtil.log(method, "Got exception " + e1.getMessage()
					+ ". Exiting.");
			System.exit(1);
		} catch (IllegalIPException e1) {
			LogUtil.log(method, "Got exception " + e1.getMessage()
					+ ". Exiting.");
			System.exit(1);
		}
		LogUtil.log(method, "DONE Updating initial file list on the server");
		
		LogUtil.log(method, "Binding the current peer to RMI at " + currentPeerIp + ":" + currentPeerPort);
		try {
			LocateRegistry.createRegistry(currentPeerPort);
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
	
	public static String getIp() {
		return currentPeerIp;
	}
	
	public static int getPort() {
		return currentPeerPort;
	}
}
