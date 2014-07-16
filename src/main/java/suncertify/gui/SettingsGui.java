package suncertify.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;

import suncertify.PropertiesReader;
import suncertify.DatabaseSettingsController;

/**
  * This class represents the Settings window.
  * @author Moses L. Jilugu
  * @version Jan 13, 2013
  */
public class SettingsGui extends JFrame{
	private PropertiesReader propertiesReader;
	private String hostStr;
	private int portNumber;
	private String databaseFile;
	private JPanel contentPane;
	private SettingsPanel settingsPanel;
	private String launchMode;
	
	/**
	  * Creates an instance of the class
	  * @param mode mode string either alone or server mode
	  */
	public SettingsGui(String mode){
		super("Bodgitt and Scarper LLC");
		this.launchMode = mode;
		initComponents();
	}
	
	/**
	  * Method to prompt the user to accept current settings as in properties file or
	  * to enter new settings.
	  */
	public void checkCurrentSettings(){
		String message = "";
		if(launchMode.equalsIgnoreCase("alone")){
			message = "Click YES to launch the application with " +
				"\ncurrent settings or NO to enter new settings: " +
				"\n\nFile Path: " + databaseFile + "\n\n";
		}else if(launchMode.equalsIgnoreCase("server")){
			message = "Click YES to launch the network server with " +
				"\ncurrent settings or NO to enter new settings: " +
				"\n\nServer Port: " + portNumber +
				"\nFile Path: " + databaseFile + "\n\n";
		}else if(launchMode.equalsIgnoreCase("clientOnly")){
			message = "Click YES to launch the application with " +
				"\ncurrent settings or NO to enter new settings: " +
				"\n\nServer Host: " + hostStr +
				"\nServer Port: " + portNumber + "\n\n";
		}
		
		int use_current_settings = JOptionPane.showConfirmDialog(this, message, "Confirm Current Settings", JOptionPane.YES_NO_OPTION);
		if(use_current_settings == JOptionPane.YES_OPTION){
			this.setVisible(false);
			this.dispose();
		}
	}
	
	/**
	  * Instantiates and initializes components on the window
	  */
	private void initComponents(){
		try{
			propertiesReader = PropertiesReader.getSharedInstance();
			hostStr = propertiesReader.getDatabaseHost();
			portNumber = propertiesReader.getDatabasePort();
			databaseFile = propertiesReader.getDatabaseFile();
			settingsPanel = new SettingsPanel(launchMode,this);
			settingsPanel.registerController(new DatabaseSettingsController());
		}catch(IOException ex){
		}
		
		contentPane = new JPanel(new GridLayout(2,1));
		contentPane.setPreferredSize(new Dimension(500,400));
		
		
		contentPane.add(settingsPanel);
		contentPane.add(new JPanel());
		this.setContentPane(contentPane);
		this.setResizable(false);
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	}
}