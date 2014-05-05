package edu.umn.sxfs.peer;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.common.util.LogUtil;

/**
 * Caches the files information from tracking server.
 * @author prashant
 *
 */
public final class FileMetaDataCache {
	private static final String CLASS_NAME = FileMetaDataCache.class.getSimpleName();

    /**
     * map: File -> PeerInfos
     */
    private Map<String, Set<PeerInfo>> metaData = new HashMap<String, Set<PeerInfo>>();
    private static FileMetaDataCache instance = null;

    private FileMetaDataCache() throws RemoteException{
    	if(instance != null) {
    		throw new IllegalStateException("Cannot instantiate this class twice.");
    	}
    }

    public synchronized static FileMetaDataCache getInstance() {
    	final String method = CLASS_NAME + ".getRawInstance()";
        if (null == instance) {
            try {
				instance = new FileMetaDataCache();
			} catch (RemoteException e) {
				LogUtil.log(method, "Remote Exception while creating FileMetaDataCache. Exiting.");
				System.exit(1);
			}
        }
        return instance;
    }

    public synchronized Set<PeerInfo> find(String fileName) {
    	return metaData.get(fileName);
    }

    public synchronized void updatePeerInfos(String filename, Set<PeerInfo> peerInfos) {
        metaData.remove(filename);
        metaData.put(filename, peerInfos);
    }
}
