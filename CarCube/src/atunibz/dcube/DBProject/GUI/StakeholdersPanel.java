package atunibz.dcube.DBProject.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import atunibz.dcube.DBProject.configuration.AppResources;

public class StakeholdersPanel extends JPanel{
	private JPanel shPanel, titlePanel, comboPanel, scrollPanel;
	private String[] caseCusCus, caseSup;
	private JComboBox cus_sup, criteria;
	private JScrollPane scrollPane;
	private JLabel search;
	private JList list;
	private JTextField searchField;
	private Connection conn;
	private JButton back, addSup, addCus, stats;
	
	
	public StakeholdersPanel() {
		conn = DatabaseConnection.getDBConnection().getConnection();
		shPanel = new JPanel();
		shPanel.setLayout(new BoxLayout(shPanel, BoxLayout.Y_AXIS));
		shPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		// Panel containing the beautiful logo
		titlePanel = AppResources.carCubePanel();
		shPanel.add(titlePanel);
		shPanel.setOpaque(false);
		
		shPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		
		// combo box to choose supplier or customer
		comboPanel = new JPanel();
		comboPanel.setLayout(new BoxLayout(comboPanel, BoxLayout.X_AXIS));
		
		String [] stakeH = {"Customers", "Suppliers"};
		cus_sup = new JComboBox (stakeH);
		comboPanel.add(Box.createRigidArea(new Dimension (45, 0)));
		comboPanel.add(cus_sup);
		cus_sup.addActionListener(new SHlistener());
		
		//combo box to choose criteria
		caseCusCus = new String[6];
		caseCusCus[0] = "Name";
		caseCusCus[1] = "Surname";
		caseCusCus[2] = "Tax Code";
		caseCusCus[3] = "Phone";
		caseCusCus[4] = "Mail";
		caseCusCus[5] = "Fax";
		
		
		caseSup = new String[5];
		caseSup[0] = "Vat";
		caseSup[1] = "Name";
		caseSup[2] = "Phone";
		caseSup[3] = "Mail";
		caseSup[4] = "Fax";
		
		criteria = new JComboBox (caseSup);
		criteria.addActionListener(new CriteriaListener());
		comboPanel.add(criteria);
		comboPanel.add(Box.createRigidArea(new Dimension (45, 0)));
		comboPanel.setOpaque(false);
		shPanel.add(comboPanel);
		
		// add the text field
		JPanel searchPanel = new JPanel ();
		JPanel support = new JPanel ();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
		search = new JLabel ();
		search.setIcon(new ImageIcon ("icons/research.png"));
		searchField = new JTextField (20);
		support.add(search);
		support.add(searchField);
		searchPanel.add(support);
		support.setOpaque(false);
		searchPanel.setOpaque(false);
		shPanel.add(searchPanel);
		searchField.getDocument().addDocumentListener(new MyDocumentListener());
		
		// JList
		String [] results = new String [0];
		list = new JList<String>(results);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// ScrolledPane
		scrollPanel = new JPanel();
		scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(350, 306));
		scrollPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.GRAY, Color.GRAY));
		scrollPane.setAlignmentX(CENTER_ALIGNMENT);
		scrollPanel.add(scrollPane);
		scrollPanel.setOpaque(false);
		shPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		shPanel.add(scrollPanel);
		
		// panel at the bottom which contains the buttons to go back, add a customer, add a supplier, see statistics (maybe?)
		shPanel.add((Box.createRigidArea(new Dimension(0, 60))));
		JPanel buttonPanel = new JPanel ();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		//back
		back = AppResources.iconButton("Go back     ", "icons/back.png");
		buttonPanel.add(back);
		back.addActionListener(new BackListener());
		buttonPanel.add((Box.createRigidArea(new Dimension(50, 0))));
		// stats
		stats = AppResources.iconButton("Statistics  ", "icons/graph.png");
		stats.addActionListener(new StatsListener());
		buttonPanel.add(stats);
		buttonPanel.add((Box.createRigidArea(new Dimension(50, 0))));
		// add customer
		addCus = AppResources.iconButton("Add customer", "icons/user.png");
		addCus.addActionListener(new addCusListener() );
		buttonPanel.add(addCus);
		buttonPanel.add((Box.createRigidArea(new Dimension(50, 0))));
		// add supplier 
		addSup = AppResources.iconButton("Add supplier", "icons/truck.png");
		addSup.addActionListener(new addSupListener() );
		buttonPanel.add(addSup);
		buttonPanel.setOpaque(false);
		
		shPanel.add(buttonPanel);
		cus_sup.setSelectedItem(stakeH[0]);
		criteria.setSelectedItem(stakeH[0]);
		add(shPanel);
	}
	
	// listener class to update the criteria combo box
	private class SHlistener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			searchField.setText("");
			String selected = (String) cus_sup.getSelectedItem();
			String [] results = null;
			
			if(selected.compareTo("Suppliers") == 0) {
				criteria.setModel(new DefaultComboBoxModel(caseSup));
				results = resultQuery("supplier", "vat", "", "vat", false); 
			} else if(selected.compareTo("Customers") == 0) {
				criteria.setModel(new DefaultComboBoxModel(caseCusCus));
				results = resultQuery("customer", "c_name", "", "tax_code", true); 
			} 
			
			DefaultListModel dlm = new DefaultListModel();
			for (int i = 0; i< results.length; i++) {
				dlm.addElement(results[i]);
			}
			list.setModel(dlm);
		}
	}
	
	// listener for the selected criteria from the combo box
	private class CriteriaListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			searchField.setText("");
			String selected = (String) cus_sup.getSelectedItem();
			String selected2 = (String) criteria.getSelectedItem();
			String [] results = null;
			
			if(selected.compareTo("Customers") == 0) {
				switch (selected2) {
				case "Name": results = resultQuery("customer", "c_name", "", "tax_code", true);
				break;
				case "Surname": results = resultQuery("customer", "c_surname", "", "tax_code", true);
				break;
				case "Tax Code": results = resultQuery("customer", "tax_code", "", "tax_code", true);
				break;
				case "Phone": results = resultQuery("phone_contact", "phone_number", "", "phone_number", true);
				break;
				case "Mail": results = resultQuery("mail_contact", "mail", "", "mail", true);
				break;
				case "Fax": results = resultQuery("fax_contact", "fax", "", "fax", true);
				break;
				}
			}
			else if(selected.compareTo("Suppliers") == 0) {
				switch (selected2) {
				case "Name": results = resultQuery("supplier", "name", "", "vat", false);
				break;
				case "Vat": results = resultQuery("supplier", "vat", "", "vat", false);
				break;
				case "Phone": results = resultQuery("phone_contact", "phone_number", "", "phone_number", false);
				break;
				case "Mail": results = resultQuery("mail_contact", "mail", "", "mail", false);
				break;
				case "Fax": results = resultQuery("fax_contact", "fax", "", "fax", false);
				break;	
				}
			}
			DefaultListModel dlm = new DefaultListModel();
			for (int i = 0; i< results.length; i++) {
				dlm.addElement(results[i]);
			}
			list.setModel(dlm);
		}
	}
	
	// listener add to the text field which dynamically searches in the database the inserted characters
	private class MyDocumentListener implements DocumentListener {
		@Override
		public void insertUpdate(DocumentEvent e) {
			searchInDatabase ();
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			searchInDatabase ();	
		}
		@Override
		public void changedUpdate(DocumentEvent e) {
			searchInDatabase ();	
		}
		public void searchInDatabase () {
			// what the user inserts in the search field
			String inserted = searchField.getText();
			// look at the case
			String selected = (String) cus_sup.getSelectedItem();
			String selected2 = (String) criteria.getSelectedItem();
			String [] results = null;
			
			if(selected.compareTo("Customers") == 0) {
				switch (selected2) {
				case "Name": results = resultQuery("customer", "c_name", inserted, "tax_code", true);
				break;
				case "Surname": results = resultQuery("customer", "c_surname", inserted, "tax_code", true);
				break;
				case "Tax Code": results = resultQuery("customer", "tax_code", inserted, "tax_code", true);
				break;
				case "Phone": results = resultQuery("phone_contact", "phone_number", inserted, "phone_number", true);
				break;
				case "Mail": results = resultQuery("mail_contact", "mail", inserted, "mail", true);
				break;
				case "Fax": results = resultQuery("fax_contact", "fax", inserted, "fax", true);
				break;
				}
			}
			else if(selected.compareTo("Suppliers") == 0) {
				switch (selected2) {
				case "Name": results = resultQuery("supplier", "name", inserted, "vat", false);
				break;
				case "Vat": results = resultQuery("supplier", "vat", inserted, "vat", false);
				break;
				case "Phone": results = resultQuery("phone_contact", "phone_number", inserted, "phone_number", false);
				break;
				case "Mail": results = resultQuery("mail_contact", "mail", inserted, "mail", false);
				break;
				case "Fax": results = resultQuery("fax_contact", "fax", inserted, "fax", false);
				break;
				}
			}
			DefaultListModel dlm = new DefaultListModel();
			for (int i = 0; i< results.length; i++) {
				dlm.addElement(results[i]);
			}
			list.setModel(dlm);
		}
	}
	
	// listener to go back to the logo Panel
	private class BackListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			MainPanel.getMainPanel().swapPanel(new LogoPanel());
			
		}
		
	}
	// listener for the statistic button
	private class StatsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			MainPanel.getMainPanel().swapPanel(new StatisticsPanel());
			
		}
		
	}
	
	//listener for the add customer button
	private class addCusListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			MainPanel.getMainPanel().swapPanel(new addCustomerPanel());
				
		}
			
	}
	
	//listener for the add supplier button
	private class addSupListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			MainPanel.getMainPanel().swapPanel(new addSupplierPanel());
			
		}
		
	}
		
	// method that executes the the research query in Database and returns the results as an array of Strings
	public String [] resultQuery (String table, String attribute, String inserted, String primaryAttribute, boolean customer) {
		ArrayList <String> resultList = null;
		try {
			Statement st = conn.createStatement();
			String baseString = "SELECT * FROM " + table + " WHERE UPPER(" + attribute + ") like UPPER('%" + inserted + "%')";
			String sql = null;
			if (table.contains("contact")) {
				if (customer)
					sql = baseString + " and owner_supplier isNULL";
				else
					sql = baseString + " and owner_customer isNULL";
			}
			else 
				sql = baseString;
			resultList = new ArrayList <String>();
			ResultSet rs = st.executeQuery(sql);
	
			while(rs.next()) {
				if (primaryAttribute.compareTo(attribute) == 0)
					resultList.add(rs.getString(attribute));
				else
					resultList.add(rs.getString(attribute) + "  (" + rs.getString (primaryAttribute) + ")");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String [] result = resultList.toArray(new String[resultList.size()]);
		return result;
	}
}
