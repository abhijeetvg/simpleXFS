package edu.umn.sxfs.peer.client.cli;

import edu.umn.sxfs.common.util.LogUtil;
import edu.umn.sxfs.peer.client.PeerClient;
import edu.umn.sxfs.peer.client.constants.CommandConstants;
import edu.umn.sxfs.peer.client.exceptions.ClientGeneralException;

import java.rmi.RemoteException;

/**
 *
 * Find Servers that contain the file.
 *
 * Created by Abhijeet on 3/30/2014.
 */
public class FindCmd extends BaseCommand {

    private static final String CLASS_NAME = FindCmd.class.getSimpleName();

    private static final int FILE_NAME_ARG = 1;

    public FindCmd(String cmd) {
        super(cmd);
    }

    @Override
    public boolean execute() throws RemoteException, ClientGeneralException {

        final String method = CLASS_NAME + ".execute()";

        PeerClient cli = PeerClient.getInstance();
        if (null == cli.getClient()) {
            throw new ClientGeneralException(CommandConstants.ERR_CLIENT_NULL);
        }

        LogUtil.log(method, "file present on Servers: " + PeerClient.getInstance().getClient()
                .find(getArgument(FILE_NAME_ARG)));
        return true;
    }

}
