package suncertify;

import java.io.Serializable;

/**
  * This class represents a database record with an additional 
  * record id field.
  * @author Moses L. Jilugu
  * @version Dec 11, 2012
  */
public class Record implements Serializable{
	private long id;
	private String name;
	private String location;
	private String specialties;
	private String size;
	private String rate;
	private String owner;
	
	/**
	  * Method to set the id value.
	  * @param id record number of the record
	  */
	public void setId(long id){
		this.id = id;
	}
	
	/**
	  * Method to get the id number.
	  * @return the id number.
	  */
	public long getId(){
		return this.id;
	}
	
	/**
	  * Method to set the name field.
	  * @param name name value.
	  */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	  * Method to get the name.
	  * @return name field value.
	  */
	public String getName(){
		return this.name;
	}
	
	/**
	  * Method to set the location.
	  * @param location location value
	  */
	public void setLocation(String location){
		this.location = location;
	}
	
	/**
	  * Method to get the location
	  * @return location value.
	  */
	public String getLocation(){
		return this.location;
	}
	
	/**
	  * Method to set specialties field.
	  * @param specialties a comma separated string value.
	  */
	public void setSpecialties(String specialties){
		this.specialties = specialties;
	}
	
	/**
	  * Method to get the specialties.
	  * @return specialties value.
	  */
	public String getSpecialties(){
		return this.specialties;
	}
	
	/**
	  * Method to set size value.
	  * @param size size value.
	  */
	public void setSize(String size){
		this.size = size;
	}
	
	/**
	  * Method to get size value.
	  * @return size value.
	  */
	public String getSize(){
		return this.size;
	}
	
	/**
	  * Method to set the rate value.
	  * @param rate rate value
	  */
	public void setRate(String rate){
		this.rate = rate;
	}
	
	/**
	  * Method to get the rate value.
	  * @return rate value.
	  */
	public String getRate(){
		return this.rate;
	}
	
	/**
	  * Method to set owner value.
	  * @param owner owner value.
	  */
	public void setOwner(String owner){
		this.owner = owner;
	}
	
	/**
	  * Method to get the owner value.
	  * @return owner value.
	  */
	public String getOwner(){
		return this.owner;
	}
}