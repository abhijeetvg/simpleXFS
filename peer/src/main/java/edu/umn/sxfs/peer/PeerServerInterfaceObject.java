package edu.umn.sxfs.peer;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import edu.umn.sxfs.common.exception.IllegalIPException;
import edu.umn.sxfs.common.exception.PeerNotConnectedException;
import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.common.util.RMIUtil;

/**
 * The interface object. This object that will connect the peer client and 
 * server.
 * The object hides all the details from the client.
 * @author prashant
 *
 */

public final class PeerServerInterfaceObject {

	private PeerServerInterfaceObject instance = null;
	
	private PeerServerInterfaceObject() {
		if(instance != null) {
			throw new IllegalStateException("Cannot instantiate this object twice.");
		}
	}
	
	public PeerServerInterfaceObject getInstance() {
		if(instance == null) {
			synchronized (PeerServerInterfaceObject.class) {
				if(instance == null) {
					instance = new PeerServerInterfaceObject();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Download the given file form the peer.
	 * @param peerInfo
	 * @param filename
	 * @return
	 * @throws PeerNotConnectedException 
	 */
	public boolean download(PeerInfo peerInfo, String filename) throws PeerNotConnectedException {
		PeerRMIInterfaceImpl peerRMIInterfaceImplObject = RMIUtil.getPeerRMIInterfaceImplObject(peerInfo.getIp(), peerInfo.getPort());
		if(peerRMIInterfaceImplObject == null) {
			throw new PeerNotConnectedException("Cannot connect to peer : " + peerInfo );
		}
		try {
			peerRMIInterfaceImplObject.download(filename);
		} catch (RemoteException e) {
			// TODO Depending on the cause of RemoteException throw appropriate exception or return false.
			e.printStackTrace();
		}
		return true;
	}
	
	public Set<PeerInfo> find (String filename) {
		try {
			return Peer.getTrackingServerRMIObjectHandler().find(filename);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new HashSet<PeerInfo>();
	}
	
	/**
	 * Updates the metadata on the server for this server.
	 * @param filename
	 * @return
	 */
	public boolean updateListOnServer(String filename) {
		Set<String> filenames = new HashSet<String>();
		filenames.add(filename);
		try {
			try {
				Peer.getTrackingServerRMIObjectHandler().updateFiles(new PeerInfo(Peer.getIp(), Peer.getPort()), filenames);
			} catch (IllegalIPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the check sum for the file on the peer.
	 * @param peerInfo
	 * @param filename
	 * @return
	 * @throws PeerNotConnectedException 
	 */
	public int getCheckSum(PeerInfo peerInfo, String filename) throws PeerNotConnectedException {
		PeerRMIInterfaceImpl peerRMIInterfaceImplObject = RMIUtil.getPeerRMIInterfaceImplObject(peerInfo.getIp(), peerInfo.getPort());
		if(peerRMIInterfaceImplObject == null) {
			throw new PeerNotConnectedException("cannot connect to Peer: " + peerInfo);
		}
		try {
			return peerRMIInterfaceImplObject.getChecksum(filename);
		} catch (RemoteException e) {
			// TODO Depending on the cause of RemoteException throw appropriate exception or return false.
		}
		return -1;
	}
	
	/**
	 * Returns the load on server.
	 * @param peerInfo
	 * @return
	 * @throws PeerNotConnectedException 
	 */
	public int getLoad(PeerInfo peerInfo) throws PeerNotConnectedException {
		PeerRMIInterfaceImpl peerRMIInterfaceImplObject = RMIUtil.getPeerRMIInterfaceImplObject(peerInfo.getIp(), peerInfo.getPort());
		if(peerRMIInterfaceImplObject == null) {
			throw new PeerNotConnectedException("cannot connect to Peer: " + peerInfo);
		}
		try {
			return peerRMIInterfaceImplObject.getLoad();
		} catch (RemoteException e) {
			// TODO Depending on the cause of RemoteException throw appropriate exception or return false.
		}
		return -1;
	}
	
}
