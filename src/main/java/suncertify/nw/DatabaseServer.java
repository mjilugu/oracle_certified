package suncertify.nw;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import suncertify.Model;

/**
  * This class represents the database server. It is the
  * only entity that interacts directly with the model in the Network server machine.
  * Its service method reads the commands from server, executes them and sends them back.
  * @author Moses L. Jilugu
  * @version March 10, 2013
  */
public class DatabaseServer implements ServerService{
	private Model model;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Logger log;
	
	/**
	  * Creates an instance of this class.
	  * @param model data access model object to be used.
	  */
	public DatabaseServer(Model model){
		this.model = model;
		log = Logger.getLogger("suncertify");
	}

	/**
	  * Reads commands from socket, executes them and sends back
	  * responses through the socket.
	  * @param socket socket object to be serviced.
	  */
	public void service(Socket socket){
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			
			while(true){
				Command command = (Command)ois.readObject();
				command.execute(model);
				oos.writeObject(command);
			}
		} catch (Exception e) {
			log.warning(e.getMessage());
		}
	}
	
	/**
	  * Reads commands from a socket's inputstream, executes them and sends back
	  * responses through the socket's outputstream.
	  * @param inStream ObjectInputStream associated with a socket
	  * @param outStream ObjectOutputStream associated with a socket
	  */
	public void service(ObjectInputStream inStream, ObjectOutputStream outStream) throws Exception{
		try {	
			while(true){
				Command command = (Command)inStream.readObject();
				command.execute(model);
				outStream.writeObject(command);
			}
		} catch (Exception e) {
			log.warning(e.getMessage() + ", closing streams.");
			inStream.close();
			outStream.close();
			throw e;
		}
	}
}