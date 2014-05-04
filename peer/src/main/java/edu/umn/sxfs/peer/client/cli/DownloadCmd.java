package edu.umn.sxfs.peer.client.cli;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.umn.sxfs.common.exception.IllegalIPException;
import edu.umn.sxfs.common.exception.PeerNotConnectedException;
import edu.umn.sxfs.common.exception.TrackingServerNotConnectedException;
import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.common.util.MD5CheckSumUtil;
import edu.umn.sxfs.peer.LoadCounter;
import edu.umn.sxfs.peer.PeerConfig;
import edu.umn.sxfs.peer.PeerServerInterfaceObject;
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
    private static boolean isCheckSum = false;

    public DownloadCmd(String cmd, boolean isCheckSum) {
        super(cmd);
        DownloadCmd.isCheckSum = isCheckSum;
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
		            PeerInfo destinationPeerInfo = null;
		            PeerServerInterfaceObject client = PeerClient.getInstance().getClient();
		            LogUtil.info("Downloading : (" + filename + ")");
		            if (argExists(PEER_IP_ARG)) {
		                try {
		                    destinationPeerInfo = new PeerInfo(getArgument(PEER_IP_ARG), Integer.parseInt(getArgument(PEER_PORT_ARG)));
		                } catch (IllegalIPException e) {
		                    throw new ClientGeneralException(LogUtil.causedBy(e));
		                }
		            }else {
		            	destinationPeerInfo = client.getPeerInfoFromPeerSelectionAlgorithm(filename);
		            }
		            LogUtil.info("Downloading : (" + filename + ")  on peer:" + destinationPeerInfo);
		            
					
					LogUtil.info("\n\nFile downloaded: " + client.download(destinationPeerInfo, filename));
		            
		            if(isCheckSum) {
		            	PeerClient.printOnShell("Matching checksums now.");
		            	byte[] localCheckSum = client.getCheckSum(null, filename);
		            	byte[] remoteCheckSum = client.getCheckSum(destinationPeerInfo, filename);
		            	if(!MD5CheckSumUtil.isEqualCheckSum(localCheckSum, remoteCheckSum)) {
		            		PeerClient.printOnShell("CheckSum check failed. Please Download again from this or different peer.");
				            PeerClient.printOnShell("Download failed for (" + filename + ")");
				            return;
		            	}else{
		            		PeerClient.printOnShell("CheckSum check Successful.");
		            		long time =  (long)((System.nanoTime() - start)/Math.pow(10,6));
				            String message = "Download successful for (" + filename + ") in " + time + "ms";
				            PeerClient.printOnShell(message);
		            	}
		            }else {
		            	long time =  (long)((System.nanoTime() - start)/Math.pow(10,6));
			            String message = "Download successful for (" + filename + ") in " + time + "ms";
			            PeerClient.printOnShell(message);
		            }
		            
		            if(!PeerConfig.isByzantineMode()) {
		            	return;
		            }
		            // Apply byzantine failure mode. Check the checksum from all the peers..
		            Map<String, Set<PeerInfo>> checkSumPeerInfosMap = new TreeMap<String, Set<PeerInfo>>(); 
		            Set<PeerInfo> availablePeers = client.find(filename);
		            for (PeerInfo peerInfo : availablePeers) {
						byte[] checkSum = client.getCheckSum(peerInfo, filename);
						String hex = MD5CheckSumUtil.toHex(checkSum);
						if(checkSumPeerInfosMap.containsKey(hex)) {
							checkSumPeerInfosMap.get(hex).add(peerInfo);
						} else {
							Set<PeerInfo> peerInfos = new HashSet<PeerInfo>();
							peerInfos.add(peerInfo);
							checkSumPeerInfosMap.put(hex, peerInfos);
						}
					}
		            // Sort the map according to the set size so as to get the majority
		            List<Set<PeerInfo>> list = new ArrayList<Set<PeerInfo>>();
		            list.addAll(checkSumPeerInfosMap.values());
		            Collections.sort(list, new SetSizeComparator());
		            
		            Set<PeerInfo> majorityPeerInfoSet = list.get(0);
		            if(majorityPeerInfoSet.contains(destinationPeerInfo)) {
		            	LogUtil.info("The file from PeerInfo:" + destinationPeerInfo + " is having the checksum in the majority. So, we have the right file.");
		            	return;
		            }
		            LogUtil.info("The file from PeerInfo:" + destinationPeerInfo + " is NOT having the checksum in the majority.");
		            LogUtil.info("Downloading the file again from majority peer");
		            
		            List<PeerInfo> majorityPeerInfoList = new ArrayList<PeerInfo>(majorityPeerInfoSet);
		            destinationPeerInfo = majorityPeerInfoList.get(0);
		            LogUtil.info("Downloading : (" + filename + ")  on peer:" + destinationPeerInfo);
		            
					
					LogUtil.info("\n\nFile downloaded: " + client.download(destinationPeerInfo, filename));
					String message = "Download successful for (" + filename + ") from " + destinationPeerInfo;
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
    
    /**
     * Compares the size of the set in the value part of the map.
     * @author prashant
     *
     */
    class SetSizeComparator implements Comparator<Set<PeerInfo>> {
    	
		@Override
		public int compare(Set<PeerInfo> arg0, Set<PeerInfo> arg1) {
			if(arg0.size() > arg1.size()) {
				return 1;
			}
			return -1;
		}
    }
}
