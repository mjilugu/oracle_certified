package suncertify.nw;

import java.io.Serializable;
import suncertify.Model;

/** 
  * This class defines the structure of commands to be used
  * in all communications between the network client and network server.
  * @author Moses L. Jilugu.
  * @version March 20, 2013.
  */
public abstract class Command implements Serializable{
	protected Object result = null;
	protected Exception exception = null;
	
	/**
	  * This method implements the action to be performed
	  * by the server upon receiving a command object from a client.
	  * @param model instance on the server for accessing data.
	  */
	protected  abstract void execute(Model model);
	
	/**
	  * This method enables a network client to get results contained
	  * in a command object returned from the network server.
	  * @return object of type Record or Exception
	  * @throws Exception
	  */
	public Object getResult() throws Exception{
		if(this.result != null){
			return this.result;
		}
		if(this.exception != null){
			throw this.exception;
		}
		
		return null;
	}
}