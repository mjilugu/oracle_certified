package com.mjilugu.oraclecertify.oraclecertify.gui;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.GroupLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.mjilugu.oraclecertify.oraclecertify.Model;
import com.mjilugu.oraclecertify.oraclecertify.Controller;
import com.mjilugu.oraclecertify.oraclecertify.Record;

/**
  * This class represents the search and records table area on the main gui
  * @author Moses L. Jilugu
  * @version Feb 12, 2013
  */
public class SearchPanel extends JPanel implements RecordsPanel, ActionListener{
	private JPanel searchPanel;
	private JPanel searchPanelBackground;
	private JPanel resultsPanel;
	private JPanel actionPanel;

	private JLabel nameLabel;
	private JLabel locLabel;

	private JTextField nameTextField;
	private JTextField locTextField;

	private JButton getAllRecordsButton;
	private JButton getRecordsButton;
	private JButton bookButton;
	private JButton cancelButton;

	private JTable recordsTable;
	private TableModel tableModel;
	
	private Controller controller;
	private Model model;
	private String[] criteria;
	
	private Logger logger = Logger.getLogger("suncertify");
	
	/**
	  * Creates an instance of the class.
	  */
	public SearchPanel(){
		initComponents();
	}
	
	/**
	  * Overriden method from action listener. Captures events from the 
	  * search panel.
	  * @param event event captured
	  */
	public void actionPerformed(ActionEvent event){
		if("Get All Records".equals(event.getActionCommand())){
			getAllRecordsActionPerformed(event);
		}else if("Get Records".equals(event.getActionCommand())){
			getRecordsActionPerformed(event);
		}else if("Book Contractors".equals(event.getActionCommand())){
			bookContractorsActionPerformed(event);
		}else if("Cancel Booking".equals(event.getActionCommand())){
			cancelBookingActionPerformed(event);
		}
	}
	
	/**
	  * Register a controller to handle events from the gui.
	  * @param controller controller object to be used.
	  */
	public void registerController(Controller controller){
		this.controller = controller;
	}
	
	/**
	  * Register a data model object.
	  * @param model data access model.
	  */
	public void registerModel(Model model){
		this.model = model;
		tableModel = (TableModel)recordsTable.getModel();
	}
	
	/**
	  * Display object on the result table or error log.
	  * @param object object to be displayed.
	  */
	public void display(Object object){
		if(object instanceof Record[])
			refreshRecordTable((Record[])object);
	}
	
	/**
	  * Refreshes the displayed data on the table.
	  */
	public void refresh(){
		try{
			Record[] records= model.getRecords(criteria);
			refreshRecordTable(records);
		}catch(Exception e){
			logger.severe(e.getMessage());
		}
	}
	
	/**
	  * Clear the results table.
	  */
	public void clear(){
		recordsTable.setModel(new TableModel());
	}
	
	/**
	  * Instantiates and initializes components on the panel
	  */
	private void initComponents(){
		//Panels
		searchPanel = new JPanel(new GridLayout(5,2));
		searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		
		resultsPanel = new JPanel(new BorderLayout());
		resultsPanel.setBorder(BorderFactory.createTitledBorder("Results"));
		
		actionPanel = new JPanel();
		actionPanel.setBorder(BorderFactory.createTitledBorder("Action"));

		//Labels
		nameLabel = new JLabel("Name");
		locLabel = new JLabel("Location");

		//TextFields
		nameTextField = new JTextField();
		locTextField = new JTextField();

		//Buttons
		getAllRecordsButton = new JButton("Get All Records");
		getAllRecordsButton.setActionCommand("Get All Records");
		getAllRecordsButton.addActionListener(this);
		
		getRecordsButton = new JButton("Get Records");
		getRecordsButton.setActionCommand("Get Records");
		getRecordsButton.addActionListener(this);
		
		bookButton = new JButton("Book Contractors");
		bookButton.setActionCommand("Book Contractors");
		bookButton.addActionListener(this);
		
		cancelButton = new JButton("Cancel Booking");
		cancelButton.setActionCommand("Cancel Booking");
		cancelButton.addActionListener(this);

		//Tables
		recordsTable = new JTable(new TableModel());
		adjustColumnsWidth();
		
		//Put elements to panels
		searchPanel.add(nameLabel);
		searchPanel.add(nameTextField);
		searchPanel.add(locLabel);
		searchPanel.add(locTextField);
		searchPanel.add(getRecordsButton);
		searchPanel.add(getAllRecordsButton);
		searchPanelBackground = new JPanel(new GridLayout(2,1));
		searchPanelBackground.add(searchPanel);
		
		actionPanel.add(bookButton);
		actionPanel.add(cancelButton);
		
		resultsPanel.add(new JScrollPane(recordsTable), BorderLayout.CENTER);
		recordsTable.setFillsViewportHeight(false);
		resultsPanel.add(actionPanel, BorderLayout.PAGE_END);
		
		//Layout
		this.setLayout(new BorderLayout());
		this.add(searchPanelBackground, BorderLayout.LINE_START);
		this.add(resultsPanel, BorderLayout.CENTER);
		
		//Set Accessible names
		nameTextField.getAccessibleContext().setAccessibleName("nameTextField");
		locTextField.getAccessibleContext().setAccessibleName("locTextField");
		getRecordsButton.getAccessibleContext().setAccessibleName("getRecordsButton");
		getAllRecordsButton.getAccessibleContext().setAccessibleName("getAllRecordsButton");
	}
	
	/**
	  * Custom table model for the data to be displayed on the 
	  * results table.
	  */
	private class TableModel extends AbstractTableModel{
		String[] columnNames = {"ID.","Name", "Location", "Specialties", "Size","Rate","Owner","Select"};
		Object[][] data;
		int rows = 1;
		int columns = 8;
		
		public TableModel(){
			this(1,8);
		}
		
		public TableModel(int rows, int columns){
			super();
			data = new Object[rows][columns];
			
			for(int i = 0; i < rows; i++){
				for(int j = 0; j < columns - 1; j++){
					data[i][j] = "";
				}
				data[i][columns - 1] = new Boolean(false);
			}
			
			this.rows = rows;
			this.columns = columns;
		}
		
		public int getColumnCount(){
			return columnNames.length;
		}
			
		public int getRowCount(){
			return data.length;
		}
			
		public String getColumnName(int col){
			return columnNames[col];
		}
			
		public Object getValueAt(int row, int col){
			return data[row][col];
		}
			
		public Class getColumnClass(int c){
			return getValueAt(0, c).getClass();
		}
			
		public boolean isCellEditable(int row, int col){
			if(col == columns - 1)
				return true;
			return false;
		}
			
		public void setValueAt(Object value, int row, int col){
			data[row][col] = value;
		}
	}
	
	/**
	  * Refreshes the displayed data on results table.
	  * @param records
	  */
	private void refreshRecordTable(Record[] records){
		try{
			tableModel = new TableModel(records.length, 8);
			
			for(int i = 0; i < records.length; i++){
				tableModel.setValueAt(records[i].getId(), i, 0);
				tableModel.setValueAt(records[i].getName(),i,1);
				tableModel.setValueAt(records[i].getLocation(),i,2);
				tableModel.setValueAt(records[i].getSpecialties(),i,3);
				tableModel.setValueAt(records[i].getSize(),i,4);
				tableModel.setValueAt(records[i].getRate(),i,5);
				tableModel.setValueAt(records[i].getOwner(),i,6);
				tableModel.setValueAt(new Boolean(false), i, 7);
			}
			recordsTable.setModel(tableModel);
			adjustColumnsWidth();
		}catch(Exception ex){
			logger.severe(ex.getMessage());
		}
	}

	/**
	  * Handle Get All button press event
	  * @param event
	  */
	private void getAllRecordsActionPerformed(ActionEvent event) {
		nameTextField.setText("");
		locTextField.setText("");
		criteria = new String[]{null,null,null,null,null,null};
		controller.handleGetAllRecordsGesture();
	}

	/**
	  * Handle Get Records button press event
	  * @param event
	  */
	private void getRecordsActionPerformed(ActionEvent event) {
		String name = nameTextField.getText();
		String loc = locTextField.getText();

		name.trim();
		loc.trim();
		if(name.length() == 0)
			name = null;
		if(loc.length() == 0)
			loc = null;
			
		criteria = new String[] {name,loc,null,null,null,null};
		controller.handleGetRecordsGesture(criteria);
	}
	
	/**
	  * Handle Book button press event
	  * @param event
	  */
	private void bookContractorsActionPerformed(ActionEvent event){
		int rows = tableModel.getRowCount();
		int columns = tableModel.getColumnCount();
		ArrayList<Record> records = new ArrayList<Record>();
		
		try{
			for(int i = 0; i < rows; i++){
				if(tableModel.getValueAt(i, columns - 1).equals(true)){
					if(tableModel.getValueAt(i, 6).toString().trim().length() > 0){
						JOptionPane.showMessageDialog(this, "You can only book records which are not currently booked.\n" +
								"Please deselect all records which have owner.\n\n" +
								"NOTE: You can use cancel booking functionality to " +
								"cancel bookings and re-book the record to another customer.\n\n","Selection Error",JOptionPane.WARNING_MESSAGE);
						return;
					}
					Record rec = new Record();
					rec.setId(Long.parseLong(tableModel.getValueAt(i, 0).toString()));
					rec.setName(tableModel.getValueAt(i, 1).toString());
					rec.setLocation(tableModel.getValueAt(i, 2).toString());
					rec.setSpecialties(tableModel.getValueAt(i, 3).toString());
					rec.setSize(tableModel.getValueAt(i, 4).toString());
					rec.setRate(tableModel.getValueAt(i,5).toString());
					records.add(rec);
				}
			}
		
			if(records.size() < 1){
				JOptionPane.showMessageDialog(this, "Please Select atleast one record.","Selection Error",JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			String owner_id = JOptionPane.showInputDialog(this,"Enter ID(8-digit number) of the Customer:");
			if(owner_id == null ||owner_id.length() != 8){
				JOptionPane.showMessageDialog(this, "You have entered invalid customer number. Customer number\n" +
						" should be exactly 8 digits.","",JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			int confirm = JOptionPane.showConfirmDialog(this, "Please Confirm Booking to Customer with id " + owner_id,"Confirm Booking.",JOptionPane.YES_NO_OPTION);
			if(confirm == JOptionPane.YES_OPTION){
				int status = controller.handleBookContractorsGesture(records, owner_id);
				if(status == 0){
					JOptionPane.showMessageDialog(this, "Records successfully booked to owner with id " + owner_id);
				}else{
					JOptionPane.showMessageDialog(this,"An error has occured, Records could not be booked to owner with id " + owner_id, "Booking Error.", JOptionPane.ERROR_MESSAGE);
				}
			}
		}catch(NumberFormatException ex){
			JOptionPane.showMessageDialog(this, "Please Do Not Select empty records.","Selection Error",JOptionPane.WARNING_MESSAGE);
			return;
		}catch(Exception ex){
			logger.warning(ex.getMessage());
		}
	}
	
	/**
	  * Handle Cancel Booking button press event
	  * @param event
	  */
	private void cancelBookingActionPerformed(ActionEvent event){
		int rows = tableModel.getRowCount();
		int columns = tableModel.getColumnCount();
		ArrayList<Record> records = new ArrayList<Record>();
		
		try{
			for(int i = 0; i < rows; i++){
				if(tableModel.getValueAt(i, columns - 1).equals(true)){
					if(tableModel.getValueAt(i, 6).toString().trim().length() == 0){
						JOptionPane.showMessageDialog(this, "You can only cancel booking for records which\n" +
								" are currently booked. Deselect records which are not booked.","Selection Error",JOptionPane.WARNING_MESSAGE);
						return;
					}
					Record rec = new Record();
					rec.setId(Long.parseLong(tableModel.getValueAt(i, 0).toString()));
					rec.setName(tableModel.getValueAt(i, 1).toString());
					rec.setLocation(tableModel.getValueAt(i, 2).toString());
					rec.setSpecialties(tableModel.getValueAt(i, 3).toString());
					rec.setSize(tableModel.getValueAt(i, 4).toString());
					rec.setRate(tableModel.getValueAt(i,5).toString());
					rec.setOwner("");
					
					records.add(rec);
				}
			}
			
			if(records.size() < 1){
				JOptionPane.showMessageDialog(this, "Please Select atleast one record.","Selection Error",JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			int confirm = JOptionPane.showConfirmDialog(this, "Please Confirm Cancellation of Bookings.","Comfirm Booking Cancelation",JOptionPane.YES_NO_OPTION);
			if(confirm == JOptionPane.YES_OPTION){
				int status = controller.handleCancelBookingGesture(records);
				if(status == 0){
					JOptionPane.showMessageDialog(this, "Successfully cancelled booking to the records.");
				}else{
					JOptionPane.showMessageDialog(this,"An error has occured, could not cancel booking to the records.", "Booking Error.", JOptionPane.ERROR_MESSAGE);
				}
			}
		}catch(NumberFormatException ex){
			JOptionPane.showMessageDialog(this, "Please Do Not Select empty records.","Selection Error",JOptionPane.WARNING_MESSAGE);
			return;
		}catch(Exception ex){
			logger.warning(ex.getMessage());
		}
	}
	
	/**
	  * Adjust column width for some columns on the results table.
	  */
	private void adjustColumnsWidth(){
		try{
			recordsTable.getColumnModel().getColumn(7).setPreferredWidth(3);
			recordsTable.getColumnModel().getColumn(6).setPreferredWidth(8);
			recordsTable.getColumnModel().getColumn(5).setPreferredWidth(8);
			recordsTable.getColumnModel().getColumn(4).setPreferredWidth(6);
			recordsTable.getColumnModel().getColumn(0).setPreferredWidth(4);
		}catch(Exception ex){
			logger.warning(ex.getMessage());
		}
	}
}