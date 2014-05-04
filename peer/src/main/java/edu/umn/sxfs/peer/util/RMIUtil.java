package edu.umn.sxfs.peer.util;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import edu.umn.sxfs.common.constants.RMIConstants;
import edu.umn.sxfs.common.rmi.PeerRMIInterface;

/**
 * Util functions for RMI connections and get the RMI objects for given ip and
 * ports.
 * 
 * @author prashant
 * 
 */
public final class RMIUtil {

	private RMIUtil() {
		throw new IllegalStateException("Cannot instantiate util class");
	}

	/**
	 * Returns the RMI object for given peer ip and port.
	 * 
	 * <br>
	 * <b>NOTE</b> Returns null if there is an exception while binding so do a null check before using.
	 * @param ip
	 * @param port
	 * @return
	 */
	public static PeerRMIInterface getPeerRMIInterfaceImplObject(String ip,
			int port) {
		PeerRMIInterface peerObject = null;
		try {
			peerObject = (PeerRMIInterface) Naming
					.lookup("rmi://" + ip + ":"
							+ port + "/"
							+ RMIConstants.PEER_SERVICE);
		} catch (MalformedURLException e) {
			return null;
		} catch (RemoteException e) {
			return null;
		} catch (NotBoundException e) {
			return null;
		}
		return peerObject;
	}
}
