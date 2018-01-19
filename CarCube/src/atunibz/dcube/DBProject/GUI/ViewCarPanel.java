package atunibz.dcube.DBProject.GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.imageio.ImageIO;
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
import javax.swing.SwingUtilities;

import atunibz.dcube.DBProject.GUI.ColorsPanel.ColorCheckBox;
import atunibz.dcube.DBProject.configuration.AppResources;
import atunibz.dcube.DBProject.configuration.GetListQuery;

public class ViewCarPanel extends JPanel {

	Connection conn;
	JPanel viewCarPanel;
	JPanel titlePanel, row, photoPanel;
	JComboBox<String> makeCombo, modelCombo, typesCombo, fuelCombo, transmissionCombo, driveCombo, serviceTypeCombo,
			constructionCombo, tireTypeCombo;
	JTextField newMake, newModel, newKm, newType, doors, seats, newYear, newFuel, newEuro, newCapacity, newHorses;
	JTextField newLength, newHeight, newWidth, newWeight, newTrunk, newTireWidth, newAspetRatio, newDiameter;
	String currentMake, currentModel, currentCarType, currentFuel, currentDrive, currentTransmission;
	int currentKm, currentDoors, currentSeats, currentYear, currentEuro, currentCapacity, currentHorsepower;
	double finalPrice;
	
	// Order of the returned array: length, height, width, trunk capacity, weight
	int[] currentDimensions, tireIntegersData; // Tire integers: width, aspet_ratio, diameter
	// Tire strings: service type, construction, tire type
	String[] tireStringsData;
	ArrayList<String> currentCarColors, currentOptionals;
	ArrayList<Integer> currentOptionalsIds;
	JButton makeModelButton, modifyKm, modifyTypeEtc, modifyPrice, modifyYear, modifyEuroFuel, modifyCapHorse,
			modifyDriveTranmission, modifyLengthWidthHeigth, modifyWeightTrunk, modifyTireDimensions, modifyTireTypes,
			modifyColors, modifyOptional;
	JButton back, sell, delete, modify;
	ArrayList<JCheckBox> optionals;
	JPanel optPanel, contentOpt;
	ArrayList<Integer> allIds;
	// global importan variables
	boolean isNewCar;
	int sold;
	String carId;
	String selCustomer;
	
	public ViewCarPanel(String id, boolean newCar, String selectedCustomer) {

		// make global for listeners and support methods
		isNewCar = newCar;
		carId = id;
		selCustomer = selectedCustomer;
		currentCarColors = new ArrayList<>();
		currentOptionals = new ArrayList<>();
		currentOptionalsIds = new ArrayList<>();
		// Get the connection
		conn = DatabaseConnection.getDBConnection().getConnection();

		// Main Panel
		viewCarPanel = new JPanel();
		viewCarPanel.setLayout(new BoxLayout(viewCarPanel, BoxLayout.Y_AXIS));
		viewCarPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		viewCarPanel.setOpaque(false);

		// Add title Panel
		titlePanel = AppResources.carCubePanel();
		viewCarPanel.add(titlePanel);

		// Panel representing the main row that contains both pic and data
		row = new JPanel();
		row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
		row.setOpaque(false);

		// Photo
		photoPanel = new JPanel();
		photoPanel.setLayout(new BorderLayout());
		photoPanel.setOpaque(false);

		// Car is sold? in this case deactivate sell and delete

		sold = getIntFromGeneralCarTable("sold", carId, isNewCar);

		// Cremo: big panel that contains photo panel and buttons
		JPanel bigLeftPanel = new JPanel();
		bigLeftPanel.setLayout(new BoxLayout(bigLeftPanel, BoxLayout.Y_AXIS));
		bigLeftPanel.setOpaque(false);
		// panels with first two buttons
		JPanel firstRowButton = new JPanel();
		firstRowButton.setOpaque(false);
		// panels with second two buttons
		JPanel secondRowButton = new JPanel();
		secondRowButton.setOpaque(false);

		// back
		back = AppResources.iconButton("Go back     ", "icons/back.png");
		back.addActionListener(new BackListener());
		firstRowButton.add(back);
		firstRowButton.add((Box.createRigidArea(new Dimension(50, 0))));

		// Modify
		modify = AppResources.iconButton("Modify     ", "icons/contacts/modify.png");
		modify.addActionListener(new ModifyListener());
		firstRowButton.add(modify);

		// Sell
		sell = AppResources.iconButton("Sell car       ", "icons/cart.png");
		sell.addActionListener(new sellCar() );
		
		if (sold == 1)
			sell.setEnabled(false);
		secondRowButton.add(sell);
		secondRowButton.add((Box.createRigidArea(new Dimension(50, 0))));

		// Delete
		delete = AppResources.iconButton("Delete     ", "icons/delete.png");
		if (sold == 1)
			delete.setEnabled(false);
		delete.addActionListener(new deleteCar() );
		secondRowButton.add(delete);

		// Inner panel in order to have it automatically centered
		JPanel photo = new JPanel();
		photo.setOpaque(false);
		photo.setLayout(new BoxLayout(photo, BoxLayout.X_AXIS));
		photo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		ImageIcon photoIcon = getPhotoFromId(id, newCar);
		JLabel photoLabel = new JLabel();
		photoLabel.setIcon(photoIcon);

		photo.add(photoLabel);
		bigLeftPanel.add(photo);
		bigLeftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		bigLeftPanel.add(firstRowButton);
		bigLeftPanel.add(secondRowButton);
		photoPanel.add(bigLeftPanel, BorderLayout.NORTH);

		row.add(photoPanel);

		// Info and description
		JPanel info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		info.setOpaque(false);

		// First row : make and model
		JPanel support = new JPanel();
		support.setOpaque(false);

		JPanel firstRow = new JPanel();
		firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.X_AXIS));
		firstRow.setOpaque(false);

		// jlabels and font
		JLabel make = new JLabel();

		AppResources.changeFont(make, Font.BOLD, 30);
		currentMake = getStringFromGeneralCarTable("make", id, newCar);
		make.setText(currentMake);

		// Cremo: add correspondent icon
		JLabel iconNewUsed = new JLabel();
		if (newCar)
			iconNewUsed.setIcon(new ImageIcon("icons/new2.png"));
		else
			iconNewUsed.setIcon(new ImageIcon("icons/used2.png"));

		firstRow.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		firstRow.add(iconNewUsed);
		firstRow.add(Box.createRigidArea(new Dimension(10, 0)));
		firstRow.add(make);

		JLabel model = new JLabel();

		AppResources.changeFont(model, Font.BOLD, 30);
		currentModel = getStringFromGeneralCarTable("model", id, newCar);
		model.setText(currentModel);
		firstRow.add(Box.createRigidArea(new Dimension(10, 0)));
		firstRow.add(model);

		// modify button
		makeModelButton = new JButton();
		makeModelButton.setIcon(new ImageIcon("icons/contacts/modify.png"));
		makeModelButton.setVisible(true);
		makeModelButton.addActionListener(new modifyMakeModel());
		firstRow.add(Box.createRigidArea(new Dimension(10, 0)));
		firstRow.add(makeModelButton);

		support.add(firstRow);
		info.add(support);

		// Description row: General info
		JPanel descPanel = new JPanel();
		descPanel.setOpaque(false);
		descPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel generalData = new JLabel("General Data");
		JLabel icon1 = new JLabel(new ImageIcon("icons/tools.png"));
		JLabel icon2 = new JLabel(new ImageIcon("icons/tools.png"));
		AppResources.changeFont(generalData, Font.BOLD, 22);

		descPanel.add(icon1);
		descPanel.add(generalData);
		descPanel.add(icon2);
		info.add(Box.createRigidArea(new Dimension(0, 10)));
		info.add(descPanel);
		info.add(Box.createRigidArea(new Dimension(0, 10)));

		// OptionalRow: kilometers only for used cars
		if (!newCar) {

			JPanel kmPanel = new JPanel();
			kmPanel.setOpaque(false);
			kmPanel.setLayout(new BorderLayout());

			JPanel kilometersRow = new JPanel();
			kilometersRow.setLayout(new BoxLayout(kilometersRow, BoxLayout.X_AXIS));
			kilometersRow.setOpaque(false);

			currentKm = getIntFromGeneralCarTable("mileage", id, false);
			JLabel km = new JLabel(currentKm + " Km");
			AppResources.changeFont(km, Font.PLAIN, 18);
			kilometersRow.add(km);

			modifyKm = new JButton();
			modifyKm.addActionListener(new modifyKmListener());
			modifyKm.setIcon(new ImageIcon("icons/contacts/modify.png"));
			modifyKm.setVisible(true);

			kilometersRow.add(Box.createRigidArea(new Dimension(10, 0)));
			// kilometersRow.add(modifyKm);

			kmPanel.add(kilometersRow, BorderLayout.WEST);
			kmPanel.add(modifyKm, BorderLayout.EAST);
			info.add(kmPanel);
		}

		// I forgot the price :)
		JPanel priceSupport = new JPanel();
		priceSupport.setOpaque(false);
		priceSupport.setLayout(new BorderLayout());

		JPanel pricePanel = new JPanel();
		pricePanel.setLayout(new BoxLayout(pricePanel, BoxLayout.X_AXIS));
		pricePanel.setOpaque(false);

		if (newCar)
			finalPrice = getIntFromGeneralCarTable("base_price", id, newCar) + calculatePrice(Integer.parseInt(id));
		else
			finalPrice = getIntFromGeneralCarTable("net_price", id, newCar);

		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
		JLabel priceLabel = new JLabel("Final price: " + currencyFormat.format(finalPrice));
		AppResources.changeFont(priceLabel, Font.PLAIN, 18);
		modifyPrice = new JButton();
		modifyPrice.setIcon(new ImageIcon("icons/contacts/modify.png"));
		modifyPrice.setVisible(true);

		pricePanel.add(priceLabel);
		// pricePanel.add(Box.createRigidArea(new Dimension(10, 0)));
		// pricePanel.add(modifyPrice);

		priceSupport.add(pricePanel, BorderLayout.WEST);
		priceSupport.add(modifyPrice, BorderLayout.EAST);
		info.add(priceSupport);

		// car type, doors and seats
		JPanel support2 = new JPanel();
		support2.setOpaque(false);
		support2.setLayout(new BorderLayout());

		JPanel secondRow = new JPanel();
		secondRow.setLayout(new BoxLayout(secondRow, BoxLayout.X_AXIS));
		secondRow.setOpaque(false);

		currentCarType = getStringFromGeneralCarTable("car_type", id, newCar);
		currentDoors = getIntFromGeneralCarTable("doors", id, newCar);
		currentSeats = getIntFromGeneralCarTable("seats", id, newCar);

		JLabel typeDoorsSeats = new JLabel(
				currentCarType + " with " + currentDoors + " doors and " + currentSeats + " seats");
		AppResources.changeFont(typeDoorsSeats, Font.PLAIN, 18);

		modifyTypeEtc = new JButton();
		modifyTypeEtc.addActionListener(new modifyTypeEtcListener());
		modifyTypeEtc.setIcon(new ImageIcon("icons/contacts/modify.png"));
		modifyTypeEtc.setVisible(true);

		secondRow.add(typeDoorsSeats);

		support2.add(secondRow, BorderLayout.WEST);
		support2.add(modifyTypeEtc, BorderLayout.EAST);
		info.add(support2);

		// END SECOND ROW

		// Car year
		JPanel support3 = new JPanel();
		support3.setOpaque(false);
		support3.setLayout(new BorderLayout());

		JPanel yearPanel = new JPanel();
		yearPanel.setLayout(new BoxLayout(yearPanel, BoxLayout.X_AXIS));
		yearPanel.setOpaque(false);

		currentYear = getIntFromGeneralCarTable("car_year", id, newCar);

		JLabel yearLabel = new JLabel("Produced in " + currentYear);
		AppResources.changeFont(yearLabel, Font.PLAIN, 18);

		modifyYear = new JButton();

		modifyYear.setIcon(new ImageIcon("icons/contacts/modify.png"));
		modifyYear.addActionListener(new modifyYear());
		modifyYear.setVisible(true);

		yearPanel.add(yearLabel);
		// yearPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		// yearPanel.add(modifyYear);

		support3.add(yearPanel, BorderLayout.WEST);
		support3.add(modifyYear, BorderLayout.EAST);
		info.add(support3);
		
		// add label for sold or not
		JPanel support4 = new JPanel();
		support4.setOpaque(false);
		support4.setLayout(new BorderLayout());
		JPanel soldPanel = new JPanel();
		soldPanel.setLayout(new BoxLayout(soldPanel, BoxLayout.X_AXIS));
		soldPanel.setOpaque(false);

		JLabel soldLabel = new JLabel();
		if (sold == 0)
			soldLabel.setText("Not sold");
		else
			soldLabel.setText("Sold");
		AppResources.changeFont(soldLabel, Font.PLAIN, 18);
		soldPanel.add(soldLabel);
		support4.add(soldPanel, BorderLayout.WEST);
		info.add(support4);

		// Engine data label
		JPanel engineData = new JPanel();
		engineData.setOpaque(false);
		// engineData.setLayout(new BorderLayout());

		JLabel techLabel = new JLabel("Engine Data");
		JLabel icon3 = new JLabel(new ImageIcon("icons/engine.png"));
		JLabel icon4 = new JLabel(new ImageIcon("icons/engine.png"));
		AppResources.changeFont(techLabel, Font.BOLD, 22);
		engineData.add(icon3);
		engineData.add(techLabel);
		engineData.add(icon4);
		info.add(Box.createRigidArea(new Dimension(0, 10)));
		info.add(engineData);
		info.add(Box.createRigidArea(new Dimension(0, 10)));

		// Euro and fuel
		JPanel supportEngine1 = new JPanel();
		supportEngine1.setOpaque(false);
		supportEngine1.setLayout(new BorderLayout());

		JPanel engine1 = new JPanel();
		engine1.setOpaque(false);
		engine1.setLayout(new BoxLayout(engine1, BoxLayout.X_AXIS));

		currentEuro = getIntFromEngineTable("euro", id);
		currentFuel = getStringFromEngineTable("fuel", id);

		JLabel euroFuel = new JLabel("Powered by a Euro " + currentEuro + " " + currentFuel + " engine");
		AppResources.changeFont(euroFuel, Font.PLAIN, 18);

		modifyEuroFuel = new JButton();
		modifyEuroFuel.setIcon(new ImageIcon("icons/contacts/modify.png"));
		modifyEuroFuel.addActionListener(new modifyEuroFuel());
		modifyEuroFuel.setVisible(true);

		engine1.add(euroFuel);
		// engine1.add(Box.createRigidArea(new Dimension(10, 0)));
		// engine1.add(modifyEuroFuel);
		supportEngine1.add(engine1, BorderLayout.WEST);
		supportEngine1.add(modifyEuroFuel, BorderLayout.EAST);
		info.add(supportEngine1);

		// Capacity and horsepower
		JPanel supportEngine2 = new JPanel();
		supportEngine2.setOpaque(false);
		supportEngine2.setLayout(new BorderLayout());

		JPanel engine2 = new JPanel();
		engine2.setOpaque(false);
		engine2.setLayout(new BoxLayout(engine2, BoxLayout.X_AXIS));

		currentCapacity = getIntFromEngineTable("capacity", id);
		currentHorsepower = getIntFromEngineTable("horsepower", id);

		JLabel capacityHorse = new JLabel(
				"It has a capacity of " + currentCapacity + " kw with " + currentHorsepower + " of horsepower");
		AppResources.changeFont(capacityHorse, Font.PLAIN, 18);

		modifyCapHorse = new JButton();
		modifyCapHorse.setIcon(new ImageIcon("icons/contacts/modify.png"));
		modifyCapHorse.addActionListener(new modifyCapacityHorsepower());
		modifyCapHorse.setVisible(true);

		engine2.add(capacityHorse);
		supportEngine2.add(engine2, BorderLayout.WEST);
		supportEngine2.add(modifyCapHorse, BorderLayout.EAST);
		info.add(supportEngine2);

		// Wheel drive and transmission

		JPanel supportEngine3 = new JPanel();
		supportEngine3.setOpaque(false);
		supportEngine3.setLayout(new BorderLayout());

		JPanel engine3 = new JPanel();
		engine3.setOpaque(false);
		engine3.setLayout(new BoxLayout(engine3, BoxLayout.X_AXIS));

		currentDrive = getStringFromEngineTable("wheel_drive", id);
		currentTransmission = getStringFromEngineTable("transmission", id);

		JLabel driveTransmission = new JLabel(
				"This car has a  " + currentDrive + "-wheel drive with " + currentTransmission + " transmission   ");
		AppResources.changeFont(driveTransmission, Font.PLAIN, 18);

		modifyDriveTranmission = new JButton();
		modifyDriveTranmission.setIcon(new ImageIcon("icons/contacts/modify.png"));
		modifyDriveTranmission.addActionListener(new modifyTransmissionDrive());
		modifyDriveTranmission.setVisible(true);

		engine3.add(driveTransmission);
		supportEngine3.add(engine3, BorderLayout.WEST);
		supportEngine3.add(modifyDriveTranmission, BorderLayout.EAST);
		info.add(supportEngine3);

		// Dimension data label
		JPanel dimensionData = new JPanel();
		dimensionData.setOpaque(false);
		// dimensionData.setLayout(new BorderLayout());

		JLabel dimensionLabel = new JLabel("Dimension");
		JLabel icon5 = new JLabel(new ImageIcon("icons/ruler.png"));
		JLabel icon6 = new JLabel(new ImageIcon("icons/ruler.png"));
		AppResources.changeFont(dimensionLabel, Font.BOLD, 22);
		dimensionData.add(icon5);
		dimensionData.add(dimensionLabel);
		dimensionData.add(icon6);
		info.add(Box.createRigidArea(new Dimension(0, 10)));
		info.add(dimensionData);
		info.add(Box.createRigidArea(new Dimension(0, 10)));

		// first initialise all dimensions
		currentDimensions = getCarDimensions(id);

		// Dimension first row: length, height and width
		JPanel dimension1support = new JPanel();
		dimension1support.setOpaque(false);
		dimension1support.setLayout(new BorderLayout());

		JPanel dimension1 = new JPanel();
		dimension1.setOpaque(false);
		dimension1.setLayout(new BoxLayout(dimension1, BoxLayout.X_AXIS));

		JLabel dim1 = new JLabel("Length: " + currentDimensions[0] + " cm - Height: " + currentDimensions[1]
				+ " cm - Width: " + currentDimensions[2] + " cm");
		AppResources.changeFont(dim1, Font.PLAIN, 18);

		modifyLengthWidthHeigth = new JButton();
		modifyLengthWidthHeigth.setIcon(new ImageIcon("icons/contacts/modify.png"));
		modifyLengthWidthHeigth.addActionListener(new modifyLengthHeightWidth());
		modifyLengthWidthHeigth.setVisible(true);

		dimension1.add(dim1);
		dimension1support.add(dimension1, BorderLayout.WEST);
		dimension1support.add(modifyLengthWidthHeigth, BorderLayout.EAST);
		info.add(dimension1support);

		// Dimension second row: weight and trunk capacitu
		JPanel dimension2support = new JPanel();
		dimension2support.setOpaque(false);
		dimension2support.setLayout(new BorderLayout());

		JPanel dimension2 = new JPanel();
		dimension2.setOpaque(false);
		dimension2.setLayout(new BoxLayout(dimension2, BoxLayout.X_AXIS));

		JLabel dim2 = new JLabel(
				"Weight: " + currentDimensions[4] + " kg - Trunk capacity: " + currentDimensions[3] + " liters");
		AppResources.changeFont(dim2, Font.PLAIN, 18);

		modifyWeightTrunk = new JButton();
		modifyWeightTrunk.setIcon(new ImageIcon("icons/contacts/modify.png"));
		modifyWeightTrunk.addActionListener(new modifyWeightTrunk());
		modifyWeightTrunk.setVisible(true);

		dimension2.add(dim2);
		dimension2support.add(dimension2, BorderLayout.WEST);
		dimension2support.add(modifyWeightTrunk, BorderLayout.EAST);
		info.add(dimension2support);

		// TIRE (whyyyyyyyy???)

		JPanel tireData = new JPanel();
		tireData.setOpaque(false);

		JLabel tireLabel = new JLabel("Tires Data");
		JLabel icon7 = new JLabel(new ImageIcon("icons/tire.png"));
		JLabel icon8 = new JLabel(new ImageIcon("icons/tire.png"));
		AppResources.changeFont(tireLabel, Font.BOLD, 22);
		tireData.add(icon7);
		tireData.add(tireLabel);
		tireData.add(icon8);
		info.add(Box.createRigidArea(new Dimension(0, 10)));
		info.add(tireData);
		info.add(Box.createRigidArea(new Dimension(0, 10)));
		// Tire integers: width, aspet_ratio, diameter
		// Tire strings: service type, construction, tire type
		initialiseTireVariables(id);

		// Tire first row
		JPanel supportTire1 = new JPanel();
		supportTire1.setLayout(new BorderLayout());
		supportTire1.setOpaque(false);

		JLabel tire1 = new JLabel("Dimensions: " + tireIntegersData[0] + "/" + tireIntegersData[1] + " "
				+ tireStringsData[1] + " " + tireIntegersData[2]);
		AppResources.changeFont(tire1, Font.PLAIN, 18);

		modifyTireDimensions = new JButton();
		modifyTireDimensions.setIcon(new ImageIcon("icons/contacts/modify.png"));
		modifyTireDimensions.addActionListener(new modifyTireDimensions());
		modifyTireDimensions.setVisible(true);

		supportTire1.add(tire1, BorderLayout.WEST);
		supportTire1.add(modifyTireDimensions, BorderLayout.EAST);

		info.add(supportTire1);

		// Tire second row
		JPanel supportTire2 = new JPanel();
		supportTire2.setLayout(new BorderLayout());
		supportTire2.setOpaque(false);

		JLabel tire2 = new JLabel("Service type: " + tireStringsData[0] + " - Tire type: " + tireStringsData[2]);
		AppResources.changeFont(tire2, Font.PLAIN, 18);

		modifyTireTypes = new JButton();
		modifyTireTypes.setIcon(new ImageIcon("icons/contacts/modify.png"));
		modifyTireTypes.addActionListener(new modifyTireType());
		modifyTireTypes.setVisible(true);

		supportTire2.add(tire2, BorderLayout.WEST);
		supportTire2.add(modifyTireTypes, BorderLayout.EAST);
		info.add(supportTire2);

		// Color label
		JPanel colorData = new JPanel();
		colorData.setOpaque(false);

		JLabel colorLabel = new JLabel("  Colors  ");
		JLabel icon9 = new JLabel(new ImageIcon("icons/pantone.png"));
		JLabel icon10 = new JLabel(new ImageIcon("icons/pantone.png"));
		AppResources.changeFont(colorLabel, Font.BOLD, 22);
		colorData.add(icon9);
		colorData.add(colorLabel);
		colorData.add(icon10);
		info.add(Box.createRigidArea(new Dimension(0, 10)));
		info.add(colorData);

		// Colors label
		getCarColors(id);

		JPanel colors = new JPanel();
		colors.setLayout(new BorderLayout());
		colors.setOpaque(false);

		String colorString = "";

		for (String c : currentCarColors) {
			if (colorString.length() > 0)
				colorString += ", " + c;
			else
				colorString += c;
		}

		JLabel colorList = new JLabel("This car is painted in " + colorString);
		AppResources.changeFont(colorList, Font.PLAIN, 18);

		modifyColors = new JButton();
		modifyColors.setIcon(new ImageIcon("icons/contacts/modify.png"));
		modifyColors.addActionListener(new modifyColors());
		modifyColors.setVisible(true);

		colors.add(colorList, BorderLayout.WEST);
		colors.add(modifyColors, BorderLayout.EAST);
		info.add(colors);

		// Optional label
		JPanel optionalData = new JPanel();
		optionalData.setOpaque(false);

		JLabel optionalLabel = new JLabel("Optionals");
		JLabel icon11 = new JLabel(new ImageIcon("icons/opt.png"));
		JLabel icon12 = new JLabel(new ImageIcon("icons/opt.png"));
		AppResources.changeFont(optionalLabel, Font.BOLD, 22);
		optionalData.add(icon11);
		optionalData.add(optionalLabel);
		optionalData.add(icon12);
		info.add(Box.createRigidArea(new Dimension(0, 10)));
		info.add(optionalData);

		// Optional list
		getCarOptionals(id);

		JPanel optionals = new JPanel();
		optionals.setLayout(new BorderLayout());
		optionals.setOpaque(false);

		String optionalString = "";

		for (String c : currentOptionals) {
			optionalString += " <li> " + c + "</li>";
		}

		JLabel optionalList = new JLabel("<html>This car is equipped with:<br><ul>" + optionalString + "</ul></html>");
		AppResources.changeFont(optionalList, Font.PLAIN, 18);

		JPanel modOptPanel = new JPanel();
		modOptPanel.setOpaque(false);
		modifyOptional = new JButton();
		modifyOptional.setIcon(new ImageIcon("icons/contacts/modify.png"));
		modifyOptional.addActionListener(new modifyOptionals());
		modifyOptional.setVisible(true);
		modOptPanel.add(modifyOptional);

		optionals.add(optionalList, BorderLayout.WEST);
		optionals.add(modOptPanel, BorderLayout.EAST);
		info.add(optionals);

		// ADD ALL
		row.add(Box.createRigidArea(new Dimension(30, 0)));
		row.add(info);
		// End info
		viewCarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		// add the row
		viewCarPanel.add(row);

		/*
		 * // Panel for buttons controls JPanel buttonPanel = new JPanel();
		 * buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		 */

		viewCarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		// End of the panel
		add(viewCarPanel);

		// Make all buttons invisible (it was for debug purposes)
		changeButtonState();
	}

	// SUPPORT METHOD
	// get Photo from id
	public ImageIcon getPhotoFromId(String id, boolean newCar) {

		BufferedImage buffImg = null;
		try {
			if (newCar)
				buffImg = ImageIO.read(new File("icons/newCars/" + id + ".jpg"));
			else
				buffImg = ImageIO.read(new File("icons/usedCars/" + id + ".jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Image image = buffImg.getScaledInstance(350, 300, Image.SCALE_SMOOTH);
		ImageIcon scaledImage = new ImageIcon(image);
		return scaledImage;
	}

	public String getStringFromGeneralCarTable(String column, String id, boolean newCar) {

		String sql;

		if (newCar) {
			int idNumb = Integer.parseInt(id);
			sql = "SELECT " + column + " FROM new_car WHERE new_car.car_id = " + idNumb;
		} else
			sql = "SELECT " + column + " FROM used_car WHERE used_car.immatriculation = '" + id + "'";

		String result = "";
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			rs.next();

			result = rs.getString(column);

			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public int getIntFromGeneralCarTable(String column, String id, boolean newCar) {

		String sql;

		if (newCar) {
			int idNumb = Integer.parseInt(id);
			sql = "SELECT " + column + " FROM new_car WHERE new_car.car_id = " + idNumb;
		} else
			sql = "SELECT " + column + " FROM used_car WHERE used_car.immatriculation = '" + id + "'";

		int result = 0;
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			rs.next();

			result = rs.getInt(column);

			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public int getIntFromEngineTable(String column, String id) {

		String sql;

		if (isNewCar) {
			int idNumb = Integer.parseInt(id);
			sql = "SELECT " + column
					+ " FROM new_car INNER JOIN engine on new_car.engine = engine.engine_id WHERE new_car.car_id = "
					+ idNumb;
		} else
			sql = "SELECT " + column
					+ " FROM used_car INNER JOIN engine on used_car.engine = engine.engine_id WHERE used_car.immatriculation = '"
					+ id + "'";

		int result = 0;

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			rs.next();

			result = rs.getInt(column);

			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public String getStringFromEngineTable(String column, String id) {

		String sql;

		if (isNewCar) {
			int idNumb = Integer.parseInt(id);
			sql = "SELECT " + column
					+ " FROM new_car INNER JOIN engine on new_car.engine = engine.engine_id WHERE new_car.car_id = "
					+ idNumb;
		} else
			sql = "SELECT " + column
					+ " FROM used_car INNER JOIN engine on used_car.engine = engine.engine_id WHERE used_car.immatriculation = '"
					+ id + "'";

		String result = "";

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			rs.next();

			result = rs.getString(column);

			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	// Order of the returned array: length, height, width, trunk capacity, weight
	public int[] getCarDimensions(String id) {

		String sql = "";

		if (isNewCar) {
			int idNumb = Integer.parseInt(id);
			sql = "SELECT * FROM new_car INNER JOIN dimension ON new_car.dimension = dimension.dimension_id WHERE new_car.car_id = "
					+ idNumb;
		} else
			sql = "SELECT * FROM used_car INNER JOIN dimension ON used_car.dimension = dimension.dimension_id WHERE used_car.immatriculation = '"
					+ id + "'";

		int[] dimensions = new int[5];

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			rs.next();

			dimensions[0] = rs.getInt("car_length");
			dimensions[1] = rs.getInt("car_heigth");
			dimensions[2] = rs.getInt("car_width");
			dimensions[3] = rs.getInt("trunk_capacity");
			dimensions[4] = rs.getInt("car_weight");

			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dimensions;
	}

	public void initialiseTireVariables(String id) {

		String sql = "";

		if (isNewCar) {
			int idNumb = Integer.parseInt(id);
			sql = "SELECT tire.* FROM new_car INNER JOIN tire ON new_car.tire = tire.tire_id WHERE new_car.car_id = "
					+ idNumb;
		} else
			sql = "SELECT tire.* FROM used_car INNER JOIN tire ON used_car.tire = tire.tire_id WHERE used_car.immatriculation = '"
					+ id + "'";
		tireIntegersData = new int[3];
		tireStringsData = new String[3];
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			rs.next();

			tireIntegersData[0] = rs.getInt("width");
			tireIntegersData[1] = rs.getInt("aspet_ratio");
			tireIntegersData[2] = rs.getInt("diameter");

			tireStringsData[0] = rs.getString("service_type");
			tireStringsData[1] = rs.getString("construction");
			tireStringsData[2] = rs.getString("tire_type");

			st.close();
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void getCarColors(String id) {
		String sql = "";

		if (isNewCar) {
			int idNumb = Integer.parseInt(id);
			sql = "SELECT color.color_name FROM new_car INNER JOIN new_painting ON new_car.car_id = new_painting.car_id INNER JOIN color ON new_painting.color_code = color.color_code WHERE new_car.car_id = "
					+ idNumb;
		} else
			sql = "SELECT color.color_name FROM used_car INNER JOIN used_painting ON used_car.immatriculation = used_painting.immatriculation INNER JOIN color ON used_painting.color_code = color.color_code WHERE used_car.immatriculation = '"
					+ id + "'";

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				currentCarColors.add(rs.getString("color_name"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void getCarOptionals(String id) {
		String sql = "";

		if (isNewCar) {
			int idNumb = Integer.parseInt(id);
			sql = "SELECT optional.optional_id, optional.opt_name FROM new_car INNER JOIN new_equipped ON new_car.car_id = new_equipped.car_id INNER JOIN optional ON new_equipped.optional_id = optional.optional_id WHERE new_car.car_id = "
					+ idNumb;
		} else
			sql = "SELECT optional.optional_id, optional.opt_name FROM used_car INNER JOIN used_equipped ON used_car.immatriculation = used_equipped.immatriculation INNER JOIN optional ON used_equipped.optional_id = optional.optional_id WHERE used_car.immatriculation = '"
					+ id + "'";

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				currentOptionals.add(rs.getString("opt_name"));
				currentOptionalsIds.add(rs.getInt("optional_id"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public int calculatePrice(int car_id) {
		int result = 0;
		try {
			String query = "SELECT sum(price) FROM new_equipped INNER JOIN optional ON new_equipped.optional_id = optional.optional_id AND new_equipped.car_id = "
					+ car_id;
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

	// LISTENER FOR CONTROL BUTTONS AT THE END
	private class BackListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			MainPanel.getMainPanel().swapPanel(new SearchCarPanel());

		}

	}

	private class ModifyListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			changeButtonState();

		}

	}

	public void changeButtonState() {

		boolean visibleState;

		if (makeModelButton.isVisible())
			visibleState = false;
		else
			visibleState = true;

		makeModelButton.setVisible(visibleState);
		if (!isNewCar)
			modifyKm.setVisible(visibleState);

		modifyTypeEtc.setVisible(visibleState);
		modifyPrice.setVisible(visibleState);
		modifyYear.setVisible(visibleState);
		modifyEuroFuel.setVisible(visibleState);
		modifyCapHorse.setVisible(visibleState);
		modifyDriveTranmission.setVisible(visibleState);
		modifyLengthWidthHeigth.setVisible(visibleState);
		modifyWeightTrunk.setVisible(visibleState);
		modifyTireDimensions.setVisible(visibleState);
		modifyTireTypes.setVisible(visibleState);
		modifyColors.setVisible(visibleState);
		modifyOptional.setVisible(visibleState);

	}

	// LISTENER FOR MODIFY BUTTONS
	private class modifyMakeModel implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			// Prepare JPanel for the option pane
			JPanel modify = new JPanel();

			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

			// Make Row
			JPanel make = new JPanel();
			make.setLayout(new BoxLayout(make, BoxLayout.X_AXIS));

			JLabel makeLabel = new JLabel("Make");
			AppResources.changeFont(makeLabel, Font.PLAIN, 18);
			make.add(makeLabel);

			String[] makes = GetListQuery.getMakes(2);
			makeCombo = new JComboBox(makes);
			makeCombo.setSelectedItem(currentMake);
			makeCombo.addActionListener(new MakeListener());
			// makeCombo.setPrototypeDisplayValue("XXXXXXXXXXXXXXX");
			make.add(Box.createRigidArea(new Dimension(10, 0)));
			make.add(makeCombo);

			newMake = new JTextField(10);
			make.add(Box.createRigidArea(new Dimension(10, 0)));
			make.add(newMake);

			container.add(make);

			// Model Row
			JPanel model = new JPanel();
			model.setLayout(new BoxLayout(model, BoxLayout.X_AXIS));

			JLabel modelLabel = new JLabel("Model");
			AppResources.changeFont(modelLabel, Font.PLAIN, 18);
			model.add(modelLabel);

			String[] models = GetListQuery.getModels(2, (String) makeCombo.getSelectedItem());
			modelCombo = new JComboBox(models);
			modelCombo.setSelectedItem(currentModel);

			// makeCombo.setPrototypeDisplayValue("XXXXXXXXXXXXXXX");
			model.add(Box.createRigidArea(new Dimension(10, 0)));
			model.add(modelCombo);

			newModel = new JTextField(10);
			model.add(Box.createRigidArea(new Dimension(10, 0)));
			model.add(newModel);

			container.add(Box.createRigidArea(new Dimension(0, 20)));
			container.add(model);

			modify.add(container);

			// Panel is ready, create and show OptionPane
			String[] options = { "Update", "Cancel" };
			String selected = "Cancel";
			int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), modify, "Modify make and model",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {
				String updateMake, updateModel;

				// Get make
				if (!newMake.getText().equals(""))
					updateMake = newMake.getText();
				else
					updateMake = (String) makeCombo.getSelectedItem();
				// Get model
				if (!newModel.getText().equals(""))
					updateModel = newModel.getText();
				else
					updateModel = (String) modelCombo.getSelectedItem();

				// Check length
				if (updateMake.length() > 20 || updateModel.length() > 20) {
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Make and model cannot be longer than 20 characters",
							"CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}

				// Update the DB
				String query = "UPDATE ";
				if (isNewCar)
					query += " new_car SET make = '" + updateMake + "', model = '" + updateModel + "' WHERE car_id = "
							+ carId;
				else
					query += " used_car SET make = '" + updateMake + "', model = '" + updateModel
							+ "' WHERE immatriculation = '" + carId + "'";

				try {
					Statement st = conn.createStatement();

					st.executeUpdate(query);
					st.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}

				MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar, ""));

			}
		}

		// listener for the change item make field
		private class MakeListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Change model accordingly
				JComboBox selectedCombo = (JComboBox) arg0.getSource();
				String selectedMake = (String) selectedCombo.getSelectedItem();
				modelCombo.removeAllItems();
				modelCombo.addItem("All Models");
				String modelsList[];
				if (selectedMake != null && selectedMake.compareTo("All Makes") != 0) {
					modelsList = GetListQuery.getModels(2, (String) makeCombo.getSelectedItem()); // See method comments
																									// for
																									// more info
					for (String s : modelsList)
						modelCombo.addItem(s);
				}

			}

		}

	}

	private class modifyKmListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			// Prepare JPanel for the option pane
			JPanel modify = new JPanel();

			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

			// Km Row
			JPanel km = new JPanel();
			km.setLayout(new BoxLayout(km, BoxLayout.X_AXIS));

			JLabel kmLabel = new JLabel("Kilometers");
			AppResources.changeFont(kmLabel, Font.PLAIN, 18);
			km.add(kmLabel);

			newKm = new JTextField(10);
			newKm.setText(currentKm + "");
			km.add(Box.createRigidArea(new Dimension(10, 0)));
			km.add(newKm);

			container.add(km);
			modify.add(container);

			// Panel is ready, create and show OptionPane
			String[] options = { "Update", "Cancel" };
			String selected = "Cancel";
			int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), modify, "Modify km",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {
				int newValue = 0;
				try {
					newValue = Integer.parseInt(newKm.getText());
				} catch (NumberFormatException n) {
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "The value entered is not an integer", "CarCube",
							JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}

				String sql = "UPDATE used_car SET mileage = " + newValue + " WHERE immatriculation = '" + carId + "'";

				try {
					Statement st = conn.createStatement();

					st.executeUpdate(sql);
					st.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
				MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar, ""));
			}
		}

	}

	private class modifyTypeEtcListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			// Prepare JPanel for the option pane
			JPanel modify = new JPanel();

			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

			// Type Row
			JPanel type = new JPanel();
			type.setLayout(new BoxLayout(type, BoxLayout.X_AXIS));

			JLabel typeLabel = new JLabel("Type");
			AppResources.changeFont(typeLabel, Font.PLAIN, 18);
			type.add(typeLabel);

			String[] types = GetListQuery.getCarTypes();
			typesCombo = new JComboBox(types);
			typesCombo.setSelectedItem(currentCarType);
			type.add(Box.createRigidArea(new Dimension(18, 0)));
			type.add(typesCombo);

			newType = new JTextField(10);
			type.add(Box.createRigidArea(new Dimension(10, 0)));
			type.add(newType);

			container.add(type);

			// Doors row

			JPanel doorsPanel = new JPanel();
			doorsPanel.setLayout(new BoxLayout(doorsPanel, BoxLayout.X_AXIS));

			JLabel doorsLabel = new JLabel("Doors");
			AppResources.changeFont(doorsLabel, Font.PLAIN, 18);
			doorsPanel.add(doorsLabel);

			doors = new JTextField(1);
			doors.setText(currentDoors + "");
			doorsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			doorsPanel.add(doors);
			container.add(doorsPanel);
			// Seats row
			JPanel seatsPanel = new JPanel();
			seatsPanel.setLayout(new BoxLayout(seatsPanel, BoxLayout.X_AXIS));

			JLabel seatsLabel = new JLabel("Seats");
			AppResources.changeFont(seatsLabel, Font.PLAIN, 18);
			seatsPanel.add(seatsLabel);

			seats = new JTextField(1);
			seats.setText(currentSeats + "");
			seatsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			seatsPanel.add(seats);
			container.add(seatsPanel);

			modify.add(container);
			String[] options = { "Update", "Cancel" };
			String selected = "Cancel";
			int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), modify, "Modify km",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {
				int updateDoors, updateSeats;
				String updateType;

				try {
					updateDoors = Integer.parseInt(doors.getText());
					updateSeats = Integer.parseInt(seats.getText());
				} catch (NumberFormatException n) {
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Doors and seats values must be integers", "CarCube",
							JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}

				// now we take the right car type
				if (!newType.getText().equals(""))
					updateType = newType.getText();
				else
					updateType = (String) typesCombo.getSelectedItem();

				if (updateType.length() > 30) {
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Car type cannot be longer than 30 characters",
							"CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}

				// Prepare and execute update query
				String sql = "";

				if (isNewCar)
					sql = "UPDATE new_car SET car_type = '" + updateType + "', doors = " + updateDoors + ", seats = "
							+ updateSeats + " WHERE car_id =" + carId;
				else
					sql = "UPDATE used_car SET car_type = '" + updateType + "', doors = " + updateDoors + ", seats = "
							+ updateSeats + " WHERE immatriculation = '" + carId + "'";

				try {
					Statement st = conn.createStatement();
					st.executeUpdate(sql);
					st.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
				MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar, ""));
			}
		}

	}

	// Modify year of production
	private class modifyYear implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// Prepare JPanel for the option pane
			JPanel modify = new JPanel();

			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

			// Single year row
			JPanel yearRow = new JPanel();
			yearRow.setLayout(new BoxLayout(yearRow, BoxLayout.X_AXIS));

			JLabel yearLabel = new JLabel("Year");
			AppResources.changeFont(yearLabel, Font.PLAIN, 18);

			newYear = new JTextField(4);
			newYear.setText("" + currentYear);

			yearRow.add(yearLabel);
			yearRow.add(Box.createRigidArea(new Dimension(10, 0)));
			yearRow.add(newYear);
			yearRow.add(Box.createRigidArea(new Dimension(20, 0)));

			container.add(yearRow);
			modify.add(container);

			String[] options = { "Update", "Cancel" };
			String selected = "Cancel";
			int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), modify, "Modify km",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {
				int yearUpdated;

				// Check if value inserted is an int
				try {
					yearUpdated = Integer.parseInt(newYear.getText());
				} catch (NumberFormatException n) {
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Year value must be an integer", "CarCube",
							JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}

				// Prepare and execute query
				String sql = "";
				if (isNewCar)
					sql = "UPDATE new_car SET car_year = " + yearUpdated + " WHERE car_id = " + carId;
				else
					sql = "UPDATE used_car SET car_year = " + yearUpdated + " WHERE immatriculation = '" + carId + "'";

				try {
					Statement st = conn.createStatement();
					st.executeUpdate(sql);

					st.close();

				} catch (SQLException e1) {

					e1.printStackTrace();
				}
				MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar, ""));
			}
		}

	}

	// Modify Fuel and Euro
	private class modifyEuroFuel implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// Prepare JPanel for the option pane
			JPanel modify = new JPanel();

			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

			// Fuel row: combo + textfield
			JPanel fuelRow = new JPanel();
			fuelRow.setLayout(new BoxLayout(fuelRow, BoxLayout.X_AXIS));

			JLabel fuel = new JLabel("Fuel");
			AppResources.changeFont(fuel, Font.PLAIN, 18);

			String[] fuelList = GetListQuery.getFuels();
			fuelCombo = new JComboBox<>(fuelList);
			fuelCombo.setSelectedItem(currentFuel);

			newFuel = new JTextField(6);

			fuelRow.add(fuel);
			fuelRow.add(Box.createRigidArea(new Dimension(10, 0)));
			fuelRow.add(fuelCombo);
			fuelRow.add(Box.createRigidArea(new Dimension(2, 0)));
			fuelRow.add(newFuel);

			container.add(fuelRow);

			// Euro row
			JPanel euroRow = new JPanel();
			euroRow.setLayout(new BoxLayout(euroRow, BoxLayout.X_AXIS));

			JLabel euro = new JLabel("Euro");
			AppResources.changeFont(euro, Font.PLAIN, 18);

			newEuro = new JTextField(5);
			newEuro.setText("" + currentEuro);

			euroRow.add(euro);
			euroRow.add(Box.createRigidArea(new Dimension(10, 0)));
			euroRow.add(newEuro);

			container.add(Box.createRigidArea(new Dimension(0, 10)));
			container.add(euroRow);
			modify.add(container);

			String[] options = { "Update", "Cancel" };
			String selected = "Cancel";
			int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), modify, "Modify fuel and euro",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {

				int euroUpdated;
				String fuelUpdated;

				// first, check if euro is integer
				try {
					euroUpdated = Integer.parseInt(newEuro.getText());
				} catch (NumberFormatException n) {
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Euro value must be an integer", "CarCube",
							JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}

				// get correctly fuel
				if (newFuel.getText().equals(""))
					fuelUpdated = (String) fuelCombo.getSelectedItem();
				else {
					if (newFuel.getText().length() > 20) {
						JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Fuel cannot be longer than 20 characters",
								"CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
						return;
					}

					fuelUpdated = newFuel.getText();
				}

				UpdateEngine(currentCapacity, fuelUpdated, currentHorsepower, currentDrive, euroUpdated,
						currentTransmission);
			}

		}
	}

	private class modifyCapacityHorsepower implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// Prepare JPanel for the option pane
			JPanel modify = new JPanel();

			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

			// capacity row
			JPanel capacityRow = new JPanel();
			capacityRow.setLayout(new BoxLayout(capacityRow, BoxLayout.X_AXIS));

			JLabel capacity = new JLabel("Capacity (kw)");
			AppResources.changeFont(capacity, Font.PLAIN, 18);

			newCapacity = new JTextField(4);
			newCapacity.setText("" + currentCapacity);

			capacityRow.add(capacity);
			capacityRow.add(Box.createRigidArea(new Dimension(10, 0)));
			capacityRow.add(newCapacity);

			container.add(capacityRow);

			// Horses row
			JPanel horsesRow = new JPanel();
			horsesRow.setLayout(new BoxLayout(horsesRow, BoxLayout.X_AXIS));

			JLabel horses = new JLabel("Horsepower");
			AppResources.changeFont(horses, Font.PLAIN, 18);

			newHorses = new JTextField(4);
			newHorses.setText("" + currentHorsepower);

			horsesRow.add(horses);
			horsesRow.add(Box.createRigidArea(new Dimension(10, 0)));
			horsesRow.add(newHorses);

			container.add(horsesRow);

			modify.add(container);

			String[] options = { "Update", "Cancel" };
			String selected = "Cancel";
			int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), modify, "Modify capacity and horsepower",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {
				int updatedCap, updatedHorses;

				// First check if they are integers
				try {
					updatedCap = Integer.parseInt(newCapacity.getText());
					updatedHorses = Integer.parseInt(newHorses.getText());
				} catch (NumberFormatException n) {
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Capacity and horsepower values must be integers",
							"CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}

				UpdateEngine(updatedCap, currentFuel, updatedHorses, currentDrive, currentEuro, currentTransmission);
			}
		}

	}

	private class modifyTransmissionDrive implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// Prepare JPanel for the option pane
			JPanel modify = new JPanel();

			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

			// Wheel drive row
			JPanel drivePanel = new JPanel();
			drivePanel.setLayout(new BoxLayout(drivePanel, BoxLayout.X_AXIS));

			JLabel drive = new JLabel("Wheel Drive");
			AppResources.changeFont(drive, Font.PLAIN, 18);

			String[] drives = GetListQuery.getWheelDrive();
			driveCombo = new JComboBox<>(drives);
			driveCombo.setSelectedItem(currentDrive);

			drivePanel.add(drive);
			drivePanel.add(Box.createRigidArea(new Dimension(10, 0)));
			drivePanel.add(driveCombo);

			container.add(drivePanel);
			// Transmission row

			JPanel transmissionPanel = new JPanel();
			transmissionPanel.setLayout(new BoxLayout(transmissionPanel, BoxLayout.X_AXIS));

			JLabel transmission = new JLabel("Transmission");
			AppResources.changeFont(transmission, Font.PLAIN, 18);

			String[] transmissions = GetListQuery.getTransmissions();
			transmissionCombo = new JComboBox<>(transmissions);
			transmissionCombo.setSelectedItem(currentTransmission);

			transmissionPanel.add(transmission);
			transmissionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			transmissionPanel.add(transmissionCombo);

			container.add(Box.createRigidArea(new Dimension(0, 10)));
			container.add(transmissionPanel);

			modify.add(container);

			String[] options = { "Update", "Cancel" };
			String selected = "Cancel";
			int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), modify, "Modify wheel drive and transmission",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {

				// no checks are needed, it is from our combobox

				String updatedDrive = (String) driveCombo.getSelectedItem();
				String updatedTransmission = (String) transmissionCombo.getSelectedItem();

				UpdateEngine(currentCapacity, currentFuel, currentHorsepower, updatedDrive, currentEuro,
						updatedTransmission);

			}

		}

	}

	// Support method to update the engine (maybe adding a new one)
	public void UpdateEngine(int capacity, String fuel, int horses, String wheel_drive, int euro, String transmission) {

		int engineKey = 0;

		// Check if this engine already exists: we have the key

		String checkEngine = "SELECT * FROM engine WHERE capacity = ? and UPPER(fuel) = UPPER(?) and horsepower = ? and UPPER(wheel_drive) = UPPER(?) and euro = ? and UPPER(transmission) = UPPER(?) ";
		try {

			PreparedStatement stat = conn.prepareStatement(checkEngine);
			stat.setInt(1, capacity);
			stat.setString(2, fuel);
			stat.setInt(3, horses);
			stat.setString(4, wheel_drive);
			stat.setInt(5, euro);
			stat.setString(6, transmission);

			ResultSet rs = stat.executeQuery();

			if (rs.next())
				engineKey = rs.getInt("engine_id");
			else {
				// Otherwise add it: we have the key (in the end, we hope)
				String addEngineString = "INSERT INTO engine (capacity, fuel, horsepower, wheel_drive, euro, transmission) VALUES (?,?,?,?,?,?)";

				PreparedStatement addEngine = conn.prepareStatement(addEngineString, Statement.RETURN_GENERATED_KEYS);
				addEngine.setInt(1, capacity);
				addEngine.setString(2, fuel);
				addEngine.setInt(3, horses);
				addEngine.setString(4, wheel_drive);
				addEngine.setInt(5, euro);
				addEngine.setString(6, transmission);

				addEngine.executeUpdate();

				ResultSet rs2 = addEngine.getGeneratedKeys();

				if (rs2.next())
					engineKey = rs2.getInt(1);

				// Close all statements and results

				addEngine.close();
				rs2.close();
			}
			stat.close();
			rs.close();
			// Now we have the key of the engine, update the car table accordingly
			String carUpdate;

			if (isNewCar)
				carUpdate = "UPDATE new_car SET engine = " + engineKey + " WHERE car_id=" + carId;
			else
				carUpdate = "UPDATE used_car SET engine = " + engineKey + " WHERE immatriculation='" + carId + "'";

			Statement st2 = conn.createStatement();
			st2.executeUpdate(carUpdate);

			st2.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar, ""));

	}

	private class modifyLengthHeightWidth implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// Prepare JPanel for the option pane
			JPanel modify = new JPanel();

			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

			// Length row
			JPanel lengthPanel = new JPanel();
			lengthPanel.setLayout(new BoxLayout(lengthPanel, BoxLayout.X_AXIS));

			JLabel length = new JLabel("Length (cm)");
			AppResources.changeFont(length, Font.PLAIN, 18);

			newLength = new JTextField(4);
			newLength.setText("" + currentDimensions[0]);

			lengthPanel.add(length);
			lengthPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			lengthPanel.add(newLength);

			container.add(lengthPanel);

			// Height row
			JPanel heightPanel = new JPanel();
			heightPanel.setLayout(new BoxLayout(heightPanel, BoxLayout.X_AXIS));

			JLabel height = new JLabel("Height (cm)");
			AppResources.changeFont(height, Font.PLAIN, 18);

			newHeight = new JTextField(4);
			newHeight.setText("" + currentDimensions[1]);

			heightPanel.add(height);
			heightPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			heightPanel.add(newHeight);

			container.add(Box.createRigidArea(new Dimension(0, 10)));
			container.add(heightPanel);

			// Width row
			JPanel widthtPanel = new JPanel();
			widthtPanel.setLayout(new BoxLayout(widthtPanel, BoxLayout.X_AXIS));

			JLabel width = new JLabel("Width (cm)");
			AppResources.changeFont(width, Font.PLAIN, 18);

			newWidth = new JTextField(4);
			newWidth.setText("" + currentDimensions[2]);

			widthtPanel.add(width);
			widthtPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			widthtPanel.add(newWidth);

			container.add(Box.createRigidArea(new Dimension(0, 10)));
			container.add(widthtPanel);

			modify.add(container);

			String[] options = { "Update", "Cancel" };
			String selected = "Cancel";
			int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), modify, "Modify length, height and width",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {

				// check if the user inserted all integers
				int updatedLenght, updateWidth, updatedHeight;
				try {
					updatedLenght = Integer.parseInt(newLength.getText());
					updateWidth = Integer.parseInt(newWidth.getText());
					updatedHeight = Integer.parseInt(newHeight.getText());
				} catch (NumberFormatException n) {
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Lenght, height and width values must be integers",
							"CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}

				// okei, if we arrived here no problems...
				updateDimension(updatedHeight, updatedLenght, currentDimensions[4], updateWidth, currentDimensions[3]);
			}

		}

	}

	private class modifyWeightTrunk implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// Prepare JPanel for the option pane
			JPanel modify = new JPanel();

			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

			// Weight row
			JPanel weightPanel = new JPanel();
			weightPanel.setLayout(new BoxLayout(weightPanel, BoxLayout.X_AXIS));

			JLabel weight = new JLabel("Weight (kg)");
			AppResources.changeFont(weight, Font.PLAIN, 18);

			newWeight = new JTextField(4);
			newWeight.setText("" + currentDimensions[4]);

			weightPanel.add(weight);
			weightPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			weightPanel.add(newWeight);

			container.add(weightPanel);

			// Trunk capacity row
			JPanel trunkPanel = new JPanel();
			trunkPanel.setLayout(new BoxLayout(trunkPanel, BoxLayout.X_AXIS));

			JLabel trunk = new JLabel("Trunk capacity (liters)");
			AppResources.changeFont(trunk, Font.PLAIN, 18);

			newTrunk = new JTextField(4);
			newTrunk.setText("" + currentDimensions[3]);

			trunkPanel.add(trunk);
			trunkPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			trunkPanel.add(newTrunk);

			container.add(trunkPanel);

			modify.add(container);

			String[] options = { "Update", "Cancel" };
			String selected = "Cancel";
			int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), modify, "Modify length, height and width",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {
				int updatedWeight, updatedTrunk;
				try {
					updatedWeight = Integer.parseInt(newWeight.getText());
					updatedTrunk = Integer.parseInt(newTrunk.getText());
				} catch (NumberFormatException n) {
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Weight and trunk capacity values must be integers",
							"CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}

				// okei, if we arrived here no problems...
				updateDimension(currentDimensions[1], currentDimensions[0], updatedWeight, currentDimensions[2],
						updatedTrunk);

			}
		}

	}

	// support method to update dimensions
	public void updateDimension(int height, int length, int weight, int width, int trunk) { // trunk � simile a drunk?

		int dimensionKey = 0;

		String checkDimension = "SELECT * FROM dimension WHERE car_heigth = ? and car_length = ? and car_weight = ? and car_width= ? and trunk_capacity= ?";

		try {
			PreparedStatement st = conn.prepareStatement(checkDimension);
			st.setInt(1, height);
			st.setInt(2, length);
			st.setInt(3, weight);
			st.setInt(4, width);
			st.setInt(5, trunk);

			ResultSet rs = st.executeQuery();

			if (rs.next())
				dimensionKey = rs.getInt("dimension_id");
			else {
				// Add new dimension

				String addDim = "INSERT INTO dimension (car_heigth, car_length, car_weight, car_width, trunk_capacity) VALUES (?,?,?,?,?)";
				PreparedStatement st2 = conn.prepareStatement(addDim, Statement.RETURN_GENERATED_KEYS);
				st2.setInt(1, height);
				st2.setInt(2, length);
				st2.setInt(3, weight);
				st2.setInt(4, width);
				st2.setInt(5, trunk);

				st2.executeUpdate();
				ResultSet rs2 = st2.getGeneratedKeys();
				if (rs2.next())
					dimensionKey = rs2.getInt(1);

				st2.close();
				rs2.close();
			}
			st.close();
			rs.close();

			// We have the key so update
			String sql = "";
			if (isNewCar)
				sql = "UPDATE new_car SET dimension = " + dimensionKey + " WHERE car_id = " + carId;
			else
				sql = "UPDATE used_car SET dimension = " + dimensionKey + " WHERE immatriculation = '" + carId + "'";

			Statement st3 = conn.createStatement();
			st3.executeUpdate(sql);

			st3.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar, ""));
	}

	private class modifyTireDimensions implements ActionListener {
		// Tire integers: width, aspet_ratio, diameter
		// Tire strings: service type, construction, tire type

		@Override
		public void actionPerformed(ActionEvent arg0) {

			// Prepare JPanel for the option pane
			JPanel modify = new JPanel();

			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

			// Width row
			JPanel widthPanel = new JPanel();
			widthPanel.setLayout(new BoxLayout(widthPanel, BoxLayout.X_AXIS));

			JLabel width = new JLabel("Tire width (mm)");
			AppResources.changeFont(width, Font.PLAIN, 18);

			newTireWidth = new JTextField(3);
			newTireWidth.setText("" + tireIntegersData[0]);

			widthPanel.add(width);
			widthPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			widthPanel.add(newTireWidth);

			container.add(widthPanel);

			// Aspet Ratio
			JPanel ratioPanel = new JPanel();
			ratioPanel.setLayout(new BoxLayout(ratioPanel, BoxLayout.X_AXIS));

			JLabel ratio = new JLabel("Aspet Ratio (mm)");
			AppResources.changeFont(ratio, Font.PLAIN, 18);

			newAspetRatio = new JTextField(3);
			newAspetRatio.setText("" + tireIntegersData[1]);

			ratioPanel.add(ratio);
			ratioPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			ratioPanel.add(newAspetRatio);

			container.add(Box.createRigidArea(new Dimension(0, 10)));
			container.add(ratioPanel);

			// Construction

			JPanel constructionPanel = new JPanel();
			constructionPanel.setLayout(new BoxLayout(constructionPanel, BoxLayout.X_AXIS));

			JLabel construction = new JLabel("Construction");
			AppResources.changeFont(construction, Font.PLAIN, 18);

			String[] constructionTypes = { "R: Radial", "D: Diagonal", "B: Bias belt" };
			constructionCombo = new JComboBox<>(constructionTypes);

			if (tireStringsData[1].equals("R"))
				constructionCombo.setSelectedIndex(0);
			else if (tireStringsData[1].equals("D"))
				constructionCombo.setSelectedIndex(1);
			else
				constructionCombo.setSelectedIndex(2);

			constructionPanel.add(construction);
			constructionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			constructionPanel.add(constructionCombo);

			container.add(Box.createRigidArea(new Dimension(0, 10)));
			container.add(constructionPanel);

			// Diameter
			JPanel diameterPanel = new JPanel();
			diameterPanel.setLayout(new BoxLayout(diameterPanel, BoxLayout.X_AXIS));

			JLabel diameter = new JLabel("Diameter (mm)");
			AppResources.changeFont(diameter, Font.PLAIN, 18);

			newDiameter = new JTextField(3);
			newDiameter.setText("" + tireIntegersData[2]);

			diameterPanel.add(diameter);
			diameterPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			diameterPanel.add(newDiameter);

			container.add(Box.createRigidArea(new Dimension(0, 10)));
			container.add(diameterPanel);

			modify.add(container);

			String[] options = { "Update", "Cancel" };
			String selected = "Cancel";
			int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), modify, "Modify tire dimensions",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {

				int UpdatedWidth = 0, UpdatedRatio = 0, updatedDiameter = 0;
				try {
					UpdatedWidth = Integer.parseInt(newTireWidth.getText());
					UpdatedRatio = Integer.parseInt(newAspetRatio.getText());
					updatedDiameter = Integer.parseInt(newDiameter.getText());

				} catch (NumberFormatException n) {
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(),
							"Tire width, aspet ratio and diameter values must be integers", "CarCube",
							JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}

				// We have passed the checks and we have the values, now we get the construction
				String selectedConstruction = ((String) constructionCombo.getSelectedItem()).substring(0, 1);

				pitStop(tireStringsData[0], UpdatedWidth, UpdatedRatio, selectedConstruction, updatedDiameter,
						tireStringsData[2]);

			}

		}

	}

	private class modifyTireType implements ActionListener {
		// Tire integers: width, aspet_ratio, diameter
		// Tire strings: service type, construction, tire type
		@Override
		public void actionPerformed(ActionEvent arg0) {

			// Prepare JPanel for the option pane
			JPanel modify = new JPanel();

			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

			// service type
			JPanel serviceTypePanel = new JPanel();
			serviceTypePanel.setLayout(new BoxLayout(serviceTypePanel, BoxLayout.X_AXIS));

			JLabel serviceType = new JLabel("Service Type");
			AppResources.changeFont(serviceType, Font.PLAIN, 18);

			String[] serviceTypes = { "P: Passenger Car", "LT: Light Truck", "ST: Special Trailer" };
			serviceTypeCombo = new JComboBox<>(serviceTypes);
			if (tireStringsData[0].contains("P"))
				serviceTypeCombo.setSelectedIndex(0);
			else if (tireStringsData[0].contains("L"))
				serviceTypeCombo.setSelectedIndex(1);
			else
				serviceTypeCombo.setSelectedIndex(2);

			serviceTypePanel.add(serviceType);
			serviceTypePanel.add(Box.createRigidArea(new Dimension(10, 0)));
			serviceTypePanel.add(serviceTypeCombo);

			container.add(serviceTypePanel);

			// Tire type
			JPanel tireTypePanel = new JPanel();
			tireTypePanel.setLayout(new BoxLayout(tireTypePanel, BoxLayout.X_AXIS));

			JLabel tireType = new JLabel("Tire Type");
			AppResources.changeFont(tireType, Font.PLAIN, 18);

			String[] tireTypes = { "summer", "winter", "universal" };

			tireTypeCombo = new JComboBox<>(tireTypes);

			if (tireStringsData[2].equals("summer"))
				tireTypeCombo.setSelectedIndex(0);
			else if (tireStringsData[2].equals("winter"))
				tireTypeCombo.setSelectedIndex(1);
			else
				tireTypeCombo.setSelectedIndex(2);

			tireTypePanel.add(tireType);
			tireTypePanel.add(Box.createRigidArea(new Dimension(10, 0)));
			tireTypePanel.add(tireTypeCombo);

			container.add(Box.createRigidArea(new Dimension(0, 10)));
			container.add(tireTypePanel);

			modify.add(container);

			String[] options = { "Update", "Cancel" };
			String selected = "Cancel";
			int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), modify, "Modify tire type",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {

				// No integers value, only comboboxes so we can simply take the selected values
				String updatedService = ((String) serviceTypeCombo.getSelectedItem()).substring(0, 1);
				String updatedType = (String) tireTypeCombo.getSelectedItem();

				pitStop(updatedService, tireIntegersData[0], tireIntegersData[1], tireStringsData[1],
						tireIntegersData[2], updatedType);
			}
		}

	}

	// Support method to update tires
	public void pitStop(String serviceType, int width, int aspet_ratio, String construction, int diameter,
			String tireType) {

		int tireKey = 0;

		// Check if the current tire already exists
		String checkTire = "SELECT * FROM tire WHERE UPPER(service_type) = UPPER(?) and width = ? and aspet_ratio = ? and UPPER(construction) = UPPER(?) and diameter = ? and UPPER(tire_type) = UPPER(?) ";

		try {
			PreparedStatement ps = conn.prepareStatement(checkTire);
			ps.setString(1, serviceType);
			ps.setInt(2, width);
			ps.setInt(3, aspet_ratio);
			ps.setString(4, construction);
			ps.setInt(5, diameter);
			ps.setString(6, tireType);

			ResultSet rs = ps.executeQuery();

			if (rs.next())
				tireKey = rs.getInt("tire_id");
			else {
				// otherwise we add it
				String addTireString = "INSERT INTO tire (service_type, width, aspet_ratio, construction, diameter, tire_type) VALUES(?,?,?,?,?,?)";

				PreparedStatement addTire = conn.prepareStatement(addTireString, Statement.RETURN_GENERATED_KEYS);

				addTire.setString(1, serviceType);
				addTire.setInt(2, width);
				addTire.setInt(3, aspet_ratio);
				addTire.setString(4, construction);
				addTire.setInt(5, diameter);
				addTire.setString(6, tireType);

				addTire.executeUpdate();

				ResultSet rs2 = addTire.getGeneratedKeys();

				if (rs2.next())
					tireKey = rs2.getInt(1);

				addTire.close();
				rs2.close();
			}
			ps.close();
			rs.close();

			// Now we have the key so we can update the table accordingly
			String updateTable = "";

			if (isNewCar)
				updateTable = "UPDATE new_car SET tire = " + tireKey + " WHERE car_id = " + carId;
			else
				updateTable = "UPDATE used_car SET tire = " + tireKey + " WHERE immatriculation = " + carId;

			Statement st = conn.createStatement();
			st.executeUpdate(updateTable);

			st.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar, ""));
	}

	// COLOR MODIFY
	private class modifyColors implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

			ColorsPanel colorPanel = new ColorsPanel();

			ArrayList<ColorCheckBox> boxes = colorPanel.getColorCheckBoxes();

			String colorQuery = "";

			if (isNewCar)
				colorQuery = "SELECT * FROM new_painting WHERE car_id = " + carId;
			else
				colorQuery = "SELECT * FROM used_painting WHERE immatriculation = '" + carId + "'";

			try {
				Statement st = conn.createStatement();

				ResultSet rs = st.executeQuery(colorQuery);

				while (rs.next()) {

					for (ColorCheckBox c : boxes) {

						System.out.println("ColorCode:" + c.getColorCode());

						if (c.getColorCode().equals(rs.getString("color_code"))) {
							c.getCheckBox().setSelected(true);
							System.out.println("Inside");
						}
					}

				}

				st.close();
				rs.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			container.add(colorPanel);

			JScrollPane pane = new JScrollPane(container);
			pane.setPreferredSize(new Dimension(520, 188));

			String[] options = { "Update", "Cancel" };
			String selected = "Cancel";
			int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), pane, "Modify colors",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {

				// Remove all colors...
				String removeColors = "";
				if (isNewCar)
					removeColors = "DELETE FROM new_painting WHERE car_id = " + carId;
				else
					removeColors = "DELETE FROM used_painting WHERE immatriculation = '" + carId + "'";

				try {
					Statement remove = conn.createStatement();
					remove.executeUpdate(removeColors);

					remove.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				// Add colors

				for (ColorCheckBox c : boxes) {

					if (c.getCheckBox().isSelected()) {

						String addColors = "";

						if (isNewCar)
							addColors = "INSERT INTO new_painting (car_id, color_code) VALUES (" + carId + ",'"
									+ c.getColorCode() + "')";
						else
							addColors = "INSERT INTO used_painting (immatriculation, color_code) VALUES ('" + carId
									+ "','" + c.getColorCode() + "')";

						try {
							Statement add = conn.createStatement();
							add.executeUpdate(addColors);

							add.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}

					}

				}

				MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar, ""));
			}
		}

	}

	// OPTIONAL MODIFY (with all support methods)
	private class modifyOptionals implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			allIds = new ArrayList<>(); // ids of all optionals
			optionals = new ArrayList<>(); // Checkbox arraylist
			ArrayList<Integer> ids = new ArrayList<>(); // Ids of selected optionals

			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

			String currentOptional = "";
			if (isNewCar)
				currentOptional = "SELECT * FROM new_equipped WHERE car_id = " + carId;
			else
				currentOptional = "SELECT * FROM used_equipped WHERE immatriculation = '" + carId + "'";

			try {
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(currentOptional);

				while (rs.next())
					ids.add(rs.getInt("optional_id"));

				// scroll panel
				optionals = new ArrayList<JCheckBox>();
				populateOptionalsCheckBoxes(ids);
				JPanel container2 = new JPanel();
				container2.setOpaque(false);
				contentOpt = new JPanel();
				contentOpt.setLayout(new BoxLayout(contentOpt, BoxLayout.Y_AXIS));
				contentOpt.setOpaque(false);
				JScrollPane pane = new JScrollPane(contentOpt);
				pane.setPreferredSize(new Dimension(280, 190));
				container2.add(pane);
				fillOptionalPanel(optionals);
				container.add(container2);

				// Add new optional
				JPanel addOptPanel = new JPanel();
				addOptPanel.setOpaque(false);
				JLabel description1 = new JLabel("Note: optionals of different car makes may have different prices");
				JLabel description2 = new JLabel("Select the correct optional, or add another one");
				description1.setAlignmentX(CENTER_ALIGNMENT);
				description2.setAlignmentX(CENTER_ALIGNMENT);
				AppResources.changeFont(description1, Font.PLAIN, 15);
				AppResources.changeFont(description2, Font.PLAIN, 15);
				JButton addOptButton = AppResources.iconButton("Add another optional", "icons/plus.png");
				AppResources.changeFont(addOptButton, Font.PLAIN, 15);
				addOptButton.addActionListener(new AddOptionalListener());
				addOptPanel.add(addOptButton);

				container.add(Box.createRigidArea(new Dimension(0, 10)));
				container.add(description1);
				container.add(description2);
				container.add(addOptPanel);
				container.add(Box.createRigidArea(new Dimension(0, 10)));

				String[] options = { "Update", "Cancel" };
				String selected = "Cancel";
				int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), container, "Modify optionals",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
						new ImageIcon("icons/minilogo.png"), options, selected);

				if (choice == 0) {

					// Remove all present optionals
					String removeOptString = "";
					if (isNewCar)
						removeOptString = "DELETE FROM new_equipped WHERE car_id = " + carId;
					else
						removeOptString = "DELETE FROM used_equipped WHERE immatriculation = '" + carId + "'";

					Statement removeOpt = conn.createStatement();
					removeOpt.executeUpdate(removeOptString);

					removeOpt.close();

					ArrayList<Integer> selectedOptKeys = new ArrayList<Integer>();
					for (JCheckBox c : optionals) {
						if (c.isSelected()) {
							String optname = c.getText().substring(0, c.getText().indexOf(" -"));
							double optprice = 0;
							String substring = c.getText().substring(c.getText().indexOf("� ") + 2,
									c.getText().lastIndexOf(","));
							Double thousand = 0.00;
							Double hundred = 0.00;
							if (substring.contains(".")) {
								thousand = Double.parseDouble(substring.substring(0, substring.indexOf(".")));
								hundred = 1000 * Double.parseDouble(substring.substring(substring.indexOf(".")));
								optprice = (thousand * 1000) + hundred;
							} else
								optprice = Double.parseDouble(substring);
							int optKey = getOptionalForeignKey(optname, optprice);
							selectedOptKeys.add(optKey);
						}
					}

					// Now we add all optionals
					for (int i = 0; i < selectedOptKeys.size(); i++) {
						int optId = selectedOptKeys.get(i);

						String addOpt = "";

						if (isNewCar)
							addOpt = "INSERT INTO new_equipped (optional_id, car_id) VALUES (" + optId + ", " + carId
									+ ")";
						else
							addOpt = "INSERT INTO used_equipped (optional_id, immatriculation) VALUES (" + optId + ", '"
									+ carId + "')";

						Statement addOptStat = conn.createStatement();
						addOptStat.executeUpdate(addOpt);
						addOptStat.close();
					}

					MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar, ""));
				}

			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		}
	}

	// method for populating the array list of comboBoxes
	public void populateOptionalsCheckBoxes(ArrayList<Integer> currentOpt) {
		optionals.removeAll(optionals);
		String query = "SELECT *  from optional";
		NumberFormat currencyFormat;
		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()) {
				int price = rs.getInt("price");
				currencyFormat = NumberFormat.getCurrencyInstance();
				String priceFormatted = currencyFormat.format(price);
				JCheckBox temp = new JCheckBox(rs.getString("opt_name") + " - " + priceFormatted);
				temp.addItemListener(new OptSelectionListener());
				temp.setOpaque(false);

				for (Integer i : currentOpt)
					if (i == rs.getInt("optional_id"))
						temp.setSelected(true);

				AppResources.changeFont(temp, Font.PLAIN, 20);
				optionals.add(temp);
			}
			stat.close();
			rs.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	// OPTIONAL FOREIGN KEY
	public int getOptionalForeignKey(String name, double price) {
		int optForeignKey = 0;
		String getOptId = "SELECT optional_id FROM optional WHERE UPPER(opt_name) = UPPER(?) and price = ?";
		try {
			PreparedStatement stat = conn.prepareStatement(getOptId);
			stat.setString(1, name);
			stat.setDouble(2, price);
			ResultSet rs = stat.executeQuery();

			if (rs.next()) {
				// We have a result (only one, we cannot have duplicate optionals)
				optForeignKey = rs.getInt("optional_id");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return optForeignKey;
	}

	// listener for adding another optional
	private class AddOptionalListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			AddOptionalPanel addOptionalPanel = new AddOptionalPanel();

			String[] options = { "Confirm", "Cancel" };
			String sel = "Cancel";
			int result = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), addOptionalPanel, "CarCube",
					JOptionPane.INFORMATION_MESSAGE, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, sel);
			if (result == 0) {
				String name = addOptionalPanel.nameField.getText();
				String price = addOptionalPanel.priceField.getText();
				int priceVal = 0;

				if (name.compareTo("") == 0) {
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Please insert a name for the optional",
							"CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}
				if (price.compareTo("") == 0) {
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Please insert a price for the optional",
							"CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}
				try {
					priceVal = Integer.parseInt(price);
				} catch (NumberFormatException n) {

					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Price must be a valid number", "CarCube",
							JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}
				try {
					// control if the optional is already present in the database
					String query = "SELECT * FROM optional WHERE UPPER(opt_name) = UPPER(?) AND price = ?";
					PreparedStatement stat = conn.prepareStatement(query);
					stat.setString(1, name);
					stat.setInt(2, priceVal);
					ResultSet rs = stat.executeQuery();
					// if we have a result, then this optional is already present
					if (rs.next()) {
						JOptionPane.showMessageDialog(MainPanel.getMainPanel(),
								"This specific optional is already present in the Database", "CarCube",
								JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					} else {
						NumberFormat currencyFormat;

						String insertQuery = "INSERT INTO optional (opt_name, price) VALUES (?, ?)";
						PreparedStatement stat2 = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
						stat2.setString(1, name);
						stat2.setInt(2, priceVal);
						stat2.executeUpdate();
						// create selected checkbox
						currencyFormat = NumberFormat.getCurrencyInstance();
						String priceFormatted = currencyFormat.format(priceVal);
						JCheckBox temp = new JCheckBox(name + " - " + priceFormatted);
						temp.setOpaque(false);
						AppResources.changeFont(temp, Font.PLAIN, 20);
						temp.addItemListener(new OptSelectionListener());
						temp.setSelected(true);
						optionals.add(temp);

						fillOptionalPanel(optionals);

						stat2.close();
						JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Optional inserted", "CarCube",
								JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					}

					stat.close();
					rs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}

	}

	// class panel for adding an optional
	private class AddOptionalPanel extends JPanel {
		public JLabel name, price;
		public JTextField nameField, priceField;

		public AddOptionalPanel() {
			JPanel addOptionalPanel = new JPanel();
			addOptionalPanel.setLayout(new BoxLayout(addOptionalPanel, BoxLayout.Y_AXIS));
			name = new JLabel("Optional");
			nameField = new JTextField(10);
			price = new JLabel("Price (€)");
			priceField = new JTextField(10);
			JPanel s1 = new JPanel();
			JPanel s2 = new JPanel();
			s1.add(name);
			s1.add(nameField);
			s2.add(price);
			s2.add(Box.createRigidArea(new Dimension(1, 0)));
			s2.add(priceField);
			addOptionalPanel.add(s1);
			addOptionalPanel.add(s2);

			add(addOptionalPanel);
		}
	}

	// listener for the selection of an optional
	private class OptSelectionListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			JCheckBox selected = (JCheckBox) e.getItem();
			// first of all, control that another optional with same name is not selected
			if (selected.isSelected()) {
				for (JCheckBox c : optionals) {
					if (c != selected && c.isSelected() && c.getText().substring(0, c.getText().indexOf(" -"))
							.compareTo(selected.getText().substring(0, selected.getText().indexOf(" -"))) == 0) {
						JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "This optional is already selected",
								"CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
						selected.setSelected(false);

					}
				}

			}

		}
	}

	// method for filling the panel that contains list of checkboxes
	public void fillOptionalPanel(ArrayList<JCheckBox> optionals) {
		contentOpt.removeAll();
		// optPanel the array with comparator
		Collections.sort(optionals, new Comparator<JCheckBox>() {
			@Override
			public int compare(JCheckBox o1, JCheckBox o2) {
				return o1.getText().compareTo(o2.getText());
			}
		});
		for (JCheckBox c : optionals) {
			JPanel auxiliaryPanel = new JPanel();
			auxiliaryPanel.setLayout(new BorderLayout());
			auxiliaryPanel.add(c, BorderLayout.WEST);
			contentOpt.add(auxiliaryPanel);
		}
		contentOpt.revalidate();
		contentOpt.repaint();

	}

	// SELL CAR
	
	
	private class sellCar implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			JPanel bigSell = new JPanel();
			bigSell.setOpaque(false);
			bigSell.setLayout(new BoxLayout(bigSell, BoxLayout.Y_AXIS));
			
			JLabel spiegation = new JLabel ("Select a customer or add a new one");
			AppResources.changeFont(spiegation, Font.PLAIN, 20);
			spiegation.setAlignmentX(CENTER_ALIGNMENT);
			
			JPanel customerPanel = new JPanel();
			customerPanel.setLayout(new BoxLayout(customerPanel, BoxLayout.X_AXIS));
			
			JPanel support = new JPanel();
			
			JComboBox<String> customers = new JComboBox<>(getStakeholderQuery("customer"));
			if(!selCustomer.equals(""))
				customers.setSelectedItem(selCustomer);
			
			JButton newCustomer = AppResources.iconButton("", "icons/user.png");
			newCustomer.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					MainPanel.getMainPanel().swapPanel(new addCustomerPanel(2,carId, isNewCar));
					Window w = SwingUtilities.getWindowAncestor(newCustomer);

				    if (w != null) {
				      w.setVisible(false);
				    }

				}
				
			});
			
			
			support.add(customers);
			support.add(Box.createRigidArea(new Dimension(10,0)));
			support.add(newCustomer);
			
			customerPanel.add(support);
			bigSell.add(spiegation);
			bigSell.add(customerPanel);
			
			String[] options = { "Sell", "Cancel" };
			String selected = "Cancel";
			int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), bigSell, "Sell car",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {
				
				JPanel confirmPanel = new JPanel();
				confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.Y_AXIS));
				JLabel confirmLabel = new JLabel("Please confirm the sale");
				AppResources.changeFont(confirmLabel, Font.BOLD, 20);
				JLabel confirm2Label = new JLabel("Sale details:");
				AppResources.changeFont(confirm2Label, Font.PLAIN, 18);
				confirmLabel.setAlignmentX(CENTER_ALIGNMENT);
				confirm2Label.setAlignmentX(CENTER_ALIGNMENT);
				// first row
				JPanel support1 = new JPanel();
				support1.setLayout(new BorderLayout());
				JPanel firstRow = new JPanel();
				String selectedMake = currentMake;
				String selectedModel = currentModel;
				
				
				JLabel image1 = new JLabel (new ImageIcon("icons/sport-car.png"));
				JLabel info1 = new JLabel(selectedMake + " "+ selectedModel);
				info1.setFont(new Font("Helvetica", Font.PLAIN, 16));
				firstRow.add(image1);
				firstRow.add(info1);
				support1.add(firstRow, BorderLayout.WEST);
				// second row
				JPanel support2 = new JPanel();
				support2.setLayout(new BorderLayout());
				JPanel secondRow = new JPanel();
				JLabel image2 = new JLabel (new ImageIcon("icons/database.png"));
				JLabel info2 = new JLabel("Sold to customer: " + customers.getSelectedItem());
				info2.setFont(new Font("Helvetica", Font.PLAIN, 16));
				secondRow.add(image2);
				secondRow.add(info2);
				support2.add(secondRow, BorderLayout.WEST);
				
				// third row
				NumberFormat currencyFormat;
				currencyFormat = NumberFormat.getCurrencyInstance();
				JPanel support3 = new JPanel();
				support3.setLayout(new BorderLayout());
				JPanel thirdRow = new JPanel();
				JLabel image3 = new JLabel(new ImageIcon("icons/price-tag.png"));
				JLabel info3 = new JLabel("Total cost: " + currencyFormat.format(finalPrice));
				info3.setFont(new Font("Helvetica", Font.PLAIN, 16));
				thirdRow.add(image3);
				thirdRow.add(info3);
				support3.add(thirdRow, BorderLayout.WEST);
				
				confirmPanel.add(confirmLabel);
				confirmPanel.add(confirm2Label);
				confirmPanel.add(Box.createRigidArea(new Dimension(0, 20)));
				confirmPanel.add(support1);
				confirmPanel.add(support2);
				confirmPanel.add(support3);
				
				String[] options2 = {"Confirm", "Cancel"};
				String sel = "Cancel";
				
				int finalChoice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), confirmPanel, "CarCube",
						JOptionPane.INFORMATION_MESSAGE, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
						options2, sel);
				
				if(finalChoice == 0) {
					
					// Add tuple to sell table
					String customer = (String) customers.getSelectedItem();
					String soldToKey = customer.substring(customer.lastIndexOf("(")+1, customer.lastIndexOf(")"));
					
					Date today = Calendar.getInstance().getTime();
				
					java.sql.Date insertDate = new java.sql.Date(today.getYear(), today.getMonth(), today.getDay());
					
					PreparedStatement addT = null;
					try {
					if(isNewCar) {
						String addTuple = "INSERT INTO new_sell (car_id, tax_code, sell_date, sell_price) VALUES(?,?,?,?)";
						
						addT = conn.prepareStatement(addTuple);
						int id = Integer.parseInt(carId);
						addT.setInt(1, id);
						addT.setString(2, soldToKey);
						addT.setDate(3, insertDate);
						addT.setDouble(4, finalPrice);
					}
					else {
						String addTuple = "INSERT INTO used_sell (immatriculation, tax_code, sell_date, sell_price) VALUES(?,?,?,?)";
						addT = conn.prepareStatement(addTuple);
						addT.setString(1, carId);
						addT.setString(2, soldToKey);
						addT.setDate(3, insertDate);
						addT.setDouble(4, finalPrice);
					}
						
					addT.executeUpdate();
					addT.close();
					
					// Change sold attribute in car table
					String changeAttr = "";
					if(isNewCar)
						changeAttr = "UPDATE new_car SET sold = 1 WHERE car_id = " + carId;
					else
						changeAttr = "UPDATE used_car SET sold = 1 WHERE immatriculation = '" + carId + "'";
					
					Statement stat = conn.createStatement();
					stat.executeUpdate(changeAttr);
					stat.close();
					
					JPanel gifPanel = new JPanel();
					gifPanel.setLayout(new BoxLayout(gifPanel, BoxLayout.Y_AXIS));
					JLabel myGIF = new JLabel(new ImageIcon("icons/sold.gif"));
					JLabel bought = new JLabel("Car sold!");
					myGIF.setAlignmentX(CENTER_ALIGNMENT);
					bought.setAlignmentX(CENTER_ALIGNMENT);
					gifPanel.add(myGIF);
					gifPanel.add(bought);
					AppResources.changeFont(bought, Font.BOLD, 25);
					
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), gifPanel, "CarCube", JOptionPane.PLAIN_MESSAGE);
					MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar, ""));
					} catch(SQLException e) {
						e.printStackTrace();
					}
					
					
				}
				
			}
		}
		
	}
	

	//  method for retrieving the list of suppliers and customers
	public String [] getStakeholderQuery (String table) {
		ArrayList <String> resultList = null;
		try {
			Statement st = conn.createStatement();
			String sql = "SELECT * FROM " + table;
			resultList = new ArrayList <String>();
			ResultSet rs = st.executeQuery(sql);
	
			while(rs.next()) {
				if (table.compareTo("customer") == 0)
					resultList.add(rs.getString("c_name") + " " + rs.getString("c_surname") +"  (" + rs.getString ("tax_code") + ")");
				else 
					resultList.add(rs.getString("name")  +"  (" + rs.getString ("vat") + ")");
			}
			
			st.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String [] result = resultList.toArray(new String[resultList.size()]);
		Arrays.sort(result);
		return result;
	}
	
	// DELETE CAR

	private class deleteCar implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			int choice = JOptionPane.showConfirmDialog(MainPanel.getMainPanel(), "Do you really want to delete this car?",
					"Delete car", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
					new ImageIcon("icons/minilogo.png"));
			if (choice == JOptionPane.YES_OPTION) {

				// We first remove optional
				try {
					String removeOptString = "";
					if (isNewCar)
						removeOptString = "DELETE FROM new_equipped WHERE car_id = " + carId;
					else
						removeOptString = "DELETE FROM used_equipped WHERE immatriculation = '" + carId + "'";

					Statement removeOpt = conn.createStatement();
					removeOpt.executeUpdate(removeOptString);

					removeOpt.close();
					
					//Then we remove the colors
					String removeColString = "";
					if (isNewCar)
						removeColString = "DELETE FROM new_painting WHERE car_id = " + carId;
					else
						removeColString = "DELETE FROM used_painting WHERE immatriculation = '" + carId + "'";
					
					Statement removecol = conn.createStatement();
					removecol.executeUpdate(removeColString);

					removecol.close();
					
					//and finally we remove the car
					String removeCarString = "";
					if (isNewCar)
						removeCarString = "DELETE FROM new_car WHERE car_id = " + carId;
					else
						removeCarString = "DELETE FROM used_car WHERE immatriculation = '" + carId + "'";
					
					Statement removeCar = conn.createStatement();
					removeCar.executeUpdate(removeCarString);

					removeCar.close();
					
					
					MainPanel.getMainPanel().swapPanel(new SearchCarPanel() );
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
}