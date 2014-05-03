package edu.umn.sxfs.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.umn.sxfs.common.constants.RMIConstants;
import edu.umn.sxfs.common.rmi.TrackingServer;
import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.common.validator.ContentValidator;

/**
 * Tracking Server implementation.
 *
 * Created by Abhijeet on 4/28/2014.
 */
public class TrackingServerImpl implements TrackingServer {

    private static final String CLASS_NAME = Server.class.getSimpleName();

    /**
     * map: Server -> Set of Files
     */
    private Map<PeerInfo, Set<String>> metaData = new HashMap<PeerInfo, Set<String>>();
    private String rmiIP;
    private int rmiPort;
    private static boolean isInitialized = false;

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

    public synchronized void startServer(String[] args) {
    	if(isInitialized) {
    		return;
    	}
        final String method = CLASS_NAME + ".startServer()";

        if (!ContentValidator.isValidIp(args[0])) {
            LogUtil.log(method, args[0] + " is not a valid IP.");
            return;
        }
        rmiIP = args[0];

        if(!ContentValidator.isValidPort(args[1])) {
            LogUtil.log(method, args[1] + " is not a valid Port.");
            return;
        }
        rmiPort = Integer.parseInt(args[1]);

        System.setProperty("java.rmi.server.hostname", rmiIP);
        LogUtil.log(method, "Starting server on " + rmiIP + ":" + rmiPort);
        LogUtil.log(method, "Binding " + RMIConstants.TRACKING_SERVER_SERVICE);

        try {
            LocateRegistry.createRegistry(rmiPort);
            Naming.rebind(RMIConstants.TRACKING_SERVER_SERVICE, TrackingServerImpl.getRawInstance());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        LogUtil.log(method, "DONE Binding " + RMIConstants.TRACKING_SERVER_SERVICE);
        isInitialized = true;
    }

    /**
     * Usage: ip port
     * @param args
     */
    public static void main(String[] args) {

        final String method = CLASS_NAME + ".main()";

        if (2 != args.length) {
            LogUtil.log(method, "Invalid cli arguments. Usage server <server ip> " +
                    "<server rmi port> <Configuration filename>");
            return;
        }

        TrackingServerImpl.getRawInstance().startServer(args);
    }
}
