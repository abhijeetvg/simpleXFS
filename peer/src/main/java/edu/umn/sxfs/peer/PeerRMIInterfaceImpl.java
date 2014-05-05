package edu.umn.sxfs.peer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import edu.umn.sxfs.common.fileio.FileMemoryObject;
import edu.umn.sxfs.common.rmi.PeerRMIInterface;
import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.common.util.FileIOUtil;
import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.common.util.MD5CheckSumUtil;
import edu.umn.sxfs.peer.file.FileStore;
import edu.umn.sxfs.peer.latency.PeerPeerLatencyStore;

/**
 * The implementation of the PeerRMIInterface.
 * @author prashant
 *
 */

public final class PeerRMIInterfaceImpl extends UnicastRemoteObject implements PeerRMIInterface {
	private static final long serialVersionUID = -4387586179613570710L;
	private final static String CLASS_NAME = PeerRMIInterfaceImpl.class.getSimpleName(); 
	private static PeerRMIInterfaceImpl instance = null;
	
	private PeerRMIInterfaceImpl() throws RemoteException{
		if(instance != null) {
			throw new IllegalStateException("Cannot instantiate PeerRMIInterfaceImpl again.");
		}
	}
	
	public static PeerRMIInterfaceImpl getInstance() {
		final String method = CLASS_NAME + ".getInstance()";
		if(instance == null) {
			synchronized (PeerRMIInterfaceImpl.class) {
				if(instance == null) {
					try {
						instance = new PeerRMIInterfaceImpl();
					} catch (RemoteException e) {
						LogUtil.log(method, "Remote Exception while creating PeerRMIInterfaceImpl. Exiting.");
						System.exit(1);
					}
				}
			}
		}
		return instance;
	}
	
	@Override
	public FileMemoryObject download(PeerInfo requesterPeerInfo, String filename) throws RemoteException {
		LoadCounter.getLoad().increaseLoad();
		if(!FileStore.getInstance().containsFile(filename)) {
			throw new RemoteException("File : "  + filename +" not found.", new FileNotFoundException());
		}
		FileMemoryObject readFileMemoryObject = null;
		try {
			 readFileMemoryObject = FileIOUtil.readFile(FileStore.getInstance().getFileStoreDirectory() + filename);
		}catch(IOException ex){
			throw new RemoteException("IOException", ex);
		}
		
		try {
			Thread.sleep(PeerPeerLatencyStore.getInstance().getLatency(Peer.getCurrentPeerInfo(), requesterPeerInfo));
		} catch (InterruptedException e) {
			throw new RemoteException("Got interrupetedException while waiting.",e);
		}
		LoadCounter.getLoad().decreaseLoad();
		return new FileMemoryObject(filename, readFileMemoryObject.getBytecontents());
	}

	@Override
	public synchronized int getLoad() throws RemoteException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		return LoadCounter.getLoad().value();
	}

	@Override
	public byte[] getChecksum(String filename) throws RemoteException {
		String completeFileName = FileStore.getInstance().getFileStoreDirectory() + filename;
		try {
			return MD5CheckSumUtil.createChecksum(completeFileName);
		} catch (IOException e) {
			throw new RemoteException("Got IOException", e);
		}
	}

	@Override
	public void mendFile(FileMemoryObject correctFile) throws RemoteException {
		LoadCounter.getLoad().increaseLoad();
		FileMemoryObject writeFileMemoryObject = new FileMemoryObject(PeerConfig.getFileStoreDirectory() + correctFile.getFilename(), correctFile.getBytecontents());
		try {
			FileIOUtil.writeToDisk(writeFileMemoryObject);
		} catch (IOException e) {
			throw new RemoteException("Got IOException", e);
		}
		LoadCounter.getLoad().decreaseLoad();
	}
}
