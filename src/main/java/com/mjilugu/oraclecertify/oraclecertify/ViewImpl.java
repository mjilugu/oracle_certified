package com.mjilugu.oraclecertify.oraclecertify;

import java.util.logging.Logger;

import com.mjilugu.oraclecertify.oraclecertify.gui.Gui;

/**
  * This class provides an implementation of the view interface.
  * @author Moses L. Jilugu
  * @version Dec 11, 2012
  */
public class ViewImpl implements View{
	private final Gui gui;
	private Model model;
	private Controller controller;
	
	/**
	  * Creates an instant of the view implementation.
	  * @param model on which this view will listen for changes
	  * @param gui object to display data and listen to user gestures. 
	  */
	public ViewImpl(Model model, Gui gui){
		try{
			this.model = model;
			model.addChangeListener(this);
		}catch(Exception e){
		}
		this.gui = gui;
	}
	
	/**
	  * Method to add a controller to handle user gestures.
	  * @param controller controller object
	  */
	public void addUserGestureListener(Controller controller){
		this.controller = controller;
		gui.addController(controller);
	}
	
	/**
	  * Method to handle records change in the data model.
	  */
	public void handleRecordsChange(){
		gui.refreshRecordsPanel();
	}
	
	/**
	  * Method to display a given object on the gui.
	  * @param object object to be displayed.
	  */
	public void showDisplay(Object object){
		gui.displayObject(object);
	}
}