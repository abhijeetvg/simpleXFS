package edu.umn.sxfs.common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.umn.sxfs.common.fileio.FileMemoryObject;
import edu.umn.sxfs.common.server.PeerInfo;

/**
 * RMI specification for Peers. Peers will communicate with each other to get
 * the files in the System. Specifies following three methods: <br>
 * <ul>
 *      <li>download: download the file from the peer</li>
 *      <li>getLoad: get the load of the current peer.</li>
 *      <li>getChecksum: return the checksum of the file.</li>
 * </ul>
 *  
 * @author prashant
 *
 */
public interface PeerRMIInterface extends Remote{
	
	/**
	 * Returns the file with the corresponding file name.
	 * @param filename
	 * @return
	 * @throws RemoteException
	 */
	// TODO prashant discuss on the return type. Mostly will need a different object to store files.
	public FileMemoryObject download(PeerInfo requesterPeerInfo, String filename) throws RemoteException;

	/**
	 * Returns the number of threads running on the current server.
	 * @return
	 * @throws RemoteException
	 */
	public int getLoad() throws RemoteException;
	
	/**
	 * Returns the check sum  of the passed filename.
	 * @param filename
	 * @return
	 * @throws RemoteException
	 */
	public byte[] getChecksum(String filename) throws RemoteException; 
}
