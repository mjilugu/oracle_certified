package com.mjilugu.oraclecertify.oraclecertify.db;

/**
 * This class represents an exception that is thrown when
 * no record matching a given search criteria is available 
 * or if the database is empty.
 * @author Moses L. Jilugu
 * @version Jan 11, 2013
 */
public class RecordNotFoundException extends Exception{
	/**
	 * Creates an instance of RecordNotFoundException.
	 */
	public RecordNotFoundException(){
	}
	
	/**
	 * Creates an instance of RecordNotFoundException.
	 * @param message message to be included with the exception.
	 */
	public RecordNotFoundException(String message){
		super(message);
	}
}