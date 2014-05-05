package edu.umn.sxfs.peer;

import java.rmi.RemoteException;
import java.util.concurrent.Callable;

import edu.umn.sxfs.peer.client.PeerClient;
import edu.umn.sxfs.peer.file.FileStore;

/**
 * Calls the updateList to update the files in local store periodically.
 * @author prashant
 *
 */
public final class PeriodicUpdateListThread implements Callable<Boolean> {

	private final long UPDATE_INTERVAL = 5 * 1000; 
	
	@Override
	public Boolean call() throws Exception {
		while(true) {
			Thread.sleep(UPDATE_INTERVAL);
			PeerClient.printOnShell("Updating the file list on server.");
			FileStore.getInstance().initialize(PeerConfig.getFileStoreDirectory());
			try {
				Peer.getTrackingServerRMIObjectHandler().updateFiles(Peer.getCurrentPeerInfo(), FileStore.getInstance()
						.getFilenames());
			} catch (RemoteException e1) {
				PeerClient.printOnShell("Got remoteexception while updating files on server. Will try again.");
				continue;
			} 
		}
	}
}
