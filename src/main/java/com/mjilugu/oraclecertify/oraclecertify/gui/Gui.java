package com.mjilugu.oraclecertify.oraclecertify.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.BorderFactory;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.mjilugu.oraclecertify.oraclecertify.Model;
import com.mjilugu.oraclecertify.oraclecertify.Controller;
import com.mjilugu.oraclecertify.oraclecertify.Record;
import com.mjilugu.oraclecertify.oraclecertify.db.RecordNotFoundException;
import com.mjilugu.oraclecertify.oraclecertify.db.SecurityException;

/**
  * This class represents the main application window.
  * @author Moses L. Jilugu
  * @version Feb 12, 2013
  */
public class Gui extends JFrame implements ActionListener{
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem exitMenuItem;
	private SearchPanel searchPanel;
	private JPanel contentPane;
	private JLabel titleLabel;
	private JTabbedPane pagesPane;
	private JPanel logsPanel;
	private JTextArea logsTextArea;
	
	private Model model;
	private Controller controller;
	
	private Logger logger = Logger.getLogger("suncertify");
	
	/**
	  * Creates an instance of this class.
	  */
	public Gui(){
		super("Bodgitt and Scarper LLC");
		initComponents();
		logger.setLevel(Level.ALL);
		logger.addHandler(new GuiLogHandler());
	}
	
	/**
	  * Creates an instance of this class.
	  * @param model data model to be used by the gui.
	  */
	public Gui(Model model){
		this();
		this.model = model;
		searchPanel.registerModel(model);
	}
	
	/**
	  * Method to add controller to handle user gesture events.
	  * @param controller controller object to be used by the gui.
	  */
	public void addController(Controller controller){
		this.controller = controller;
		searchPanel.registerController(controller);
	}
	
	/**
	  * Method to display object on the gui.
	  * The object will be display based on its type.
	  * @param object object to be displayed on the gui.
	  */
	public void displayObject(Object object){
		if(object instanceof Record[]){
			logsTextArea.setText("");
			searchPanel.display(object);
		}else if(object instanceof RecordNotFoundException){
			searchPanel.clear();
			logger.info(((Exception)object).getMessage());
		}else if(object instanceof SecurityException){
			searchPanel.clear();
			logger.severe(((Exception)object).getMessage());
		}
	}
	
	/**
	  * This method refreshes the records displayed on the records panel.
	  */
	public void refreshRecordsPanel(){
		try{
			searchPanel.refresh();
		}catch(Exception ex){
			logger.severe(ex.getMessage());
		}
	}
	
	/**
	  * Listen for user actions on the main window.
	  * @param event object containing event information.
	  */
	public void actionPerformed(ActionEvent event){
		if("exit".equals(event.getActionCommand())){
			System.exit(0);
		}
	}
	
	/**
	  * Formats logs to be displayed on the gui.
	  */
	private class GuiLogHandler extends java.util.logging.Handler{
		String message = "";
		
		public void publish(LogRecord logRecord){
			message = logRecord.getLevel() + ": " + logRecord.getMessage();
			logsTextArea.setText(message);
		}
		
		public void flush(){
		}
		
		public void close(){
			message = null;
		}
	}
	
	/**
	  * Instantiates and initializes GUI components.
	  */
	private void initComponents(){
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setActionCommand("exit");
		exitMenuItem.addActionListener(this);
		fileMenu.add(exitMenuItem);
		menuBar.add(fileMenu);
	
		searchPanel = new SearchPanel();
		
		titleLabel = new JLabel("<html><font size=14 align=center>Bodgitt and Scarper LLC</font>", JLabel.CENTER);
		titleLabel.setPreferredSize(new Dimension(200,80));
		titleLabel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		logsTextArea = new JTextArea(5, 100);
		logsTextArea.setEditable(false);
		
		logsPanel = new JPanel(new BorderLayout());
		logsPanel.setBorder(BorderFactory.createTitledBorder("Logs"));
		logsPanel.add(new JScrollPane(logsTextArea), BorderLayout.CENTER);
		
		pagesPane = new JTabbedPane();
		pagesPane.addTab("Search",null,searchPanel,"Search for Contractors");
		
		contentPane = new JPanel(new BorderLayout());
		contentPane.add(titleLabel, BorderLayout.PAGE_START);
		contentPane.add(pagesPane, BorderLayout.CENTER);
		contentPane.add(logsPanel, BorderLayout.PAGE_END);
		
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setJMenuBar(menuBar);
		this.setContentPane(contentPane);
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	}
}