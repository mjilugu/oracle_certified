package com.mjilugu.oraclecertify.oraclecertify;

/**
  * This interface defines methods in a view object.
  * A view object will represent the interface between the application and the user.
  * @author Moses L. Jilugu
  * @version Dec 11, 2012
  */
public interface View{
	/**
	  * Method to register a controller for the view. The controller
	  * provides methods to handle events risen by users.
	  * @param controller controller object
	  */
	public void addUserGestureListener(Controller controller);
	
	/**
	  * Method to notify the view of changes in records in the data model.
	  * This will cause the view to refresh its display by re-fetching current
	  * displayed records from database.
	  */
	public void handleRecordsChange();
	
	/** 
	  * Method called to display a given object on the view.
	  * Appropriate action will be taken depending on the type of object.
	  * @param object object to be displayed.
	  */
	public void showDisplay(Object object);
}