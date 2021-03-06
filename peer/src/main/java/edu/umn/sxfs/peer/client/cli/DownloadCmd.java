package edu.umn.sxfs.peer.client.cli;

import java.io.FileNotFoundException;
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
import edu.umn.sxfs.peer.Peer;
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
    private static final ExecutorService service = Executors.newFixedThreadPool(100); 

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
		        PeerClient.printOnShell("");
		        LoadCounter.getLoad().decreaseLoad();
			}

			private void download()
					throws ClientGeneralException {
				while(true) {
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
			            	if(destinationPeerInfo == null) {
			            		LogUtil.info("Cannot select peer to download file. Try again.");
			            		return;
			            	}
			            }
			            if(Peer.getCurrentPeerInfo().equals(destinationPeerInfo)) {
			            	LogUtil.info("Please give a different peer ip and port for download");
			            	return;
			            }
			            LogUtil.info("Downloading : (" + filename + ")  from peer:" + destinationPeerInfo);
			            
						
						try {
							LogUtil.info("\nFile downloaded: " + client.download(destinationPeerInfo, filename));
						} catch (PeerNotConnectedException e2) {
							handlePeerNotFoundException(destinationPeerInfo,
									client);
							continue;
						}
			            
		            	PeerClient.printOnShell("Matching checksums now.");
		            	byte[] localCheckSum = null;
						try {
							localCheckSum = client.getCheckSum(null, filename);
						} catch (PeerNotConnectedException e1) {
							// DO nothing
							e1.printStackTrace();
						}
		            	byte[] remoteCheckSum = null;
						try {
							remoteCheckSum = client.getCheckSum(destinationPeerInfo, filename);
						} catch (PeerNotConnectedException e1) {
							handlePeerNotFoundException(destinationPeerInfo,
									client);
							continue;
						}
		            	if(!MD5CheckSumUtil.isEqualCheckSum(localCheckSum, remoteCheckSum)) {
		            		PeerClient.printOnShell("CheckSum check failed. Please Download again from this or different peer.");
				            PeerClient.printOnShell("Download failed for (" + filename + "). Please try again.");
				            return;
		            	}else{
		            		PeerClient.printOnShell("CheckSum check Successful.");
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
			            	if(peerInfo.equals(Peer.getCurrentPeerInfo())) {
			            		continue;
			            	}
			            	byte[] checkSum = null;
			            	try {
			            		 checkSum = client.getCheckSum(peerInfo, filename);
			            	}
							catch (PeerNotConnectedException ex) {
								handlePeerNotFoundException(destinationPeerInfo,
										client);
								continue;
							}
							String hex = MD5CheckSumUtil.toHex(checkSum);
							if(checkSumPeerInfosMap.containsKey(hex)) {
								checkSumPeerInfosMap.get(hex).add(peerInfo);
							} else {
								Set<PeerInfo> peerInfos = new HashSet<PeerInfo>();
								peerInfos.add(peerInfo);
								checkSumPeerInfosMap.put(hex, peerInfos);
							}
						}
			            LogUtil.info("Hex map " + checkSumPeerInfosMap);
			            // Sort the map according to the set size so as to get the majority
			            List<Set<PeerInfo>> list = new ArrayList<Set<PeerInfo>>();
			            list.addAll(checkSumPeerInfosMap.values());
			            Collections.sort(list, new SetSizeComparator());
			            
			            Set<PeerInfo> majorityPeerInfoSet = list.get(0);
			            LogUtil.info("Majority set is " + majorityPeerInfoSet);
			            if(majorityPeerInfoSet.contains(destinationPeerInfo)) {
			            	LogUtil.info("The file from PeerInfo:" + destinationPeerInfo + " is having the checksum in the majority. So, we have the right file.");
			            	break;
			            }
			            LogUtil.info("The file from PeerInfo:" + destinationPeerInfo + " is NOT having the checksum in the majority.");
			            LogUtil.info("Downloading the file again from majority peer");
			            
			            List<PeerInfo> majorityPeerInfoList = new ArrayList<PeerInfo>(majorityPeerInfoSet);
			            PeerInfo majorityDestinationPeerInfo = majorityPeerInfoList.get(0);
			            LogUtil.info("Downloading : (" + filename + ")  from peer:" + majorityDestinationPeerInfo);
			            try {
							LogUtil.info("\nFile downloaded: " + client.download(majorityDestinationPeerInfo, filename));
						} catch (PeerNotConnectedException e1) {
							handlePeerNotFoundException(destinationPeerInfo,
									client);
							continue;
						}
			            
						
						LogUtil.info("\nSending correct file to PeerInfo + " + destinationPeerInfo);
						try {
							client.mendFile(destinationPeerInfo, filename);
						} catch (PeerNotConnectedException e) {
							handlePeerNotFoundException(destinationPeerInfo,
									client);
							continue;
						}
						LogUtil.info("\nDONE Sending correct file to PeerInfo + " + destinationPeerInfo);
						String message = "Download successful for (" + filename + ") from " + majorityDestinationPeerInfo;
						PeerClient.printOnShell(message);
			        } catch (TrackingServerNotConnectedException e) {
			        	LogUtil.info("Lost connection to Tracking Server. Reconnecting.");
			        	Peer.refreshConnectionToTrackingServer();
			        	LogUtil.info("Connected to tracking server. Retrying download.");
			        	continue;
			        } catch (FileNotFoundException e) {
			        	throw new ClientGeneralException(LogUtil.causedBy(e));
			        }
					break;
				}
			}

			public void handlePeerNotFoundException(
					PeerInfo destinationPeerInfo,
					PeerServerInterfaceObject client) {
				PeerClient.printOnShell(destinationPeerInfo + " down. Removing it from tracking server.");
				client.removePeer(destinationPeerInfo);
				PeerClient.printOnShell("Removed dead peer info : " + destinationPeerInfo + ".Retrying.");
			}
		};
		
		service.submit(task);
        return true;
    }
    
    private void exceptionHandler(ClientGeneralException ex) {
    	LogUtil.info("Exception while downloading file:");
    	ex.printStackTrace();
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
				return -1;
			}
			return 1;
		}
    }
}
