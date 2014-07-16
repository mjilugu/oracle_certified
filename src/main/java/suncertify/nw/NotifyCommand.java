package suncertify.nw;

import suncertify.Model;

/**
  * This class represents server notifications to clients.
  * @author Moses L. Jilugu
  * @version March 23, 2013
  */
public class NotifyCommand extends Command{
	private String notification = "refresh view";
	
	/**
	  * Overriden method execute currently does nothing for
	  * notify command because the clients only need to know that
	  * an instance of NotifyCommand is sent.
	  * @param model local data access model
	  */
	public void execute(Model model){
	}
}