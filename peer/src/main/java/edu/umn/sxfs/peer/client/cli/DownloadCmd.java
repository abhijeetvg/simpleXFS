package edu.umn.sxfs.peer.client.cli;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.umn.sxfs.common.exception.IllegalIPException;
import edu.umn.sxfs.common.exception.PeerNotConnectedException;
import edu.umn.sxfs.common.exception.TrackingServerNotConnectedException;
import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.peer.client.PeerClient;
import edu.umn.sxfs.peer.client.exceptions.ClientGeneralException;

/**
 *
 * Download a file from specified peer.
 *
 * Created by Abhijeet on 3/30/2014.
 */
public class DownloadCmd extends BaseCommand {

    private static final String CLASS_NAME = DownloadCmd.class.getSimpleName();

    private static final int FILE_NAME_ARG = 1;
    private static final int PEER_PORT_ARG = 3;
    private static final int PEER_IP_ARG = 2;

    public DownloadCmd(String cmd) {
        super(cmd);
    }

    @Override
    public boolean execute() throws RemoteException, ClientGeneralException  {

        final String method = CLASS_NAME + ".execute()";

        Runnable task = new Runnable() {
			@Override
			public void run() {
		        try {
					download(method);
				} catch (ClientGeneralException e) {
					exceptionHandler(e);
				}
				
			}

			private void download(final String method)
					throws ClientGeneralException {
				try {
		            PeerInfo pInfo = null;
		            if (argExists(PEER_IP_ARG)) {
		                try {
		                    pInfo = new PeerInfo(getArgument(PEER_IP_ARG), Integer.parseInt(getArgument(PEER_PORT_ARG)));
		                } catch (IllegalIPException e) {
		                    throw new ClientGeneralException(LogUtil.causedBy(e));
		                }
		            }

		            LogUtil.log(method, "File downloaded: " + PeerClient.getInstance().getClient().download(pInfo
		                    , getArgument(FILE_NAME_ARG)));
		            LogUtil.log(method, "Download successful");

		        } catch (PeerNotConnectedException e) {
		            throw new ClientGeneralException(LogUtil.causedBy(e));
		        } catch (TrackingServerNotConnectedException e) {
		        	throw new ClientGeneralException(LogUtil.causedBy(e));
		        }
			}
		};
		
		ExecutorService service = Executors.newFixedThreadPool(100);
		service.submit(task);

        return true;
    }
    
    private void exceptionHandler(ClientGeneralException ex) {
    	
    }
}
