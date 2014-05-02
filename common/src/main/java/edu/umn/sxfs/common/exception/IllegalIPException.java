package edu.umn.sxfs.common.exception;

/**
 * Thrown when IP string is not not a valid IP structure
 * @author prashant
 *
 */
public class IllegalIPException extends Exception {
	
	private static final long serialVersionUID = 1262978446446297668L;

	public IllegalIPException(){
        super();
    }

    public IllegalIPException(String s){
        super(s);
    }

}
