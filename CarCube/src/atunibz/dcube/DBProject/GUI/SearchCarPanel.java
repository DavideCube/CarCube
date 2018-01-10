package atunibz.dcube.DBProject.GUI;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import atunibz.dcube.DBProject.configuration.AppResources;

public class SearchCarPanel extends JPanel{
	private Connection conn;
	private JPanel scPanel, titlePanel, researchPanel, buttonPanel;
	private JPanel carPanel;
	private JScrollPane pane;
	private JCheckBox newCar, usedCar;
	private JComboBox<String> make, model, price, year, sold;
	private String[] allMakes, allModels;
	private JButton search, advancedSearch, back, addCar;
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
		make.addActionListener(new MakeListener());
		
		//Model at beginning disable
		model = new JComboBox<String>();
		model.addItem("All Models");
		allModels = getModels(2); // see comment above
		
		for(String s: allModels)
			model.addItem(s);
		model.setSelectedIndex(0);
		model.addActionListener(new ModelListener());
		
		// add combobox to search starting from a specific year
		year = new JComboBox <String>();
		
		// fill the combobox with years starting from 1950
		year.addItem (OPTION);
		for (int i = 0; i <=68; i++) {
			year.addItem(Integer.toString(2018 - i));
		}
		year.setSelectedIndex(0);
		year.addActionListener(new YearListener());
		
		// combo box for selecting the maximum price for the research
		price = new JComboBox <String> ();
		
		// fill the combobox with prices
		price.addItem (OPTION2);
		for (int j = 0; j < 20; j++) {
			price.addItem(5000 + (5000*j) + " €");
		}
		price.setSelectedIndex(0);
		price.addActionListener(new PriceListener());
		
		// sold comboBox
		sold = new JComboBox <String> ();
		sold.addItem("Sold or not");
		sold.addItem("Not sold");
		sold.addItem("Sold");
		sold.setSelectedIndex(0);
		sold.addActionListener(new SoldListener());
		
		// add button to search
		search = new JButton ();
		search.setIcon(new ImageIcon("icons/searchCar.png"));
		search.setHorizontalTextPosition(SwingConstants.RIGHT);
		search.setForeground(new Color (255, 128, 0));
		search.addActionListener(new SearchListener());
		AppResources.changeFont(search, Font.BOLD, 20);
		researchQuery();
		
		//Advanced Search button
		advancedSearch = new JButton("Advanced Search");
		AppResources.changeFont(advancedSearch, Font.PLAIN, 17);
		advancedSearch.addActionListener(new AdvancedSearchListener() );
		
		researchPanel.add(newCar);
		researchPanel.add(usedCar);
		researchPanel.add(make);
		researchPanel.add(model);
		researchPanel.add(year);
		researchPanel.add(price);
		researchPanel.add(sold);
		researchPanel.add(advancedSearch);
		researchPanel.add(search);
		
		scPanel.add(researchPanel);
		scPanel.add((Box.createRigidArea(new Dimension(0, 10))));
		
		// panel that shows the list of cars
		JPanel bigContainer = new JPanel();
		JPanel bigcarPanel = new JPanel ();
		carPanel = new JPanel();
		carPanel.setOpaque(false);
		carPanel.setLayout(new BoxLayout(carPanel, BoxLayout.Y_AXIS));
		bigcarPanel.add(carPanel);
		pane = new JScrollPane(bigcarPanel);
		pane.setOpaque(false);
		pane.setPreferredSize(new Dimension (830, 400));
		bigContainer.add(pane);
		bigContainer.setOpaque(false);
		scPanel.add(bigContainer);
		
		// buttonPanel
		scPanel.add((Box.createRigidArea(new Dimension(0, 20))));
		buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		// back
		back = AppResources.iconButton("Go back     ", "icons/back.png");
		back.addActionListener(new BackListener());
		buttonPanel.add(back);
		// add new car
		addCar = AppResources.iconButton("Add Car    ", "icons/transportation.png");
		buttonPanel.add(Box.createRigidArea(new Dimension (50,0)));
		buttonPanel.add(addCar);
		
		
		scPanel.add(buttonPanel);
		
		
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
			model.addItem("All Models");
			for(String s: allModels)
				model.addItem(s);
			researchQuery();
		}
		
	}
	
	//listener for the change item make field sembrava che aggiungessi parole a caso
	private class MakeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			//Change make accordingly
			int param = 0;
			if(newCar.isSelected() && !usedCar.isSelected())
				param = 0;
			else if(!newCar.isSelected() && usedCar.isSelected())
				param = 1;
			else
				param = 2;
			
			//Change model accordingly
			JComboBox selectedCombo = (JComboBox) e.getSource();
			String selectedMake = (String) selectedCombo.getSelectedItem();	
			model.removeAllItems();
			model.addItem("All Models");
			
			if (selectedMake != null && selectedMake.compareTo("All Makes") != 0) {
				allModels = getModels(param); //See method comments for more info
				for(String s: allModels)
					model.addItem(s);
			}
			
			String makeSelected = (String) make.getSelectedItem();
			System.out.println("Make Listener");
			if (makeSelected != null && make.getItemCount() != 1)
				researchQuery();
		}
	}
	
	// listener class for the combobox that allows the selection of the model
	private class ModelListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Model Listener");
			String modelSelected = (String) model.getSelectedItem();
			String makeSelected = (String) make.getSelectedItem();
			if (modelSelected != null && makeSelected != null && model.getItemCount() != 1)
				researchQuery();
			
		}
		
	}
	
	// listener class for the combobox that allows the selection of the year
	private class YearListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Year Listener");
			String modelSelected = (String) model.getSelectedItem();
			String makeSelected = (String) make.getSelectedItem();
			if (modelSelected != null && makeSelected != null)
				researchQuery();
			
		}
		
	}
	// listener class for the combobox that allows the selection of the price
	private class PriceListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Price Listener");
			String modelSelected = (String) model.getSelectedItem();
			String makeSelected = (String) make.getSelectedItem();
			if (modelSelected != null && makeSelected != null)
				researchQuery();
			
		}
		
	}
	
	// listener class for the combobox that allows the selection of the price
		private class SoldListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Sold Listener");
				String modelSelected = (String) model.getSelectedItem();
				String makeSelected = (String) make.getSelectedItem();
				if (modelSelected != null && makeSelected != null)
					researchQuery();
				
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
			stat.close();
			rs.close();
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
			stat.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tPiccola.toArray(new String[tPiccola.size()]);
	}
	
	
	// very looong method that executes the correct query according to which combination of combo boxes is selected
	// the method executes the query and set the text of the search button with the number of results of the query
	public void researchQuery () {
		String totalQuery = null;
		String newCarQuery = "SELECT make, model FROM new_car";
		String usedCarQuery = "SELECT make, model FROM used_car";
		// first of all, get all the selected item from the combo boxes
		String makeSelected = (String) make.getSelectedItem();
		String modelSelected = (String) model.getSelectedItem();
		String yearSelected = (String) year.getSelectedItem();
		String priceSelected = (String) price.getSelectedItem();
		String soldSelected = (String) sold.getSelectedItem();
		int yearInt = 0;
		int priceInt = 0;
		// evaluate if we are searching in new cars, used cars, or both
	
		if (newCar.isSelected()) {
			// look at the make combo box (if it is equal "All makes" then no changes in the query)
			if (makeSelected.compareTo("All Makes") != 0) {
				newCarQuery += " WHERE make = '" + makeSelected + "'";
				// look at the model (inside this "if" because if we are not entered here, then no model is automatically not selected
				if (modelSelected.compareTo("All Models") != 0) {
					newCarQuery += " AND model = '" + modelSelected + "'";
				}
			}
			// look at the year
			if (yearSelected.compareTo("From year") != 0) {
				yearInt = Integer.parseInt(yearSelected);
				newCarQuery += " INTERSECT SELECT make, model FROM new_car WHERE car_year >= " + yearInt;
			}
			// look at the price
			if (priceSelected.compareTo("Price up to") != 0) {
				priceInt = Integer.parseInt(priceSelected.substring(0, priceSelected.lastIndexOf(" €")));
				newCarQuery += " INTERSECT SELECT make, model FROM new_car WHERE base_price <= " + priceInt;
			}
			// look at sold or not sold
			if (soldSelected.compareTo("Sold or not") != 0) {
				if (soldSelected.compareTo("Not sold") == 0)
					newCarQuery += " INTERSECT SELECT make, model FROM new_car WHERE sold = 0";
				else
					newCarQuery += " INTERSECT SELECT make, model FROM new_car WHERE sold = 1";
			}
				
		}
		
		if (usedCar.isSelected()) {
			// look at the make combo box (if it is equal "All makes" then no changes in the query)
			if (makeSelected.compareTo("All Makes") != 0) {
				usedCarQuery += " WHERE make = '" + makeSelected + "'";
				// look at the model (inside this "if" because if we are not entered here, then no model is automatically not selected
				if (modelSelected.compareTo("All Models") != 0) {
					usedCarQuery += " AND model = '" + modelSelected + "'";
				}
			}
			// look at the year
			if (yearSelected.compareTo(OPTION) != 0) {
				yearInt = Integer.parseInt(yearSelected);
				usedCarQuery += " INTERSECT SELECT make, model FROM used_car WHERE car_year >= " + yearInt;
			}
			// look at the price
			if (priceSelected.compareTo(OPTION2) != 0) {
				priceInt = Integer.parseInt(priceSelected.substring(0, priceSelected.lastIndexOf(" €")));
				usedCarQuery += " INTERSECT SELECT make, model FROM used_car WHERE net_price <= " + priceInt;
			}
			// look at sold or not sold
			if (soldSelected.compareTo("Sold or not") != 0) {
				if (soldSelected.compareTo("Not sold") == 0)
					usedCarQuery += " INTERSECT SELECT make, model FROM used_car WHERE sold = 0";
				else
					usedCarQuery += " INTERSECT SELECT make, model FROM used_car WHERE sold = 1";
			}
		}
		// CASE BOTH
		if (newCar.isSelected() && usedCar.isSelected())
			totalQuery = "(" + newCarQuery + ") UNION ALL (" + usedCarQuery + ")";
		// CASE NEW
		else if (newCar.isSelected() && !usedCar.isSelected())
			totalQuery = newCarQuery;
		// CASE USED
		else
			totalQuery = usedCarQuery;
		
		try {
			Statement stat = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			System.out.println(totalQuery);
			ResultSet rs = stat.executeQuery(totalQuery);
			int rowcount = 0;
			if (rs.last()) {
			  rowcount = rs.getRow();
			}
			if (rowcount == 0) {
				search.setEnabled(false);;
			}
			else 
				search.setEnabled(true);
			
			search.setText(rowcount + ((rowcount == 1) ? " car" : " cars"));
			stat.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// listener for back button
	private class BackListener implements ActionListener  {

		@Override
		public void actionPerformed(ActionEvent e) {
			MainPanel.getMainPanel().swapPanel(new LogoPanel());
			
		}
		
	}
	
	//Listener for the advanced search button
	private class AdvancedSearchListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			MainPanel.getMainPanel().swapPanel(new AdvancedSearchPanel(newCar.isSelected(), usedCar.isSelected(), make.getSelectedIndex(), model.getSelectedIndex(), year.getSelectedIndex(), price.getSelectedIndex(), sold.getSelectedIndex()));	
		}
		
	}
	
	private class SearchListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String newCarQuery = "SELECT * FROM new_car";
			String usedCarQuery = "SELECT * FROM used_car";
			// first of all, get all the selected item from the combo boxes
			String makeSelected = (String) make.getSelectedItem();
			String modelSelected = (String) model.getSelectedItem();
			String yearSelected = (String) year.getSelectedItem();
			String priceSelected = (String) price.getSelectedItem();
			String soldSelected = (String) sold.getSelectedItem();
			int yearInt = 0;
			int priceInt = 0;
			// evaluate if we are searching in new cars, used cars, or both
			if (newCar.isSelected()) {
				

				// look at the make combo box (if it is equal "All makes" then no changes in the
				// query)
				if (makeSelected.compareTo("All Makes") != 0) {
					newCarQuery += " WHERE make = '" + makeSelected + "'";
					// look at the model (inside this "if" because if we are not entered here, then
					// no model is automatically not selected
					if (modelSelected.compareTo("All Models") != 0) {
						newCarQuery += " AND model = '" + modelSelected + "'";
					}
				}
				// look at the year
				if (yearSelected.compareTo("From year") != 0) {
					yearInt = Integer.parseInt(yearSelected);
					newCarQuery += " INTERSECT SELECT * FROM new_car WHERE car_year >= " + yearInt;
				}
				// look at the price
				if (priceSelected.compareTo("Price up to") != 0) {
					priceInt = Integer.parseInt(priceSelected.substring(0, priceSelected.lastIndexOf(" €")));
					newCarQuery += " INTERSECT SELECT * FROM new_car WHERE base_price <= " + priceInt;
				}
				// look at sold or not sold
				if (soldSelected.compareTo("Sold or not") != 0) {
					if (soldSelected.compareTo("Not sold") == 0)
						newCarQuery += " INTERSECT SELECT * FROM new_car WHERE sold = 0";
					else
						newCarQuery += " INTERSECT SELECT * FROM new_car WHERE sold = 1";
				}
				
			}
			if (usedCar.isSelected()) {
				// look at the make combo box (if it is equal "All makes" then no changes in the query)
				if (makeSelected.compareTo("All Makes") != 0) {
					usedCarQuery += " WHERE make = '" + makeSelected + "'";
					// look at the model (inside this "if" because if we are not entered here, then no model is automatically not selected
					if (modelSelected.compareTo("All Models") != 0) {
						usedCarQuery += " AND model = '" + modelSelected + "'";
					}
				}
				// look at the year
				if (yearSelected.compareTo(OPTION) != 0) {
					yearInt = Integer.parseInt(yearSelected);
					usedCarQuery += " INTERSECT SELECT * FROM used_car WHERE car_year >= " + yearInt;
				}
				// look at the price
				if (priceSelected.compareTo(OPTION2) != 0) {
					priceInt = Integer.parseInt(priceSelected.substring(0, priceSelected.lastIndexOf(" €")));
					usedCarQuery += " INTERSECT SELECT * FROM used_car WHERE net_price <= " + priceInt;
				}
				// look at sold or not sold
				if (soldSelected.compareTo("Sold or not") != 0) {
					if (soldSelected.compareTo("Not sold") == 0)
						usedCarQuery += " INTERSECT SELECT * FROM used_car WHERE sold = 0";
					else
						usedCarQuery += " INTERSECT SELECT * FROM used_car WHERE sold = 1";
				}
			}
			
			// CASE BOTH
			if (newCar.isSelected() && usedCar.isSelected()) {
				try {
					Statement stat = conn.createStatement();
					ResultSet rs = stat.executeQuery(newCarQuery);
					carPanel.removeAll();
					createPanelList (rs);
					
					System.out.println("CASE BOTH: \n");
					System.out.println("\t" + newCarQuery);
					System.out.println("\n\t" + usedCarQuery);
					stat = conn.createStatement();
					rs = stat.executeQuery(usedCarQuery);
					//carPanel.removeAll();
					createPanelListUsed(rs);
					
					scPanel.revalidate();
					scPanel.repaint();
					
					
					stat.close();
					rs.close();
				} catch (SQLException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
			// CASE NEW
			else if (newCar.isSelected() && !usedCar.isSelected()) {
				Statement stat;
				try {
					stat = conn.createStatement();
					ResultSet rs = stat.executeQuery(newCarQuery);
					carPanel.removeAll();
					createPanelList (rs);
					scPanel.revalidate();
					scPanel.repaint();
					
					stat.close();
					rs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			// CASE USED
			else {
				Statement stat;
				try {
					stat = conn.createStatement();
					ResultSet rs = stat.executeQuery(usedCarQuery);
					carPanel.removeAll();
					createPanelListUsed (rs);
					scPanel.revalidate();
					scPanel.repaint();
					
					stat.close();
					rs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	// method for creating a panel for each result
	public void createPanelList (ResultSet rs) {
		
		try {
			while (rs.next()) {
				// create panel
				JPanel currentCarPanel = new JPanel ();
				currentCarPanel.setLayout(new BoxLayout(currentCarPanel, BoxLayout.X_AXIS));
				currentCarPanel.setPreferredSize(new Dimension (800, 200));
				
				// create panel and label for the car image
				JLabel imageLabel = new JLabel();
				int car_id = rs.getInt("car_id");
				imageLabel.setIcon(AppResources.scaleProfileImage(Integer.toString(car_id), true));
				
				// create support panel 
				JPanel informationPanel = new JPanel();
				//informationPanel.setPreferredSize(new Dimension (300, 50));
				informationPanel.setLayout(new BoxLayout(informationPanel, BoxLayout.Y_AXIS));
				
				//create first row panel
				JPanel firstRow = new JPanel();
				firstRow.setLayout(new BorderLayout());
				JPanel support = new JPanel ();
				JPanel support2 = new JPanel();
				JLabel make = new JLabel(rs.getString("make"));
				make.setFont(new Font ("Helvetica", Font.BOLD, 20));
				make.setIcon(new ImageIcon ("icons/new.png"));
				make.setHorizontalTextPosition(SwingConstants.RIGHT);
				JLabel model = new JLabel (rs.getString("model"));
				model.setFont(new Font ("Helvetica", Font.PLAIN, 20));
				int calcPrice = (rs.getInt("base_price") + calculatePrice (car_id));
				NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
				String priceFormatted = currencyFormat.format(calcPrice);
				JLabel price = new JLabel (priceFormatted);
				price.setFont(new Font ("Helvetica", Font.BOLD, 20));
				support.add(make);
				support.add(model);
				support2.add(price);
				firstRow.add(support, BorderLayout.WEST);
				firstRow.add(support2, BorderLayout.EAST);
				informationPanel.add(firstRow);
				
				// create second row panel
				JPanel secondRow = new JPanel();
				secondRow.setLayout(new BorderLayout());
				String forLabel = "<html>   <p style=\"padding-left:5px;\"><b>" + Character.toUpperCase(rs.getString("car_type").charAt(0)) + rs.getString("car_type").substring(1) + "</b> with <b>" + rs.getInt("doors") + " doors </b> and <b>" + rs.getInt("seats") + " seats </b>";
				JPanel support3 = new JPanel(); 
				
				
				// create third row panel
				
				String query = "select engine.horsepower, engine.fuel, engine.transmission, color.color_name from new_car \n" + 
						"inner join engine on new_car.engine = engine.engine_id \n" + 
						"inner join new_painting on new_car.car_id = new_painting.car_id\n" + 
						"inner join color on new_painting.color_code = color.color_code\n" + 
						"WHERE new_car.car_id = " + car_id;
				Statement stat= conn.createStatement();
				ResultSet rs2 = stat.executeQuery(query);
				String horsepower = "", fuel = "", transmission = "";
				String colorString = "";
				boolean firstColor = true;
				while(rs2.next()) {
					
					horsepower = rs2.getString("horsepower");
					fuel = rs2.getString("fuel");
					transmission = rs2.getString("transmission");
					if(firstColor) {
						colorString+= rs2.getString("color_name");
						firstColor = false;
					}
					else
						colorString+= ", " + rs2.getString("color_name");
				}
				
				stat.close();
				rs2.close();
				forLabel += "<br>Powered by a <b>" + fuel + "</b> engine with <b>" + horsepower + " kw</b> and <b>" + transmission + "</b> transmission";
				forLabel += "<br> Colored in <b>" + colorString + "</b> </p> </html>";
				
				JLabel info1 = new JLabel(forLabel);
				info1.setFont(info1.getFont().deriveFont(Font.PLAIN, 15));
				support3.add(info1);
				secondRow.add(support3, BorderLayout.WEST);
				informationPanel.add(secondRow);
				
				// third Row
				JPanel lastRow = new JPanel();
				lastRow.setLayout(new BorderLayout());
				JPanel support10 = new JPanel ();
				JPanel support11 = new JPanel();
				JButton jenson = new JButton("View car");
				jenson.setFont(new Font ("Helvetica", Font.BOLD, 15));
				jenson.setIcon(new ImageIcon ("icons/eye.png"));
				jenson.setHorizontalTextPosition(SwingConstants.RIGHT);
				JLabel soldOrNotSold =  new JLabel ();;
				if (rs.getInt("sold") == 1) 
					soldOrNotSold.setIcon(new ImageIcon ("icons/jenson.png"));
				else
					soldOrNotSold.setIcon(new ImageIcon ("icons/sale.png"));
					
				support10.add(jenson);
				support11.add(soldOrNotSold);
				lastRow.add(support11, BorderLayout.WEST);
				lastRow.add(support10, BorderLayout.EAST);
				informationPanel.add(lastRow);
				
				
				currentCarPanel.add(imageLabel);
				currentCarPanel.add(informationPanel);
				currentCarPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
				carPanel.add(currentCarPanel);
				
				rs.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// method for creating a panel for each result
		public void createPanelListUsed (ResultSet rs) {
			
			try {
				while (rs.next()) {
					// create panel
					JPanel currentCarPanel = new JPanel ();
					currentCarPanel.setLayout(new BoxLayout(currentCarPanel, BoxLayout.X_AXIS));
					currentCarPanel.setPreferredSize(new Dimension (800, 200));
					
					// create panel and label for the car image
					JLabel imageLabel = new JLabel();
					String car_id = rs.getString("immatriculation");
					imageLabel.setIcon(AppResources.scaleProfileImage(car_id, false));
					
					// create support panel 
					JPanel informationPanel = new JPanel();
					//informationPanel.setPreferredSize(new Dimension (300, 50));
					informationPanel.setLayout(new BoxLayout(informationPanel, BoxLayout.Y_AXIS));
					
					//create first row panel
					JPanel firstRow = new JPanel();
					firstRow.setLayout(new BorderLayout());
					JPanel support = new JPanel ();
					JPanel support2 = new JPanel();
					JLabel make = new JLabel(rs.getString("make"));
					make.setFont(new Font ("Helvetica", Font.BOLD, 20));
					make.setIcon(new ImageIcon ("icons/used.png"));
					make.setHorizontalTextPosition(SwingConstants.RIGHT);
					JLabel model = new JLabel (rs.getString("model"));
					model.setFont(new Font ("Helvetica", Font.PLAIN, 20));
					int calcPrice = (rs.getInt("net_price") );
					NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
					String priceFormatted = currencyFormat.format(calcPrice);
					JLabel price = new JLabel (priceFormatted);
					price.setFont(new Font ("Helvetica", Font.BOLD, 20));
					support.add(make);
					support.add(model);
					support2.add(price);
					firstRow.add(support, BorderLayout.WEST);
					firstRow.add(support2, BorderLayout.EAST);
					informationPanel.add(firstRow);
					
					// create second row panel
					JPanel secondRow = new JPanel();
					secondRow.setLayout(new BorderLayout());
					String forLabel = "<html>   <p style=\"padding-left:5px;\"><b> "+ rs.getInt("mileage") + " km</b> <br> <b>" + Character.toUpperCase(rs.getString("car_type").charAt(0)) + rs.getString("car_type").substring(1) + "</b> with <b>" + rs.getInt("doors") + " doors </b> and <b>" + rs.getInt("seats") + " seats </b>";
					JPanel support3 = new JPanel(); 
					
					
					// create third row panel
					
					String query = "select engine.horsepower, engine.fuel, engine.transmission, color.color_name FROM used_car \n" + 
							"inner join engine on used_car.engine = engine.engine_id \n" + 
							"inner join used_painting on used_car.immatriculation = used_painting.immatriculation\n" + 
							"inner join color on used_painting.color_code = color.color_code\n" + 
							"WHERE used_car.immatriculation = '" + car_id + "'";
					Statement stat= conn.createStatement();
					ResultSet rs2 = stat.executeQuery(query);
					String horsepower = "", fuel = "", transmission = "";
					String colorString = "";
					boolean firstColor = true;
					while(rs2.next()) {
						
						horsepower = rs2.getString("horsepower");
						fuel = rs2.getString("fuel");
						transmission = rs2.getString("transmission");
						if(firstColor) {
							colorString+= rs2.getString("color_name");
							firstColor = false;
						}
						else
							colorString+= ", " + rs2.getString("color_name");
					}
					
					stat.close();
					rs2.close();
					forLabel += "<br>Powered by a <b>" + fuel + "</b> engine with <b>" + horsepower + " kw</b> and <b>" + transmission + "</b> transmission";
					forLabel += "<br> Colored in <b>" + colorString + "</b> </p> </html>";
					
					JLabel info1 = new JLabel(forLabel);
					info1.setFont(info1.getFont().deriveFont(Font.PLAIN, 15));
					support3.add(info1);
					secondRow.add(support3, BorderLayout.WEST);
					informationPanel.add(secondRow);
					
					// third Row
					JPanel lastRow = new JPanel();
					lastRow.setLayout(new BorderLayout());
					JPanel support10 = new JPanel ();
					JPanel support11 = new JPanel();
					JButton jenson = new JButton("View car");
					jenson.setFont(new Font ("Helvetica", Font.BOLD, 15));
					jenson.setIcon(new ImageIcon ("icons/eye.png"));
					jenson.setHorizontalTextPosition(SwingConstants.RIGHT);
					JLabel soldOrNotSold =  new JLabel ();;
					if (rs.getInt("sold") == 1) 
						soldOrNotSold.setIcon(new ImageIcon ("icons/jenson.png"));
					else
						soldOrNotSold.setIcon(new ImageIcon ("icons/sale.png"));
						
					support10.add(jenson);
					support11.add(soldOrNotSold);
					lastRow.add(support11, BorderLayout.WEST);
					lastRow.add(support10, BorderLayout.EAST);
					informationPanel.add(lastRow);
					
					
					currentCarPanel.add(imageLabel);
					currentCarPanel.add(informationPanel);
					currentCarPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
					carPanel.add(currentCarPanel);
					
					rs.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	public int calculatePrice (int car_id) {
		int result = 0;
		try {
			String query = "SELECT sum(price) FROM new_equipped INNER JOIN optional ON new_equipped.optional_id = optional.optional_id AND new_equipped.car_id = " + car_id;
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			rs.next();
			result = rs.getInt("sum");
			
			stat.close();
			rs.close();
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return result;
		
	}

}
