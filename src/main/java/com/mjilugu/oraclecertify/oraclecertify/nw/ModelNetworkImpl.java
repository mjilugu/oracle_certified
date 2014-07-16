package com.mjilugu.oraclecertify.oraclecertify.nw;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.mjilugu.oraclecertify.oraclecertify.Model;
import com.mjilugu.oraclecertify.oraclecertify.View;
import com.mjilugu.oraclecertify.oraclecertify.Record;
import com.mjilugu.oraclecertify.oraclecertify.db.RecordNotFoundException;
import com.mjilugu.oraclecertify.oraclecertify.db.SecurityException;

/**
 * This class provides an implementation of data model
 * to be used when accessing data from a remote data source.
 * @author Moses L. Jilugu
 * @version March 9, 2012
 */
public class ModelNetworkImpl implements Model{
	private final NetworkClient nwClient;
	private List changeListeners = new ArrayList();
	private Thread remoteModelMonitor;
	private Lock lock = new ReentrantLock();
	
	/**
	  * Creates a new instance of the class.
	  * @param client network client to be used to send queries and receive results.
	  */
	public ModelNetworkImpl(NetworkClient client){
		this.nwClient = client;
		
		// listen to remote model changes and notify
		// change listeners.
		remoteModelMonitor = new Thread(){
			public void run(){
				while(true){
					try{
						lock.lock();
						if(nwClient.isNotificationAvailable()){
							Command command = (Command)nwClient.receiveNotification();
							for(Object object : changeListeners){
									if(object instanceof View){
										((View)object).handleRecordsChange();
									}
							}
						}
					}catch(Exception ex){
						System.out.println("remoteModelMonitor: Interrupted");
					}finally{
						lock.unlock();
					}
				}
			}
		};
		remoteModelMonitor.start();
	}
	
	/**
	  * Adds a listener to the list of change listeners.
	  * @param listener object to be notified when updates are made.
	  */
	public void addChangeListener(Object listener){
		changeListeners.add(listener);
	}
	
	/**
	 * Fetches all records available in the database
	 * @return an array of record objects
	 * @throws RecordNotFoundException if the dabatase is empty
	 */
	public Record[] getAllRecords() throws RecordNotFoundException{
		String[] criteria = {null,null,null,null,null,null};
		Record[] records = getRecords(criteria);
		
		return records;
	}
	
	/**
	 * Fetches all records that satisfies the criteria given as parameter
	 * @param criteria search criteria for each field in record
     * @return an array of record objects satisfying the criteria
     * @throws RecordNotFoundException if no record satisfies the criteria	 
	 */
	public Record[] getRecords(String[] criteria) throws RecordNotFoundException{
		Command command;
		Object result;
		Record[] records = null;
		
		try{
			lock.lock();
			command = new GetRecordsCommand(criteria);
			nwClient.send(command);
			command = (Command)nwClient.receive();
			result = command.getResult();
			records = (Record[])result;
		}catch(Exception e){
			if(e instanceof RecordNotFoundException)
				throw (RecordNotFoundException)e;
		}finally{
			lock.unlock();
		}
		
		return records;
	}
	
	/**
	  * Updates the given records.
	  * @param records records list
	  * @throws SecurityException
	  * @throws RecordNotFoundException
	  */
	public void updateRecords(ArrayList<Record> records) throws SecurityException, RecordNotFoundException{
		try{
			lock.lock();
			Command command = new UpdateRecordsCommand(records);
			nwClient.send(command);
			command = (Command)nwClient.receive();
			if(command.exception != null){
				throw command.exception;
			}
		}catch(Exception ex){
			if(ex instanceof RecordNotFoundException){
				throw (RecordNotFoundException)ex;
			}else if(ex instanceof SecurityException){
				throw (SecurityException)ex;
			}
		}finally{
			lock.unlock();
		}
	}
}