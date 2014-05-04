package edu.umn.sxfs.peer.client.cli;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.umn.sxfs.common.exception.IllegalIPException;
import edu.umn.sxfs.common.exception.PeerNotConnectedException;
import edu.umn.sxfs.common.exception.TrackingServerNotConnectedException;
import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.peer.LoadCounter;
import edu.umn.sxfs.peer.client.PeerClient;
import edu.umn.sxfs.peer.client.exceptions.ClientGeneralException;

/**
 *
 * Download a file from specified peer.
 *
 * Created by Abhijeet on 3/30/2014.
 */
public class DownloadCmd extends BaseCommand {
    private static final int FILE_NAME_ARG = 1;
    private static final int PEER_PORT_ARG = 3;
    private static final int PEER_IP_ARG = 2;

    public DownloadCmd(String cmd) {
        super(cmd);
    }

    @Override
    public boolean execute() throws RemoteException, ClientGeneralException  {

        Runnable task = new Runnable() {
			@Override
			public void run() {
				LoadCounter.getLoad().increaseLoad();
		        try {
					download();
				} catch (ClientGeneralException e) {
					exceptionHandler(e);
				}
		        LoadCounter.getLoad().decreaseLoad();
			}

			private void download()
					throws ClientGeneralException {
				try {
					long start = System.nanoTime();
					String filename = getArgument(FILE_NAME_ARG);
		            PeerInfo pInfo = null;
		            LogUtil.info("Downloading : (" + filename + ")");
		            if (argExists(PEER_IP_ARG)) {
		                try {
		                    pInfo = new PeerInfo(getArgument(PEER_IP_ARG), Integer.parseInt(getArgument(PEER_PORT_ARG)));
		                } catch (IllegalIPException e) {
		                    throw new ClientGeneralException(LogUtil.causedBy(e));
		                }
		            }
		            LogUtil.info("Downloading : (" + filename + ")  on peer:" + pInfo);
		            
					LogUtil.info("\n\nFile downloaded: " + PeerClient.getInstance().getClient().download(pInfo
		                    , filename));
		            long time =  (long)((System.nanoTime() - start)/Math.pow(10,6));
		            String message = "Download successful for (" + filename + ") in " + time + "ms";
		            PeerClient.printOnShell(message);

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
    	LogUtil.info("Exception while downloading file:" +ex.getCause());
    }
}
