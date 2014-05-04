package edu.umn.sxfs.peer.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import edu.umn.sxfs.peer.Peer;
import edu.umn.sxfs.peer.client.cli.BaseCommand;
import edu.umn.sxfs.peer.client.cli.CommandFactory;
import edu.umn.sxfs.peer.client.constants.CommandConstants;
import edu.umn.sxfs.peer.client.exceptions.ClientGeneralException;
import edu.umn.sxfs.peer.client.exceptions.ClientNullException;
import edu.umn.sxfs.peer.client.exceptions.IllegalCommandException;
import edu.umn.sxfs.peer.PeerServerInterfaceObject;
import edu.umn.sxfs.common.util.LogUtil;

/**
 * RMI Client, shell interface for peer.
 * Starts Peer Server and then client. These are tightly coupled right now and are closed together.
 *
 * @author Abhijeet
 *
 */
public class PeerClient {

    private static final String CLASS_NAME = PeerClient.class.getSimpleName();

    private static final String CMD_PROMPT = "\nSXFS-Client-1.0$ ";
    private static final String GOOD_BYE_MSG = "Good Bye!";

    private static final String USAGE_HELP = "Usage: <current peer ip> <current peer port> <tracking server ip> " +
            "<tracking server port> <peer_base_dir>";

    private PeerServerInterfaceObject client;

    private static PeerClient instance = null;

    private PeerClient(){}

    public synchronized static PeerClient getInstance() {
        if (null == instance) {
            instance = new PeerClient();
        }

        return instance;
    }

    public void setClient(PeerServerInterfaceObject cli) {
        this.client = cli;
    }

    public PeerServerInterfaceObject getClient() {
        return PeerServerInterfaceObject.getInstance();
    }

    void executeCmd(String cmdStr) throws NumberFormatException,
            ClientNullException {

        BaseCommand cmd;
        try {
            cmd = CommandFactory.getCommand(cmdStr);

            long start = System.nanoTime();
            if (!cmd.execute()) {
                LogUtil.info(CommandConstants.ERR_COMMAND_EXEC_FAILED);
            }

            System.out.println("Time elapsed: " + (System.nanoTime()
                    - start)/Math.pow(10,6) + " ms.");

        } catch (IllegalCommandException e) {
            LogUtil.error("", e.getMessage());
        } catch (RemoteException e) {
            LogUtil.causedBy(e);
        } catch (ClientGeneralException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the shell and accepts commands. Ends when "exit" or "quit" is
     * encountered.
     *
     * @throws IOException
     * @throws ClientNullException
     * @throws NumberFormatException
     */
    public void startShell() throws IOException, NumberFormatException,
            ClientNullException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String cmd;

        try {

            System.out.print(CMD_PROMPT);

            while ((cmd = in.readLine()) != null) {
                if (cmd.isEmpty() || cmd.startsWith("#")) {
                    System.out.print(CMD_PROMPT);
                    continue;
                }

                if (cmd.trim().equalsIgnoreCase("exit")
                        || cmd.trim().equalsIgnoreCase("quit")) {
                    break;
                }

                executeCmd(cmd);

                System.out.print(CMD_PROMPT);
            }

            System.out.print(CMD_PROMPT);
        } finally {
            LogUtil.info(GOOD_BYE_MSG);
        }
    }

    /**
     * Drives the client execution.
     *
     * TODO: Poor exception handling, everything is propagated and printed in
     * this method currently, custom err messages should be printed where the
     * exception is raised.
     *
     * @param args
     */
    public static void main(String[] args) {

        final String method = CLASS_NAME + ".main()";

        try {

            if (5 != args.length) {
                throw new IllegalArgumentException("Invalid number of arguments. " + USAGE_HELP);
            }

            if (!Peer.start(args)) {
                throw new RuntimeException("Peer Server could not be initialized.");
            }

            LogUtil.log(method, "Peer started successfully.");
            LogUtil.log(method, "Starting Client.");
            PeerClient.getInstance().startShell();

        } catch (RemoteException e) {
            LogUtil.causedBy(e);
        } catch (MalformedURLException e) {
            LogUtil.log(method, LogUtil.causedBy(e));
        } catch (NumberFormatException e) {
            LogUtil.log(method, LogUtil.causedBy(e));
        } catch (IOException e) {
            LogUtil.log(method, LogUtil.causedBy(e));
        } catch (ClientNullException e) {
            LogUtil.log(method, LogUtil.causedBy(e));
        }

    }
}
