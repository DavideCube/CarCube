package atunibz.dcube.DBProject.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import atunibz.dcube.DBProject.configuration.AppResources;

// Panel for adding a new car in the database
public class AddCarPanel extends JPanel{
	private JPanel addCarPanel, titlePanel, sellerPanel, fromCustomerPanel, fromSupplierPanel;
	private Connection conn;
	private JRadioButton newCar, usedCar;
	private JComboBox <String> supplierChoice, customerChoice;
	private ButtonGroup group;
	private String [] supplierList, customerList;
	private JButton newSupplier, newCustomer;
	
	// Constructor
	public AddCarPanel () {
		
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
		newCar = new JRadioButton("New", true);
		usedCar = new JRadioButton("Used");
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
		supplierList = new String []{"Prova", "Ciao", "Athos"};
		supplierChoice = new JComboBox <String>(supplierList);
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
		customerList = new String []{"Prova", "Ciao", "Athos"};
		customerChoice = new JComboBox <String>(customerList);
		newCustomer = AppResources.iconButton("", "icons/user.png");
		customerChoice.setEnabled(false);
		newCustomer.setEnabled(false);
		JPanel support2 = new JPanel ();
		support2.setOpaque(false);
		support2.add(customerChoice);
		support2.add(Box.createRigidArea(new Dimension (5,0)));
		support2.add(newCustomer);
		fromCustomerPanel.add(fromCusLabel);
		fromCustomerPanel.add(support2);
		
		sellerPanel.add(fromSupplierPanel);
		sellerPanel.add(Box.createRigidArea(new Dimension (300,0)));
		sellerPanel.add(fromCustomerPanel);
		addCarPanel.add(sellerPanel);
		
		
		
		
		// LISTENER TIME
		newCar.addActionListener(new RadioButtonListener ());
		usedCar.addActionListener(new RadioButtonListener ());
		
		
		
		
		
		
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
			}
			else {
				customerChoice.setEnabled(true);
				newCustomer.setEnabled(true);
				supplierChoice.setEnabled(false);
				newSupplier.setEnabled(false);
			}
				
			
		}
		
	}
	

}
