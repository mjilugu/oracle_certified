package suncertify.nw;

import java.net.Socket;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import suncertify.Model;

/**
  * This class represents the server thread pool manager. The pool manager
  * keeps track of available server threads and adds new threads to the pool
  * when necessary.
  * @author Moses L. Jilugu
  * @version March 20, 2013
  */
public class ServerThreadPoolManager implements ServerService{
	private Model model;
	private ArrayList<ServerThread> threads;
	private ArrayList<ObjectInputStream> inStreams;
	private ArrayList<ObjectOutputStream> outStreams;
	
	/**
	  * Creates an instance of this class
	  * @param model local data access model on the server
	  */
	public ServerThreadPoolManager(Model model){
		this.model = model;
		threads = new ArrayList<ServerThread>();
		inStreams = new ArrayList<ObjectInputStream>();
		outStreams = new ArrayList<ObjectOutputStream>();
	}
	
	/**
	  * Services a new socket connection by assigning a server thread to service its 
	  * i/o streams.
	  * @param socket socket connection to be serviced.
	  */
	public void service(Socket socket) throws Exception{
		try{
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			inStreams.add(inStream);
			outStreams.add(outStream);
			model.addChangeListener(outStream);
			
			ServerThread thread = getThread();
			thread.setStreams(inStream, outStream);
		}catch(Exception ex){
			System.out.println("ServerThreadPoolManager: Exception " + ex.getMessage());
		}
	}
	
	/**
	  * Gets an idle thread from the pool or generates new server thread
	  * if necessary.
	  * @return a server thread instant.
	  */
	private ServerThread getThread(){
		for(ServerThread t: threads){
			if(t.isIdle()){
				return t;
			}
		}
		
		ServerThread thread = new ServerThread(new DatabaseServer(model));
		(new Thread(thread)).start();
		threads.add(thread);
		
		// Sleep for a while to let the new thread to initialize
		try{
			Thread.sleep(3000);
		}catch(InterruptedException ex){
		}
		return thread;
	}
}