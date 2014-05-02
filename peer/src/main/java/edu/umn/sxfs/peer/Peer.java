package edu.umn.sxfs.peer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import edu.umn.sxfs.common.constants.RMIConstants;
import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.common.validator.ContentValidator;
import edu.umn.sxfs.server.TrackingServerImpl;

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
	private static TrackingServerImpl trackingServerRMIObjectHandle = null;

	public static void main(String[] args) {
		final String method = CLASS_NAME + ".main()";
		
		if(args.length != 4) {
			LogUtil.log(method, "Usage peer <current peer ip> <current peer port> <tracking server ip> <tracking server port>");
			return;
		}

		// Current ip and port
		if(!ContentValidator.isValidIp(args[0])) {
			LogUtil.log(method, "Invalid ip:" + args[0]);
			return;
		}
		currentPeerIp = args[0];
		if(!ContentValidator.isValidPort(args[1])) {
			LogUtil.log(method, "Invalid port:" + args[0]);
			return;
		}
		currentPeerPort = Integer.parseInt(args[1]);
		
		// tracking server Ip and port
		if(!ContentValidator.isValidIp(args[2])) {
			LogUtil.log(method, "Invalid ip:" + args[2]);
			return;
		}
		trackingServerIp = args[2];

		if(!ContentValidator.isValidPort(args[3])) {
			LogUtil.log(method, "Invalid port:" + args[3]);
			return;
		}
		trackingServerRMIPort = Integer.parseInt(args[3]);
		
		System.setProperty("java.rmi.server.hostname", currentPeerIp);
		
		LogUtil.log(method, "Getting the Tracking server object at " + trackingServerIp + ":" + trackingServerRMIPort);
		
		try {
				trackingServerRMIObjectHandle = (TrackingServerImpl) Naming
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
	}
	
	public TrackingServerImpl getTrackingServerRMIObjectHandler() {
		return trackingServerRMIObjectHandle;
	}
}
