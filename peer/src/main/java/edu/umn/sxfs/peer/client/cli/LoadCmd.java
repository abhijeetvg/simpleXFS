package edu.umn.sxfs.peer.client.cli;

import java.rmi.RemoteException;

import edu.umn.sxfs.common.exception.IllegalIPException;
import edu.umn.sxfs.common.exception.PeerNotConnectedException;
import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.peer.client.PeerClient;
import edu.umn.sxfs.peer.client.exceptions.ClientGeneralException;

/**
 * Get Load on the specified peer.
 *
 * @author Abhijeet
 */
public class LoadCmd extends BaseCommand {

    private static final String CLASS_NAME = LoadCmd.class.getSimpleName();

    private static final int PEER_PORT_ARG = 2;
    private static final int PEER_IP_ARG = 1;

    public LoadCmd(String cmd) {
		super(cmd);
	}

    @Override
    public boolean execute() throws RemoteException, ClientGeneralException  {

        final String method = CLASS_NAME + ".execute()";

        try {

            PeerInfo pInfo = null;
            if (argExists(PEER_IP_ARG)) {
                try {
                    pInfo = new PeerInfo(getArgument(PEER_IP_ARG), Integer.parseInt(getArgument(PEER_PORT_ARG)));
                } catch (IllegalIPException e) {
                    throw new ClientGeneralException(LogUtil.causedBy(e));
                }
            }

            LogUtil.log(method, "Load : " + PeerClient.getInstance().getClient().getLoad(pInfo));
        } catch (PeerNotConnectedException e) {
            throw new ClientGeneralException(LogUtil.causedBy(e));
        }

        return true;
    }
}
