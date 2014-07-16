package com.mjilugu.oraclecertify.oraclecertify;

import java.util.ArrayList;

import com.mjilugu.oraclecertify.oraclecertify.db.RecordNotFoundException;
import com.mjilugu.oraclecertify.oraclecertify.db.SecurityException;

/**
  * This interface provides methods to access and modify data in the database.
  * @author Moses L. Jilugu
  * @version Dec 11, 2012
  */
public interface Model{
	/**
	  * Adds an object to be notified whenever data is changed in the database.
	  * @param listener Object which can be a local view or an outputstream to a socket connection.
	  */
	public void addChangeListener(Object listener);
	
	/**
	  * Method to fetch all records in the database
	  * @return an array of record objects
	  * @throws RecordNotFoundException if database is empty
	  */
	public Record[] getAllRecords() throws RecordNotFoundException;
	
	/**
	  * Method to fetch records matching a given criteria.
	  * @param criteria search criteria array.
	  * @return an array of record objects
	  * @throws RecordNotFoundException
	  */
	public Record[] getRecords(String[] criteria) throws RecordNotFoundException;
	
	/**
	  * Method to update records in the database.
	  * @param records list containing updated data for the records.
	  * @throws RecordNotFoundException 
	  * @throws SecutityException 
	  */
	public void updateRecords(ArrayList<Record> records) throws SecurityException, RecordNotFoundException;
}