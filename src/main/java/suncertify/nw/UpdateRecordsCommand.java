package suncertify.nw;

import java.util.ArrayList;

import suncertify.Model;
import suncertify.Record;

/**
  * Network command to update a list of records.
  * @author Moses L. Jilugu
  * @version March 20, 2013
  */
public class UpdateRecordsCommand extends Command{
	private ArrayList<Record> records;
	
	/**
	  * Creates a new instance of the class.
	  * @param records records list to be serviced
	  */
	public UpdateRecordsCommand(ArrayList<Record> records){
		this.records = records;
	}

	/**
	  * Updates the records on an instance of model passed to the command.
	  * @param model data access model object
	  */
	public void execute(Model model){
		try{
			model.updateRecords(records);
		}catch(Exception ex){
			this.result = null;
			this.exception = ex;
		}
	}
}
