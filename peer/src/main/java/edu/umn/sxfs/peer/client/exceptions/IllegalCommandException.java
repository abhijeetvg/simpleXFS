package edu.umn.sxfs.peer.client.exceptions;

/**
 * Thrown when No command was found to handle the user request.
 * 
 * @author Abhijeet
 *
 */
public class IllegalCommandException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8550645624371040070L;

	public IllegalCommandException(){
        super();
    }

    public IllegalCommandException(String s){
        super(s);
    }
	
}
