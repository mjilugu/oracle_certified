package com.mjilugu.oraclecertify.oraclecertify;

import java.util.Properties;
import java.io.FileOutputStream;
import java.io.IOException;

/**
  * This class provides methods to handle events from the settings window.
  * @author Moses L. Jilugu
  * @version Dec 11, 2012
  */
public class DatabaseSettingsController{
	private PropertiesReader propertiesReader;
	
	/**
	  * Creates an instance of this class.
	  * @throws IOException if properties file could not be opened.
	  */
	public DatabaseSettingsController() throws IOException{
		propertiesReader = PropertiesReader.getSharedInstance();
	}
	
	/**
	  * Method to set properties values and save them into the properties file
	  * @param host host ip or host name string
	  * @param port port number of the server
	  * @param file path to database file
	  */
	public void persistDatabaseSettings(String host, String port, String file){
		if(host != null)
			propertiesReader.setDatabaseHost(host);
		if(port != null)
			propertiesReader.setDatabasePort(port);
		if(file != null)
			propertiesReader.setDatabaseFile(file);
		propertiesReader.storeProperties();
	}
	
	/**
	  * Overriden method to set properties values and save them into the properties file
	  * @param port number of the server
	  * @param file path to database file
	  */
	public void persistDatabaseSettings(String port, String file){
		persistDatabaseSettings(null,port,file);
	}
	
	/**
	  * Overriden method to set properties values and save them into the properties file
	  * @param file path to database file
	  */
	public void persistDatabaseSettings(String file){
		persistDatabaseSettings(null,null,file);
	}
}