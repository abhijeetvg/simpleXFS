package edu.umn.sxfs.peer;

import java.rmi.RemoteException;

import edu.umn.sxfs.common.rmi.PeerRMIInterface;

/**
 * The implementation of the PeerRMIInterface.
 * @author prashant
 *
 */
public final class PeerRMIInterfaceImpl implements PeerRMIInterface {

	private static PeerRMIInterfaceImpl instance = null;
	private static int load = 0;
	
	private static Object loadLock = new Object();
	
	private PeerRMIInterfaceImpl() {
		if(instance != null) {
			throw new IllegalStateException("Cannot instantiate PeerRMIInterfaceImpl again.");
		}
	}
	
	public static PeerRMIInterfaceImpl getInstance() {
		if(instance == null) {
			synchronized (PeerRMIInterfaceImpl.class) {
				if(instance == null) {
					instance = new PeerRMIInterfaceImpl();
				}
			}
		}
		return instance;
	}
	
	@Override
	public String download(String filename) throws RemoteException {
		synchronized (loadLock) {
			load++;
		}
		
		synchronized (loadLock) {
			load--;
		}
		return null;
	}

	@Override
	public int getLoad() throws RemoteException {
		synchronized (loadLock) {
			return load;	
		}
	}

	@Override
	public int getChecksum(String filename) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

}
