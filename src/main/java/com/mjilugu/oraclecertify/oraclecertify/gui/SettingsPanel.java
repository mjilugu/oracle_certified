package com.mjilugu.oraclecertify.oraclecertify.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import com.mjilugu.oraclecertify.oraclecertify.DatabaseSettingsController;

/**
  * This class represents the main panel of the Settings window.
  * @author Moses L. Jilugu
  * @version Feb 13, 2013
  */
public class SettingsPanel extends JPanel implements ActionListener{
	private JPanel contentPane;
	private JPanel dbLocationPanel;
	private JPanel actionPanel;
	private JPanel pathPanel;
	private JPanel labelsPanel;
	private JPanel inputsPanel;

	private JLabel hostLabel;
	private JLabel portLabel;
	private JLabel pathLabel;
	private JTextField hostTextField;
	private JTextField portTextField;
	private JTextField pathTextField;

	private JButton setButton;
	private JButton resetButton;
	private JButton setDefaultButton;
	private JButton cancelButton;
	private JButton browseButton;
	private JFileChooser fileChooser;
	private JFrame parentFrame;
	private DatabaseSettingsController controller;
	private String launchMode;
	
	/**
	  * Creates an instance of this class.
	  * @param mode mode string
	  * @param parent parent frame
	  */
	public SettingsPanel(String mode, JFrame parent){
		this.launchMode = mode;
		this.parentFrame = parent;
		initComponents();
	}
	
	/**
	  * Method to register the controller to handle events from this panel.
	  * @param controller controller object to be used.
	  */
	public void registerController(DatabaseSettingsController controller){
		this.controller = controller;
	}
	
	/**
	  * Overriden method from ActionListener. Receives events 
	  * and dispatches to appropriate handlers.
	  * @param event event captured.
	  */
	public void actionPerformed(ActionEvent event){
		if("Set".equals(event.getActionCommand())){
			setActionPerformed(event);
		}else if("Set Default".equals(event.getActionCommand())){
			setDefaultActionPerformed(event);
		}else if("browse".equals(event.getActionCommand())){
			browseActionPerformed(event);
		}
	}

	/**
	  * Instantiates and initializes components on this panel.
	  */
	private void initComponents(){
		dbLocationPanel = new JPanel(new BorderLayout());
		dbLocationPanel.setBorder(BorderFactory.createTitledBorder("DB Location"));
		
		actionPanel = new JPanel(new GridLayout(1,3));
		actionPanel.setBorder(BorderFactory.createTitledBorder("Action"));
		
		pathPanel = new JPanel(new BorderLayout());
		
		labelsPanel = new JPanel(new GridLayout(4,1));
		inputsPanel = new JPanel(new GridLayout(4,1));
		
		hostLabel = new JLabel("Database Host");
		portLabel = new JLabel("Database Port");
		pathLabel = new JLabel("File Path");
		hostTextField = new JTextField(30);
		portTextField = new JTextField(4);
		pathTextField = new JTextField(30);
		
		setButton = new JButton("Set");
		setButton.setActionCommand("Set");
		setButton.addActionListener(this);
		
		resetButton = new JButton("Reset");
		resetButton.setActionCommand("Reset");
		resetButton.addActionListener(this);
		
		setDefaultButton = new JButton("Set Default");
		setDefaultButton.setActionCommand("Set Default");
		setDefaultButton.addActionListener(this);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		
		browseButton = new JButton("browse...");
		browseButton.setActionCommand("browse");
		browseButton.addActionListener(this);
		
		fileChooser = new JFileChooser();
		
		// Layout
		pathPanel.add(pathTextField, BorderLayout.CENTER);
		pathPanel.add(browseButton, BorderLayout.LINE_END);
		
		labelsPanel.add(hostLabel);
		labelsPanel.add(portLabel);
		labelsPanel.add(pathLabel);
		labelsPanel.add(new JPanel());
		inputsPanel.add(hostTextField);
		inputsPanel.add(portTextField);
		inputsPanel.add(pathPanel);
		inputsPanel.add(new JPanel());
		dbLocationPanel.add(labelsPanel, BorderLayout.LINE_START);
		dbLocationPanel.add(inputsPanel, BorderLayout.CENTER);
		
		actionPanel.add(new JPanel());
		actionPanel.add(setButton);
		actionPanel.add(setDefaultButton);
		
		if(launchMode.equals("alone")){			
			hostTextField.setEditable(false);
			portTextField.setEditable(false);
		}else if(launchMode.equals("server")){
			hostTextField.setEditable(false);
		}else if(launchMode.equals("clientOnly")){
			pathTextField.setEditable(false);
			browseButton.setEnabled(false);
		}
		
		this.setPreferredSize(new Dimension(85, 5));
		this.setLayout(new BorderLayout());
		this.add(dbLocationPanel, BorderLayout.CENTER);
		this.add(actionPanel, BorderLayout.SOUTH);
	}
	
	/**
	  * Method to handle the Set button press
	  * @param event
	  */
	private void setActionPerformed(ActionEvent event){
		String hostStr = hostTextField.getText();
		String portStr = portTextField.getText();
		String pathStr = pathTextField.getText();
		int portInt;
		
		try{
			String message = "";
			if(launchMode.equalsIgnoreCase("alone")){
				// check path value
				if(pathStr == null || pathStr.length() == 0){
					JOptionPane.showMessageDialog(this,"Database file path cannot be blank.","Input Error.",JOptionPane.WARNING_MESSAGE);
					return;
				}
				message = "The following Settings will be saved:\n" +
					"\nPath: "+pathStr+"\n\n" +
					"Please confirm this settings.\n\n";
			}else if(launchMode.equalsIgnoreCase("server")){
				// check path value
				if(pathStr == null || pathStr.length() == 0){
					JOptionPane.showMessageDialog(this,"Database file path cannot be blank.","Input Error.",JOptionPane.WARNING_MESSAGE);
					return;
				}
				// check port value
				if(!portStr.matches("^[0-9]{1,4}$")){
					JOptionPane.showMessageDialog(this,"Port value can only take upto 4 digit integers.","Input Error.",JOptionPane.WARNING_MESSAGE);
					return;
				}
				portInt = Integer.parseInt(portStr);
				message = "The following Settings will be saved:\n" +
					"\nPort: "+portStr+"\nPath: "+pathStr+"\n\n" +
					"Please confirm this settings.\n\n";
			}else if(launchMode.equalsIgnoreCase("clientOnly")){
				// check host string
				if(!(hostStr.trim().matches("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$") || hostStr.trim().equalsIgnoreCase("localhost"))){
					JOptionPane.showMessageDialog(this, "The host IP should be in form ddd.ddd.ddd.ddd where d " +
							"\nis a digit","Input Error",JOptionPane.WARNING_MESSAGE);
					return;
				}
				// check port value
				if(!portStr.matches("^[0-9]{1,4}$")){
					JOptionPane.showMessageDialog(this,"Port value can only take upto 4 digit integers.","Input Error.",JOptionPane.WARNING_MESSAGE);
					return;
				}
				portInt = Integer.parseInt(portStr);
				message = "The following Settings will be saved:\n" +
					"\nHost: "+hostStr +"\nPort: "+portStr+"\n\n" +
					"Please confirm this settings.\n\n";
			}
			
			int confirm = JOptionPane.showConfirmDialog(this, message,"Confirm Settings.",JOptionPane.YES_NO_OPTION);
			if(confirm == JOptionPane.YES_OPTION){
				if(launchMode.equalsIgnoreCase("alone")){
					controller.persistDatabaseSettings(null,null,pathStr);
					message = "Settings have been saved. To begin using your\n" +
					" new settings, click YES.\n";
				}else if(launchMode.equalsIgnoreCase("server")){
					controller.persistDatabaseSettings(null,portStr, pathStr);
					message = "Settings have been saved. To Launch the server\n" +
					" with the new settings, click YES.\n";
				}else if(launchMode.equalsIgnoreCase("clientOnly")){
					controller.persistDatabaseSettings(hostStr, portStr,null);
					message = "Settings have been saved. To begin using your\n" +
					" new settings, click YES.\n";
				}
				
				JOptionPane.showMessageDialog(this, message);
				parentFrame.setVisible(false);
				parentFrame.dispose();
			}
		}catch(Exception e){
			if(e instanceof NumberFormatException)
				JOptionPane.showMessageDialog(this,"Port value can only take upto 4 digit integers.","Input Error.",JOptionPane.ERROR_MESSAGE);
			else
				JOptionPane.showMessageDialog(this,"An error has occured, Settings were not saved.","Input Error.",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	  * Method to handle the Set Default button press.
	  * @param event
	  */
	private void setDefaultActionPerformed(ActionEvent event){
		String message = "";
		if(launchMode.equalsIgnoreCase("alone")){
			pathTextField.setText("db-2x2.db");
			message = "The following Settings will be saved:\n" +
				"\nPath: db-2x2.db\n\n" +
				"Please confirm this settings.\n";
		}else if(launchMode.equalsIgnoreCase("server")){
			portTextField.setText("4469");
			pathTextField.setText("db-2x2.db");
			message = "The following Settings will be saved:\n" +
				"\nPort: 4469\nPath: db-2x2.db\n\n" +
				"Please confirm this settings.\n";
		}else if(launchMode.equalsIgnoreCase("clientOnly")){
			hostTextField.setText("localhost");
			portTextField.setText("4469");
			message = "The following Settings will be saved:\n" +
				"\nHost: localhost\nPort: 4469\n\n" +
				"Please confirm this settings.\n";
		}
		
		int confirm = JOptionPane.showConfirmDialog(this, message,"Confirm Settings.",JOptionPane.YES_NO_OPTION);
		if(confirm == JOptionPane.YES_OPTION){
			if(launchMode.equalsIgnoreCase("alone")){
				controller.persistDatabaseSettings(null,null,"db-2x2.db");
				message = "Settings have been saved. To begin using your\n" +
				" new settings, click YES.";
			}else if(launchMode.equalsIgnoreCase("server")){
				controller.persistDatabaseSettings(null,"4469", "db-2x2.db");
				message = "Settings have been saved. To launch the server with\n" +
				" your new settings, click YES.";
			}else if(launchMode.equalsIgnoreCase("clientOnly")){
				controller.persistDatabaseSettings("localhost","4469",null);
				message = "Settings have been saved. To begin using your\n" +
				" new settings, click YES.";
			}
			
			JOptionPane.showMessageDialog(this, message);
			parentFrame.setVisible(false);
			parentFrame.dispose();
		}
	}
	
	/**
	  * Method to handle browse button press event.
	  * @param event
	  */
	private void browseActionPerformed(ActionEvent event){
		int returnVal = fileChooser.showOpenDialog(SettingsPanel.this);
		
		if(returnVal == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			pathTextField.setText(file.getAbsolutePath());
		}
	}
}