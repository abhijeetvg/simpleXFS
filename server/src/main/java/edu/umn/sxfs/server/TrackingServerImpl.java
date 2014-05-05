package edu.umn.sxfs.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.umn.sxfs.common.rmi.TrackingServer;
import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.common.util.LogUtil;

/**
 * Tracking Server implementation.
 *
 * Created by Abhijeet on 4/28/2014.
 */
public class TrackingServerImpl extends UnicastRemoteObject implements TrackingServer, Serializable {

	private static final String CLASS_NAME = TrackingServerImpl.class.getSimpleName();
	private static final long serialVersionUID = 6294308753271868256L;

    /**
     * map: Server -> Set of Files
     */
    private Map<PeerInfo, Set<String>> metaData = new HashMap<PeerInfo, Set<String>>();
    private static TrackingServerImpl instance = null;

    private TrackingServerImpl() throws RemoteException{
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
    	final String method = CLASS_NAME + ".getRawInstance()";
        if (null == instance) {
            try {
				instance = new TrackingServerImpl();
			} catch (RemoteException e) {
				LogUtil.log(method, "Remote Exception while creating TrackingServiceImpl. Exiting.");
				System.exit(1);
			}
        }
        return instance;
    }

    @Override
    public synchronized Set<PeerInfo> find(String fileName) throws RemoteException {
    	final String method = CLASS_NAME + ".find()";
        Set <PeerInfo> servers = new HashSet<PeerInfo>();
        LogUtil.log(method, "Finding filename: " + fileName);
        for (PeerInfo server : metaData.keySet()) {
            if (metaData.get(server).contains(fileName)) {
                servers.add(server);
            }
        }
        LogUtil.log(method, "Returning Servers : " + servers + " for filename: " + fileName);	
        return servers;
    }

    @Override
    public synchronized void updateFiles(PeerInfo peerInfo, Set<String> files) throws RemoteException {
    	final String method = CLASS_NAME + ".updateFiles()";
    	LogUtil.log(method, "Updating files : " + files + " for PeerInfo: " + peerInfo);
        metaData.remove(peerInfo);
        metaData.put(peerInfo, files);
    }

	@Override
	public synchronized void removeDeadNode(PeerInfo deadPeerInfo) throws RemoteException {
		final String method = CLASS_NAME + ".removeDeadNode()";
    	LogUtil.log(method, "Removinf dead PeerInfo: " + deadPeerInfo);
        metaData.remove(deadPeerInfo);
	}
}
