package atunibz.dcube.DBProject.GUI;

import java.awt.BorderLayout;
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
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import atunibz.dcube.DBProject.GUI.ColorsPanel.ColorCheckBox;
import atunibz.dcube.DBProject.configuration.AppResources;
import atunibz.dcube.DBProject.configuration.GetListQuery;

public class AdvancedSearchPanel extends JPanel {

	private Connection conn;
	// private JScrollPane mainPane;
	private JPanel advancedSearch, titlePanel;
	private JPanel carGeneralData, carSpecificData, colorPanel, optionalPanel, carPanel;
	private JScrollPane pane;
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
	private ArrayList<JCheckBox> optional;
	private ArrayList<ColorCheckBox> colors;
	private ArrayList<String> colorKeys;
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

		allMakes = GetListQuery.getMakes(param); // See method comments for more info
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
		allModels = GetListQuery.getModels(param, (String) make.getSelectedItem()); // see comment above

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

		allCarTypes = GetListQuery.getCarTypes();
		for (String s : allCarTypes)
			carTypes.addItem(s);

		firstRow.add(carTypes);

		// Seats
		seats = new JComboBox<String>();
		seats.addItem(OPTION4);

		allSeats = GetListQuery.getSeats();
		for (String s : allSeats)
			seats.addItem(s);

		firstRow.add(seats);

		// Doors
		doors = new JComboBox<String>();
		doors.addItem(OPTION5);

		allDoors = GetListQuery.getDoors();
		for (String s : allDoors)
			doors.addItem(s);

		firstRow.add(doors);
		
		// fuel
		fuel = new JComboBox<String>();
		fuel.addItem(OPTION6);

		allFuel = GetListQuery.getFuels();

		for (String s : allFuel)
			fuel.addItem(s);
		
		firstRow.add(fuel);
		
		// transmissions
		transmissions = new JComboBox<String>();
		transmissions.addItem(OPTION7);

		allTransmissions = GetListQuery.getTransmissions();

		for (String s : allTransmissions)
			transmissions.addItem(s);
		
		firstRow.add(transmissions);

		carSpecificData.add(Box.createRigidArea(new Dimension(0, 5)));
		carSpecificData.add(firstRow);


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
		
		JLabel maxHoursepower = new JLabel("Max horsepower (kw):");
		horsepower = new JTextField(3);
		thirdRow.add(Box.createRigidArea(new Dimension(5, 0)));
		thirdRow.add(maxHoursepower);
		thirdRow.add(Box.createRigidArea(new Dimension(5, 0)));
		thirdRow.add(horsepower);

		carSpecificData.add(Box.createRigidArea(new Dimension(0, 10)));
		carSpecificData.add(thirdRow);

		// Fourth Row
		JPanel fourthRow = new JPanel();
		fourthRow.setOpaque(false);
		fourthRow.setLayout(new BoxLayout(fourthRow, BoxLayout.X_AXIS));

		

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
		JPanel supportPanel = new JPanel();
		supportPanel.setOpaque(false);
		ColorsPanel colPanel = new ColorsPanel();
		JScrollPane content = new JScrollPane(colPanel);
		content.setPreferredSize(new Dimension (520, 188));
		content.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		content.setOpaque(false);
		supportPanel.add(content);
		
		//Get colors and keys
		colors = colPanel.getColorCheckBoxes();
		colorKeys = colPanel.getColorKeys();
		
		
		colorPanel.add(supportPanel);
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
		

		// Add
		search = new JButton();
		search.setIcon(new ImageIcon("icons/searchCar.png"));
		search.setHorizontalTextPosition(SwingConstants.RIGHT);
		search.setForeground(new Color(255, 128, 0));
		AppResources.changeFont(search, Font.BOLD, 20);

		researchQuery();

		buttonPanel.add(search);
		
		// panel that shows the list of cars
		JPanel bigContainer = new JPanel();
		JPanel bigcarPanel = new JPanel();
		carPanel = new JPanel();
		carPanel.setOpaque(false);
		carPanel.setLayout(new BoxLayout(carPanel, BoxLayout.Y_AXIS));
		bigcarPanel.add(carPanel);
		pane = new JScrollPane(bigcarPanel);
		pane.setOpaque(false);
		pane.setPreferredSize(new Dimension(830, 400));
		bigContainer.add(pane);
		bigContainer.setOpaque(false);
		
		// panel for back
		JPanel backPanel = new JPanel();
		backPanel.setOpaque(false);
		// back
		back = AppResources.iconButton("Go back     ", "icons/back.png");
		back.addActionListener(new BackListener());
		backPanel.add(back);

		// Add all main panels
		advancedSearch.add(carGeneralData);
		advancedSearch.add(Box.createRigidArea(new Dimension(0, 10)));
		advancedSearch.add(carSpecificData);
		advancedSearch.add(Box.createRigidArea(new Dimension(0, 10)));
		advancedSearch.add(colorPanel);
		advancedSearch.add(Box.createRigidArea(new Dimension(0, 10)));
		advancedSearch.add(optionalPanel);
		advancedSearch.add(Box.createRigidArea(new Dimension(0, 20)));
		advancedSearch.add(buttonPanel);
		advancedSearch.add(Box.createRigidArea(new Dimension(0, 60)));
		advancedSearch.add(bigContainer);
		advancedSearch.add(Box.createRigidArea(new Dimension(0, 10)));
		advancedSearch.add(backPanel);
		
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
		search.addActionListener(new SearchListener());
		
		height.getDocument().addDocumentListener(new changedTextListener());
		length.getDocument().addDocumentListener(new changedTextListener());
		width.getDocument().addDocumentListener(new changedTextListener());
		horsepower.getDocument().addDocumentListener(new changedTextListener());
		
			//listener on optionals
		for(JCheckBox c : optional)
			c.addActionListener(new ChangedListener() );
		
		for(ColorCheckBox current : colors) {
			current.getCheckBox().addActionListener(new ChangedListener() );
		}
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

			allMakes = GetListQuery.getMakes(param); // See method comments for more info
			make.removeAllItems();
			make.addItem("All Makes");
			for (String s : allMakes)
				make.addItem(s);

			// Change model accordingly
			allModels = GetListQuery.getModels(param, (String) make.getSelectedItem()); // See method comments for more info
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
				allModels = GetListQuery.getModels(param, (String) make.getSelectedItem()); // See method comments for more info
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
			
			stat.close();
			rs.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

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
				newCarQuery += " INTERSECT ALL SELECT make, model FROM new_car WHERE car_year >= " + yearInt;
			}
			// look at the price
			if (priceSelected.compareTo("Price up to") != 0) {
				priceInt = Integer.parseInt(priceSelected.substring(0, priceSelected.lastIndexOf(" €")));
				newCarQuery += " INTERSECT ALL SELECT make, model FROM new_car WHERE base_price <= " + priceInt;
			}
			
			// look at sold or not sold
			if (soldSelected.compareTo("Sold or not") != 0) {
				if (soldSelected.compareTo("Not sold") == 0)
					newCarQuery += " INTERSECT ALL SELECT make, model FROM new_car WHERE sold = 0";
				else
					newCarQuery += " INTERSECT ALL SELECT make, model FROM new_car WHERE sold = 1";
			}

			// look at the car type
			if (typeSelected.compareTo(OPTION3) != 0) {
				newCarQuery += " INTERSECT ALL SELECT make, model FROM new_car WHERE car_type = '" + typeSelected + "'";
			}

			// look at the seats
			if (seatsSelected.compareTo(OPTION4) != 0) {
				seats = Integer.parseInt(seatsSelected);
				newCarQuery += " INTERSECT ALL SELECT make, model FROM new_car WHERE seats = " + seats;
			}

			// look at the doors
			if (doorsSelected.compareTo(OPTION5) != 0) {
				doors = Integer.parseInt(doorsSelected);
				newCarQuery += " INTERSECT ALL SELECT make, model FROM new_car WHERE doors = " + doors;
			}

			// look at the fuel (inner join)
			if (fuelsSelected.compareTo(OPTION6) != 0) {

				newCarQuery += " INTERSECT ALL SELECT new_car.make, new_car.model FROM new_car INNER JOIN engine ON new_car.engine = engine.engine_id WHERE engine.fuel = '"
						+ fuelsSelected + "'";
			}

			// look at the transmission (inner join)
			if (transmissionSelected.compareTo(OPTION7) != 0) {

				newCarQuery += " INTERSECT ALL SELECT new_car.make, new_car.model FROM new_car INNER JOIN engine ON new_car.engine = engine.engine_id WHERE engine.transmission = '"
						+ transmissionSelected + "'";
			}

			// look at the car height (inner join)
			if (maxHeight.compareTo("") != 0) {
				try {
				maxHeightVal = Integer.parseInt(maxHeight);

				newCarQuery += " INTERSECT ALL SELECT new_car.make, new_car.model FROM new_car INNER JOIN dimension on new_car.dimension = dimension.dimension_id WHERE dimension.car_heigth <= "
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

				newCarQuery += " INTERSECT ALL SELECT new_car.make, new_car.model FROM new_car INNER JOIN dimension on new_car.dimension = dimension.dimension_id WHERE dimension.car_length <= "
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

				newCarQuery += " INTERSECT ALL SELECT new_car.make, new_car.model FROM new_car INNER JOIN dimension on new_car.dimension = dimension.dimension_id WHERE dimension.car_width <= "
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

					newCarQuery += " INTERSECT ALL SELECT new_car.make, new_car.model FROM new_car INNER JOIN engine on new_car.engine = engine.engine_id WHERE engine.horsepower <= "
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
					
					newCarQuery += " INTERSECT ALL SELECT new_car.make, new_car.model FROM new_car inner join new_equipped on new_car.car_id = new_equipped.car_id " + 
							"inner join optional on new_equipped.optional_id = optional.optional_id WHERE optional.opt_name = '"
							+ c.getText() + "'";
				}
				
			}
			
			//Colors: now we just print
			for(int i = 0; i < colors.size(); i++) {
				if(colors.get(i).getCheckBox().isSelected())
					newCarQuery += " INTERSECT ALL SELECT new_car.make, new_car.model FROM new_car inner join new_painting on new_car.car_id = new_painting.car_id " + 
							"inner join color on new_painting.color_code = color.color_code WHERE color.color_code = '"
							+ colorKeys.get(i) + "'";
					//System.out.println("Color selected: " + colorKeys.get(i));
			}
			
			System.out.println("Reasearch query: " + newCarQuery);
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
				usedCarQuery += " INTERSECT ALL SELECT make, model FROM used_car WHERE car_year >= " + yearInt;
			}
			// look at the price
			if (priceSelected.compareTo(OPTION2) != 0) {
				priceInt = Integer.parseInt(priceSelected.substring(0, priceSelected.lastIndexOf(" €")));
				usedCarQuery += " INTERSECT ALL SELECT make, model FROM used_car WHERE net_price <= " + priceInt;
			}
			// look at sold or not sold
			if (soldSelected.compareTo("Sold or not") != 0) {
				if (soldSelected.compareTo("Not sold") == 0)
					usedCarQuery += " INTERSECT ALL SELECT make, model FROM used_car WHERE sold = 0";
				else
					usedCarQuery += " INTERSECT ALL SELECT make, model FROM used_car WHERE sold = 1";
			}

			// look at the car type
			if (typeSelected.compareTo(OPTION3) != 0) {
				usedCarQuery += " INTERSECT ALL SELECT make, model FROM used_car WHERE car_type = '" + typeSelected + "'";
			}

			// look at the seats
			if (seatsSelected.compareTo(OPTION4) != 0) {
				seats = Integer.parseInt(seatsSelected);
				usedCarQuery += " INTERSECT ALL SELECT make, model FROM used_car WHERE seats = " + seats;
			}

			// look at the doors
			if (doorsSelected.compareTo(OPTION5) != 0) {
				doors = Integer.parseInt(doorsSelected);
				usedCarQuery += " INTERSECT ALL SELECT make, model FROM used_car WHERE doors = " + doors;
			}

			// look at the fuel (inner join)
			if (fuelsSelected.compareTo(OPTION6) != 0) {

				usedCarQuery += " INTERSECT ALL SELECT used_car.make, used_car.model FROM used_car INNER JOIN engine ON used_car.engine = engine.engine_id WHERE engine.fuel = '"
						+ fuelsSelected + "'";
			}

			// look at the transmission (inner join)
			if (transmissionSelected.compareTo(OPTION7) != 0) {

				usedCarQuery += " INTERSECT ALL SELECT used_car.make, used_car.model FROM used_car INNER JOIN engine ON used_car.engine = engine.engine_id WHERE engine.transmission = '"
						+ transmissionSelected + "'";
			}

			// look at the car height (inner join)
			if (maxHeight.compareTo("") != 0) {
				try {
				maxHeightVal = Integer.parseInt(maxHeight);

				usedCarQuery += " INTERSECT ALL SELECT used_car.make, used_car.model FROM used_car INNER JOIN dimension on used_car.dimension = dimension.dimension_id WHERE dimension.car_heigth <= "
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

				usedCarQuery += " INTERSECT ALL SELECT used_car.make, used_car.model FROM used_car INNER JOIN dimension on used_car.dimension = dimension.dimension_id WHERE dimension.car_length <= "
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

				usedCarQuery += " INTERSECT ALL SELECT used_car.make, used_car.model FROM used_car INNER JOIN dimension on used_car.dimension = dimension.dimension_id WHERE dimension.car_width <= "
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

					usedCarQuery += " INTERSECT ALL SELECT used_car.make, used_car.model FROM used_car INNER JOIN engine on used_car.engine = engine.engine_id WHERE engine.horsepower <= "
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
					
					usedCarQuery += " INTERSECT ALL SELECT used_car.make, used_car.model FROM used_car inner join used_equipped on used_car.immatriculation = used_equipped.immatriculation " + 
							"inner join optional on used_equipped.optional_id = optional.optional_id WHERE optional.opt_name = '"
							+ c.getText() + "'";
				}
				
			}
			
			for(int i = 0; i < colors.size(); i++) {
				if(colors.get(i).getCheckBox().isSelected())
					usedCarQuery += " INTERSECT ALL SELECT used_car.make, used_car.model FROM used_car inner join used_painting on used_car.immatriculation = used_painting.immatriculation " + 
							"inner join color on used_painting.color_code = color.color_code WHERE color.color_code = '"
							+ colorKeys.get(i) + "'";
					//System.out.println("Color selected: " + colorKeys.get(i));
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
			
			stat.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private class SearchListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub		
			String newCarQuery = "SELECT * FROM new_car";
			String newCarQueryWhere = "";
			String usedCarQuery = "SELECT * FROM used_car";
			String usedCarQueryWhere = "";
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
			// evaluate if we are searching in new cars, used cars, or both
			if (newCar.isSelected()) {
				
				// look at the make combo box (if it is equal "All makes" then no changes in the
				// query)
				if (makeSelected.compareTo("All Makes") != 0) {
					newCarQueryWhere += "new_car.make = '" + makeSelected + "'";
					// look at the model (inside this "if" because if we are not entered here, then
					// no model is automatically not selected
					if (modelSelected.compareTo("All Models") != 0) {
						newCarQueryWhere += " AND new_car.model = '" + modelSelected + "'";
					}
				}
				// look at the year
				if (yearSelected.compareTo("From year") != 0) {
					yearInt = Integer.parseInt(yearSelected);
					if(newCarQueryWhere.length() > 0)
						newCarQueryWhere += " AND new_car.car_year >=" + yearInt;
					else
						newCarQueryWhere += " new_car.car_year >=" + yearInt;
				}
				
				// look at the price
				if (priceSelected.compareTo("Price up to") != 0) {
					priceInt = Integer.parseInt(priceSelected.substring(0, priceSelected.lastIndexOf(" €")));
					if(newCarQueryWhere.length() > 0)
						newCarQueryWhere += " AND new_car.base_price <=" + priceInt;
					else
						newCarQueryWhere += " new_car.base_price <=" + priceInt;
				}
				
				// look at sold or not sold
				if (soldSelected.compareTo("Sold or not") != 0) {
					
					if (soldSelected.compareTo("Not sold") == 0) {
						
						if(newCarQueryWhere.length() > 0)
							newCarQueryWhere += " AND new_car.sold = 0";
						else
							newCarQueryWhere += " new_car.sold = 0";
					}
					else {
						if(newCarQueryWhere.length() > 0)
							newCarQueryWhere += " AND new_car.sold = 1";
						else
							newCarQueryWhere += " new_car.sold = 1";
					}
				}
				
				//Look at doors and seats (there is also one car with 3 seats..... INCREDIBLE :)
				// look at the car type
				if (typeSelected.compareTo(OPTION3) != 0) {
					
					if(newCarQueryWhere.length() > 0)
						newCarQueryWhere += " AND new_car.car_type = '" + typeSelected + "'";
					else
						newCarQueryWhere += " new_car.car_type = '"+ typeSelected + "'";
				}

				// look at the seats
				if (seatsSelected.compareTo(OPTION4) != 0) {
					seats = Integer.parseInt(seatsSelected);
					
					if(newCarQueryWhere.length() > 0)
						newCarQueryWhere += " AND new_car.seats = " + seats;
					else
						newCarQueryWhere += " new_car.seats = " + seats;
				}

				// look at the doors
				if (doorsSelected.compareTo(OPTION5) != 0) {
					doors = Integer.parseInt(doorsSelected);
					
					if(newCarQueryWhere.length() > 0)
						newCarQueryWhere += " AND new_car.doors = " + doors;
					else
						newCarQueryWhere += " new_car.doors = " + doors;
				}
				
				//Engine joinssss
				// look at the fuel (inner join)
				if (fuelsSelected.compareTo(OPTION6) != 0) {
					
					newCarQuery +=" INNER JOIN engine on new_car.engine = engine.engine_id";
					
					if(newCarQueryWhere.length() > 0)
						newCarQueryWhere += " AND engine.fuel = '" + fuelsSelected + "'";
					else
						newCarQueryWhere += " engine.fuel = '" + fuelsSelected + "'";
				}

				// look at the transmission (inner join)
				if (transmissionSelected.compareTo(OPTION7) != 0) {
					
					if(!newCarQuery.contains(" INNER JOIN engine on new_car.engine = engine.engine_id"))
						newCarQuery +=" INNER JOIN engine on new_car.engine = engine.engine_id";
					
					if(newCarQueryWhere.length() > 0)
						newCarQueryWhere += " AND engine.transmission = '" + transmissionSelected + "'";
					else
						newCarQueryWhere += " engine.transmission = '" + transmissionSelected + "'";
				}
				
				// look at the hoursepower (inner join)
				if (maxHorses.compareTo("") != 0) {
					try {
						maxHorsesVal = Integer.parseInt(maxHorses);

						if(!newCarQuery.contains(" INNER JOIN engine on new_car.engine = engine.engine_id"))
							newCarQuery +=" INNER JOIN engine on new_car.engine = engine.engine_id";
						
						if(newCarQueryWhere.length() > 0)
							newCarQueryWhere += " AND engine.horsepower <= " + maxHorsesVal;
						else
							newCarQueryWhere += " engine.horsepower <= " + maxHorsesVal;
						
					} catch (NumberFormatException n) {

						JOptionPane.showMessageDialog(advancedSearch, "Max Horses must be a valid number", "CarCube",
								JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
						return;
					}
				}
				
				
				// TO BE CONTINUED...
				// DIMENSION!
				// look at the car height (inner join)
				if (maxHeight.compareTo("") != 0) {
					try {
						newCarQuery +=" INNER JOIN dimension on new_car.dimension = dimension.dimension_id";
						maxHeightVal = Integer.parseInt(maxHeight);

						if(newCarQueryWhere.length() > 0)
							newCarQueryWhere += " AND dimension.car_heigth <= " + maxHeightVal;
						else
							newCarQueryWhere += " dimension.car_heigth <= " + maxHorsesVal;
					} catch(NumberFormatException n) {
						
						JOptionPane.showMessageDialog(advancedSearch, "Max Height must be a valid number", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
						return;
					}
				}

				// look at the car length (inner join)
				if (maxLength.compareTo("") != 0) {
					try {
						maxLengthVal = Integer.parseInt(maxLength);
						if(!newCarQuery.contains(" INNER JOIN dimension on new_car.dimension = dimension.dimension_id"))
							newCarQuery +=" INNER JOIN dimension on new_car.dimension = dimension.dimension_id";

						if (newCarQueryWhere.length() > 0)
							newCarQueryWhere += " AND dimension.car_length <= " + maxLengthVal;
						else
							newCarQueryWhere += " dimension.car_length <= " + maxLengthVal;
					} catch (NumberFormatException n) {

						JOptionPane.showMessageDialog(advancedSearch, "Max Length must be a valid number", "CarCube",
								JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
						return;
					}
				}
				
				// look at the car width (inner join)
				if (maxWidth.compareTo("") != 0) {
					try {
						maxWidthVal = Integer.parseInt(maxWidth);

						if (!newCarQuery.contains(" INNER JOIN dimension on new_car.dimension = dimension.dimension_id"))
							newCarQuery += " INNER JOIN dimension on new_car.dimension = dimension.dimension_id";

						if (newCarQueryWhere.length() > 0)
							newCarQueryWhere += " AND dimension.car_width <= " + maxWidthVal;
						else
							newCarQueryWhere += " dimension.car_width <= " + maxWidthVal;
					} catch (NumberFormatException n) {

						JOptionPane.showMessageDialog(advancedSearch, "Max Width must be a valid number", "CarCube",
								JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
						return;
					}
				}
				
				//and now check all optionals...
				String optionaQuery = "";
				String innerQuery0 = "";
				for(JCheckBox c : optional) {
					
					if(c.isSelected()) {
						
						if(optionaQuery.length() == 0) {
							optionaQuery = " INTERSECT "+ newCarQuery + " WHERE car_id IN (";
							
						}
						
						if(innerQuery0.length() > 0 )
							innerQuery0 += " intersect SELECT new_car.car_id FROM new_car inner join new_equipped on new_car.car_id = new_equipped.car_id inner join optional on new_equipped.optional_id = optional.optional_id where optional.opt_name = '" + c.getText() + "'";
						else
							innerQuery0 += " SELECT new_car.car_id FROM new_car inner join new_equipped on new_car.car_id = new_equipped.car_id inner join optional on new_equipped.optional_id = optional.optional_id where optional.opt_name = '" + c.getText() + "'";
					}
					
				}
				
				String colorQuery = "";
				String innerQuery = "";
				
				for(int i = 0; i < colors.size(); i++) {
					
					if(colors.get(i).getCheckBox().isSelected()) {
						
						if(colorQuery.length() == 0) {
							colorQuery = " INTERSECT "+ newCarQuery + " WHERE car_id IN (";
							
						}
						
						if(innerQuery.length() > 0 )
							innerQuery += " intersect SELECT new_car.car_id FROM new_car inner join new_painting on new_car.car_id = new_painting.car_id inner join color on new_painting.color_code = color.color_code where color.color_code = '" + colorKeys.get(i) + "'";
						else
							innerQuery += " SELECT new_car.car_id FROM new_car inner join new_painting on new_car.car_id = new_painting.car_id inner join color on new_painting.color_code = color.color_code where color.color_code = '" + colorKeys.get(i) + "'";
					}
						
				}
				if (newCarQueryWhere.length() >0)
					newCarQuery += " WHERE " + newCarQueryWhere;
				
				if(colorQuery.length() > 0)
					newCarQuery += colorQuery + innerQuery + ")";
				
				if(optionaQuery.length() > 0)
					newCarQuery += optionaQuery + innerQuery0 + ")";
				
				System.out.println("New car Query: " + newCarQuery);
				
			}
			
			// USED CAR!!!!
			if (usedCar.isSelected()) {

				// look at the make combo box (if it is equal "All makes" then no changes in the
				// query)
				if (makeSelected.compareTo("All Makes") != 0) {
					usedCarQueryWhere += "used_car.make = '" + makeSelected + "'";
					// look at the model (inside this "if" because if we are not entered here, then
					// no model is automatically not selected
					if (modelSelected.compareTo("All Models") != 0) {
						usedCarQueryWhere += " AND used_car.model = '" + modelSelected + "'";
					}
				}
				// look at the year
				if (yearSelected.compareTo("From year") != 0) {
					yearInt = Integer.parseInt(yearSelected);
					if (usedCarQueryWhere.length() > 0)
						usedCarQueryWhere += " AND used_car.car_year >=" + yearInt;
					else
						usedCarQueryWhere += " used_car.car_year >=" + yearInt;
				}

				// look at the price
				if (priceSelected.compareTo("Price up to") != 0) {
					priceInt = Integer.parseInt(priceSelected.substring(0, priceSelected.lastIndexOf(" €")));
					if (usedCarQueryWhere.length() > 0)
						usedCarQueryWhere += " AND used_car.net_price <=" + priceInt;
					else
						usedCarQueryWhere += " used_car.net_price <=" + priceInt;
				}

				// look at sold or not sold
				if (soldSelected.compareTo("Sold or not") != 0) {

					if (soldSelected.compareTo("Not sold") == 0) {

						if (usedCarQueryWhere.length() > 0)
							usedCarQueryWhere += " AND used_car.sold = 0";
						else
							usedCarQueryWhere += " used_car.sold = 0";
					} else {
						if (usedCarQueryWhere.length() > 0)
							usedCarQueryWhere += " AND used_car.sold = 1";
						else
							usedCarQueryWhere += " used_car.sold = 1";
					}
				}

				// Look at doors and seats (there is also one car with 3 seats..... INCREDIBLE
				// :)
				// look at the car type
				if (typeSelected.compareTo(OPTION3) != 0) {

					if (usedCarQueryWhere.length() > 0)
						usedCarQueryWhere += " AND used_car.car_type = '" + typeSelected + "'";
					else
						usedCarQueryWhere += " used_car.car_type = '" + typeSelected + "'";
				}

				// look at the seats
				if (seatsSelected.compareTo(OPTION4) != 0) {
					seats = Integer.parseInt(seatsSelected);

					if (usedCarQueryWhere.length() > 0)
						usedCarQueryWhere += " AND used_car.seats = " + seats;
					else
						usedCarQueryWhere += " used_car.seats = " + seats;
				}

				// look at the doors
				if (doorsSelected.compareTo(OPTION5) != 0) {
					doors = Integer.parseInt(doorsSelected);

					if (usedCarQueryWhere.length() > 0)
						usedCarQueryWhere += " AND used_car.doors = " + doors;
					else
						usedCarQueryWhere += " used_car.doors = " + doors;
				}

				// Engine joinssss
				// look at the fuel (inner join)
				if (fuelsSelected.compareTo(OPTION6) != 0) {

					usedCarQuery += " INNER JOIN engine on used_car.engine = engine.engine_id";

					if (usedCarQueryWhere.length() > 0)
						usedCarQueryWhere += " AND engine.fuel = '" + fuelsSelected + "'";
					else
						usedCarQueryWhere += " engine.fuel = '" + fuelsSelected + "'";
				}

				// look at the transmission (inner join)
				if (transmissionSelected.compareTo(OPTION7) != 0) {

					if (!usedCarQuery.contains(" INNER JOIN engine on used_car.engine = engine.engine_id"))
						usedCarQuery += " INNER JOIN engine on used_car.engine = engine.engine_id";

					if (usedCarQueryWhere.length() > 0)
						usedCarQueryWhere += " AND engine.transmission = '" + transmissionSelected + "'";
					else
						usedCarQueryWhere += " engine.transmission = '" + transmissionSelected + "'";
				}

				// look at the hoursepower (inner join)
				if (maxHorses.compareTo("") != 0) {
					try {
						maxHorsesVal = Integer.parseInt(maxHorses);

						if (!usedCarQuery.contains(" INNER JOIN engine on used_car.engine = engine.engine_id"))
							usedCarQuery += " INNER JOIN engine on used_car.engine = engine.engine_id";

						if (usedCarQueryWhere.length() > 0)
							usedCarQueryWhere += " AND engine.horsepower <= " + maxHorsesVal;
						else
							usedCarQueryWhere += " engine.horsepower <= " + maxHorsesVal;

					} catch (NumberFormatException n) {

						JOptionPane.showMessageDialog(advancedSearch, "Max Horses must be a valid number", "CarCube",
								JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
						return;
					}
				}

				// TO BE CONTINUED...
				// DIMENSION!
				// look at the car height (inner join)
				if (maxHeight.compareTo("") != 0) {
					try {
						usedCarQuery += " INNER JOIN dimension on used_car.dimension = dimension.dimension_id";
						maxHeightVal = Integer.parseInt(maxHeight);

						if (usedCarQueryWhere.length() > 0)
							usedCarQueryWhere += " AND dimension.car_heigth <= " + maxHeightVal;
						else
							usedCarQueryWhere += " dimension.car_heigth <= " + maxHorsesVal;
					} catch (NumberFormatException n) {

						JOptionPane.showMessageDialog(advancedSearch, "Max Height must be a valid number", "CarCube",
								JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
						return;
					}
				}

				// look at the car length (inner join)
				if (maxLength.compareTo("") != 0) {
					try {
						maxLengthVal = Integer.parseInt(maxLength);
						if (!usedCarQuery
								.contains(" INNER JOIN dimension on used_car.dimension = dimension.dimension_id"))
							usedCarQuery += " INNER JOIN dimension on used_car.dimension = dimension.dimension_id";

						if (usedCarQueryWhere.length() > 0)
							usedCarQueryWhere += " AND dimension.car_length <= " + maxLengthVal;
						else
							usedCarQueryWhere += " dimension.car_length <= " + maxLengthVal;
					} catch (NumberFormatException n) {

						JOptionPane.showMessageDialog(advancedSearch, "Max Length must be a valid number", "CarCube",
								JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
						return;
					}
				}

				// look at the car width (inner join)
				if (maxWidth.compareTo("") != 0) {
					try {
						maxWidthVal = Integer.parseInt(maxWidth);

						if (!usedCarQuery
								.contains(" INNER JOIN dimension on used_car.dimension = dimension.dimension_id"))
							usedCarQuery += " INNER JOIN dimension on used_car.dimension = dimension.dimension_id";

						if (usedCarQueryWhere.length() > 0)
							usedCarQueryWhere += " AND dimension.car_width <= " + maxWidthVal;
						else
							usedCarQueryWhere += " dimension.car_width <= " + maxWidthVal;
					} catch (NumberFormatException n) {

						JOptionPane.showMessageDialog(advancedSearch, "Max Width must be a valid number", "CarCube",
								JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
						return;
					}
				}

				// and now check all optionals...
				String optionaQuery = "";
				String innerQuery0 = "";
				for(JCheckBox c : optional) {
					
					if(c.isSelected()) {
						
						if(optionaQuery.length() == 0) {
							optionaQuery = " INTERSECT "+ usedCarQuery + " WHERE immatriculation IN (";
							
						}
						
						if(innerQuery0.length() > 0 )
							innerQuery0 += " intersect SELECT used_car.immatriculation FROM used_car inner join used_equipped on used_car.immatriculation = used_equipped.immatriculation inner join optional on used_equipped.optional_id = optional.optional_id where optional.opt_name = '" + c.getText() + "'";
						else
							innerQuery0 += " SELECT used_car.immatriculation FROM used_car inner join used_equipped on used_car.immatriculation = used_equipped.immatriculation inner join optional on used_equipped.optional_id = optional.optional_id where optional.opt_name = '" + c.getText() + "'";
					}
					
				}

				String colorQuery = "";
				String innerQuery = "";
				
				for(int i = 0; i < colors.size(); i++) {
					
					if(colors.get(i).getCheckBox().isSelected()) {
						
						if(colorQuery.length() == 0) {
							colorQuery = " INTERSECT "+ usedCarQuery + " WHERE immatriculation IN (";
							
						}
						
						if(innerQuery.length() > 0 )
							innerQuery += " intersect SELECT used_car.immatriculation FROM used_car inner join used_painting on used_car.immatriculation = used_painting.immatriculation inner join color on used_painting.color_code = color.color_code where color.color_code = '" + colorKeys.get(i) + "'";
						else
							innerQuery += " SELECT used_car.immatriculation FROM used_car inner join used_painting on used_car.immatriculation = used_painting.immatriculation inner join color on used_painting.color_code = color.color_code where color.color_code = '" + colorKeys.get(i) + "'";
					}
						
				}
				if (usedCarQueryWhere.length() >0)
					usedCarQuery += " WHERE " + usedCarQueryWhere;
				
				if(colorQuery.length() > 0)
					usedCarQuery += colorQuery + innerQuery + ")";
				if(optionaQuery.length() > 0)
					usedCarQuery += optionaQuery + innerQuery0 + ")";
				
				System.out.println("Used car Query: " + usedCarQuery);
			}
			
			// CASE BOTH
			if (newCar.isSelected() && usedCar.isSelected()) {
				try {
					Statement stat = conn.createStatement();
					ResultSet rs = stat.executeQuery(newCarQuery);
					carPanel.removeAll();
					createPanelList (rs);
					
					//closing statement and result sets
					stat.close();
					rs.close();
					
					System.out.println("CASE BOTH: \n");
					System.out.println("\t" + newCarQuery);
					System.out.println("\n\t" + usedCarQuery);
					stat = conn.createStatement();
					rs = stat.executeQuery(usedCarQuery);
					//carPanel.removeAll();
					createPanelListUsed(rs);
					
					advancedSearch.revalidate();
					advancedSearch.repaint();
					
					//closing statement and result sets
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
					System.out.println(newCarQuery);
					ResultSet rs = stat.executeQuery(newCarQuery);
					carPanel.removeAll();
					createPanelList (rs);
					advancedSearch.revalidate();
					advancedSearch.repaint();
					
					//closing statement and result sets
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
					advancedSearch.revalidate();
					advancedSearch.repaint();
					
					//closing statement and result sets
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
					jenson.addActionListener(new viewCarListener(car_id));
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
					
					
				}
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		private class viewCarListener implements ActionListener{
			
			String id;
			
			public viewCarListener(int idInit) {
				id = "" + idInit;
			}
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				MainPanel.getMainPanel().swapPanel(new ViewCarPanel(id,true ));
				
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
						jenson.addActionListener(new viewUsedCarListener(car_id));
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
						
					}
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			private class viewUsedCarListener implements ActionListener{
				
				String id;
				
				public viewUsedCarListener(String idInit) {
					id = idInit;
				}
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					MainPanel.getMainPanel().swapPanel(new ViewCarPanel(id,false ));
					
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