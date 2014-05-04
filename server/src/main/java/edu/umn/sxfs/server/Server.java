package edu.umn.sxfs.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import edu.umn.sxfs.common.constants.RMIConstants;
import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.common.validator.ContentValidator;

public class Server {

	private final static String CLASS_NAME = Server.class.getSimpleName();
    private static String rmiIP;
    private static int rmiPort;
	
	public static void main(String[] args) {
		final String method = CLASS_NAME + ".main()";

		if (2 != args.length) {
            LogUtil.log(method, "Invalid cli arguments. Usage server <server ip> " +
                    "<server rmi port>");
            return;
        }
		
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
	}
}
