package edu.umn.sxfs.peer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;

import edu.umn.sxfs.common.fileio.FileMemoryObject;
import edu.umn.sxfs.common.rmi.PeerRMIInterface;
import edu.umn.sxfs.common.util.FileIOUtil;
import edu.umn.sxfs.common.util.MD5CheckSumUtil;
import edu.umn.sxfs.peer.file.FileStore;

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
	public FileMemoryObject download(String filename) throws RemoteException {
		synchronized (loadLock) {
			load++;
		}
		if(!FileStore.getInstance().containsFile(filename)) {
			throw new RemoteException("File : "  + filename +" not found.", new FileNotFoundException());
		}
		FileMemoryObject readFile = null;
		try {
			 readFile = FileIOUtil.readFile(filename);
		}catch(IOException ex){
			throw new RemoteException("IOException", ex);
		}
		synchronized (loadLock) {
			load--;
		}
		return readFile;
	}

	@Override
	public int getLoad() throws RemoteException {
		synchronized (loadLock) {
			return load;	
		}
	}

	@Override
	public byte[] getChecksum(String filename) throws RemoteException {
		String completeFileName = FileStore.getInstance().getFileStoreDirectory() + filename;
		try {
			return MD5CheckSumUtil.createChecksum(completeFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
