package suncertify.db;

/**
 * This class represents an exception which is thrown 
 * when an attempt is made to store a database record
 * with data that exactly matches another record in the
 * database.
 * @author Moses L. Jilugu
 * @version Jan 11, 2013
 */
public class DuplicateKeyException extends Exception{

	/**
	 * Creates an instance of DuplicateKeyException
	 */
	public DuplicateKeyException(){
	}
	
	/**
	 * Creates an instance of DuplicateKeyException
	 * @param message message to be included in the exception
	 */
	public DuplicateKeyException(String message){
		super(message);
	}
}