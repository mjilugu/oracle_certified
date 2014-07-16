package suncertify.db;

/**
 * This class represents an exception that is thrown
 * when an attempt to update, delete or unlock a record
 * using a cookie other than the one used to lock the record.
 * @author Moses L. Jilugu
 * @version Jan 11, 2013
 */
public class SecurityException extends Exception {
	/**
	 * Creates an instance of SecurityException.
	 */
	public SecurityException(){
	}
	
	/**
	 * Creates an instance of SecurityException.
	 * @param message message to be included with the exception
	 */
	public SecurityException(String message){
		super(message);
	}
}