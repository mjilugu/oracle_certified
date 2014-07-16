package com.mjilugu.oraclecertify.oraclecertify.gui;

import com.mjilugu.oraclecertify.oraclecertify.Model;
import com.mjilugu.oraclecertify.oraclecertify.Controller;

/**
  * This interface provides methods that every records panel component
  * in the main gui must implement.
  * @author Moses L. Jilugu
  * @version Feb 12, 2013.
  */
public interface RecordsPanel{
	/**
	  * Method to register event handler class for events in 
	  * the records panel.
	  * @param controller controller object.
	  */
	void registerController(Controller controller);
	
	/**
	  * Method to register data model.
	  * @param model model object
	  */
	void registerModel(Model model);
	
	/**
	  * Method to display object on the records panel
	  * @param object object to be displayed.
	  */
	void display(Object object);
	
	/**
	  * Method to refresh current displayed data with 
	  * updated data from data model.
	  */
	void refresh();
}