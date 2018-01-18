package atunibz.dcube.DBProject.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import atunibz.dcube.DBProject.GUI.ColorsPanel.ColorCheckBox;
import atunibz.dcube.DBProject.configuration.AppResources;
import atunibz.dcube.DBProject.configuration.GetListQuery;

// Panel for adding a new car in the database
public class AddCarPanel extends JPanel{
	private AddOptionalPanel addOptionalPanel;
	private JPanel addCarPanel, titlePanel, sellerPanel, fromCustomerPanel, fromSupplierPanel, onlyForUsedPanel, contentOpt;
	private Connection conn;
	private JRadioButton newCar, usedCar;
	private JComboBox <String> supplierChoice, customerChoice, make, model, type, year, fuel, trans, wDrive, serviceType, construction, tireType;
	private ButtonGroup group;
	private String [] supplierList, customerList, makesList, modelsList, typesList, fuelList, transList, wDriveList;
	private JButton newSupplier, newCustomer, uploadImage, buyCar, back;
	private JLabel iconLabel, uploadLabel;
	private JTextField makeField, modelField, typeField, doorsField, seatsField, priceField, fuelField;
	private JTextField capacityField, hPowerField, euroField, lengthField, heightField, widthField, trunkField, weightField;
	private JTextField licenseField, mileageField, aspetRatioField, tireWField, diameterField;
	private ArrayList<JCheckBox> optionals;
	private double totalPrice = 0;
	private int quantity = 1;
	private ArrayList<ColorCheckBox> colors;
	private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
	private File fileInserted;
	private ArrayList<String> colorKeys;
	private JDatePickerImpl datePicker;

	
	// Constructor
	public AddCarPanel (boolean supplier, String primaryKey) {
		
		// open the connection with the database
		conn = DatabaseConnection.getDBConnection().getConnection();
		// initialise add optional panel
		addOptionalPanel = new AddOptionalPanel();
		
		// create panel which will be the general container of all others GUI objects
		addCarPanel = new JPanel();
		addCarPanel.setLayout(new BoxLayout(addCarPanel, BoxLayout.Y_AXIS));
		addCarPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		
		// Panel containing the beautiful logo
		titlePanel = AppResources.carCubePanel();
		addCarPanel.add(titlePanel);
		addCarPanel.setOpaque(false);
		addCarPanel.add((Box.createRigidArea(new Dimension(0, 10))));
		
		// panel containing label that inform user to add a car
		JPanel labelPanel = new JPanel();
		labelPanel.setOpaque(false);
		JLabel info = new JLabel ("Add a car! New or used?");
		AppResources.changeFont(info, Font.BOLD, 25);
		labelPanel.add(info);
		addCarPanel.add(labelPanel);
		
		// panel that contains the radio buttons with associated group (only one can be selected)
		JPanel radioBoxPanel = new JPanel();
		radioBoxPanel.setOpaque(false);
		newCar = new JRadioButton("New");
		usedCar = new JRadioButton("Used");
		if (supplier) 
			newCar.setSelected(true);
		else
			usedCar.setSelected(true);
		radioBoxPanel.add(newCar);
		radioBoxPanel.add(Box.createRigidArea(new Dimension (10, 0)));
		radioBoxPanel.add(usedCar);
		group = new ButtonGroup();
		group.add(newCar);
		group.add(usedCar);
		addCarPanel.add(radioBoxPanel);
		
		// panel that contains the two different possible sellers (customer or supplier)
		sellerPanel = new JPanel();
		sellerPanel.setOpaque(false);
		// supplier
		fromSupplierPanel = new JPanel();
		fromSupplierPanel.setOpaque(false);
		fromSupplierPanel.setLayout(new BoxLayout(fromSupplierPanel, BoxLayout.Y_AXIS));
		JLabel fromSupLabel = new JLabel ("From Supplier");
		fromSupLabel.setAlignmentX(CENTER_ALIGNMENT);
		AppResources.changeFont(fromSupLabel, Font.BOLD, 20);
		supplierList = getStakeholderQuery ("supplier");
		supplierChoice = new JComboBox <String>(supplierList);
		supplierChoice.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		if (primaryKey.compareTo("") == 0)
			supplierChoice.setSelectedIndex(0);
		else 
			supplierChoice.setSelectedIndex(getIndexOfKey(true, primaryKey));
		
		newSupplier = AppResources.iconButton("", "icons/addSup.png");
		JPanel support = new JPanel ();
		support.setOpaque(false);
		support.add(supplierChoice);
		support.add(Box.createRigidArea(new Dimension (5,0)));
		support.add(newSupplier);
		fromSupplierPanel.add(fromSupLabel);
		fromSupplierPanel.add(support);
		
		// customer
		fromCustomerPanel = new JPanel();
		fromCustomerPanel.setOpaque(false);
		fromCustomerPanel.setLayout(new BoxLayout(fromCustomerPanel, BoxLayout.Y_AXIS));
		JLabel fromCusLabel = new JLabel ("From Customer");
		fromCusLabel.setAlignmentX(CENTER_ALIGNMENT);
		AppResources.changeFont(fromCusLabel, Font.BOLD, 20);
		customerList = getStakeholderQuery ("customer");
		customerChoice = new JComboBox <String>(customerList);
		customerChoice.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		if (primaryKey.compareTo("") == 0)
			customerChoice.setSelectedIndex(0);
		else 
			customerChoice.setSelectedIndex(getIndexOfKey(false, primaryKey));
			
		newCustomer = AppResources.iconButton("", "icons/user.png");
		// control in which case we are
		if (supplier) {
			customerChoice.setEnabled(false);
			newCustomer.setEnabled(false);
		}
		else {
			supplierChoice.setEnabled(false);
			newSupplier.setEnabled(false);
		}
		JPanel support2 = new JPanel ();
		support2.setOpaque(false);
		support2.add(customerChoice);
		support2.add(Box.createRigidArea(new Dimension (5,0)));
		support2.add(newCustomer);
		fromCustomerPanel.add(fromCusLabel);
		fromCustomerPanel.add(support2);
		
		// icon Panel
		JPanel iconPanel = new JPanel();
		iconPanel.setOpaque(false);
		iconLabel = new JLabel();
		if (supplier)
			iconLabel.setIcon(new ImageIcon("icons/left.png"));
		else
			iconLabel.setIcon(new ImageIcon("icons/right.png"));
		iconPanel.add(iconLabel);
		
		sellerPanel.add(fromSupplierPanel);
		sellerPanel.add(Box.createRigidArea(new Dimension (115,0)));
		sellerPanel.add(iconPanel);
		sellerPanel.add(Box.createRigidArea(new Dimension (115,0)));
		sellerPanel.add(fromCustomerPanel);
		addCarPanel.add(sellerPanel);
		addCarPanel.add(Box.createRigidArea(new Dimension (0,16)));
		
		// START WITH ALL FIELDS TO FILL IN ORDER TO ADD A CAR (BIG PANEL WITH X LAYOUT)
		JPanel bigHorizontalPanel = new JPanel();
		bigHorizontalPanel.setOpaque(false);
		bigHorizontalPanel.setLayout(new BoxLayout(bigHorizontalPanel, BoxLayout.X_AXIS));
		
		// START WITH GENERAL DATA
		JPanel generalDataPanel = new JPanel();
		generalDataPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		generalDataPanel.setOpaque(false);
		generalDataPanel.setLayout(new BoxLayout(generalDataPanel, BoxLayout.Y_AXIS));
		JLabel titleGeneralLabel = new JLabel ("General data");
		titleGeneralLabel.setAlignmentX(CENTER_ALIGNMENT);
		AppResources.changeFont(titleGeneralLabel, Font.BOLD, 30);
		// first row
		JPanel firstRowGeneralPanel = new JPanel ();
		firstRowGeneralPanel.setOpaque(false);
		JLabel makeLabel = new JLabel ("Make");
		AppResources.changeFont(makeLabel, Font.PLAIN, 18);
		makesList = GetListQuery.getMakes(2);
		make = new JComboBox <String> (makesList);
		make.setPrototypeDisplayValue("XXXXXXXXXXXXXXX");
		make.setSelectedIndex(0);
		makeField = new JTextField(10);
		firstRowGeneralPanel.add(makeLabel);
		firstRowGeneralPanel.add(Box.createRigidArea(new Dimension(1,0)));
		firstRowGeneralPanel.add(make);
		firstRowGeneralPanel.add(makeField);
		//second row
		JPanel secondRowGeneralPanel = new JPanel ();
		secondRowGeneralPanel.setOpaque(false);
		JLabel modelLabel = new JLabel ("Model");
		AppResources.changeFont(modelLabel, Font.PLAIN, 18);
		modelsList = GetListQuery.getModels(2, (String) make.getSelectedItem());
		model = new JComboBox <String> (modelsList);
		model.setPrototypeDisplayValue("XXXXXXXXXXXXXXX");
		model.setSelectedIndex(0);
		modelField = new JTextField(10);
		secondRowGeneralPanel.add(modelLabel);
		secondRowGeneralPanel.add(model);
		secondRowGeneralPanel.add(modelField);
		// third row
		JPanel thirdRowGeneralPanel = new JPanel();
		thirdRowGeneralPanel.setOpaque(false);
		JLabel typeLabel = new JLabel("Type");
		AppResources.changeFont(typeLabel, Font.PLAIN, 18);
		typesList = GetListQuery.getCarTypes();
		type = new JComboBox<String>(typesList);
		type.setPrototypeDisplayValue("XXXXXXXXXXXXXXX");
		type.setSelectedIndex(0);
		typeField = new JTextField(10);
		thirdRowGeneralPanel.add(typeLabel);
		thirdRowGeneralPanel.add(Box.createRigidArea(new Dimension(1,0)));
		thirdRowGeneralPanel.add(type);
		thirdRowGeneralPanel.add(typeField);
		// fourth row
		JPanel fourthRowGeneralPanel = new JPanel();
		fourthRowGeneralPanel.setOpaque(false);
		JLabel yearLabel = new JLabel("Year");
		AppResources.changeFont(yearLabel, Font.PLAIN, 18);
		year = new JComboBox<String>();
		for (int i = 0; i <=68; i++) {
			year.addItem(Integer.toString(2018 - i));
		}
		year.setSelectedIndex(0);
		JLabel doorsLabel = new JLabel("Doors");
		AppResources.changeFont(doorsLabel, Font.PLAIN, 18);
		doorsField = new JTextField(1);
		JLabel seatsLabel = new JLabel("Seats");
		AppResources.changeFont(seatsLabel, Font.PLAIN, 18);
		seatsField = new JTextField(1);
		fourthRowGeneralPanel.add(Box.createRigidArea(new Dimension(1,0)));
		fourthRowGeneralPanel.add(yearLabel);
		fourthRowGeneralPanel.add(year);
		fourthRowGeneralPanel.add(Box.createRigidArea(new Dimension(28,0)));
		fourthRowGeneralPanel.add(doorsLabel);
		fourthRowGeneralPanel.add(doorsField);
		fourthRowGeneralPanel.add(Box.createRigidArea(new Dimension(28,0)));
		fourthRowGeneralPanel.add(seatsLabel);
		fourthRowGeneralPanel.add(seatsField);
		// fifth row
		JPanel fifthRowGeneralPanel = new JPanel();
		fifthRowGeneralPanel.setOpaque(false);
		JLabel priceLabel = new JLabel("Select purchase price");
		AppResources.changeFont(priceLabel, Font.PLAIN, 18);
		priceField = new JTextField(10);
		JLabel euroLabel = new JLabel(" €");
		AppResources.changeFont(euroLabel, Font.PLAIN, 18);
		fifthRowGeneralPanel.add(priceLabel);
		fifthRowGeneralPanel.add(Box.createRigidArea(new Dimension(15,0)));
		fifthRowGeneralPanel.add(priceField);
		fifthRowGeneralPanel.add(euroLabel);

		generalDataPanel.add(titleGeneralLabel);
		generalDataPanel.add(firstRowGeneralPanel);
		generalDataPanel.add(secondRowGeneralPanel);
		generalDataPanel.add(thirdRowGeneralPanel);
		generalDataPanel.add(fourthRowGeneralPanel);
		generalDataPanel.add(fifthRowGeneralPanel);
		
		
		// ENGINE DATA
		JPanel enginePanel = new JPanel();
		enginePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		enginePanel.setOpaque(false);
		enginePanel.setLayout(new BoxLayout(enginePanel, BoxLayout.Y_AXIS));
		JLabel titleEngineLabel = new JLabel ("Engine");
		titleEngineLabel.setAlignmentX(CENTER_ALIGNMENT);
		AppResources.changeFont(titleEngineLabel, Font.BOLD, 30);
		// first row
		JPanel firstRowEnginePanel = new JPanel ();
		firstRowEnginePanel.setOpaque(false);
		JLabel fuelLabel = new JLabel ("Fuel");
		AppResources.changeFont(fuelLabel, Font.PLAIN, 18);
		fuelList = GetListQuery.getFuels();
		fuel = new JComboBox <String> (fuelList);
		fuel.setPrototypeDisplayValue("XXXXXXXXXXXXXXX");
		fuel.setSelectedIndex(0);
		fuelField = new JTextField(10);
		firstRowEnginePanel.add(fuelLabel);
		firstRowEnginePanel.add(Box.createRigidArea(new Dimension(1,0)));
		firstRowEnginePanel.add(fuel);
		firstRowEnginePanel.add(fuelField);
		// second row
		JPanel secondRowEnginePanel = new JPanel();
		secondRowEnginePanel.setOpaque(false);
		JLabel transmissionLabel = new JLabel("Transmission");
		AppResources.changeFont(transmissionLabel, Font.PLAIN, 18);
		transList = GetListQuery.getTransmissions();
		trans = new JComboBox<String>(transList);
		trans.setPrototypeDisplayValue("XXXXXXXXXXXXXXX");
		trans.setSelectedIndex(0);
		secondRowEnginePanel.add(transmissionLabel);
		secondRowEnginePanel.add(Box.createRigidArea(new Dimension(63,0)));
		secondRowEnginePanel.add(trans);
		// third row
		JPanel thirdRowEnginePanel = new JPanel();
		thirdRowEnginePanel.setOpaque(false);
		JLabel wheelDriveLabel = new JLabel("Wheel Drive");
		AppResources.changeFont(wheelDriveLabel, Font.PLAIN, 18);
		wDriveList = GetListQuery.getWheelDrive();
		wDrive = new JComboBox<String>(wDriveList);
		wDrive.setPrototypeDisplayValue("XXXXXXXXXXXXXXX");
		wDrive.setSelectedIndex(0);
		thirdRowEnginePanel.add(wheelDriveLabel);
		thirdRowEnginePanel.add(Box.createRigidArea(new Dimension(63,0)));
		thirdRowEnginePanel.add(wDrive);
		
		// fourth and fifth row
		JPanel fourthRowEnginePanel = new JPanel();
		fourthRowEnginePanel.setOpaque(false);
		JPanel fifthRowEnginePanel = new JPanel();
		fifthRowEnginePanel.setOpaque(false);
		JLabel capacityLabel = new JLabel("Capacity");
		AppResources.changeFont(capacityLabel, Font.PLAIN, 18);
		capacityField = new JTextField (6);
		JLabel kwLabel = new JLabel("kw");
		AppResources.changeFont(kwLabel, Font.PLAIN, 18);
		JLabel hPowerLabel = new JLabel("Horsepower");
		AppResources.changeFont(hPowerLabel, Font.PLAIN, 18);
		hPowerField = new JTextField (11);
		JLabel euroL = new JLabel("Euro");
		AppResources.changeFont(euroL, Font.PLAIN, 18);
		euroField = new JTextField (1);
		fourthRowEnginePanel.add(hPowerLabel);
		fourthRowEnginePanel.add(Box.createRigidArea(new Dimension(70,0)));
		fourthRowEnginePanel.add(hPowerField);
		fourthRowEnginePanel.add(Box.createRigidArea(new Dimension(1,0)));
		fourthRowEnginePanel.add(kwLabel);
		fifthRowEnginePanel.add(capacityLabel);
		fifthRowEnginePanel.add(Box.createRigidArea(new Dimension(10,0)));
		fifthRowEnginePanel.add(capacityField);
		fifthRowEnginePanel.add(Box.createRigidArea(new Dimension(96,0)));
		fifthRowEnginePanel.add(euroL);
		fifthRowEnginePanel.add(Box.createRigidArea(new Dimension(10,0)));
		fifthRowEnginePanel.add(euroField);
		
		enginePanel.add(titleEngineLabel);
		enginePanel.add(firstRowEnginePanel);
		enginePanel.add(secondRowEnginePanel);
		enginePanel.add(thirdRowEnginePanel);
		enginePanel.add(fourthRowEnginePanel);
		enginePanel.add(fifthRowEnginePanel);
		
		// DIMENSION DATA
		JPanel dimensionPanel = new JPanel();
		dimensionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		dimensionPanel.setOpaque(false);
		dimensionPanel.setLayout(new BoxLayout(dimensionPanel, BoxLayout.Y_AXIS));
		JLabel titleDimensionLabel = new JLabel ("Dimensions");
		titleDimensionLabel.setAlignmentX(CENTER_ALIGNMENT);
		AppResources.changeFont(titleDimensionLabel, Font.BOLD, 30);
		// first row
		JPanel firstRowDimensionPanel = new JPanel ();
		firstRowDimensionPanel.setOpaque(false);
		JLabel dimenLabel = new JLabel ("Length  /  Height  /  Width   (cm)");
		AppResources.changeFont(dimenLabel, Font.PLAIN, 18);
		JLabel separatorLabel = new JLabel ("/");
		AppResources.changeFont(separatorLabel, Font.PLAIN, 18);
		JLabel separatorLabel2 = new JLabel ("/");
		AppResources.changeFont(separatorLabel2, Font.PLAIN, 18);
		lengthField = new JTextField(5);
		heightField = new JTextField(5);
		widthField = new JTextField(5);
		firstRowDimensionPanel.add(Box.createRigidArea(new Dimension(44, 0)));
		firstRowDimensionPanel.add(dimenLabel);
		firstRowDimensionPanel.add(Box.createRigidArea(new Dimension(44, 0)));
		// second row
		JPanel secondRowDimensionPanel = new JPanel ();
		secondRowDimensionPanel.setOpaque(false);
		secondRowDimensionPanel.add(lengthField);
		secondRowDimensionPanel.add(Box.createRigidArea(new Dimension(4, 0)));
		secondRowDimensionPanel.add(separatorLabel);
		secondRowDimensionPanel.add(Box.createRigidArea(new Dimension(4, 0)));
		secondRowDimensionPanel.add(heightField);
		secondRowDimensionPanel.add(Box.createRigidArea(new Dimension(4, 0)));
		secondRowDimensionPanel.add(separatorLabel2);
		secondRowDimensionPanel.add(Box.createRigidArea(new Dimension(4, 0)));
		secondRowDimensionPanel.add(widthField);
		// third row
		JPanel thirdRowDimensionPanel = new JPanel ();
		thirdRowDimensionPanel.setOpaque(false);
		JLabel trunkLabel = new JLabel ("Trunk capacity (liters)");
		AppResources.changeFont(trunkLabel, Font.PLAIN, 18);
		trunkField = new JTextField (5);
		thirdRowDimensionPanel.add(trunkLabel);
		thirdRowDimensionPanel.add(Box.createRigidArea(new Dimension(15, 0)));
		thirdRowDimensionPanel.add(trunkField);
		// fourth row
		JPanel fourthRowDimensionPanel = new JPanel();
		fourthRowDimensionPanel.setOpaque(false);
		JLabel weightLabel = new JLabel("Car weight (kg)");
		AppResources.changeFont(weightLabel, Font.PLAIN, 18);
		weightField = new JTextField(5);
		fourthRowDimensionPanel.add(weightLabel);
		fourthRowDimensionPanel.add(Box.createRigidArea(new Dimension(71, 0)));
		fourthRowDimensionPanel.add(weightField);
		JPanel fifthRowDimensionPanel = new JPanel();
		fifthRowDimensionPanel.setOpaque(false);
		fifthRowDimensionPanel.add(new JLabel (" "));
		
		dimensionPanel.add(titleDimensionLabel);
		dimensionPanel.add(firstRowDimensionPanel);
		dimensionPanel.add(secondRowDimensionPanel);
		dimensionPanel.add(thirdRowDimensionPanel);
		dimensionPanel.add(fourthRowDimensionPanel);
		dimensionPanel.add(fifthRowDimensionPanel);

		bigHorizontalPanel.add(generalDataPanel);
		bigHorizontalPanel.add(enginePanel);
		bigHorizontalPanel.add(dimensionPanel);
		addCarPanel.add(bigHorizontalPanel);
		addCarPanel.add(Box.createRigidArea(new Dimension (0,35)));
		
		// LICENSE PLATE AND KILOMETERS FOR USED CAR
		onlyForUsedPanel = new JPanel();
		onlyForUsedPanel.setOpaque(false);
		onlyForUsedPanel.setLayout(new BoxLayout(onlyForUsedPanel, BoxLayout.Y_AXIS));
		if (!supplier) 
			fillUsedPanel(onlyForUsedPanel);
		addCarPanel.add(onlyForUsedPanel);

		
		// TIRE INFORMATION
		JPanel tirePanel = new JPanel();
		tirePanel.setOpaque(false);
		tirePanel.setLayout(new BoxLayout(tirePanel, BoxLayout.Y_AXIS));
		JPanel tireTitlePanel = new JPanel();
		tireTitlePanel.setOpaque(false);
		JLabel icon1 = new JLabel (new ImageIcon ("icons/tire.png"));
		JLabel icon2 = new JLabel (new ImageIcon ("icons/tire2.png"));
		JLabel tirePresentationLabel = new JLabel ("Insert information about the car tires");
		AppResources.changeFont(tirePresentationLabel, Font.BOLD, 24);
		tireTitlePanel.add(icon1);
		tireTitlePanel.add(tirePresentationLabel);
		tireTitlePanel.add(icon2);
		// firstRow
		JPanel firstRowTirePanel = new JPanel();
		firstRowTirePanel.setOpaque(false);
		JPanel secondRowTirePanel = new JPanel();
		secondRowTirePanel.setOpaque(false);
		JPanel thirdRowTirePanel = new JPanel();
		thirdRowTirePanel.setOpaque(false);
		JLabel serviceLabel = new JLabel ("Service type");
		AppResources.changeFont(serviceLabel, Font.PLAIN, 18);
		JLabel constructionLabel = new JLabel ("Construction");
		AppResources.changeFont(constructionLabel, Font.PLAIN, 18);
		JLabel tireTypeLabel = new JLabel ("Type");
		AppResources.changeFont(tireTypeLabel, Font.PLAIN, 18);
		String [] tire1 = {"P: Passenger Car", "LT: Light Truck", "ST: Special Trailer"};
		String [] tire2 = {"R: Radial", "D: Diagonal", "B: Bias belt"};
		String [] tire3 = {"summer", "winter", "universal"};
		serviceType = new JComboBox <String> (tire1);
		serviceType.setPrototypeDisplayValue("XXXXXXXXXXXXXXX");
		serviceType.setSelectedIndex(0);
		construction = new JComboBox <String> (tire2);
		construction.setPrototypeDisplayValue("XXXXXXXXXXXXXXX");
		construction.setSelectedIndex(0);
		tireType = new JComboBox <String> (tire3);
		tireType.setPrototypeDisplayValue("XXXXXXXXXXXXXXX");
		tireType.setSelectedIndex(0);
		JLabel widthLabel = new JLabel ("Width");
		AppResources.changeFont(widthLabel, Font.PLAIN, 18);
		JLabel aspetLabel = new JLabel ("Aspet Ratio");
		AppResources.changeFont(aspetLabel, Font.PLAIN, 18);
		JLabel diameterLabel = new JLabel ("Diameter");
		AppResources.changeFont(diameterLabel, Font.PLAIN, 18);
		aspetRatioField = new JTextField(2);
		tireWField= new JTextField(2);
		diameterField= new JTextField(2);
		firstRowTirePanel.add(serviceLabel);
		firstRowTirePanel.add(serviceType);
		firstRowTirePanel.add(Box.createRigidArea(new Dimension(20,0)));
		firstRowTirePanel.add(aspetLabel);
		firstRowTirePanel.add(aspetRatioField);
		secondRowTirePanel.add(constructionLabel);
		secondRowTirePanel.add(construction);
		secondRowTirePanel.add(Box.createRigidArea(new Dimension(64,0)));
		secondRowTirePanel.add(widthLabel);
		secondRowTirePanel.add(tireWField);
		thirdRowTirePanel.add(tireTypeLabel);
		thirdRowTirePanel.add(Box.createRigidArea(new Dimension(56,0)));
		thirdRowTirePanel.add(tireType);
		thirdRowTirePanel.add(Box.createRigidArea(new Dimension(40,0)));
		thirdRowTirePanel.add(diameterLabel);
		thirdRowTirePanel.add(diameterField);


		tirePanel.add(tireTitlePanel);
		tirePanel.add(firstRowTirePanel);
		tirePanel.add(secondRowTirePanel);
		tirePanel.add(thirdRowTirePanel);
		addCarPanel.add(tirePanel);
		addCarPanel.add(Box.createRigidArea(new Dimension (0,35)));

		
		// COLOR PANEL
		JPanel colorPanel = new JPanel();
		colorPanel.setOpaque(false);
		colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
		JPanel colorTitlePanel = new JPanel();
		colorTitlePanel.setOpaque(false);
		JLabel coloricon1 = new JLabel (new ImageIcon ("icons/pantone.png"));
		JLabel coloricon2 = new JLabel (new ImageIcon ("icons/pantone.png"));
		JLabel colorPresentationLabel = new JLabel ("Insert the colors of the car");
		AppResources.changeFont(colorPresentationLabel, Font.BOLD, 24);
		colorTitlePanel.add(coloricon1);
		colorTitlePanel.add(colorPresentationLabel);
		colorTitlePanel.add(coloricon2);
		colorPanel.add(colorTitlePanel);
		
		JPanel supportPanel = new JPanel();
		supportPanel.setOpaque(false);
		ColorsPanel colPanel = new ColorsPanel();
		JScrollPane content = new JScrollPane(colPanel);
		content.setPreferredSize(new Dimension (520, 188));
		content.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		content.setOpaque(false);
		supportPanel.add(content);
		colorPanel.add(supportPanel);
		colors = colPanel.getColorCheckBoxes();
		colorKeys = colPanel.getColorKeys();
		
		addCarPanel.add(colorPanel);
		addCarPanel.add(Box.createRigidArea(new Dimension (0,35)));
		
		// OPTIONAL PANEL
		JPanel optionalPanel = new JPanel();
		optionalPanel.setOpaque(false);
		optionalPanel.setLayout(new BoxLayout(optionalPanel, BoxLayout.Y_AXIS));
		JPanel optionalTitlePanel = new JPanel();
		optionalTitlePanel.setOpaque(false);
		JLabel opticon1 = new JLabel (new ImageIcon ("icons/opt.png"));
		JLabel opticon2 = new JLabel (new ImageIcon ("icons/opt.png"));
		JLabel optPresentationLabel = new JLabel ("Insert the optionals");
		AppResources.changeFont(optPresentationLabel, Font.BOLD, 24);
		optionalTitlePanel.add(opticon1);
		optionalTitlePanel.add(optPresentationLabel);
		optionalTitlePanel.add(opticon2);
		optionalPanel.add(optionalTitlePanel);
		
		// scroll panel
		optionals = new ArrayList<JCheckBox>();
		populateOptionalsCheckBoxes();
		JPanel container = new JPanel();
		container.setOpaque(false);
		contentOpt = new JPanel();
		contentOpt.setLayout(new BoxLayout(contentOpt, BoxLayout.Y_AXIS));
		contentOpt.setOpaque(false);
		JScrollPane pane = new JScrollPane(contentOpt);
		pane.setPreferredSize(new Dimension (280, 190));
		container.add(pane);
		fillOptionalPanel(optionals);
		optionalPanel.add(container);
		
		// panel for adding a new optional
		JPanel addOptPanel = new JPanel();
		addOptPanel.setOpaque(false);
		JLabel description1 = new JLabel ("Note: optionals of different car makes may have different prices");
		JLabel description2 = new JLabel ("Select the correct optional, or add another one");
		description1.setAlignmentX(CENTER_ALIGNMENT);
		description2.setAlignmentX(CENTER_ALIGNMENT);
		AppResources.changeFont(description1, Font.PLAIN, 15);
		AppResources.changeFont(description2, Font.PLAIN, 15);
		JButton addOptButton = AppResources.iconButton("Add another optional", "icons/plus.png");
		AppResources.changeFont(addOptButton, Font.PLAIN, 15);
		addOptPanel.add(addOptButton);
		optionalPanel.add(description1);
		optionalPanel.add(description2);
		optionalPanel.add(addOptPanel);
	
		addCarPanel.add(optionalPanel);
		addCarPanel.add(Box.createRigidArea(new Dimension (0,35)));
		
		// PANEL FOR INSERTING IMAGE
		JPanel imagePanel = new JPanel();
		imagePanel.setOpaque(false);
		imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
		JPanel imageTitlePanel = new JPanel();
		imageTitlePanel.setOpaque(false);
		JLabel myicon1 = new JLabel (new ImageIcon ("icons/add-image.png"));
		JLabel myicon2 = new JLabel (new ImageIcon ("icons/add-image.png"));
		JLabel imageLabel = new JLabel ("Select a picture for the car");
		AppResources.changeFont(imageLabel, Font.BOLD, 24);
		imageTitlePanel.add(myicon1);
		imageTitlePanel.add(imageLabel);
		imageTitlePanel.add(myicon2);
		imagePanel.add(imageTitlePanel);
		// button for inserting image
		JPanel imagefirstRow = new JPanel ();
		imagefirstRow.setOpaque(false);
		uploadImage = new JButton("Upload image");
		AppResources.changeFont(uploadImage, Font.PLAIN, 15);
		imagefirstRow.add(uploadImage);
		uploadLabel = new JLabel (" ");
		AppResources.changeFont(uploadLabel, Font.PLAIN, 15);
		imagefirstRow.add(uploadLabel);
		imagePanel.add(imagefirstRow);

		addCarPanel.add(imagePanel);
		addCarPanel.add(Box.createRigidArea(new Dimension (0,35)));
		
		// panel for adding the date
		JPanel datePanel = new JPanel();
		datePanel.setOpaque(false);
		datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
		JPanel dateTitlePanel = new JPanel();
		dateTitlePanel.setOpaque(false);
		JLabel myicon3 = new JLabel (new ImageIcon ("icons/day.png"));
		JLabel myicon4 = new JLabel (new ImageIcon ("icons/day.png"));
		JLabel dateLabel = new JLabel ("Select date of the purchase");
		AppResources.changeFont(dateLabel, Font.BOLD, 24);
		dateTitlePanel.add(myicon3);
		dateTitlePanel.add(dateLabel);
		dateTitlePanel.add(myicon4);
		datePanel.add(dateTitlePanel);
		// calendar
		JPanel datefirstPanel = new JPanel();
		datefirstPanel.setOpaque(false);
		UtilDateModel model = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl dateCPanel = new JDatePanelImpl(model, p);
		datePicker = new JDatePickerImpl(dateCPanel, new DateLabelFormatter());
		datePicker.setOpaque(false);
		datefirstPanel.add(datePicker);
		datePanel.add(datefirstPanel);
		
		addCarPanel.add(datePanel);
		addCarPanel.add(Box.createRigidArea(new Dimension (0,35)));

		// button for adding a car
		JPanel buttonP = new JPanel ();
		buttonP.setOpaque(false);
		buyCar = AppResources.iconButton("Buy Car", "icons/money-bag2.png");
		AppResources.changeFont(buyCar, Font.BOLD, 30);
		buyCar.setForeground(new Color (255, 128, 0));
		buttonP.add(buyCar);
		back =  AppResources.iconButton("Go back", "icons/back2.png");
		AppResources.changeFont(back, Font.BOLD, 30);
		buttonP.add(Box.createRigidArea(new Dimension(20,0)));
		buttonP.add(back);
		
		
		
		addCarPanel.add(buttonP);



		
		// IT'S LISTENER TIME
		newCar.addActionListener(new RadioButtonListener ());
		usedCar.addActionListener(new RadioButtonListener ());
		newSupplier.addActionListener(new AddStakeholderListener());
		newCustomer.addActionListener(new AddStakeholderListener());
		make.addActionListener(new MakeListener());
		addOptButton.addActionListener(new AddOptionalListener());
		uploadImage.addActionListener(new UploadListener());
		buyCar.addActionListener(new BuyListener());
		priceField.getDocument().addDocumentListener(new changedTextListener(true, "Price", priceField));
		doorsField.getDocument().addDocumentListener(new changedTextListener(false, "Doors", doorsField));
		seatsField.getDocument().addDocumentListener(new changedTextListener(false, "Seats", seatsField));
		capacityField.getDocument().addDocumentListener(new changedTextListener(false, "Capacity", capacityField));
		hPowerField.getDocument().addDocumentListener(new changedTextListener(false, "Horsepower", hPowerField));
		euroField.getDocument().addDocumentListener(new changedTextListener(false, "Euro", euroField));
		lengthField.getDocument().addDocumentListener(new changedTextListener(false, "Length", lengthField));
		heightField.getDocument().addDocumentListener(new changedTextListener(false, "Height", heightField));
		widthField.getDocument().addDocumentListener(new changedTextListener(false, "Width", widthField));
		trunkField.getDocument().addDocumentListener(new changedTextListener(false, "Trunk capacity", trunkField));
		weightField.getDocument().addDocumentListener(new changedTextListener(false, "Weight", weightField));
		aspetRatioField.getDocument().addDocumentListener(new changedTextListener(false, "Aspet Ratio", aspetRatioField));
		tireWField.getDocument().addDocumentListener(new changedTextListener(false, "Tire width", tireWField));
		diameterField.getDocument().addDocumentListener(new changedTextListener(false, "Diameter", diameterField));
		back.addActionListener(new BackListener());


		for (JCheckBox c : optionals)
			c.addItemListener(new OptSelectionListener());
		

		add(addCarPanel);
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
	
	
	// method for retrieving the index from the primaryKey String (tax code or vat)
	public int getIndexOfKey (boolean supplier, String primaryKey) {
		int indexSelected = 0;
		if (!supplier) {
			for (int i = 0; i < customerList.length; i++) {
				if (customerList[i].contains(primaryKey)) {
					indexSelected = i;
					break;
				}
			}
		}
		else {
			for (int i = 0; i < supplierList.length; i++) {
				if (supplierList[i].contains(primaryKey)) {
					indexSelected = i;
					break;
				}
			}
		}
		return indexSelected;
	}
	
	// method for populating the array list of comboBoxes
	public void populateOptionalsCheckBoxes() {
		optionals.removeAll(optionals);
		String query = "SELECT *  from optional";

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()) {
				int price = rs.getInt("price");
				currencyFormat = NumberFormat.getCurrencyInstance();
				String priceFormatted = currencyFormat.format(price);
				JCheckBox temp = new JCheckBox(rs.getString("opt_name") + " - " + priceFormatted);
				temp.setOpaque(false);
				AppResources.changeFont(temp, Font.PLAIN, 20);
				optionals.add(temp);
			}
			stat.close();
			rs.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}
	
	// formatter for the date Picker
	public class DateLabelFormatter extends AbstractFormatter {

		private String datePattern = "yyyy-MM-dd";
		private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

		@Override
		public Object stringToValue(String text) throws ParseException {
			return dateFormatter.parseObject(text);
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			if (value != null) {
				Calendar cal = (Calendar) value;
				return dateFormatter.format(cal.getTime());
			}

			return "";
		}
	}
	
	// method for filling the onlyForUsedPanel 
	public void fillUsedPanel (JPanel onlyForUsedPanel) {
		JPanel titleP = new JPanel();
		titleP.setOpaque(false);
		JLabel icon1 = new JLabel (new ImageIcon ("icons/licensePlate.png"));
		JLabel icon2 = new JLabel (new ImageIcon ("icons/mileage.png"));
		JLabel presentationLabel = new JLabel ("Insert license plate and mileage of the used car");
		AppResources.changeFont(presentationLabel, Font.BOLD, 24);
		titleP.add(icon1);
		titleP.add(presentationLabel);
		titleP.add(icon2);
		onlyForUsedPanel.add(titleP);
		JPanel supportPanel = new JPanel();
		supportPanel.setOpaque(false);
		JLabel licenseLabel = new JLabel ("License plate");
		AppResources.changeFont(licenseLabel, Font.PLAIN, 18);
		JLabel mileageLabel = new JLabel ("Mileage (km)");
		AppResources.changeFont(mileageLabel, Font.PLAIN, 18);
		licenseField = new JTextField(10);
		mileageField = new JTextField(10);
		mileageField.getDocument().addDocumentListener(new changedTextListener(false, "Mileage", mileageField));
		supportPanel.add(licenseLabel);
		supportPanel.add(licenseField);
		supportPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		supportPanel.add(mileageLabel);
		supportPanel.add(mileageField);
		onlyForUsedPanel.add(supportPanel);
		onlyForUsedPanel.add(Box.createRigidArea(new Dimension (0,35)));

	}
	
	
	// method for filling the panel that contains list of checkboxes
	public void fillOptionalPanel (ArrayList<JCheckBox> optionals) {
		contentOpt.removeAll();
		// sort the array with comparator
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
	
	// method for calculate totalPrice and set button
	public void priceUpdating() {
		if (priceField.getText().compareTo("") != 0) {
			totalPrice = Double.parseDouble(priceField.getText());
			for (JCheckBox c : optionals) {
				if (c.isSelected()) {
					String substring = c.getText().substring(c.getText().indexOf("€ ") + 2,
							c.getText().lastIndexOf(","));
					Double thousand = 0.00;
					Double hundred = 0.00;
					double optionalPrice = 0.00;
					if (substring.contains(".")) {
						thousand = Double.parseDouble(substring.substring(0, substring.indexOf(".")));
						hundred = 1000 * Double.parseDouble(substring.substring(substring.indexOf(".")));
						optionalPrice = (thousand * 1000) + hundred;
					} else
						optionalPrice = Double.parseDouble(substring);
					totalPrice += optionalPrice;
				}
			}
		} else
			totalPrice = 0;

		if (totalPrice == 0) {
			buyCar.setText("Buy Car");
		} else
			buyCar.setText("Buy Car - " + currencyFormat.format(totalPrice));
	}
	
	// method for checking all the constraints
	public boolean respectConstraints () {
		if (wrongNumber (doorsField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Doors must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (wrongNumber (seatsField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Seats must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (wrongNumber (priceField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Price must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (wrongNumber (hPowerField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Horsepower must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (wrongNumber (capacityField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Capacity must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (wrongNumber (euroField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Euro must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (wrongNumber (lengthField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Length must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (wrongNumber (heightField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Height must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (wrongNumber (widthField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Width must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (wrongNumber (trunkField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Trunk capacity must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (wrongNumber (weightField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Weight must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (wrongNumber (aspetRatioField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Tire Aspet Ratio must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (wrongNumber (tireWField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Tire width must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (wrongNumber (diameterField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Tire Diameter must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (usedCar.isSelected() && wrongNumber (mileageField)) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Mileage must be an integer and cannot be empty","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (usedCar.isSelected() && licenseField.getText().equals("")) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Please insert the license plate of the used car","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		if (usedCar.isSelected() && !licenseField.getText().equals("")) {
			try {
				String query = "SELECT immatriculation FROM used_car WHERE UPPER(immatriculation) = UPPER('" +licenseField.getText() + "')";
				Statement stat = conn.createStatement();
				ResultSet rs = stat.executeQuery(query);
				if (rs.next()) {
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "This license plate already exists","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
					stat.close();
					rs.close();
					return false;
				}
				stat.close();
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// at least one color has to be selected
		boolean colorSelected = false;
		for(int i = 0; i < colors.size(); i++) {
			if(colors.get(i).getCheckBox().isSelected()) {
				colorSelected = true;
				break;
			}
		}
		if (!colorSelected) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Please insert at least one color for the car","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		// control image is uploaded
		if (uploadLabel.getText().equals("") || !uploadLabel.getText().contains(".jpg")) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Please insert a \"jpg\" image for the car","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		// control date
		if (datePicker.getModel().getValue() == null) {
			JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Please insert the date of the purchase","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
			return false;
		}
		
		return true;
	}
	
	// method for seeing if a textField has a correct number
	public boolean wrongNumber (JTextField textField) {
		boolean result = false;
		try {
			int content = Integer.parseInt(textField.getText());
		} catch (NumberFormatException e) {
			result = true;
		}
		return result;
	}
	
	// big method for adding a new car
	public int addNewCar () {
		// NOW WE CAN MOVE ON. To avoid confusion, get all values that i need for adding a new car
		int carkey = 0;
		int tireKey = getTireForeignKey();
		int dimensionKey = getDimensionForeignKey();
		int engineKey = getEngineForeignKey();
		String selectedMake = null;
		String selectedModel = null;
		String selectedType = null;
		String selectedYear = (String) year.getSelectedItem();
		int car_year = Integer.parseInt(selectedYear);
		int doors = Integer.parseInt(doorsField.getText());
		int seats = Integer.parseInt(seatsField.getText());
		int sold = 0;
		int price = Integer.parseInt(priceField.getText());
		String selectedSupp = (String) supplierChoice.getSelectedItem();
		String boughtFromKey = selectedSupp.substring(selectedSupp.lastIndexOf("(")+1, selectedSupp.lastIndexOf(")"));
		Date date = (Date) datePicker.getModel().getValue();
		java.sql.Date mydate= new java.sql.Date (date.getYear(), date.getMonth(), date.getDay());
		
		// control if the user selected a particular value from combo boxes or added a new one
		if (makeField.getText().equals(""))
			selectedMake = (String) make.getSelectedItem();
		else
			selectedMake = makeField.getText();
		if (modelField.getText().equals(""))
			selectedModel = (String) model.getSelectedItem();
		else
			selectedModel = modelField.getText();
		if (typeField.getText().equals(""))
			selectedType = (String) type.getSelectedItem();
		else
			selectedType = typeField.getText();
		
		// WE HAVE ALL WHAT WE NEED FOR ADDING A NEW CAR (except from color and optionals, this will be done later)
		String addCarQuery = "INSERT INTO new_car (make, model, car_type, car_year, doors, seats, sold, base_price, bought_from, bought_date, dimension, engine, tire)VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement stat = conn.prepareStatement(addCarQuery, Statement.RETURN_GENERATED_KEYS);
			stat.setString(1, selectedMake);
			stat.setString(2, selectedModel);
			stat.setString(3, selectedType);
			stat.setInt(4, car_year);
			stat.setInt(5, doors);
			stat.setInt(6, seats);
			stat.setInt(7, sold);
			stat.setInt(8, price);
			stat.setString(9, boughtFromKey);
			stat.setDate(10, mydate);
			stat.setInt(11, dimensionKey);
			stat.setInt(12, engineKey);
			stat.setInt(13, tireKey);
			
			stat.executeUpdate();
			
			ResultSet rs = stat.getGeneratedKeys();
			
			if (rs.next())
				carkey = rs.getInt(1);
			
			stat.close();
			rs.close();
			
			// NOW WE CONCLUDE WITH COLORS AND OPTIONALS
			PreparedStatement colorStat = null, optStat = null;
			for(int i = 0; i < colors.size(); i++) {
				if(colors.get(i).getCheckBox().isSelected()) {
					String colorQuery = "INSERT INTO new_painting VALUES(?, ?)";
					colorStat = conn.prepareStatement(colorQuery);
					colorStat.setInt(1, carkey);
					colorStat.setString(2, colorKeys.get(i));
					colorStat.executeUpdate();
				}	
			}
			
			ArrayList<Integer> selectedOptKeys = new ArrayList<Integer> ();
			for (JCheckBox c: optionals) {
				if (c.isSelected()) {
					String optname = c.getText().substring(0, c.getText().indexOf(" -"));
					double optprice = 0;
					String substring = c.getText().substring(c.getText().indexOf("€ ") + 2,
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
			
			for (Integer n : selectedOptKeys) {
				String optQuery = "INSERT INTO new_equipped VALUES(?, ?)";
				optStat = conn.prepareStatement(optQuery);
				optStat.setInt(1, n);
				optStat.setInt(2, carkey);
				optStat.executeUpdate();
			}
			
			colorStat.close();
			optStat.close();
			
			// NOW COPY IMAGE
			// copy
			try {
				Files.copy(fileInserted.toPath(), Paths.get(System.getProperty("user.dir") + "/icons/newCars", carkey + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return carkey;
		
	}
	
	// big method for adding a new car
	public String addUsedCar() {
		// NOW WE CAN MOVE ON. To avoid confusion, get all values that i need for adding
		// a new car
		int tireKey = getTireForeignKey();
		int dimensionKey = getDimensionForeignKey();
		int engineKey = getEngineForeignKey();
		String selectedMake = null;
		String selectedModel = null;
		String selectedType = null;
		String selectedYear = (String) year.getSelectedItem();
		String licensePlate = licenseField.getText();
		int mileage = Integer.parseInt(mileageField.getText());
		int car_year = Integer.parseInt(selectedYear);
		int doors = Integer.parseInt(doorsField.getText());
		int seats = Integer.parseInt(seatsField.getText());
		int sold = 0;
		int price = Integer.parseInt(priceField.getText());
		String selectedCus = (String) customerChoice.getSelectedItem();
		String boughtFromKey = selectedCus.substring(selectedCus.lastIndexOf("(") + 1, selectedCus.lastIndexOf(")"));
		Date date = (Date) datePicker.getModel().getValue();
		java.sql.Date mydate = new java.sql.Date(date.getYear(), date.getMonth(), date.getDay());

		// control if the user selected a particular value from combo boxes or added a
		// new one
		if (makeField.getText().equals(""))
			selectedMake = (String) make.getSelectedItem();
		else
			selectedMake = makeField.getText();
		if (modelField.getText().equals(""))
			selectedModel = (String) model.getSelectedItem();
		else
			selectedModel = modelField.getText();
		if (typeField.getText().equals(""))
			selectedType = (String) type.getSelectedItem();
		else
			selectedType = typeField.getText();

		// WE HAVE ALL WHAT WE NEED FOR ADDING A NEW CAR (except from color and
		// optionals, this will be done later)
		String addCarQuery = "INSERT INTO used_car VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement stat = conn.prepareStatement(addCarQuery);
			stat.setString(1, licensePlate);
			stat.setString(2, selectedMake);
			stat.setString(3, selectedModel);
			stat.setString(4, selectedType);
			stat.setInt(5, car_year);
			stat.setInt(6, doors);
			stat.setInt(7, seats);
			stat.setInt(8, sold);
			stat.setDouble(9, totalPrice);
			stat.setInt(10, mileage);
			stat.setString(11, boughtFromKey);
			stat.setDate(12, mydate);
			stat.setInt(13, dimensionKey);
			stat.setInt(14, engineKey);
			stat.setInt(15, tireKey);

			stat.executeUpdate();
			stat.close();
			

			// NOW WE CONCLUDE WITH COLORS AND OPTIONALS
			PreparedStatement colorStat = null, optStat = null;
			for (int i = 0; i < colors.size(); i++) {
				if (colors.get(i).getCheckBox().isSelected()) {
					String colorQuery = "INSERT INTO used_painting VALUES(?, ?)";
					colorStat = conn.prepareStatement(colorQuery);
					colorStat.setString(1, licensePlate);
					colorStat.setString(2, colorKeys.get(i));
					colorStat.executeUpdate();
				}
			}

			ArrayList<Integer> selectedOptKeys = new ArrayList<Integer>();
			for (JCheckBox c : optionals) {
				if (c.isSelected()) {
					String optname = c.getText().substring(0, c.getText().indexOf(" -"));
					double optprice = 0;
					String substring = c.getText().substring(c.getText().indexOf("€ ") + 2,
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

			for (Integer n : selectedOptKeys) {
				String optQuery = "INSERT INTO used_equipped VALUES(?, ?)";
				optStat = conn.prepareStatement(optQuery);
				optStat.setInt(1, n);
				optStat.setString(2, licensePlate);
				optStat.executeUpdate();
			}

			colorStat.close();
			optStat.close();

			// NOW COPY IMAGE
			// copy
			try {
				Files.copy(fileInserted.toPath(),
						Paths.get(System.getProperty("user.dir") + "/icons/usedCars", licensePlate + ".jpg"),
						StandardCopyOption.REPLACE_EXISTING);

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return licensePlate;

	}
	////////////////////////////////// ADDING QUERY METHODS //////////////////////////////
	
	// TIRE FOREIGN KEY
	public int getTireForeignKey () {
		// first, control if a tire with all values like that one inserted already exists
		int tireForeignKey = 0;
		String checkTire = "SELECT * FROM tire WHERE UPPER(service_type) = UPPER(?) and width = ? and aspet_ratio = ? and UPPER(construction) = UPPER(?) and diameter = ? and UPPER (tire_type) = UPPER(?)";
		try {
			PreparedStatement stat = conn.prepareStatement(checkTire);
			String selectedServiceType = (String) serviceType.getSelectedItem();
			String selectedConstruction = (String) construction.getSelectedItem();
			String selectedType = (String) tireType.getSelectedItem();
			stat.setString(1, selectedServiceType.substring(0, selectedServiceType.indexOf(":")));
			stat.setInt(2, Integer.parseInt(tireWField.getText()));
			stat.setInt(3, Integer.parseInt(aspetRatioField.getText()));
			stat.setString(4, selectedConstruction.substring(0, 1));
			stat.setInt(5, Integer.parseInt(diameterField.getText()));
			stat.setString(6, selectedType);

			ResultSet rs = stat.executeQuery();
			
			if (rs.next()) {
			// We have a result (only one, we cannot have duplicate tires)
				tireForeignKey = rs.getInt("tire_id");
			} else {
				// No data, we have to insert a new tire

				String addTireQuery = "INSERT INTO tire (service_type, width, aspet_ratio, construction, diameter, tire_type) VALUES (?, ?, ?, ?, ?, ?)";

				PreparedStatement addTire = conn.prepareStatement(addTireQuery,
						Statement.RETURN_GENERATED_KEYS);

				addTire.setString(1, selectedServiceType.substring(0, 1));
				addTire.setInt(2, Integer.parseInt(tireWField.getText()));
				addTire.setInt(3, Integer.parseInt(aspetRatioField.getText()));
				addTire.setString(4, selectedConstruction.substring(0, 1));
				addTire.setInt(5, Integer.parseInt(diameterField.getText()));
				addTire.setString(6, selectedType);

				addTire.executeUpdate();
				ResultSet key = addTire.getGeneratedKeys();

				if (key.next())
					tireForeignKey = key.getInt(1);
				
				addTire.close();
				key.close();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tireForeignKey;
	}
	
	// DIMENSION FOREIGN KEY
	public int getDimensionForeignKey() {
		// first, control if a dimension with all values like that one inserted already
		// exists
		int dimForeignKey = 0;
		String checkDimension = "SELECT * FROM dimension WHERE car_heigth = ? and car_length = ? and car_weight = ? and car_width = ? and trunk_capacity = ?";
		try {
			PreparedStatement stat = conn.prepareStatement(checkDimension);
			stat.setInt(1, Integer.parseInt(heightField.getText()));
			stat.setInt(2, Integer.parseInt(lengthField.getText()));
			stat.setInt(3, Integer.parseInt(weightField.getText()));
			stat.setInt(4, Integer.parseInt(widthField.getText()));
			stat.setInt(5, Integer.parseInt(trunkField.getText()));

			ResultSet rs = stat.executeQuery();

			if (rs.next()) {
				// We have a result (only one, we cannot have duplicate dimensions)
				dimForeignKey = rs.getInt("dimension_id");
			} else {
				// No data, we have to insert a new dimension

				String addDimensionQuery = "INSERT INTO dimension (car_heigth, car_length, car_weight, car_width, trunk_capacity) VALUES (?, ?, ?, ?, ?)";

				PreparedStatement addDim = conn.prepareStatement(addDimensionQuery, Statement.RETURN_GENERATED_KEYS);

				addDim.setInt(1, Integer.parseInt(heightField.getText()));
				addDim.setInt(2, Integer.parseInt(lengthField.getText()));
				addDim.setInt(3, Integer.parseInt(weightField.getText()));
				addDim.setInt(4, Integer.parseInt(widthField.getText()));
				addDim.setInt(5, Integer.parseInt(trunkField.getText()));

				addDim.executeUpdate();
				ResultSet key = addDim.getGeneratedKeys();

				if (key.next())
					dimForeignKey = key.getInt(1);

				addDim.close();
				key.close();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dimForeignKey;
	}

	// ENGINE FOREIGN KEY
	public int getEngineForeignKey() {
		// first, control if an engine with all values like that one inserted already
		// exists
		int engineForeignKey = 0;
		String checkEngine = "SELECT * FROM engine WHERE capacity = ? and UPPER(fuel) = UPPER(?) and horsepower = ? and UPPER(wheel_drive) = UPPER (?) and euro = ? and UPPER(transmission) = UPPER(?)";
		try {
			PreparedStatement stat = conn.prepareStatement(checkEngine);
			String selectedFuel = null;
			if (fuelField.getText().equals("")) 
				selectedFuel = (String) fuel.getSelectedItem();
			else
				selectedFuel = fuelField.getText();
			String selectedWDrive = (String) wDrive.getSelectedItem();
			String selectedTransmission = (String) trans.getSelectedItem();
			stat.setInt(1,  Integer.parseInt(capacityField.getText()));
			stat.setString(2, selectedFuel);
			stat.setInt(3, Integer.parseInt(hPowerField.getText()));
			stat.setString(4, selectedWDrive);
			stat.setInt(5, Integer.parseInt(euroField.getText()));
			stat.setString(6, selectedTransmission);

			ResultSet rs = stat.executeQuery();

			if (rs.next()) {
				// We have a result (only one, we cannot have duplicate tires)
				engineForeignKey = rs.getInt("engine_id");
			} else {
				// No data, we have to insert a new tire

				String addEngineQuery = "INSERT INTO engine (capacity, fuel, horsepower, wheel_drive, euro, transmission) VALUES (?, ?, ?, ?, ?, ?)";

				PreparedStatement addEngine = conn.prepareStatement(addEngineQuery, Statement.RETURN_GENERATED_KEYS);

				addEngine.setInt(1,  Integer.parseInt(capacityField.getText()));
				addEngine.setString(2, selectedFuel);
				addEngine.setInt(3, Integer.parseInt(hPowerField.getText()));
				addEngine.setString(4, selectedWDrive);
				addEngine.setInt(5, Integer.parseInt(euroField.getText()));
				addEngine.setString(6, selectedTransmission);

				addEngine.executeUpdate();
				ResultSet key = addEngine.getGeneratedKeys();

				if (key.next())
					engineForeignKey = key.getInt(1);

				addEngine.close();
				key.close();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return engineForeignKey;
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
	
	
	
	////////////////////////////////////// INNER CLASS ///////////////////////////////////
	
	// class panel for adding an optional
	private class AddOptionalPanel extends JPanel  {
		public JLabel name, price;
		public JTextField nameField, priceField;
		
		public AddOptionalPanel () {
			JPanel addOptionalPanel = new JPanel();
			addOptionalPanel.setLayout(new BoxLayout(addOptionalPanel, BoxLayout.Y_AXIS));
			name = new JLabel ("Optional");
			nameField = new JTextField(10);
			price = new JLabel ("Price (€)");
			priceField = new JTextField(10);
			JPanel s1 = new JPanel();
			JPanel s2 = new JPanel();
			s1.add(name);
			s1.add(nameField);
			s2.add(price);
			s2.add(Box.createRigidArea(new Dimension(1,0)));
			s2.add(priceField);
			addOptionalPanel.add(s1);
			addOptionalPanel.add(s2);

			add(addOptionalPanel);
		}
	}
	
	// class for selecting the quantity
	public class SelectQuantityPanel extends JPanel {
		public JLabel selectLabel, quantityLabel;
		public JButton addQuantity, removeQuantity;
		public SelectQuantityPanel () {
			JPanel qPanel = new JPanel();
			qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));
			selectLabel = new JLabel("Select number of car units");
			AppResources.changeFont(selectLabel, Font.PLAIN, 18);
			selectLabel.setAlignmentX(CENTER_ALIGNMENT);
			qPanel.add(selectLabel);
			JPanel row = new JPanel();
			addQuantity = new JButton("+");
			addQuantity.setPreferredSize(new Dimension(25, 25));
			AppResources.changeFont(addQuantity, Font.PLAIN, 18);
			removeQuantity = new JButton("-");
			AppResources.changeFont(removeQuantity, Font.PLAIN, 18);
			removeQuantity.setPreferredSize(new Dimension(25, 25));
			quantityLabel = new JLabel (quantity + "");
			AppResources.changeFont(quantityLabel, Font.PLAIN, 20);
			addQuantity.addActionListener(new AddQListener());
			removeQuantity.addActionListener(new RemoveQListener());
			row.add(addQuantity);
			row.add(quantityLabel);
			row.add(removeQuantity);

			
			qPanel.add(row);
			
			add(qPanel);
		}
		private class AddQListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				quantity++;
				quantityLabel.setText(quantity + "");
				
			}
			
		}
		private class RemoveQListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (quantity > 1) {
					quantity--;
					quantityLabel.setText(quantity + "");
				}
				
			}
			
		}
		
	}
	
	// class for asking confirm
	public class ConfirmPurchasePanel extends JPanel {
		
		public ConfirmPurchasePanel () {
			JPanel confirmPanel = new JPanel();
			confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.Y_AXIS));
			JLabel confirmLabel = new JLabel("Please confirm the purchase");
			AppResources.changeFont(confirmLabel, Font.BOLD, 20);
			JLabel confirm2Label = new JLabel("Order details:");
			AppResources.changeFont(confirm2Label, Font.PLAIN, 18);
			confirmLabel.setAlignmentX(CENTER_ALIGNMENT);
			confirm2Label.setAlignmentX(CENTER_ALIGNMENT);
			// first row
			JPanel support1 = new JPanel();
			support1.setLayout(new BorderLayout());
			JPanel firstRow = new JPanel();
			String selectedMake = null;
			String selectedModel = null;
			if (makeField.getText().equals(""))
				selectedMake = (String) make.getSelectedItem();
			else
				selectedMake = makeField.getText();
			if (modelField.getText().equals(""))
				selectedModel = (String) model.getSelectedItem();
			else
				selectedModel = modelField.getText();
			
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
			JLabel info2 = new JLabel("Number of units: " + quantity);
			info2.setFont(new Font("Helvetica", Font.PLAIN, 16));
			secondRow.add(image2);
			secondRow.add(info2);
			support2.add(secondRow, BorderLayout.WEST);
			
			// third row
			JPanel support3 = new JPanel();
			support3.setLayout(new BorderLayout());
			JPanel thirdRow = new JPanel();
			JLabel image3 = new JLabel(new ImageIcon("icons/price-tag.png"));
			JLabel info3 = new JLabel("Total cost: " + currencyFormat.format(totalPrice * quantity));
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
			add(confirmPanel);
		}
	}
	///////////////////////////////// LISTENERS /////////////////////////////////////////
	
	// listener for radio buttons
	private class RadioButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JRadioButton selected = (JRadioButton) e.getSource();
			if (selected == newCar) {
				customerChoice.setEnabled(false);
				newCustomer.setEnabled(false);
				supplierChoice.setEnabled(true);
				newSupplier.setEnabled(true);
				iconLabel.setIcon(new ImageIcon("icons/left.png"));
				onlyForUsedPanel.removeAll();
			} else {
				customerChoice.setEnabled(true);
				newCustomer.setEnabled(true);
				supplierChoice.setEnabled(false);
				newSupplier.setEnabled(false);
				iconLabel.setIcon(new ImageIcon("icons/right.png"));
				fillUsedPanel(onlyForUsedPanel);

			}
			revalidate();
			repaint();
		}
	}

	// action listener for adding a new supplier if it is not already present in the
	// combo box "supplierList"
	private class AddStakeholderListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton selected = (JButton) e.getSource();
			if (selected == newSupplier)
				MainPanel.getMainPanel().swapPanel(new addSupplierPanel(true));
			else
				MainPanel.getMainPanel().swapPanel(new addCustomerPanel(true));

		}
	}

	// listener for the change item make field
	private class MakeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// Change model accordingly
			JComboBox selectedCombo = (JComboBox) arg0.getSource();
			String selectedMake = (String) selectedCombo.getSelectedItem();
			model.removeAllItems();

			if (selectedMake != null && selectedMake.compareTo("All Makes") != 0) {
				modelsList = GetListQuery.getModels(2, (String) make.getSelectedItem()); // See method comments for
																							// more info
				for (String s : modelsList)
					model.addItem(s);
			}

		}

	}

	// listener for adding another optional
	private class AddOptionalListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String [] options = {"Confirm", "Cancel"};
			String sel = "Cancel";
			int result = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), addOptionalPanel, "CarCube",
					JOptionPane.INFORMATION_MESSAGE, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"), options, sel);
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
						priceUpdating();

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

	// listener for uploading an image
	private class UploadListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// file chooser
			JFileChooser fileC = new JFileChooser();
			fileC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileC.setFileFilter(new FileNameExtensionFilter("Only JPG files", "jpg"));
			// response
			int reply = fileC.showOpenDialog(null);
			if (reply == JFileChooser.APPROVE_OPTION) {
				uploadLabel.setText(fileC.getSelectedFile().getName());
				fileInserted = fileC.getSelectedFile();
			}
		}
	}

	// document listener for the JTextField components, the changedListener above
	// would not have worked
	private class changedTextListener implements DocumentListener {
		boolean priceTextField;
		String nameField;
		JTextField selected;

		public changedTextListener(boolean priceTextField, String nameField, JTextField selected) {
			this.priceTextField = priceTextField;
			this.nameField = nameField;
			this.selected = selected;
		}

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
			try {
				if (selected.getText().compareTo("") != 0) {
					int intInserted = 0;
					if (priceTextField) {
						totalPrice = Double.parseDouble(priceField.getText());

						priceUpdating();
					} else
						intInserted = Integer.parseInt(selected.getText());
				} else {
					if (priceTextField)
						buyCar.setText("Buy Car");
				}
				
			} catch (NumberFormatException n) {
				JOptionPane.showMessageDialog(MainPanel.getMainPanel(), nameField + " must be an integer", "CarCube",
						JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/minilogo.png"));
			}
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
			priceUpdating();

		}
	}

	// listener for buying a car
	private class BuyListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// FIRST OF ALL, see if all constraints are respected
			boolean canWeContinue = respectConstraints();
			if (!canWeContinue)
				return;
			JPanel selectQuantityPanel = new SelectQuantityPanel();
			String [] options = {"Confirm", "Cancel"};
			String sel = "Cancel";
			int result = 0;
			if (newCar.isSelected()) {
				result = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), selectQuantityPanel, "CarCube",
					JOptionPane.INFORMATION_MESSAGE, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
					options, sel);
			}
			if (result == 0) {
				JPanel confirmPanel = new ConfirmPurchasePanel();
				int choice = JOptionPane.showOptionDialog(MainPanel.getMainPanel(), confirmPanel, "CarCube",
						JOptionPane.INFORMATION_MESSAGE, JOptionPane.PLAIN_MESSAGE, new ImageIcon("icons/minilogo.png"),
						options, sel);
				if (choice == 0) {
					String usedCarKey = "";
					int newCarKey = 0;
					if (newCar.isSelected()) {
						for (int i = 0; i<quantity; i++)
							newCarKey = addNewCar();
					}
					else
						usedCarKey = addUsedCar();

					JPanel gifPanel = new JPanel();
					gifPanel.setLayout(new BoxLayout(gifPanel, BoxLayout.Y_AXIS));
					JLabel myGIF = new JLabel(new ImageIcon("icons/200.gif"));
					JLabel bought = new JLabel("Car bought!");
					myGIF.setAlignmentX(CENTER_ALIGNMENT);
					bought.setAlignmentX(CENTER_ALIGNMENT);
					gifPanel.add(myGIF);
					gifPanel.add(bought);
					AppResources.changeFont(bought, Font.BOLD, 25);
					JOptionPane.showMessageDialog(MainPanel.getMainPanel(), gifPanel, "CarCube",
							JOptionPane.PLAIN_MESSAGE);
					if (newCar.isSelected())
						MainPanel.getMainPanel().swapPanel(new ViewCarPanel (newCarKey + "", true));
					else
						MainPanel.getMainPanel().swapPanel(new ViewCarPanel (usedCarKey + "", false));
				}
				quantity = 1;
			}
			else 
				quantity = 1;
		}

	}

	// listener for back button
	private class BackListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			MainPanel.getMainPanel().swapPanel(new SearchCarPanel());

		}

	}
}
