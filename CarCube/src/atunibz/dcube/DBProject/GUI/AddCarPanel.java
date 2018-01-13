package atunibz.dcube.DBProject.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import atunibz.dcube.DBProject.configuration.AppResources;
import atunibz.dcube.DBProject.configuration.GetListQuery;

// Panel for adding a new car in the database
public class AddCarPanel extends JPanel{
	private JPanel addCarPanel, titlePanel, sellerPanel, fromCustomerPanel, fromSupplierPanel, onlyForUsedPanel, contentOpt;
	private Connection conn;
	private JRadioButton newCar, usedCar;
	private JComboBox <String> supplierChoice, customerChoice, make, model, type, year, fuel, trans, wDrive, serviceType, construction, tireType;
	private ButtonGroup group;
	private String [] supplierList, customerList, makesList, modelsList, typesList, fuelList, transList, wDriveList;
	private JButton newSupplier, newCustomer;
	private JLabel iconLabel;
	private JTextField makeField, modelField, typeField, doorsField, seatsField, priceField, fuelField;
	private JTextField capacityField, hPowerField, euroField, lengthField, heightField, widthField, trunkField, weightField;
	private JTextField licenseField, mileageField, aspetRatioField, tireWField, diameterField;
	private ArrayList<JCheckBox> optionals;

	
	// Constructor
	public AddCarPanel (boolean supplier, String primaryKey) {
		
		// open the connection with the database
		conn = DatabaseConnection.getDBConnection().getConnection();
		
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
		firstRowDimensionPanel.add(Box.createRigidArea(new Dimension(60, 0)));
		firstRowDimensionPanel.add(dimenLabel);
		firstRowDimensionPanel.add(Box.createRigidArea(new Dimension(60, 0)));
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
		String [] tire2 = {"R: Radial", "D: diagonal", "B: Bias belt"};
		String [] tire3 = {"Summer", "Winter", "Universal"};
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
		pane.setPreferredSize(new Dimension (250, 190));
		container.add(pane);
		fillOptionalPanel(optionals);
		optionalPanel.add(container);
		
		addCarPanel.add(optionalPanel);
		
		
		
		
		
		
		
		
		
		// IT'S LISTENER TIME
		newCar.addActionListener(new RadioButtonListener ());
		usedCar.addActionListener(new RadioButtonListener ());
		newSupplier.addActionListener(new AddStakeholderListener());
		newCustomer.addActionListener(new AddStakeholderListener());
		make.addActionListener(new MakeListener());
		

		add(addCarPanel);
	}
	
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
			}
			else {
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

		String query = "SELECT *  from optional";

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
			while (rs.next()) {
				int price = rs.getInt("price");
				currencyFormat = NumberFormat.getCurrencyInstance();
				String priceFormatted = currencyFormat.format(price);
				JCheckBox temp = new JCheckBox(rs.getString("opt_name") + " - " + priceFormatted);
				temp.setOpaque(false);
				AppResources.changeFont(temp, Font.PLAIN, 20);
				optionals.add(temp);
			}
			// sort the array with comparator
			Collections.sort(optionals, new Comparator <JCheckBox> () {
				@Override
				public int compare(JCheckBox o1, JCheckBox o2) {
					return o1.getText().compareTo(o2.getText());
				}
			});
			stat.close();
			rs.close();
		} catch (SQLException e) {

			e.printStackTrace();
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
		supportPanel.add(licenseLabel);
		supportPanel.add(licenseField);
		supportPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		supportPanel.add(mileageLabel);
		supportPanel.add(mileageField);
		onlyForUsedPanel.add(supportPanel);
		onlyForUsedPanel.add(Box.createRigidArea(new Dimension (0,35)));

	}
	
	// action listener for adding a new supplier if it is not already present in the combo box "supplierList"
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
	
	// method for filling the panel that contains list of checkboxes
	public void fillOptionalPanel (ArrayList<JCheckBox> optionals) {
		contentOpt.removeAll();
		for (JCheckBox c : optionals) {
			JPanel auxiliaryPanel = new JPanel();
			auxiliaryPanel.setLayout(new BorderLayout());
			auxiliaryPanel.add(c, BorderLayout.WEST);
			contentOpt.add(auxiliaryPanel);
		}
		contentOpt.revalidate();
		contentOpt.repaint();
		
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
	
	

}
