package atunibz.dcube.DBProject.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import atunibz.dcube.DBProject.configuration.AppResources;

public class AdvancedSearchPanel extends JPanel {

	private Connection conn;
	// private JScrollPane mainPane;
	private JPanel advancedSearch, titlePanel;
	private JPanel carGeneralData, carSpecificData, colorPanel, optionalPanel;

	private JCheckBox newCar, usedCar;
	private JComboBox<String> make, model, price, year, sold, carTypes, seats, doors, fuel, transmissions;
	private JTextField height, length, width, horsepower;
	private String[] allMakes, allModels, allCarTypes, allSeats, allDoors, allFuel, allTransmissions;
	private static final String OPTION = "From year";
	private static final String OPTION2 = "Price up to";
	private static final String OPTION3 = "All types";
	private static final String OPTION4 = "All seats";
	private static final String OPTION5 = "All doors";
	private static final String OPTION6 = "All fuels";
	private static final String OPTION7 = "All transmissions";
	private ArrayList<JCheckBox> colors, optional;
	private JButton back, search;

	public AdvancedSearchPanel(boolean newCarSel, boolean usedCarSel, int selectedMake, int selectedModel,
			int selectedYear, int selectedPrice, int selectedSold) {

		conn = DatabaseConnection.getDBConnection().getConnection();
		advancedSearch = new JPanel();
		advancedSearch.setOpaque(false);
		advancedSearch.setLayout(new BoxLayout(advancedSearch, BoxLayout.Y_AXIS));
		advancedSearch.add((Box.createRigidArea(new Dimension(0, 30))));

		// Initialise arraylists
		optional = new ArrayList<JCheckBox>();

		// Param for first search based on constructor indication
		int param;
		if (newCarSel && !usedCarSel)
			param = 0;
		else if (!newCarSel && usedCarSel)
			param = 1;
		else
			param = 2;

		// Panel containing the beautiful logo
		titlePanel = AppResources.carCubePanel();
		advancedSearch.add(titlePanel);
		advancedSearch.setOpaque(false);
		advancedSearch.add((Box.createRigidArea(new Dimension(0, 30))));

		// CAR GENERAL DATA

		carGeneralData = new JPanel();
		carGeneralData.setOpaque(false);
		carGeneralData.setLayout(new BoxLayout(carGeneralData, BoxLayout.Y_AXIS));

		// Label
		JPanel carTypeLabelPanel = new JPanel();
		carTypeLabelPanel.setOpaque(false);
		JLabel carTypeLabel = new JLabel("General Data");
		carTypeLabel.setHorizontalAlignment(SwingConstants.LEFT);
		AppResources.changeFont(carTypeLabel, Font.BOLD, 25);
		carTypeLabelPanel.add(carTypeLabel);

		// Content -> New or used?

		JPanel carTypeContent = new JPanel();
		carTypeContent.setOpaque(false);
		carTypeContent.setLayout(new BoxLayout(carTypeContent, BoxLayout.X_AXIS));
		newCar = new JCheckBox("New");
		newCar.setOpaque(false);
		
		if (newCarSel)
			newCar.setSelected(true);
		else
			newCar.setSelected(false);

		usedCar = new JCheckBox("Used");
		usedCar.setOpaque(false);
		//usedCar.addItemListener(new BoxListener());
		if (usedCarSel)
			usedCar.setSelected(true);
		else
			usedCar.setSelected(false);

		carTypeContent.add(newCar);
		carTypeContent.add(usedCar);

		carGeneralData.add(carTypeLabelPanel);
		carGeneralData.add(Box.createRigidArea(new Dimension(0, 5)));
		carGeneralData.add(carTypeContent);

		// Make, model, year and price

		JPanel generalDataInfo = new JPanel();
		generalDataInfo.setOpaque(false);
		generalDataInfo.setLayout(new BoxLayout(generalDataInfo, BoxLayout.X_AXIS));

		make = new JComboBox<String>();

		allMakes = getMakes(param); // See method comments for more info
		make.addItem("All Makes");
		for (String s : allMakes)
			make.addItem(s);
		make.setSelectedIndex(selectedMake);
		make.addActionListener(new MakeListener());

		generalDataInfo.add(make);

		// Model at beginning disable
		model = new JComboBox<String>();
		// listeners are added at the end in order to avoiding null pointer exceptions
		model.addItem("All Models");
		allModels = getModels(param); // see comment above

		for (String s : allModels)
			model.addItem(s);
		model.setSelectedIndex(selectedModel);
		generalDataInfo.add(model);

		// Year
		year = new JComboBox<String>();

		// fill the combobox with years starting from 1950
		year.addItem(OPTION);
		for (int i = 0; i <= 68; i++) {
			year.addItem(Integer.toString(2018 - i));
		}
		year.setSelectedIndex(selectedYear);
		generalDataInfo.add(year);
		// combo box for selecting the maximum price for the research
		price = new JComboBox<String>();

		// fill the combobox with prices
		price.addItem(OPTION2);
		for (int j = 0; j < 20; j++) {
			price.addItem(5000 + (5000 * j) + " €");
		}
		price.setSelectedIndex(selectedPrice);
		generalDataInfo.add(price);
		
		// combobox for sold or not sold
		sold = new JComboBox <String> ();
		sold.addItem("Sold or not");
		sold.addItem("Not sold");
		sold.addItem("Sold");
		sold.setSelectedIndex(selectedSold);
		generalDataInfo.add(sold);

		carGeneralData.add(Box.createRigidArea(new Dimension(0, 5)));
		carGeneralData.add(generalDataInfo);

		// SPECIFIC DATA ABOUT THE CAR (TYPE, SEATS, DOORS, OPTIONALS, ETC).

		carSpecificData = new JPanel();
		carSpecificData.setOpaque(false);
		carSpecificData.setLayout(new BoxLayout(carSpecificData, BoxLayout.Y_AXIS));

		// Label
		JPanel carSpecificPanel = new JPanel();
		carSpecificPanel.setOpaque(false);
		JLabel carSpecificDataLabel = new JLabel("Specific Data");

		AppResources.changeFont(carSpecificDataLabel, Font.BOLD, 25);
		carSpecificPanel.add(carSpecificDataLabel);
		carSpecificData.add(carSpecificPanel);

		// Content

		// First row
		JPanel firstRow = new JPanel();
		firstRow.setOpaque(false);
		firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.X_AXIS));

		// car types
		carTypes = new JComboBox<String>();
		carTypes.addItem(OPTION3);

		allCarTypes = getCarTypes();
		for (String s : allCarTypes)
			carTypes.addItem(s);

		firstRow.add(carTypes);

		// Seats
		seats = new JComboBox<String>();
		seats.addItem(OPTION4);

		allSeats = getSeats();
		for (String s : allSeats)
			seats.addItem(s);

		firstRow.add(seats);

		// Doors
		doors = new JComboBox<String>();
		doors.addItem(OPTION5);

		allDoors = getDoors();
		for (String s : allDoors)
			doors.addItem(s);

		firstRow.add(doors);

		carSpecificData.add(Box.createRigidArea(new Dimension(0, 5)));
		carSpecificData.add(firstRow);

		// Second Row
		JPanel secondRow = new JPanel();
		secondRow.setOpaque(false);
		secondRow.setLayout(new BoxLayout(secondRow, BoxLayout.X_AXIS));

		// fuel
		fuel = new JComboBox<String>();
		fuel.addItem(OPTION6);

		allFuel = getFuels();

		for (String s : allFuel)
			fuel.addItem(s);

		secondRow.add(fuel);

		// transmission

		transmissions = new JComboBox<String>();
		transmissions.addItem(OPTION7);

		allTransmissions = getTransmissions();

		for (String s : allTransmissions)
			transmissions.addItem(s);

		secondRow.add(transmissions);

		carSpecificData.add(Box.createRigidArea(new Dimension(0, 10)));
		carSpecificData.add(secondRow);

		// Third Row
		JPanel thirdRow = new JPanel();
		thirdRow.setOpaque(false);
		thirdRow.setLayout(new BoxLayout(thirdRow, BoxLayout.X_AXIS));

		// max car height
		JLabel maxHeight = new JLabel("Max height (cm):");
		height = new JTextField(3);

		thirdRow.add(maxHeight);
		thirdRow.add(Box.createRigidArea(new Dimension(5, 0)));
		thirdRow.add(height);

		// max car length
		JLabel maxLength = new JLabel("Max length (cm):");
		length = new JTextField(3);

		thirdRow.add(Box.createRigidArea(new Dimension(5, 0)));
		thirdRow.add(maxLength);
		thirdRow.add(Box.createRigidArea(new Dimension(5, 0)));
		thirdRow.add(length);

		// max car length
		JLabel maxWidth = new JLabel("Max width (cm):");
		width = new JTextField(3);

		thirdRow.add(Box.createRigidArea(new Dimension(5, 0)));
		thirdRow.add(maxWidth);
		thirdRow.add(Box.createRigidArea(new Dimension(5, 0)));
		thirdRow.add(width);

		carSpecificData.add(Box.createRigidArea(new Dimension(0, 10)));
		carSpecificData.add(thirdRow);

		// Fourth Row
		JPanel fourthRow = new JPanel();
		fourthRow.setOpaque(false);
		fourthRow.setLayout(new BoxLayout(fourthRow, BoxLayout.X_AXIS));

		// max car length
		JLabel maxHoursepower = new JLabel("Max horsepower (kw):");
		horsepower = new JTextField(3);

		fourthRow.add(maxHoursepower);
		fourthRow.add(Box.createRigidArea(new Dimension(5, 0)));
		fourthRow.add(horsepower);
		fourthRow.add(Box.createRigidArea(new Dimension(300, 0)));

		carSpecificData.add(Box.createRigidArea(new Dimension(0, 10)));
		carSpecificData.add(fourthRow);

		// COLOR PANEL
		colorPanel = new JPanel();
		colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
		colorPanel.setOpaque(false);

		// label
		JPanel colorLabelPanel = new JPanel();
		colorLabelPanel.setOpaque(false);

		JLabel colorLabel = new JLabel("Color");
		AppResources.changeFont(colorLabel, Font.BOLD, 25);
		colorLabelPanel.add(colorLabel);
		colorPanel.add(colorLabelPanel);

		// Content
		JPanel colorContent = new JPanel();
		colorContent.setLayout(new BoxLayout(colorContent, BoxLayout.Y_AXIS));
		colorContent.setOpaque(false);

		// COLORS CONTENT GOES HERE

		colorPanel.add(colorContent);

		// OPTIONALS PANEL
		optionalPanel = new JPanel();
		optionalPanel.setLayout(new BoxLayout(optionalPanel, BoxLayout.Y_AXIS));
		optionalPanel.setOpaque(false);

		// label
		JPanel optionalLabelPanel = new JPanel();
		optionalLabelPanel.setOpaque(false);

		JLabel optionalLabel = new JLabel("Optionals");
		AppResources.changeFont(optionalLabel, Font.BOLD, 25);
		optionalLabelPanel.add(optionalLabel);
		optionalPanel.add(optionalLabelPanel);

		// Content
		JPanel optionalContent = new JPanel();
		optionalContent.setLayout(new BoxLayout(optionalContent, BoxLayout.Y_AXIS));
		optionalContent.setOpaque(false);

		// we fill our arrayList (optional) with checkboxes
		populateOptionalsCheckBoxes();

		int countCurrentPanel = 0; // used to place 4 checkboxes in a row

		JPanel rowPanel;
		rowPanel = new JPanel();
		rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
		rowPanel.setOpaque(false);

		// create JPanel with checkboxes
		for (int i = 0; i < optional.size(); i++) {

			if (countCurrentPanel > 4) { // we reset the row before adding elements

				optionalContent.add(Box.createRigidArea(new Dimension(0, 10)));
				optionalContent.add(rowPanel);

				rowPanel = new JPanel();
				rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
				rowPanel.setOpaque(false);
				countCurrentPanel = 0;
				
			}

			rowPanel.add(optional.get(i));
			countCurrentPanel++;
			
			if(i == optional.size() - 1 && countCurrentPanel != 1) {
				optionalContent.add(Box.createRigidArea(new Dimension(0, 10)));
				optionalContent.add(rowPanel);
			}
		}

		optionalPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		optionalPanel.add(optionalContent);

		// Panel for buttons controls
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setOpaque(false);
		// back
		back = AppResources.iconButton("Go back     ", "icons/back.png");
		back.addActionListener(new BackListener());
		buttonPanel.add(back);
		buttonPanel.add((Box.createRigidArea(new Dimension(50, 0))));

		// Add
		search = new JButton();
		search.setIcon(new ImageIcon("icons/searchCar.png"));
		search.setHorizontalTextPosition(SwingConstants.RIGHT);
		search.setForeground(new Color(255, 128, 0));
		AppResources.changeFont(search, Font.BOLD, 20);

		researchQuery();

		buttonPanel.add(search);

		// Add all main panels
		advancedSearch.add(carGeneralData);
		advancedSearch.add(Box.createRigidArea(new Dimension(0, 10)));
		advancedSearch.add(carSpecificData);
		advancedSearch.add(Box.createRigidArea(new Dimension(0, 10)));
		advancedSearch.add(colorPanel);
		advancedSearch.add(Box.createRigidArea(new Dimension(0, 10)));
		advancedSearch.add(optionalPanel);
		advancedSearch.add(Box.createRigidArea(new Dimension(0, 50)));
		advancedSearch.add(buttonPanel);
		advancedSearch.add(Box.createRigidArea(new Dimension(0, 150)));

		add(advancedSearch);

		// IT'S LISTENER TIME
		newCar.addItemListener(new BoxListener());
		usedCar.addItemListener(new BoxListener());
		model.addActionListener(new ModelListener());
		price.addActionListener(new ChangedListener());
		year.addActionListener(new ChangedListener());
		sold.addActionListener(new ChangedListener());
		carTypes.addActionListener(new ChangedListener());
		seats.addActionListener(new ChangedListener());
		doors.addActionListener(new ChangedListener());
		fuel.addActionListener(new ChangedListener());
		transmissions.addActionListener(new ChangedListener());

		height.getDocument().addDocumentListener(new changedTextListener());
		length.getDocument().addDocumentListener(new changedTextListener());
		width.getDocument().addDocumentListener(new changedTextListener());
		horsepower.getDocument().addDocumentListener(new changedTextListener());
		
			//listener on optionals
		for(JCheckBox c : optional)
			c.addActionListener(new ChangedListener() );
	}

	// listener for the chechboxes
	private class BoxListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			// who generates it?
			JCheckBox which = (JCheckBox) e.getItem();
			// Block remove all checkboxes
			if (which == newCar) {
				if (!which.isSelected() && !usedCar.isSelected())
					which.setSelected(true);

			} else {
				if (!which.isSelected() && !newCar.isSelected())
					which.setSelected(true);
			}

			// Change make accordingly
			int param = 0;
			if (newCar.isSelected() && !usedCar.isSelected())
				param = 0;
			else if (!newCar.isSelected() && usedCar.isSelected())
				param = 1;
			else
				param = 2;

			allMakes = getMakes(param); // See method comments for more info
			make.removeAllItems();
			make.addItem("All Makes");
			for (String s : allMakes)
				make.addItem(s);

			// Change model accordingly
			allModels = getModels(param); // See method comments for more info
			model.removeAllItems();
			model.addItem("All Models");
			for (String s : allModels)
				model.addItem(s);
			researchQuery();

		}

	}

	// listener for the change item make field sembrava che aggiungessi parole a
	// caso
	private class MakeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			// Change make accordingly
			int param = 0;
			if (newCar.isSelected() && !usedCar.isSelected())
				param = 0;
			else if (!newCar.isSelected() && usedCar.isSelected())
				param = 1;
			else
				param = 2;

			// Change model accordingly
			JComboBox selectedCombo = (JComboBox) arg0.getSource();
			String selectedMake = (String) selectedCombo.getSelectedItem();
			model.removeAllItems();
			model.addItem("All Models");

			if (selectedMake != null && selectedMake.compareTo("All Makes") != 0) {
				allModels = getModels(param); // See method comments for more info
				for (String s : allModels)
					model.addItem(s);
			}

			String makeSelected = (String) make.getSelectedItem();
			System.out.println("Make Listener");
			if (makeSelected != null && make.getItemCount() != 1)
				researchQuery();
		}

	}

	// listener class for the combobox that allows the selection of the model
	private class ModelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Model Listener");
			String modelSelected = (String) model.getSelectedItem();
			String makeSelected = (String) make.getSelectedItem();
			if (modelSelected != null && makeSelected != null && model.getItemCount() != 1)
				researchQuery();

		}

	}

	// listener class for all the components, on changed event
	private class ChangedListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Changed Listener");
			String modelSelected = (String) model.getSelectedItem();
			String makeSelected = (String) make.getSelectedItem();
			if (modelSelected != null && makeSelected != null)
				researchQuery();

		}

	}

	// document listener for the JTextField components, the changedListener above
	// would not have worked
	private class changedTextListener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			act();

		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			act();

		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			act();

		}

		public void act() {
			System.out.println("Changed Text Listener");
			String modelSelected = (String) model.getSelectedItem();
			String makeSelected = (String) make.getSelectedItem();
			if (modelSelected != null && makeSelected != null)
				researchQuery();
		}

	}
	
	
	//of course we cannot live without ...
	// .. our Back Listener :)
	private class BackListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			MainPanel.getMainPanel().swapPanel(new SearchCarPanel());	
		}
		
	}

	// Get all makes from DBDBDBDBDBDB
	// Commento serio: 0 = new car; 1 = used car; 2 = boat cars;
	public String[] getMakes(int typeOfQuery) {

		ArrayList<String> tPiccola = new ArrayList<String>();
		String getMakes = null;
		switch (typeOfQuery) {
		case 0:
			getMakes = "SELECT DISTINCT make FROM new_car";
			break;
		case 1:
			getMakes = "SELECT DISTINCT make FROM used_car";
			break;
		case 2:
			getMakes = "SELECT DISTINCT make FROM new_car UNION DISTINCT SELECT DISTINCT make FROM used_car";
			break;
		}

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(getMakes);
			while (rs.next()) {
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

	// Get all car types from db (there is no filter)
	public String[] getCarTypes() {

		ArrayList<String> carTypes = new ArrayList<String>();

		String query = "SELECT car_type from new_car UNION DISTINCT SELECT car_type from used_car ORDER BY car_type";

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()) {
				carTypes.add(rs.getString("car_type"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return carTypes.toArray(new String[carTypes.size()]);
	}

	// Get all seats from db (there is no filter)
	public String[] getSeats() {

		ArrayList<String> seats = new ArrayList<String>();

		String query = "SELECT seats from new_car UNION DISTINCT SELECT seats from used_car ORDER BY seats";

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()) {
				seats.add(rs.getString("seats"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return seats.toArray(new String[seats.size()]);
	}

	// Get all doors from db (there is no filter)
	public String[] getDoors() {

		ArrayList<String> doors = new ArrayList<String>();

		String query = "SELECT doors from new_car UNION DISTINCT SELECT doors from used_car ORDER BY doors";

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()) {
				doors.add(rs.getString("doors"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return doors.toArray(new String[doors.size()]);
	}

	public void populateOptionalsCheckBoxes() {

		String query = "SELECT DISTINCT opt_name from optional";

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()) {
				JCheckBox temp = new JCheckBox(rs.getString("opt_name"));
				temp.setOpaque(false);
				AppResources.changeFont(temp, Font.PLAIN, 20);
				optional.add(temp);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	// Get all fuels type from db (there is no filter)
	public String[] getFuels() {

		ArrayList<String> fuels = new ArrayList<String>();

		String query = "SELECT DISTINCT fuel from engine";

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()) {
				fuels.add(rs.getString("fuel"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fuels.toArray(new String[fuels.size()]);
	}

	// Get all fuels type from db (there is no filter)
	public String[] getTransmissions() {

		ArrayList<String> transmissions = new ArrayList<String>();

		String query = "SELECT DISTINCT transmission from engine";

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()) {
				transmissions.add(rs.getString("transmission"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return transmissions.toArray(new String[transmissions.size()]);
	}

	// it could be a long story...
	// ... and in fact it is! But, luckily, it is similar to that of
	// the search button in the Search Panel....
	// a Summary? Prepare and execute the query according to the selected criterias!
	public void researchQuery() {

		String totalQuery = null;

		// Query that will be build

		String newCarQuery = "SELECT make, model FROM new_car";
		String usedCarQuery = "SELECT make, model FROM used_car";

		String makeSelected = (String) make.getSelectedItem();
		String modelSelected = (String) model.getSelectedItem();
		String yearSelected = (String) year.getSelectedItem();
		String priceSelected = (String) price.getSelectedItem();
		String soldSelected = (String) sold.getSelectedItem();
		String typeSelected = (String) carTypes.getSelectedItem();
		String seatsSelected = (String) seats.getSelectedItem();
		String doorsSelected = (String) doors.getSelectedItem();
		String fuelsSelected = (String) fuel.getSelectedItem();
		String transmissionSelected = (String) transmissions.getSelectedItem();
		String maxHeight = height.getText();
		String maxLength = length.getText();
		String maxWidth = width.getText();
		String maxHorses = horsepower.getText();

		// color still need to be added
		// optionals are available in the ArrayList optional

		int yearInt = 0;
		int priceInt = 0;
		int seats = 0;
		int doors = 0;
		int maxHeightVal = 0;
		int maxLengthVal = 0;
		int maxWidthVal = 0;
		int maxHorsesVal = 0;

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

			// look at the car type
			if (typeSelected.compareTo(OPTION3) != 0) {
				newCarQuery += " INTERSECT SELECT make, model FROM new_car WHERE car_type = '" + typeSelected + "'";
			}

			// look at the seats
			if (seatsSelected.compareTo(OPTION4) != 0) {
				seats = Integer.parseInt(seatsSelected);
				newCarQuery += " INTERSECT SELECT make, model FROM new_car WHERE seats = " + seats;
			}

			// look at the doors
			if (doorsSelected.compareTo(OPTION5) != 0) {
				doors = Integer.parseInt(doorsSelected);
				newCarQuery += " INTERSECT SELECT make, model FROM new_car WHERE doors = " + doors;
			}

			// look at the fuel (inner join)
			if (fuelsSelected.compareTo(OPTION6) != 0) {

				newCarQuery += " INTERSECT SELECT new_car.make, new_car.model FROM new_car INNER JOIN engine ON new_car.engine = engine.engine_id WHERE engine.fuel = '"
						+ fuelsSelected + "'";
			}

			// look at the transmission (inner join)
			if (transmissionSelected.compareTo(OPTION7) != 0) {

				newCarQuery += " INTERSECT SELECT new_car.make, new_car.model FROM new_car INNER JOIN engine ON new_car.engine = engine.engine_id WHERE engine.transmission = '"
						+ transmissionSelected + "'";
			}

			// look at the car height (inner join)
			if (maxHeight.compareTo("") != 0) {
				try {
				maxHeightVal = Integer.parseInt(maxHeight);

				newCarQuery += " INTERSECT SELECT new_car.make, new_car.model FROM new_car INNER JOIN dimension on new_car.dimension = dimension.dimension_id WHERE dimension.car_heigth <= "
						+ maxHeightVal;
				} catch(NumberFormatException n) {
					
					JOptionPane.showMessageDialog(advancedSearch, "Max Height must be a valid number", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
					return;
				}
			}

			// look at the car length (inner join)
			if (maxLength.compareTo("") != 0) {
				try {
				maxLengthVal = Integer.parseInt(maxLength);

				newCarQuery += " INTERSECT SELECT new_car.make, new_car.model FROM new_car INNER JOIN dimension on new_car.dimension = dimension.dimension_id WHERE dimension.car_length <= "
						+ maxLengthVal;
				} catch(NumberFormatException n) {
					
					JOptionPane.showMessageDialog(advancedSearch, "Max Length must be a valid number", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
					return;
				}
			}

			// look at the car width (inner join) 
			if (maxWidth.compareTo("") != 0) {
				try {
				maxWidthVal = Integer.parseInt(maxWidth);

				newCarQuery += " INTERSECT SELECT new_car.make, new_car.model FROM new_car INNER JOIN dimension on new_car.dimension = dimension.dimension_id WHERE dimension.car_width <= "
						+ maxWidthVal;
				} catch(NumberFormatException n) {
					
					JOptionPane.showMessageDialog(advancedSearch, "Max Width must be a valid number", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
					return;
				}
			}
			
			// look at the hoursepower (inner join)
			if (maxHorses.compareTo("") != 0) {
				try {
					maxHorsesVal = Integer.parseInt(maxHorses);

					newCarQuery += " INTERSECT SELECT new_car.make, new_car.model FROM new_car INNER JOIN engine on new_car.engine = engine.engine_id WHERE engine.horsepower <= "
							+ maxHorsesVal;
				} catch (NumberFormatException n) {

					JOptionPane.showMessageDialog(advancedSearch, "Max Horses must be a valid number", "CarCube",
							JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}
			}
			
			//and now check all optionals...
			for(JCheckBox c : optional) {
				
				if(c.isSelected()) {
					
					newCarQuery += " INTERSECT SELECT new_car.make, new_car.model FROM new_car inner join new_equipped on new_car.car_id = new_equipped.car_id " + 
							"inner join optional on new_equipped.optional_id = optional.optional_id WHERE optional.opt_name = '"
							+ c.getText() + "'";
				}
				
			}
		}

		if (usedCar.isSelected()) {
			// look at the make combo box (if it is equal "All makes" then no changes in the
			// query)
			if (makeSelected.compareTo("All Makes") != 0) {
				usedCarQuery += " WHERE make = '" + makeSelected + "'";
				// look at the model (inside this "if" because if we are not entered here, then
				// no model is automatically not selected
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

			// look at the car type
			if (typeSelected.compareTo(OPTION3) != 0) {
				usedCarQuery += " INTERSECT SELECT make, model FROM used_car WHERE car_type = '" + typeSelected + "'";
			}

			// look at the seats
			if (seatsSelected.compareTo(OPTION4) != 0) {
				seats = Integer.parseInt(seatsSelected);
				usedCarQuery += " INTERSECT SELECT make, model FROM used_car WHERE seats = " + seats;
			}

			// look at the doors
			if (doorsSelected.compareTo(OPTION5) != 0) {
				doors = Integer.parseInt(doorsSelected);
				usedCarQuery += " INTERSECT SELECT make, model FROM used_car WHERE doors = " + doors;
			}

			// look at the fuel (inner join)
			if (fuelsSelected.compareTo(OPTION6) != 0) {

				usedCarQuery += " INTERSECT SELECT used_car.make, used_car.model FROM used_car INNER JOIN engine ON used_car.engine = engine.engine_id WHERE engine.fuel = '"
						+ fuelsSelected + "'";
			}

			// look at the transmission (inner join)
			if (transmissionSelected.compareTo(OPTION7) != 0) {

				usedCarQuery += " INTERSECT SELECT used_car.make, used_car.model FROM used_car INNER JOIN engine ON used_car.engine = engine.engine_id WHERE engine.transmission = '"
						+ transmissionSelected + "'";
			}

			// look at the car height (inner join)
			if (maxHeight.compareTo("") != 0) {
				try {
				maxHeightVal = Integer.parseInt(maxHeight);

				usedCarQuery += " INTERSECT SELECT used_car.make, used_car.model FROM used_car INNER JOIN dimension on used_car.dimension = dimension.dimension_id WHERE dimension.car_heigth <= "
						+ maxHeightVal;
				} catch(NumberFormatException n) {
					
					JOptionPane.showMessageDialog(advancedSearch, "Max Height must be a valid number", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
					return;
				}
			}

			// look at the car length (inner join)
			if (maxLength.compareTo("") != 0) {
				try {
				maxLengthVal = Integer.parseInt(maxLength);

				usedCarQuery += " INTERSECT SELECT used_car.make, used_car.model FROM used_car INNER JOIN dimension on used_car.dimension = dimension.dimension_id WHERE dimension.car_length <= "
						+ maxLengthVal;
				} catch(NumberFormatException n) {
					
					JOptionPane.showMessageDialog(advancedSearch, "Max Length must be a valid number", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
					return;
				}
			}
			
			// look at the car width (inner join)
			if (maxWidth.compareTo("") != 0) {
				try {
				maxWidthVal = Integer.parseInt(maxWidth);

				usedCarQuery += " INTERSECT SELECT used_car.make, used_car.model FROM used_car INNER JOIN dimension on used_car.dimension = dimension.dimension_id WHERE dimension.car_width <= "
						+ maxWidthVal;
				} catch(NumberFormatException n) {
					
					JOptionPane.showMessageDialog(advancedSearch, "Max Width must be a valid number", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
					return;
				}
			}
			
			// look at the hoursepower (inner join)
			if (maxHorses.compareTo("") != 0) {
				try {
					maxHorsesVal = Integer.parseInt(maxHorses);

					usedCarQuery += " INTERSECT SELECT used_car.make, used_car.model FROM used_car INNER JOIN engine on used_car.engine = engine.engine_id WHERE engine.horsepower <= "
							+ maxHorsesVal;
				} catch (NumberFormatException n) {

					JOptionPane.showMessageDialog(advancedSearch, "Max Horses must be a valid number", "CarCube",
							JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}
			}

			//and now check all optionals...
			for(JCheckBox c : optional) {
				
				if(c.isSelected()) {
					
					usedCarQuery += " INTERSECT SELECT used_car.make, used_car.model FROM used_car inner join used_equipped on used_car.immatriculation = used_equipped.immatriculation " + 
							"inner join optional on used_equipped.optional_id = optional.optional_id WHERE optional.opt_name = '"
							+ c.getText() + "'";
				}
				
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
				search.setEnabled(false);
				;
			} else
				search.setEnabled(true);

			search.setText(rowcount + ((rowcount == 1) ? " car" : " cars"));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}