package atunibz.dcube.DBProject.GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import atunibz.dcube.DBProject.configuration.AppResources;
import atunibz.dcube.DBProject.configuration.GetListQuery;

public class ViewCarPanel extends JPanel {

	Connection conn;
	JPanel viewCarPanel;
	JPanel titlePanel, row, photoPanel;
	JComboBox<String> makeCombo, modelCombo, typesCombo, fuelCombo, transmissionCombo, driveCombo;
	JTextField newMake, newModel, newKm, newType, doors, seats, newYear, newFuel, newEuro, newCapacity, newHorses;
	String currentMake, currentModel, currentCarType, currentFuel, currentDrive, currentTransmission;
	int currentKm, currentDoors, currentSeats, finalPrice, currentYear, currentEuro, currentCapacity, currentHorsepower;
	// Order of the returned array: length, height, width, trunk capacity, weight
	int[] currentDimensions, tireIntegersData;
	String[] tireStringsData;
	ArrayList<String> currentCarColors, currentOptionals;
	ArrayList<Integer> currentOptionalsIds;
	JButton makeModelButton, modifyKm, modifyTypeEtc, modifyPrice, modifyYear, modifyEuroFuel, modifyCapHorse,
			modifyDriveTranmission, modifyLengthWidthHeigth, modifyWeightTrunk, modifyTireDimensions, modifyTireTypes,
			modifyColors, modifyOptional;
	JButton back, sell, delete, modify;

	// global importan variables
	boolean isNewCar;
	String carId;

	public ViewCarPanel(String id, boolean newCar) {

		// make global for listeners and support methods
		isNewCar = newCar;
		carId = id;
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

		// Inner panel in order to have it automatically centered
		JPanel photo = new JPanel();
		photo.setLayout(new BoxLayout(photo, BoxLayout.X_AXIS));
		ImageIcon photoIcon = getPhotoFromId(id, newCar);
		JLabel photoLabel = new JLabel();
		photoLabel.setIcon(photoIcon);

		photo.add(photoLabel);
		photoPanel.add(photo, BorderLayout.NORTH);

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
		AppResources.changeFont(generalData, Font.BOLD, 22);

		descPanel.add(generalData);
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

		// Engine data label
		JPanel engineData = new JPanel();
		engineData.setOpaque(false);
		// engineData.setLayout(new BorderLayout());

		JLabel techLabel = new JLabel("Engine Data");
		AppResources.changeFont(techLabel, Font.BOLD, 22);
		engineData.add(techLabel);
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
		modifyDriveTranmission.addActionListener(new modifyTransmissionDrive() );
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
		AppResources.changeFont(dimensionLabel, Font.BOLD, 22);
		dimensionData.add(dimensionLabel);
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
				"Weight: " + currentDimensions[3] + " kg - Trunk capacity: " + currentDimensions[4] + " liters");
		AppResources.changeFont(dim2, Font.PLAIN, 18);

		modifyWeightTrunk = new JButton();
		modifyWeightTrunk.setIcon(new ImageIcon("icons/contacts/modify.png"));
		modifyWeightTrunk.setVisible(true);

		dimension2.add(dim2);
		dimension2support.add(dimension2, BorderLayout.WEST);
		dimension2support.add(modifyWeightTrunk, BorderLayout.EAST);
		info.add(dimension2support);

		// TIRE (whyyyyyyyy???)

		JPanel tireData = new JPanel();
		tireData.setOpaque(false);

		JLabel tireLabel = new JLabel("Tires Data");
		AppResources.changeFont(tireLabel, Font.BOLD, 22);
		tireData.add(tireLabel);
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
		modifyTireTypes.setVisible(true);

		supportTire2.add(tire2, BorderLayout.WEST);
		supportTire2.add(modifyTireTypes, BorderLayout.EAST);
		info.add(supportTire2);

		// Color label
		JPanel colorData = new JPanel();
		colorData.setOpaque(false);

		JLabel colorLabel = new JLabel("Colors");
		AppResources.changeFont(colorLabel, Font.BOLD, 22);
		colorData.add(colorLabel);
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
		modifyColors.setVisible(true);

		colors.add(colorList, BorderLayout.WEST);
		colors.add(modifyColors, BorderLayout.EAST);
		info.add(colors);

		// Optional label
		JPanel optionalData = new JPanel();
		optionalData.setOpaque(false);

		JLabel optionalLabel = new JLabel("Optionals");
		AppResources.changeFont(optionalLabel, Font.BOLD, 22);
		optionalData.add(optionalLabel);
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
		modifyOptional.setVisible(true);
		modOptPanel.add(modifyOptional);

		optionals.add(optionalList, BorderLayout.WEST);
		optionals.add(modOptPanel, BorderLayout.EAST);
		info.add(optionals);

		// ADD ALL
		row.add(Box.createRigidArea(new Dimension(10, 0)));
		row.add(info);
		// End info
		viewCarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		// add the row
		viewCarPanel.add(row);

		// Panel for buttons controls
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		// back
		back = AppResources.iconButton("Go back     ", "icons/back.png");
		back.addActionListener(new BackListener());
		buttonPanel.add(back);
		buttonPanel.add((Box.createRigidArea(new Dimension(50, 0))));

		// Modify
		modify = AppResources.iconButton("Modify     ", "icons/contacts/modify.png");
		modify.addActionListener(new ModifyListener());
		buttonPanel.add(modify);
		buttonPanel.add((Box.createRigidArea(new Dimension(50, 0))));

		// Sell
		sell = AppResources.iconButton("Sell     ", "icons/sale.png");
		// modify.addActionListener(new AddListener(fromCarPanels));
		buttonPanel.add(sell);
		buttonPanel.add((Box.createRigidArea(new Dimension(50, 0))));

		// Delete
		delete = AppResources.iconButton("Delete     ", "icons/sale.png");
		// modify.addActionListener(new AddListener(fromCarPanels));
		buttonPanel.add(delete);

		buttonPanel.setOpaque(false);

		// end
		viewCarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		viewCarPanel.add(buttonPanel);
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
			int choice = JOptionPane.showOptionDialog(viewCarPanel, modify, "Modify make and model",
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
					JOptionPane.showMessageDialog(viewCarPanel, "Make and model cannot be longer than 20 characters",
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

				MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar));

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
			int choice = JOptionPane.showOptionDialog(viewCarPanel, modify, "Modify km",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {
				int newValue = 0;
				try {
					newValue = Integer.parseInt(newKm.getText());
				} catch (NumberFormatException n) {
					JOptionPane.showMessageDialog(viewCarPanel, "The value entered is not an integer", "CarCube",
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
				MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar));
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
			int choice = JOptionPane.showOptionDialog(viewCarPanel, modify, "Modify km",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {
				int updateDoors, updateSeats;
				String updateType;

				try {
					updateDoors = Integer.parseInt(doors.getText());
					updateSeats = Integer.parseInt(seats.getText());
				} catch (NumberFormatException n) {
					JOptionPane.showMessageDialog(viewCarPanel, "Doors and seats values must be integers", "CarCube",
							JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}

				// now we take the right car type
				if (!newType.getText().equals(""))
					updateType = newType.getText();
				else
					updateType = (String) typesCombo.getSelectedItem();

				if (updateType.length() > 30) {
					JOptionPane.showMessageDialog(viewCarPanel, "Car type cannot be longer than 30 characters",
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
				MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar));
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
			int choice = JOptionPane.showOptionDialog(viewCarPanel, modify, "Modify km",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {
				int yearUpdated;

				// Check if value inserted is an int
				try {
					yearUpdated = Integer.parseInt(newYear.getText());
				} catch (NumberFormatException n) {
					JOptionPane.showMessageDialog(viewCarPanel, "Year value must be an integer", "CarCube",
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
				MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar));
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
			int choice = JOptionPane.showOptionDialog(viewCarPanel, modify, "Modify fuel and euro",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {

				int euroUpdated;
				String fuelUpdated;

				// first, check if euro is integer
				try {
					euroUpdated = Integer.parseInt(newEuro.getText());
				} catch (NumberFormatException n) {
					JOptionPane.showMessageDialog(viewCarPanel, "Euro value must be an integer", "CarCube",
							JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}

				// get correctly fuel
				if (newFuel.getText().equals(""))
					fuelUpdated = (String) fuelCombo.getSelectedItem();
				else {
					if (newFuel.getText().length() > 20) {
						JOptionPane.showMessageDialog(viewCarPanel, "Fuel cannot be longer than 20 characters",
								"CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
						return;
					}

					fuelUpdated = newFuel.getText();
				}

				// Prepare and execute query
				String sql = "";

				if (isNewCar)
					sql = "UPDATE engine SET fuel = '" + fuelUpdated + "', euro = " + euroUpdated
							+ " FROM new_car WHERE new_car.engine = engine.engine_id AND new_car.car_id = " + carId;
				else
					sql = "UPDATE engine SET fuel = '" + fuelUpdated + "', euro = " + euroUpdated
							+ " FROM used_car WHERE used_car.engine = engine.engine_id AND used_car.immatriculation = '"
							+ carId + "'";

				try {
					Statement st = conn.createStatement();
					st.executeUpdate(sql);

					st.close();

				} catch (SQLException e1) {

					e1.printStackTrace();
				}
				MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar));
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
			int choice = JOptionPane.showOptionDialog(viewCarPanel, modify, "Modify capacity and horsepower",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {
				int updatedCap, updatedHorses;

				// First check if they are integers
				try {
					updatedCap = Integer.parseInt(newCapacity.getText());
					updatedHorses = Integer.parseInt(newHorses.getText());
				} catch (NumberFormatException n) {
					JOptionPane.showMessageDialog(viewCarPanel, "Capacity and horsepower values must be integers",
							"CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
					return;
				}

				String sql;
				// prepare and execute query
				if (isNewCar)
					sql = "UPDATE engine SET capacity = " + updatedCap + ", horsepower = " + updatedHorses
							+ " FROM new_car WHERE new_car.engine = engine.engine_id AND new_car.car_id = " + carId;
				else
					sql = "UPDATE engine SET capacity = " + updatedCap + ", horsepower = " + updatedHorses
							+ " FROM used_car WHERE used_car.engine = engine.engine_id AND used_car.immatriculation = '"
							+ carId + "'";

				try {
					Statement st = conn.createStatement();
					st.executeUpdate(sql);

					st.close();

				} catch (SQLException e1) {

					e1.printStackTrace();
				}
				MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar));
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
			//Transmission row
			
			JPanel transmissionPanel = new JPanel();
			transmissionPanel.setLayout(new BoxLayout(transmissionPanel, BoxLayout.X_AXIS));
			
			JLabel transmission = new JLabel("Transmission");
			AppResources.changeFont(transmission, Font.PLAIN, 18);
			
			String[] transmissions = GetListQuery.getTransmissions();
			transmissionCombo = new JComboBox<>(transmissions);
			transmissionCombo.setSelectedItem(currentDrive);
			
			transmissionPanel.add(transmission);
			transmissionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
			transmissionPanel.add(transmissionCombo);
			
			container.add(Box.createRigidArea(new Dimension(0,10)));
			container.add(transmissionPanel);
			
			modify.add(container);
			
			String[] options = { "Update", "Cancel" };
			String selected = "Cancel";
			int choice = JOptionPane.showOptionDialog(viewCarPanel, modify, "Modify wheel drive and transmission",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, selected);

			if (choice == 0) {
				
				//no checks are needed, it is from our combobox
				
				String updatedDrive = (String) driveCombo.getSelectedItem();
				String updatedTransmission = (String) transmissionCombo.getSelectedItem();
				
				
				String sql;
				// prepare and execute query
				if (isNewCar)
					sql = "UPDATE engine SET wheel_drive = '" + updatedDrive + "', transmission = '" + updatedTransmission
							+ "' FROM new_car WHERE new_car.engine = engine.engine_id AND new_car.car_id = " + carId;
				else
					sql = "UPDATE engine SET wheel_drive = '" + updatedDrive + "', transmission = '" + updatedTransmission
							+ "' FROM used_car WHERE used_car.engine = engine.engine_id AND used_car.immatriculation = '"
							+ carId + "'";

				try {
					Statement st = conn.createStatement();
					st.executeUpdate(sql);

					st.close();

				} catch (SQLException e1) {

					e1.printStackTrace();
				}
				MainPanel.getMainPanel().swapPanel(new ViewCarPanel(carId, isNewCar));

			}
			
		}

	}
}