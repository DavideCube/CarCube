package atunibz.dcube.DBProject.GUI;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import atunibz.dcube.DBProject.configuration.AppResources;

public class SearchCarPanel extends JPanel{
	private Connection conn;
	private JPanel scPanel, titlePanel, researchPanel;
	private JCheckBox newCar, usedCar;
	private JComboBox<String> make, model, price, year;
	private String[] allMakes, allModels;
	private JButton search;
	private static final String OPTION = "From year";
	private static final String OPTION2 = "Price up to";
	
	
	public SearchCarPanel () {
		conn = DatabaseConnection.getDBConnection().getConnection();
		scPanel = new JPanel();
		scPanel.setLayout(new BoxLayout(scPanel, BoxLayout.Y_AXIS));
		scPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		// Panel containing the beautiful logo
		titlePanel = AppResources.carCubePanel();
		scPanel.add(titlePanel);
		scPanel.setOpaque(false);
		scPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		
		// panel in which we will select the various fields
		researchPanel = new JPanel();
		researchPanel.setLayout(new BoxLayout(researchPanel, BoxLayout.X_AXIS));
		researchPanel.setOpaque(false);
		//cbg = new CheckboxGroup ();
		newCar = new JCheckBox ("New", true);
		newCar.setOpaque(false);
		newCar.addItemListener(new BoxListener() );
		usedCar = new JCheckBox ("Used", true);
		usedCar.setOpaque(false);
		usedCar.addItemListener(new BoxListener() );
		
		//MAKE combobox with strange things
		make = new JComboBox<String>();
		
		allMakes = getMakes(2); //See method comments for more info
		make.addItem("All Makes");
		for(String s: allMakes)
			make.addItem(s);
		make.setSelectedIndex(0);
		make.addItemListener(new MakeListener());
		
		//Model at beginning disable
		model = new JComboBox<String>();
		model.addItem("All Models");
		allModels = getModels(2); // see comment above
		
		for(String s: allModels)
			model.addItem(s);
		model.setSelectedIndex(0);
		
		// add combobox to search starting from a specific year
		year = new JComboBox <String>();
		
		// fill the combobox with years starting from 1950
		year.addItem (OPTION);
		for (int i = 0; i <=68; i++) {
			year.addItem(Integer.toString(2018 - i));
		}
		
		// combo box for selecting the maximum price for the research
		price = new JComboBox <String> ();
		
		// fill the combobox with prices
		price.addItem (OPTION2);
		for (int j = 0; j < 20; j++) {
			price.addItem(5000 + (5000*j) + " €");
		}
		
		// add button to search
		search = new JButton ("Search");
		search.setForeground(new Color (255, 128, 0));
		AppResources.changeFont(search, Font.BOLD, 20);
		
		researchPanel.add(newCar);
		researchPanel.add(usedCar);
		researchPanel.add(make);
		researchPanel.add(model);
		researchPanel.add(year);
		researchPanel.add(price);
		researchPanel.add(search);
		scPanel.add(researchPanel);
		
		
		add(scPanel);
	}
	
	// listener for the chechboxes
	private class BoxListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			// who generates it?
			JCheckBox which = (JCheckBox) e.getItem();
			//Block remove all checkboxes
			if (which == newCar) {
				if (!which.isSelected() && !usedCar.isSelected() )
					which.setSelected(true);
				
			} else {
				if (!which.isSelected() && !newCar.isSelected() )
					which.setSelected(true);
			}
			
			//Change make accordingly
			int param = 0;
			if(newCar.isSelected() && !usedCar.isSelected())
				param = 0;
			else if(!newCar.isSelected() && usedCar.isSelected())
				param = 1;
			else
				param = 2;
			
			
			allMakes = getMakes(param); //See method comments for more info
			make.removeAllItems();
			make.addItem("All Makes");
			for(String s: allMakes)
				make.addItem(s);
			
			//Change model accordingly
			allModels = getModels(param); //See method comments for more info
			model.removeAllItems();
			model.addItem("All models");
			for(String s: allModels)
				model.addItem(s);
		}
		
	}
	
	//listener for the change item make field sembrava che aggiungessi parole a caso
	private class MakeListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			
			//Change make accordingly
			int param = 0;
			if(newCar.isSelected() && !usedCar.isSelected())
				param = 0;
			else if(!newCar.isSelected() && usedCar.isSelected())
				param = 1;
			else
				param = 2;
			
			//Change model accordingly
			String selectedMake = (String) e.getItem();	
			model.removeAllItems();
			model.addItem("All Models");
			if (selectedMake.compareTo("All Makes") != 0) {
				allModels = getModels(param); //See method comments for more info
				for(String s: allModels)
					model.addItem(s);
			}
		}
	}
	//Get all makes from DBDBDBDBDBDB
	//Commento serio: 0 = new car; 1 = used car; 2 = boat cars;
	public String[] getMakes(int typeOfQuery) {
		
		ArrayList<String> tPiccola = new ArrayList<String>();
		String getMakes = null;
		switch(typeOfQuery) {
		case 0: getMakes = "SELECT DISTINCT make FROM new_car"; break;
		case 1: getMakes = "SELECT DISTINCT make FROM used_car"; break;
		case 2: getMakes = "SELECT DISTINCT make FROM new_car UNION DISTINCT SELECT DISTINCT make FROM used_car"; break;
		}
		
		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(getMakes);
			while(rs.next()) {
				tPiccola.add(rs.getString("make"));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tPiccola.toArray(new String[tPiccola.size()]);

	}
	
	// Get all models from DBDBDBDBDBDB
	// Commento serio: 0 = new car; 1 = used car; 2 = boat cars;
	public String[] getModels(int typeOfQuery) {

		ArrayList<String> tPiccola = new ArrayList<String>();
		String getModels = null;
		String selectedMake = (String) make.getSelectedItem();
		switch (typeOfQuery) {
		case 0:
			getModels = "SELECT DISTINCT model FROM new_car WHERE make='" + selectedMake + "'";
			break;
		case 1:
			getModels = "SELECT DISTINCT model FROM used_car WHERE make='" + selectedMake + "'";
			break;
		case 2:
			getModels = "SELECT DISTINCT model FROM new_car WHERE make = '" + selectedMake
					+ "' UNION DISTINCT SELECT DISTINCT model FROM used_car WHERE make='" + selectedMake + "'";
			break;
		}

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(getModels);
			while (rs.next()) {
				tPiccola.add(rs.getString("model"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tPiccola.toArray(new String[tPiccola.size()]);
	}
	
	
	
	
}
