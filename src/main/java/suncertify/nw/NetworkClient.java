package suncertify.nw;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
  * This class represents a network client.
  * @author Moses L. Jilugu
  * @version March 11, 2013.
  */
public class NetworkClient{
	private String host;
	private int port;
	private Socket socket;
	private InputStream is;
	private ObjectInputStream ois;
	private OutputStream os;
	private ObjectOutputStream oos;
	private Queue data_buffer;
	private Queue notification_buffer;
	private Thread streamReader;
	private Logger log;
	
	/**
	  * Creates an instance of NwClient.
	  * @param host string representing the IP of the network server host.
	  * @param port number on which the network server is listening.
	  * @throws IOException
	  * @throws UnknownHostException
	  */
	public NetworkClient(String host, int port) throws IOException, UnknownHostException{
		this.host = host;
		this.port = port;
		this.data_buffer = new LinkedList();
		this.notification_buffer = new LinkedList();
		log = Logger.getLogger("suncertify");
		
		connect();
	}
	
	/**
	  * Connects to the network server.
	  * @throws IOException
	  * @throws UnknownHostException
	  */
	public void connect() throws IOException, UnknownHostException{
		try{
			socket = new Socket(host,port);
			log.info("Connected to server at " + socket.getInetAddress().getHostAddress());
			is = socket.getInputStream();
			os = socket.getOutputStream();
			oos = new ObjectOutputStream(os);
			ois = null;
			
			// A thread to read and buffer socket input. used to capture
			// random notification of changes in remote model.
			streamReader = new Thread(){
				public void run(){
					while(true){
						try{
							read();
						}catch(Exception ex){
							log.severe("Connection to Server was lost.");
							break;
						}
					}
				}
			};
			streamReader.start();
		}catch(UnknownHostException ex){
			log.severe(ex.getMessage());
			throw ex;
		}catch(IOException ex){
			log.severe(ex.getMessage());
			throw ex;
		}
	}
	
	/**
	  * Connects to the network server.
	  * @param host string representing the IP of the network server host.
	  * @param port number on which the network server is listening.
	  * @throws IOException
	  * @throws UnknownHostException
	  */
	public void connect(String host, int port) throws IOException, UnknownHostException{
		this.host = host;
		this.port = port;
		
		connect();
	}
	
	/**
	  * Method to send an object across the network connection to the network server.
	  * @param object to be sent across the network connection to the network server.
	  * @throws Exception.
	  */
	public void send(Object object) throws Exception{
		oos.writeObject(object);
	}
	
	/**
	  * Method to receive object from the network server.
	  * @throws Exception.
	  * @return Object received from the network server.
	  */
	public Object receive() throws Exception{
		// simple spin lock to allow reader thread to read
		while(data_buffer.peek() == null){
			try{
				Thread.sleep(100);
			}catch(Exception ex){
			}
		}
		return data_buffer.remove();
	}
	
	/**
	  * Method to receive notification object from the network server.
	  * @throws Exception.
	  * @return Object received from the network server.
	  */
	public Object receiveNotification() throws Exception{
		// simple spin lock to allow reader thread to read
		while(notification_buffer.peek() == null){
			try{
				Thread.sleep(100);
			}catch(Exception ex){
			}
		}
		return notification_buffer.remove();
	}
	
	/**
	  * Method to check whether there is data in data buffer.
	  * @return boolean true if data is available and false otherwise.
	  */
	public boolean isDataAvailable(){
		return data_buffer.peek() != null;
	}
	
	/**
	  * Method to check whether there is a notification in buffer.
	  * @return boolean true if notification is available and false otherwise.
	  */
	public boolean isNotificationAvailable(){
		return notification_buffer.peek() != null;
	}
	
	/**
	  * Closes the connection with the server.
	  */
	public void close(){
		try{
			socket.close();
			log.info("Connection to server is closed.");
		}catch(IOException ex){
			log.severe(ex.getMessage());
		}
	}
	
	/**
	  * Method to read objects from socket input and put them in
	  * either the data buffer or the notification buffer.
	  * @throws Exception.
	  */
	private void read() throws Exception{
		if(ois == null){
			ois = new ObjectInputStream(is);
		}
		Object obj = ois.readObject();
		if(obj instanceof NotifyCommand){
			notification_buffer.offer(obj);
		}else{
			data_buffer.offer(obj);
		}
	}
}