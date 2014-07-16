package suncertify.db;

import suncertify.Model;
import suncertify.View;
import suncertify.Record;
import suncertify.nw.NotifyCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.net.Socket;
import java.io.ObjectOutputStream;

/**
 * This class provides an implementation of the model interface for
 * access to a local data access object.
 * The database and the entity accessing it must be on the same
 * machine.
 * @author Moses L. Jilugu
 * @version Jan 11, 2013
 */
public class ModelImpl implements Model {
	private Data data;
	private List changeListeners = new ArrayList();
	private ObjectOutputStream oos;
	private Logger log = Logger.getLogger("suncertify");
	
	/**
	 * Create new instance of the model implementation.
	 * @param data object providing interface to the database.
	 */
	public ModelImpl(Data data){
		this.data = data;
	}
	
	/**
	 * Adds a change listener to the list of change listeners.
	 * @param listener listener object either a view or an objectoutputstream to a socket
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
		return getRecords(criteria);
	}
	
	/**
	 * Fetches all records that satisfies the criteria given as parameter
	 * @param criteria criteria for each field in record
     * @return an array of record objects satisfying the criteria
     * @throws RecordNotFoundException if no record satisfies the criteria	 
	 */
	public Record[] getRecords(String[] criteria) throws RecordNotFoundException{
		Record[] records;
		long[] recNos;
		
		recNos = data.findByCriteria(criteria);
		
		if(recNos == null || recNos.length == 0){
			throw new RecordNotFoundException("No record was found with that criteria");
		}
		records = new Record[recNos.length];
		for(int i = 0; i < recNos.length; i++){
			String[] rec = data.readRecord(recNos[i]);
			records[i] = new Record();
			records[i].setId(recNos[i]);
			records[i].setName(rec[0]);
			records[i].setLocation(rec[1]);
			records[i].setSpecialties(rec[2]);
			records[i].setSize(rec[3]);
			records[i].setRate(rec[4]);
			records[i].setOwner(rec[5]);
		}
		
		return records;
	}
	
	/**
	 * Updates a list of records into the database
	 * @param records records list with updated data.
	 * @throws SecurityException
	 * @throws RecordNotFoundException
	 */
	public void updateRecords(ArrayList<Record> records) throws SecurityException, RecordNotFoundException{
		for(Record record : records){
			String[] rec_data = new String[]{record.getName(),record.getLocation(),record.getSpecialties(),
					record.getSize(),record.getRate(),record.getOwner()};
			
			long cookie = data.lockRecord(record.getId());
			String[] current_record = data.readRecord(record.getId());
			
			/* Update the record only if either of the following conditions is true
			 * 1. Record is to be booked to new owner and current record's owner field is blank, otherwise another user has booked the record
			 *    while this user has not finished booking
			 * 2. User wants to cancel booking. Cancellation is permitted in all scenarios.
			 */
			if(((record.getOwner().trim().length() != 0) && (current_record[5].trim().length() == 0)) || (record.getOwner().trim().length() == 0)){
				data.updateRecord(record.getId(), rec_data, cookie);
			}
			data.unlock(record.getId(), cookie);
		}
		
		// Notify change listeners.
		for(Object object : changeListeners){
			if(object instanceof View){
				// local change listener
				((View)object).handleRecordsChange();
			}else if(object instanceof ObjectOutputStream){
				try{
					ObjectOutputStream outStream = (ObjectOutputStream)object;
					outStream.writeObject(new NotifyCommand());
				}catch(Exception ex){
					log.severe(ex.getMessage());
				}
			}
		}
	}
}