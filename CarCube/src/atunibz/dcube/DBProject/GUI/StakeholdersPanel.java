package atunibz.dcube.DBProject.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
	private String[] caseCusCus, caseSup, caseBoth;
	private JComboBox cus_sup, criteria;
	private JScrollPane scrollPane;
	private JLabel search;
	private JList list;
	private JTextField searchField;
	private Connection conn;
	
	
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
		
		String [] stakeH = {"Suppliers", "Customers", "Both"};
		cus_sup = new JComboBox (stakeH);
		comboPanel.add(cus_sup);
		cus_sup.addActionListener(new SHlistener());
		shPanel.add(comboPanel);
		//combo box to choose criteria
		String [] machecazzoserve = {"Select stakeholder first"};
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
		
		criteria = new JComboBox (machecazzoserve);
		criteria.setEnabled(false);
		criteria.addActionListener(new CriteriaListener());
		comboPanel.add(criteria);
		
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
		shPanel.add((Box.createRigidArea(new Dimension(0, 20))));
		shPanel.add(scrollPanel);
		
		
		
		
		add(shPanel);
	}
	
	// listener class to update the criteria combo box
	private class SHlistener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			searchField.setText("");
			String selected = (String) cus_sup.getSelectedItem();
			
			if(selected.compareTo("Suppliers") == 0) {
				criteria.setModel(new DefaultComboBoxModel(caseSup));
			} else if(selected.compareTo("Customers") == 0) {
				criteria.setModel(new DefaultComboBoxModel(caseCusCus));
			} else {
				
			}
			
			criteria.setEnabled(true);
		}
		
	}
	
	private class CriteriaListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			searchField.setText("");
			String selected = (String) cus_sup.getSelectedItem();
			String selected2 = (String) criteria.getSelectedItem();
			String [] results = null;
			
			if(selected.compareTo("Customers") == 0) {
				switch (selected2) {
				case "Name": results = resultQuery("customer", "c_name", "");
				break;
				case "Surname": results = resultQuery("customer", "c_surname", "");
				break;
				case "Tax Code": results = resultQuery("customer", "tax_code", "");
				break;
				case "Phone": results = resultQuery("phone_contact", "phone_number", "");
				break;
				case "Mail": results = resultQuery("mail_contact", "mail", "");
				break;
				case "Fax": results = resultQuery("fax_contact", "fax", "");
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
				case "Name": results = resultQuery("customer", "c_name", inserted);
				break;
				case "Surname": results = resultQuery("customer", "c_surname", inserted);
				break;
				case "Tax Code": results = resultQuery("customer", "tax_code", inserted);
				break;
				case "Phone": results = resultQuery("phone_contact", "phone_number", inserted);
				break;
				case "Mail": results = resultQuery("mail_contact", "mail", inserted);
				break;
				case "Fax": results = resultQuery("fax_contact", "fax", inserted);
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
	
	String [] resultQuery (String table, String attribute, String inserted) {
		ArrayList <String> resultList = null;
		try {
			Statement st = conn.createStatement();
			String sql = "SELECT * FROM " + table + " WHERE UPPER(" + attribute + ") like UPPER('%" + inserted + "%')";
			resultList = new ArrayList <String>();
			ResultSet rs = st.executeQuery(sql);
		
			
			while(rs.next()) {
				resultList.add(rs.getString(attribute));
			}
	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Results are " + resultList.size());
		String [] result = resultList.toArray(new String[resultList.size()]);
		return result;
	}

}
