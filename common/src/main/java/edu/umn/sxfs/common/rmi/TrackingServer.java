package edu.umn.sxfs.common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import edu.umn.sxfs.common.server.PeerInfo;

/**
 *
 * RMI specification for Tracking Server. Peers will communicate with this server to get
 * meta data about the files in the System. Specifies following two methods: <br>
 * <ul>
 *      <li>Find: Find the servers on which the given file recides.<li/>
 *      <li>UpdateList: Update the set of files of a particular Server.</li>
 * <ul/>
 *
 * Created by Abhijeet on 4/27/2014.
 */
public interface TrackingServer extends Remote {

    /**
     * Find the servers on which the <code>fileName</code> recides.
     *
     * @param fileName
     * @return Set of Servers
     * @throws RemoteException
     */
    Set<PeerInfo> find(String fileName) throws RemoteException;

    /**
     * Update the set of files of a Server with ID <code>macID</code>.
     *
     * @param macID machine ID.
     * @param files all the set of files that Server has.
     * @throws RemoteException
     */
    void updateFiles(PeerInfo peerInfo, Set<String> files) throws RemoteException;

}
