package com.mjilugu.oraclecertify.oraclecertify.nw;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
  * This class represents the network server.
  * @author Moses L. Jilugu
  * @version March 20, 2013
  */
public class NetworkServer {
	private ServerSocket serverSocket;
	private int port;
	private ServerService service;
	private Logger logger;
	
	/**
	  * Creates an instance of this class.
	  * @param port port number to launch the server
	  * @param service ServerService instance to pass on accepted connections from clients
	  */
	public NetworkServer(int port, ServerService service){
		this.port = port;
		this.service = service;
		logger = Logger.getLogger("suncertify");
		
		listen();
	}
	
	/**
	  * Creates a ServerSocket and listens for connections.
	  */
	private void listen(){
		try {
			serverSocket = new ServerSocket(port);
			while(true){
				logger.info("Server listening on port " + port);
				Socket socket = serverSocket.accept();
				logger.info("Connection from " + socket.getInetAddress().getHostAddress() + " accepted.");
				service.service(socket);
			}
		} catch (Exception e) {
			logger.severe(e.getMessage());
		}
	}
}
