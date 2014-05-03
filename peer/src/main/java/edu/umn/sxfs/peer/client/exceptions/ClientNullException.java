package edu.umn.sxfs.peer.client.exceptions;

/**
 * Exception thrown when client passed as argument to any of the commands is NULL.
 * 
 * @author Abhijeet
 *
 */
public class ClientNullException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2746476945191270173L;

	public ClientNullException(){
        super();
    }

    public ClientNullException(String s){
        super(s);
    }
	
}
