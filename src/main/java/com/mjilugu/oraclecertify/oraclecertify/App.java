package com.mjilugu.oraclecertify.oraclecertify;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import com.mjilugu.oraclecertify.oraclecertify.db.Data;
import com.mjilugu.oraclecertify.oraclecertify.db.ModelImpl;
import com.mjilugu.oraclecertify.oraclecertify.gui.Gui;
import com.mjilugu.oraclecertify.oraclecertify.gui.SettingsGui;
import com.mjilugu.oraclecertify.oraclecertify.nw.ModelNetworkImpl;
import com.mjilugu.oraclecertify.oraclecertify.nw.NetworkClient;
import com.mjilugu.oraclecertify.oraclecertify.nw.NetworkServer;
import com.mjilugu.oraclecertify.oraclecertify.nw.ServerThreadPoolManager;

/**
 * This is the main class for the application. It provides
 * methods to get user settings, initialize database parameters from
 * properties file and to launch the application whether in single mode or client only mode
 * or to launch the network server mode.
 * @author Moses L. Jilugu
 * @version Dec 11, 2012
 */
public class App extends WindowAdapter{
	private Data data;
	private Model model;
	private View view;
	private Controller controller;
	
	private static PropertiesReader propertiesReader;
	private static String serverHost;
	private static int portNumber;
	private static String databaseFile;
	
	private static String launchMode;
	private static Logger logger;
	private static final App app = new App();
	
	/**
	  * This is the main entry point of the application
	  * @param args String array of commandline arguments.
	  */
	public static void main(String[] args){
		try{
			propertiesReader = PropertiesReader.getSharedInstance();
			logger = Logger.getLogger("suncertify");
			if(args == null || args.length == 0 ){
				launchMode = "clientOnly";
				app.getUserSettings();
			}else if(args[0].equalsIgnoreCase("alone")){
				launchMode = "alone";
				app.getUserSettings();
			}else if(args[0].equalsIgnoreCase("server")){	
				launchMode = "server";
				app.getUserSettings();
			}else{
				printUsage();
			}
		}catch(Exception ex){
			logger.severe(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	/**
	  * This method launches the application and allows access to a file
	  * in the local machine and not through the network server.
	  */
	public void launchSingleMode(){
		try{
			data = new Data(databaseFile);
			model = new ModelImpl(data);
			final Gui gui = new Gui(model);
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run(){
					gui.pack();
					gui.setVisible(true);
				}
			});
			
			view = new ViewImpl(model, gui);
			controller = new ControllerImpl(model,view);
		}catch(Exception ex){
			logger.severe(ex.getMessage());
			System.exit(-1);
		}
	}
	
	/**
	  * This method launches the network server that access the specified file
	  * and listens on the specified port for client connections.
	  */
	public void launchServer(){
		try{
			Data data = new Data(databaseFile);
			Model model = new ModelImpl(data);
			ServerThreadPoolManager ioPoolManager = new ServerThreadPoolManager(model);
			NetworkServer nwServer = new NetworkServer(portNumber, ioPoolManager);
		}catch(Exception e){
			logger.severe(e.getMessage());
		}
	}
	
	/**
	  * Launches the GUI and the network client 
	  * that connects to a remote server.
	  */
	public void launchClient(){
		try{
			NetworkClient nwClient = new NetworkClient(serverHost, portNumber);
			Model model = new ModelNetworkImpl(nwClient);
			
			final Gui gui = new Gui(model);
			java.awt.EventQueue.invokeLater(new Runnable(){
				public void run(){
					gui.pack();
					gui.setVisible(true);
				}
			});
			
			View view = new ViewImpl(model, gui);
			Controller controller = new ControllerImpl(model, view);
		}catch(Exception e){
			logger.severe(e.getMessage());
		}
	}
	
	/**
	  * This method is an overridden method from WindowAdapter. It responds
	  * to the close event of the first window in the launch sequence i.e. the settings window.
	  */
	public void windowClosed(WindowEvent e){
		initDBParameters();
		launchApp();
	}
	
	/**
	  * This method launches the network server or
	  * the application in either single mode or
	  * as a client to the server.
	  */
	private void launchApp(){
		if(launchMode.equalsIgnoreCase("alone")){
			app.launchSingleMode();
		}else if(launchMode.equalsIgnoreCase("server")){
			app.launchServer();
		}else if(launchMode.equalsIgnoreCase("clientOnly")){
			app.launchClient();
		}
	}
	
	/**
	  * This method launches the first window in launch sequence to 
	  * confirm current settings read from properties file or persist
	  * new settings from the user.
	  */
	private void getUserSettings(){
		SettingsGui gui = new SettingsGui(launchMode);
		gui.addWindowListener(this);
		gui.pack();
		gui.setLocationRelativeTo(null);
		gui.setVisible(true);
		gui.checkCurrentSettings();
	}
	
	/**
	  * This method reads and initializes database parameters
	  * from the properties file.
	  */
	private void initDBParameters(){
		serverHost = propertiesReader.getDatabaseHost();
		portNumber = propertiesReader.getDatabasePort();
		databaseFile = propertiesReader.getDatabaseFile();
	}
	
	/**
	  * This method prints how the application should be launched
	  * in case the user provide wrong commandline options.
	  */
	private static void printUsage(){
		System.out.println("Usage: ");
		System.out.println("\tjava -jar <path_and_filename> [<mode>]");
		System.out.println("\nViable Mode options:");
		System.out.println("\talone: launches the application as a standalone application.");
		System.out.println("\tserver: launches the network server.");
		System.out.println("\tOR provide no argument to launch network client and gui.");
	}
}