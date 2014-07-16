package com.mjilugu.oraclecertify.oraclecertify;

import java.util.Properties;
import java.util.logging.Logger;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
  * This class enables access to the properties file. It provides
  * methods to get and set properties in the properties file.
  * @author Moses L. Jilugu
  * @version Dec 11, 2012
  */
public class PropertiesReader{
	public static PropertiesReader sharedInstance;
	Logger log = Logger.getLogger("suncertify");
	private Properties properties;
	
	/**
	  * Returns a shared instance of PropertiesReader to be used by any
	  * class that needs to use properties in the properties file.
	  * @throws IOException if properties file cannot be found.
	  */
	public static PropertiesReader getSharedInstance() throws IOException{
		if(sharedInstance == null){
			sharedInstance = new PropertiesReader();
		}
		return sharedInstance;
	}
	
	/**
	  * Returns the value of database host IP or hostname.
	  * @return database host string.
	  */
	public String getDatabaseHost(){
		return properties.getProperty("database.host");
	}
	
	/**
	  * Sets the value of database host property.
	  * @param hostStr newtork server IP or hostname
	  */
	public void setDatabaseHost(String hostStr){
		properties.setProperty("database.host", hostStr);
	}
	
	/**
	  * Returns the value of database server port number.
	  * @return port number
	  */
	public int getDatabasePort(){
		int portNum = 0;
		try {
			portNum = Integer.parseInt(properties.getProperty("database.port"));
		}catch(Exception e){
			portNum = 0;
			log.severe(e.getMessage());
		}
		
		return portNum;
	}
	
	/**
	  * Sets the value of database server port number.
	  * @param portNumber port number of the server
	  */
	public void setDatabasePort(String portNumber){
		properties.setProperty("database.port", portNumber);
	}
	
	/**
	  * Returns the value for database file path.
	  * @return path to database file.
	  */
	public String getDatabaseFile(){
		return properties.getProperty("database.file");
	}
	
	/**
	  * Sets the value of path to database file.
	  * @param dbFile String path to database file.
	  */
	public void setDatabaseFile(String dbFile){
		properties.setProperty("database.file", dbFile);
	}
	
	/**
	  * This method persists the properties to the properties file.
	  */
	public void storeProperties(){
		try{
			properties.store(new FileOutputStream("suncertify.properties"), "Suncertify Database Properties");
		}catch(Exception ex){
			log.severe(ex.getMessage());
		}
	}
	
	/**
	  * Loads properties from properties file.
	  * @throws IOException if the properties file cannot be found.
	  */
	private PropertiesReader() throws IOException{
		properties = new Properties();
		try{
			properties.load(new FileInputStream("suncertify.properties"));
			initProperties();
		}catch(IOException e){
			log.severe(e.getMessage());
			throw e;
		}
	}
	
	/**
	  * Sets default values for properties that are not set.
	  */
	private void initProperties(){
		if(getDatabaseHost() == null){
			setDatabaseHost("localhost");
		}
		
		if(getDatabasePort() == 0){
			setDatabasePort("4469");
		}
		
		if(getDatabaseFile() == null){
			setDatabaseFile("db-2x2.db");
		}
	}
}