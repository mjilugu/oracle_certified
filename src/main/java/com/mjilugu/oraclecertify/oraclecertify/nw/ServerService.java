package com.mjilugu.oraclecertify.oraclecertify.nw;

import java.net.Socket;

/**
 * This class provides a common interface for various
 * entities on the network server. Each entity on the server
 * performs a service on a request from a client represented by
 * a network socket.
 * @author Moses L. Jilugu.
 * @version March 13, 2013.
 */
public interface ServerService {
	
	/**
	  * This method will define action that an entity on the network server
      * performs on a socket connection.
	  * @param socket socket connection to be serviced.
	  */
	public void service(Socket socket) throws Exception;
}
