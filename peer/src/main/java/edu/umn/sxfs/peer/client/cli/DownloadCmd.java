package edu.umn.sxfs.peer.client.cli;

import edu.umn.sxfs.common.exception.IllegalIPException;
import edu.umn.sxfs.common.exception.PeerNotConnectedException;
import edu.umn.sxfs.common.server.PeerInfo;
import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.peer.client.PeerClient;
import edu.umn.sxfs.peer.client.constants.CommandConstants;
import edu.umn.sxfs.peer.client.exceptions.ClientGeneralException;

import java.rmi.RemoteException;

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

        PeerClient cli = PeerClient.getInstance();
        if (null == cli.getClient()) {
            throw new ClientGeneralException(CommandConstants.ERR_CLIENT_NULL);
        }

        try {

            PeerInfo pInfo = null;
            if (argExists(PEER_IP_ARG)) {
                try {
                    pInfo = new PeerInfo(getArgument(PEER_IP_ARG), Integer.parseInt(getArgument(PEER_PORT_ARG)));
                } catch (IllegalIPException e) {
                    throw new ClientGeneralException(LogUtil.causedBy(e));
                }
            }

            //TODO: This should return file path downloaded, change accordingly
            LogUtil.log(method, "File downloaded: " + PeerClient.getInstance().getClient().download(pInfo
                    , getArgument(FILE_NAME_ARG)));
            LogUtil.log(method, "Download successful");

        } catch (PeerNotConnectedException e) {
            throw new ClientGeneralException(LogUtil.causedBy(e));
        }

        return true;
    }
}
