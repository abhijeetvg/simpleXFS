package edu.umn.sxfs.peer.client.cli;

import java.rmi.RemoteException;

import edu.umn.sxfs.peer.client.exceptions.ClientGeneralException;
import edu.umn.sxfs.peer.client.exceptions.ClientNullException;

/**
 * Base class for all command types. One or more RMI methods are invoked by each command.
 *  
 * @author Abhijeet
 *
 */
public abstract class BaseCommand {
	
	private String[] arguments;
	
	public BaseCommand(String cmd) {
		this.arguments = cmd.split(" ");
	}
	
	/**
	 * Execute the command using the RMI client.
	 * 
	 * @return boolean: true or false depending on the success or failure.
	 * @throws RemoteException
	 * @throws ClientGeneralException
	 * @throws ClientNullException
	 */
	public abstract boolean execute() throws RemoteException, ClientGeneralException;
	
	protected String getArgument(int position) throws ClientGeneralException {
		
		//sanity
		if (position <= 0 || position >= arguments.length) {
			throw new ClientGeneralException("Illegal arguments.");
		}
		
		return arguments[position];
	}

    protected boolean argExists(int position) {
        return (position >= 0 && position < arguments.length);
    }
	
}
