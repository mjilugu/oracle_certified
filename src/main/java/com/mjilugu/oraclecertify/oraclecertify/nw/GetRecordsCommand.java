package com.mjilugu.oraclecertify.oraclecertify.nw;

import com.mjilugu.oraclecertify.oraclecertify.Model;

/**
  * This class represents the command to fetch records
  * from a network server that matches a given criteria.
  * @author Moses L. Jilugu
  * @version March 20, 2013.
  */
public class GetRecordsCommand extends Command{
	private String[] criteria = null;
	
	/**
	  * Creates an instance of GetRecordsCommand.
	  * @param criteria search criteria for each field of the desired records.
	  */
	public GetRecordsCommand(String[] criteria){
		this.criteria = new String[criteria.length];
		System.arraycopy(criteria, 0, this.criteria, 0, criteria.length);
	}
	
	/**
	  * This method is run on the network server to retrieve
	  * records from the database model that matches the criteria passed.
	  * @param model instance on the server for accessing data.
	  */
	public void execute(Model model){
		try{
			result = model.getRecords(this.criteria);
		}catch(Exception ex){
			result = null;
			this.exception = ex;
		}
	}
}