package edu.umn.sxfs.peer.client.cli;

import java.rmi.RemoteException;

import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.peer.client.PeerClient;
import edu.umn.sxfs.peer.client.exceptions.ClientGeneralException;

/**
 * Update list on Server.
 *
 * Created by Abhijeet on 3/30/2014.
 */
public class ULOSCmd extends BaseCommand {

    private static final String CLASS_NAME = ULOSCmd.class.getSimpleName();

    public ULOSCmd(String cmd) {
        super(cmd);
    }

    @Override
    public boolean execute() throws RemoteException, ClientGeneralException {

        final String method = CLASS_NAME + ".execute()";

        LogUtil.log(method, "Update list on servers: " + PeerClient.getInstance().getClient()
                .updateListOnServer());
        return true;
    }
}
