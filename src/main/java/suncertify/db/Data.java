package suncertify.db;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.EOFException;

/**
 * This class provides an interface to the textfile based database.
 * @author Moses L. Jilugu
 * @version Jan 11, 2013
 */
public class Data implements DBAccess {
	private static Map<Long,Long> cookies;
	private static Map<Long,Lock> locks;
	private String filename;
	
	private RandomAccessFile randomAccessFile;
	private FileInputStream fileInputStream;
	private DataInputStream dataInputStream;
	private Logger log = Logger.getLogger("suncertify");
	
	private int MAGIC_COOKIE;
	private int OFFSET_TO_RECORD_ZERO;
	private short NO_OF_FIELDS;
	private final int HEADER_SIZE = 10;
	private final int SCHEMA_SIZE = 60;		// Calculated from schema given in instructions.html
	private final int RECORD_SIZE = 184;	// Calculated from schema given in instructions.html
	
	static {
		cookies = new HashMap<Long,Long>();	// Maps a record number to a cookie
		locks = new HashMap<Long, Lock>();	// Maps a record number to a Lock object
	}
	
	/**
	 * @param fileName path to the database file.
	 */
	public Data(String fileName) throws IOException, FileNotFoundException, SecurityException{
		this.filename = fileName;
		
		// Check validity of database file.
		// Initialize the offset to record zero.
		// Initialize the number of fields in each record.
		try{
			fileInputStream = new FileInputStream(filename);
			dataInputStream = new DataInputStream(fileInputStream);
			
			MAGIC_COOKIE = dataInputStream.readInt();
			OFFSET_TO_RECORD_ZERO = dataInputStream.readInt();
			NO_OF_FIELDS = dataInputStream.readShort();
			
			// Assume magic cookie value of 514 as detected in the original database file provided for
			// for the assignment, throw SecurityException if value read from file is different.
			if(MAGIC_COOKIE != 514){
				throw new SecurityException("INVALID_DATABASE_FILE: Magic cookie in the database file provided is invalid. Expected 514");
			}
			
			// Throw exception if number of fields is not equal to 6
			if(NO_OF_FIELDS != 6){
				throw new IOException("INVALID_DATABASE_FILE: This implementation works with records with 6 fields only.");
			}
		}catch(Exception ex){
			if(ex instanceof SecurityException || ex instanceof IOException || ex instanceof FileNotFoundException)
				throw ex;
			else
				log.severe(ex.getMessage());
		}
	}
	
	/**
	 * Reads a record from the file. Returns an array where each
     * element is a record value.
	 *
	 * @param recNo a long number equal to or greater than one indicating position of record.
	 * @return a String array containing the record data
	 * @throws RecordNotFoundException if the record is invalid or does not exist
	 */
	@Override 
    public String[] readRecord(long recNo) throws RecordNotFoundException{
		
		String[] record = new String[6];
		short flag;
		
		try{
			fileInputStream = new FileInputStream(filename);
			dataInputStream = new DataInputStream(fileInputStream);
			// Skip header and Schema
			dataInputStream.skip(OFFSET_TO_RECORD_ZERO);
			
			// Skip to the record
			dataInputStream.skip((recNo - 1) * RECORD_SIZE);
			
			// Read Record;
			flag = dataInputStream.readShort();
			if(flag != 0x0000){
				throw new RecordNotFoundException("Record " + recNo + " was not found.");
			}
			record = readRecord(dataInputStream);
			
			fileInputStream.close();
			dataInputStream.close();
		}catch(EOFException ex){
			throw new RecordNotFoundException("Record " + recNo + " was not found.");
		}catch(IOException e){
			log.severe(e.getMessage());
		}
		return record;
	}
	
	/**
	 * Modifies the fields of a record. The new value for field n
     * appears in data[n]. Throws SecurityException
     * if the record is locked with a cookie other than lockCookie.
	 *
	 * @param recNo      indicating the record to be updated
	 * @param data       a String array containing data to be stored
	 * @param lockCookie a cookie used to lock the record prior to updating
	 * @throws RecordNotFoundException if the record is invalid or does not exist.
	 * @throws SecurityException if the record is locked with a cookie other than lockCookie.
	 */
	@Override
    public void updateRecord(long recNo, String[] data, long lockCookie) throws RecordNotFoundException, SecurityException{
		// check if cookies match
		if(cookies.get(recNo) == null || lockCookie != cookies.get(recNo)){
			throw new SecurityException("Record " + recNo + " is currently locked.");
		}
		
		// check if record exists (throws RecordNotFoundException)
		readRecord(recNo);
		
		// update record data;
		editRecord(data);
		try{
			randomAccessFile = new RandomAccessFile(filename, "rws");
			// skip to the record
			randomAccessFile.seek(OFFSET_TO_RECORD_ZERO + (recNo - 1) * RECORD_SIZE);
			
			// write new record
			randomAccessFile.writeShort(0x0000);
			writeRecord(randomAccessFile, data);
			randomAccessFile.close();
		}catch(IOException e){
			log.severe(e.getMessage());
		}
	}
    
	/**
     * Deletes a record, making the record number and associated disk
     * storage available for reuse.
     * Throws SecurityException if the record is locked with a cookie
	 * other than lockCookie.
	 *
	 * @param recNo		 indicating the record to be updated
	 * @param lockCookie a cookie used to lock the record prior to updating
	 * @throws RecordNotFoundException if the record is invalid or does not exist
	 * @throws SecurityException if the record is locked with a cookie other than lockCookie
	 */
	@Override
    public void deleteRecord(long recNo, long lockCookie) throws RecordNotFoundException, SecurityException{
		// check if record exists (throws RecordNotFoundException)
		readRecord(recNo);
		
		// check if cookies match
		if(cookies.get(recNo) == null || lockCookie != cookies.get(recNo)){
			throw new SecurityException("Record " + recNo + " is currently locked.");
		}
		
		//delete record.
		try{
			randomAccessFile = new RandomAccessFile(filename, "rw");
			// skip to the record
			randomAccessFile.seek(OFFSET_TO_RECORD_ZERO + (recNo - 1) * RECORD_SIZE);
			
			// Change record flag to indicate deleted i.e. 0x8000
			randomAccessFile.writeShort(0x8000);
			randomAccessFile.close();
		}catch(IOException e){
			log.severe(e.getMessage());
		}
	}
    
	/**
     * Returns an array of record numbers that match the specified
     * criteria. Field n in the database file is described by 
     * criteria[n]. A null value in criteria[n] matches any field
     * value. A non-null value in criteria[n] matches any field
     * value that begins with criteria[n]. (For example, "Fred"
     * matches "Fred" or "Freddy".)
	 *
	 * @param criteria a String array containing search constraints
	 * @return an array of record numbers of records matching the criteria
	 */
	@Override
    public long[] findByCriteria(String[] criteria){	
		ArrayList<Long> recordNumbers = new ArrayList<Long>();
		long recordNo = 0;
		int count = 0;
		short validity_flag = (short)0x8000;
		
		try{
			fileInputStream = new FileInputStream(filename);
			dataInputStream = new DataInputStream(fileInputStream);
			
			// Skip to First record
			dataInputStream.skip(OFFSET_TO_RECORD_ZERO);
			while(true){
				// read the record, record is deleted if flag is 0x8000 or -32768 in decimal
				validity_flag = dataInputStream.readShort();
				if(validity_flag == -32768){
					// deleted record, proceed to next
					recordNo++;
					continue;
				}
			
				recordNo++;
				String[] record = readRecord(dataInputStream);
				// compare record with criteria and update recordNo[]
				boolean match = false;
				for(int i = 0; i < criteria.length; i++){
					if(criteria[i] == null){
						match = true;
					}else if(record[i].toLowerCase().indexOf(criteria[i].toLowerCase()) == 0){
						match = true;
					}else{
						match = false;
						break;
					}
				}
				if(match == true && validity_flag == 0){
					recordNumbers.add(recordNo);
				}
			}
		}catch(EOFException ex){
			// End of file is reached, no need to log
		}catch(IOException e){
			log.severe(e.getMessage());
		}
		
		long[] recNums = new long[recordNumbers.size()];
		for(int i = 0; i < recNums.length; i++){
			recNums[i] = recordNumbers.get(i).longValue();
		}
		return recNums;
	}
    
	/**
     * Creates a new record in the database (possibly reusing a
     * deleted entry). Inserts the given data, and returns the record
     * number of the new record.
	 *
	 * @param data a string array containing data for the record
	 * @return a record number of the newly created record
	 * @throws DuplicateKeyException if there exists a record with the same data
	 */
	@Override
    public long createRecord(String[] data) throws DuplicateKeyException{
		long recordNo = 0;
		long[] matches = findByCriteria(data);
		
		// check if similar record exists.
		if(matches != null && matches.length != 0){
			throw new DuplicateKeyException("A record with same data already exists.");
		}
		
		editRecord(data);
		try{
			fileInputStream = new FileInputStream(filename);
			dataInputStream = new DataInputStream(fileInputStream);
			dataInputStream.skip(OFFSET_TO_RECORD_ZERO);
			recordNo = getRecordNumber(dataInputStream);
			fileInputStream.close();
			dataInputStream.close();
			
			randomAccessFile = new RandomAccessFile(filename, "rw");
			randomAccessFile.seek(OFFSET_TO_RECORD_ZERO + (recordNo - 1) * RECORD_SIZE);
			
			// write validity flag 0x0000
			randomAccessFile.writeShort(0x0000);
			writeRecord(randomAccessFile, data);
			randomAccessFile.close();
		}catch(IOException e){
			log.severe(e.getMessage());
		}
		return recordNo;
	}
	
	/**
     * Locks a record so that it can only be updated or deleted by this client.
     * Returned value is a cookie that must be used when the record is unlocked,
     * updated, or deleted. If the specified record is already locked by a different
     * client, the current thread gives up the CPU and consumes no CPU cycles until
     * the record is unlocked.
	 *
	 * @param recNo the record to be locked
	 * @return a cookie to be used in updates, deletes or unlocking
	 * @throws RecordNotFoundException if the record is invalid or does not exist
	 */
	@Override
    public long lockRecord(long recNo)  throws RecordNotFoundException{
		Random random = new Random();
		long cookie;
		
		// check if record exists (throws RecordNotFoundException if not)
		readRecord(recNo);		
		
		// lock the record, creating new lock if necessary.
		createLock(recNo);
		locks.get(recNo).lock();
		cookie = 100 + Math.abs(random.nextLong());
		cookies.put(recNo,cookie);
		
		return cookie;
	}
    
	/**
     * Releases the lock on a record. Cookie must be the cookie
     * returned when the record was locked; otherwise throws SecurityException.
	 *
	 * @param recNo  the record to be unlocked
	 * @param cookie the cookie returned when locking the record
	 * @throws SecurityException if record locked with cookie other than given cookie
	 */
	@Override
    public void unlock(long recNo, long cookie) throws SecurityException{
		if(cookies.get(recNo) == null || cookies.get(recNo) != cookie){
			throw new SecurityException("Attempted to unlock Record " + recNo + " with invalid cookie.");
		}else{
			cookies.remove(recNo);
			locks.get(recNo).unlock();
		}
	}
	
	/*************** Private Methods ****************/
	
	/**
	  * Reads an array of bytes from an inputstream.
	  */
	private String readField(DataInputStream data, byte[] field) throws IOException{
		data.read(field);
		return new String(field);
	}
	
	/**
	  * Reads an entire record from an inputstream.
	  */
	private String[] readRecord(DataInputStream data) throws IOException{
		// Assume record with 6 fields as given in schema in instructions.html
		String[] record = new String[6];
		record[0] = readField(data, new byte[32]);
		record[1] = readField(data, new byte[64]);
		record[2] = readField(data, new byte[64]);
		record[3] = readField(data, new byte[6]);
		record[4] = readField(data, new byte[8]);
		record[5] = readField(data, new byte[8]);
		return record;
	}
	
	/**
	  * Writes an entire record to a file
	  */
	private void writeRecord(RandomAccessFile file, String[] record) throws IOException{
		for(int i = 0; i < record.length; i++){
			file.writeBytes(record[i]);
		}
	}
    
	/**
	  * Generates a record number for a new record.
	  */
	private long getRecordNumber(DataInputStream dataInputStream) throws IOException{
		long recordNo = 1;
		short flag = 0;
		while(true){
			try{
				flag = dataInputStream.readShort();
				// record deleted flag 0x8000 or -32768 in decimal
				// record space can be reused.
				if(flag == -32768){
					break;
				}
				readRecord(dataInputStream);
				recordNo++;
			}catch(EOFException ex){
				break;
			}
		}
		return recordNo;
	}
	
	/**
	  * Edits record fields according to the schema
	  */
	private void editRecord(String[] record){
		// Assume record with 6 fields as given in schema in instructions.html
		record[0] = editString(record[0],32);
		record[1] = editString(record[1],64);
		record[2] = editString(record[2],64);
		record[3] = editString(record[3],6);
		record[4] = editString(record[4],8);
		record[5] = editString(record[5],8);
	}
	
	/**
	  * Edits a record field according to length size.
	  */
	private String editString(String str, int len){
		if(str.length() != len){
			StringBuilder string = new StringBuilder(str);
			if(string.length() > len){
				string = string.delete(len,string.length());
			}else{
				char[] post_fix = new char[len - string.length()];
				for(int i = 0; i < post_fix.length; i++){
					post_fix[i] = ' ';
				}
				string.append(post_fix);
			}
			str = string.toString();
		}
		return str;
	}
	
	/**
	  * Creates a new lock if the given record number does not have
	  * a lock object in the locks map otherwise ignores request.
	  */
	private void createLock(long recNo){
		synchronized(locks){
			if(!locks.containsKey(recNo)){
				Lock lock = new ReentrantLock();
				locks.put(recNo,lock);
			}
		}
	}
}