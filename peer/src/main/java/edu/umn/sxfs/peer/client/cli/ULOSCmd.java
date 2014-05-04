package edu.umn.sxfs.peer.client.cli;

import edu.umn.sxfs.peer.client.PeerClient;
import edu.umn.sxfs.peer.client.constants.CommandConstants;
import edu.umn.sxfs.peer.client.exceptions.ClientGeneralException;
import edu.umn.sxfs.common.util.LogUtil;

import java.rmi.RemoteException;

/**
 * Update list on Server.
 *
 * Created by Abhijeet on 3/30/2014.
 */
public class ULOSCmd extends BaseCommand {

    private static final String CLASS_NAME = ULOSCmd.class.getSimpleName();

    private static final int FILE_NAME_ARG = 1;

    public ULOSCmd(String cmd) {
        super(cmd);
    }

    @Override
    public boolean execute() throws RemoteException, ClientGeneralException {

        final String method = CLASS_NAME + ".execute()";

        LogUtil.log(method, "Update list on servers: " + PeerClient.getInstance().getClient()
                .updateListOnServer(getArgument(FILE_NAME_ARG)));
        return true;
    }
}
