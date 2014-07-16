package com.mjilugu.oraclecertify.oraclecertify;

import java.util.ArrayList;

/**
  * This interface provides methods to handle events on the main 
  * Gui of the application.
  * @author Moses L. Jilugu
  * @version Dec 11, 2012
  */
public interface Controller{
	/**
	  * Handles the get all records request.
	  */
	public void handleGetAllRecordsGesture();
	
	/**
	  * Handles the get records request.
	  * @param criteria String array for the search.
	  */
	public void handleGetRecordsGesture(String[] criteria);
	
	/**
	  * Handles book contractors request.
      * @param records list of records to be updated.
	  * @param customerNumber ID number of the new owner.
	  * @return integer 0 on success or 1 on failure.
	  */
	public int handleBookContractorsGesture(ArrayList<Record> records, String customerNumber);
	
	/**
	  * Handles cancel booking gesture.
	  * @param records list of records to be updated.
	  * @return integer 0 on success or 1 on failure.
	  */
	public int handleCancelBookingGesture(ArrayList<Record> records);
}