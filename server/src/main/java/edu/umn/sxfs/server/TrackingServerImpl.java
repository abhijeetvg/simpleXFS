package edu.umn.sxfs.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.umn.sxfs.common.rmi.TrackingServer;
import edu.umn.sxfs.common.server.PeerInfo;

/**
 * Tracking Server implementation.
 *
 * Created by Abhijeet on 4/28/2014.
 */
public class TrackingServerImpl implements TrackingServer, Serializable {

	private static final long serialVersionUID = 6294308753271868256L;

    /**
     * map: Server -> Set of Files
     */
    private Map<PeerInfo, Set<String>> metaData = new HashMap<PeerInfo, Set<String>>();
    private static TrackingServerImpl instance = null;

    private TrackingServerImpl() {
    	if(instance != null) {
    		throw new IllegalStateException("Cannot instantiate this class twice.");
    	}
    }

    /**
     * First call is Raw, call <code>startup(args)</code> after this to properly initialize the state of
     * the server.
     *
     * @return singleton instance
     */
    public synchronized static TrackingServerImpl getRawInstance() {
        if (null == instance) {
            instance = new TrackingServerImpl();
        }

        return instance;
    }

    @Override
    public synchronized Set<PeerInfo> find(String fileName) throws RemoteException {

        Set <PeerInfo> servers = new HashSet<PeerInfo>();

        for (PeerInfo server : metaData.keySet()) {
            if (metaData.get(server).contains(fileName)) {
                servers.add(server);
            }
        }

        return servers;
    }

    @Override
    public synchronized void updateFiles(PeerInfo peerInfo, Set<String> files) throws RemoteException {
        metaData.remove(peerInfo);
        metaData.put(peerInfo, files);
    }
}
