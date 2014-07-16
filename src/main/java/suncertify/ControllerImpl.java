package suncertify;

import java.util.Properties;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.IOException;

/**
  * This class provides an implementation of the controller interface
  * @author Moses L. Jilugu
  * @version 0.0 Dec 11, 2012
  */
public class ControllerImpl implements Controller {
	private Model model;
	private View view;
	
	/**
	  * Creates an instance of the class.
	  * @param model the data model to be accessed by the controller.
	  * @param view the view object containing the gui that rises events.
	  */
	public ControllerImpl(Model model, View view){
		this.model = model;
		this.view = view;
		view.addUserGestureListener(this);
	}
	
	/**
	  * Handles the get-all request from the view.
	  */
	public void handleGetAllRecordsGesture(){
		Record[] records;
		try{
			records = model.getAllRecords();
			view.showDisplay(records);
		}catch(Exception e){
			e.printStackTrace();
			view.showDisplay(e);
		}
	}
	
	/**
	  * Handles the get-records request from the view.
	  * @param criteria criteria array for the search
	  */
	public void handleGetRecordsGesture(String[] criteria){
		Record[] records;
		try{
			records = model.getRecords(criteria);
			view.showDisplay(records);
		}catch(Exception e){
			view.showDisplay(e);
		}
	}
	
	/**
	  * Handles request to book a list of contrators.
	  * @param records list to be booked.
	  * @param customerNumber of the new owner
	  * @return integer 0 on success or 1 on failure.
	  */
	public int handleBookContractorsGesture(ArrayList<Record> records, String customerNumber){
		try{
			for(Record record : records){
				record.setOwner(customerNumber);
			}
			model.updateRecords(records);
			return 0;
		}catch(Exception e){
			view.showDisplay(e);
		}
		return 1;
	}
	
	/**
	  * Handles request to cancel bookings.
	  * @param records list of bookings to cancel.
	  * @return integer 0 on success or 1 on failure.
	  */
	public int handleCancelBookingGesture(ArrayList<Record> records){
		try{
			model.updateRecords(records);
			return 0;
		}catch(Exception e){
			view.showDisplay(e);
		}
		return 1;
	}
}