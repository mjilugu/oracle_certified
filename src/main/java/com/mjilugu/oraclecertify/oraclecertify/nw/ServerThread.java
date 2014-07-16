package com.mjilugu.oraclecertify.oraclecertify.nw;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
  * This class represents a server thread which encapsulates the interaction
  * of the server with a particular client. Once the connection between the client and 
  * the server is closed, the server thread can be reused.
  * @author Moses L. Jilugu
  * @version March 20, 2013
  */
public class ServerThread implements Runnable{
	private DatabaseServer service;
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private final Lock lock = new ReentrantLock();
	private final Condition newWork = lock.newCondition();
	
	/**
	  * Creates an instance of this class.
	  * @param service ServerService to service a live connection
	  */
	public ServerThread(ServerService service){
		this.service = (DatabaseServer)service;
	}

	/**
	  * Overriden run method. Waits for connections and services services them
	  * when woken up.
	  */
	public void run(){
		while(true){
			lock.lock();
			try {
				newWork.await();
				service.service(ois,oos);
			} catch (Exception e) {
				ois = null;
				oos = null;
			} finally{
				lock.unlock();
			}
		}
	}
	
	/**
	  * Receives a new connection and notifies the run method to service
	  * the connection.
	  * @param socket socket connection to be serviced by this thread.
	  */
	public synchronized void setSocket(Socket socket){
		lock.lock();
		try{
			this.socket = socket;
			newWork.signal();
		}finally{
			lock.unlock();
		}
	}
	
	/**
	  * Receives a new connection and notifies the run method to service
	  * the connection. This is a convinience method similar to setSocket
	  * but by using share streams, various entities on the server can 
	  * service a socket without terminating a connection.
	  * @param ois objectinputstream of a given socket
	  * @param oos ObjectOutputStream of a given socket
	  */
	public synchronized void setStreams(ObjectInputStream ois, ObjectOutputStream oos){
		lock.lock();
		try{
			this.ois = ois;
			this.oos = oos;
			newWork.signal();
		}finally{
			lock.unlock();
		}
	}
	
	/**
	  * Checks if this server thread instance is is available
	  * to service new connections. ServerThread becomes idle when 
	  * the connection being serviced by the thread is terminated.
	  * @return true if idle and false otherwise.
	  */
	public boolean isIdle(){
		return (ois == null || oos == null);
	}
}
