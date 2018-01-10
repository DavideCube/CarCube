package atunibz.dcube.DBProject.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import atunibz.dcube.DBProject.configuration.AppResources;

public class addCustomerPanel extends JPanel {

	private JPanel addCustomerPanel, titlePanel, descriptionPanel, formPanel;
	JPanel emailPanel, phonePanel, faxPanel;
	private JLabel mainDescriptionLabel;
	private JTextField taxField, nameField, surnameField, streetField, civicNumberField, postcodeField;
	private JTextField cityField, nationField;
	private JTextField firstMail, firstPhone, firstFax;
	private ArrayList<JTextField> emails, phoneNumbers, faxNumbers;
	private JButton back, add;
	private Connection conn;

	public addCustomerPanel() {

		// Initialise Variables
		emails = new ArrayList<>();
		phoneNumbers = new ArrayList<>();
		faxNumbers = new ArrayList<>();
		conn = DatabaseConnection.getDBConnection().getConnection();

		addCustomerPanel = new JPanel();
		addCustomerPanel.setLayout(new BoxLayout(addCustomerPanel, BoxLayout.Y_AXIS));

		// Add title Panel
		addCustomerPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		titlePanel = AppResources.carCubePanel();
		addCustomerPanel.add(titlePanel);
		addCustomerPanel.setOpaque(false);

		addCustomerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		// Add descriptionPanel
		descriptionPanel = new JPanel();
		descriptionPanel.setOpaque(false);
		descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.X_AXIS));

		mainDescriptionLabel = new JLabel("* required fields");
		AppResources.changeFont(mainDescriptionLabel, Font.PLAIN, 30);
		descriptionPanel.add(mainDescriptionLabel);

		addCustomerPanel.add(descriptionPanel);

		// General form panel containing both labels and fields
		formPanel = new JPanel();
		formPanel.setOpaque(false);
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

		// TaxCode Panel
		JPanel taxCodePanel = new JPanel();
		taxCodePanel.setLayout(new BoxLayout(taxCodePanel, BoxLayout.X_AXIS));
		taxCodePanel.setOpaque(false);
		JLabel taxLabel = new JLabel("Taxcode *");
		//taxLabel.setForeground(Color.white);
		AppResources.changeFont(taxLabel, Font.PLAIN, 25);
		taxCodePanel.add(taxLabel);

		taxField = new JTextField(10);

		taxCodePanel.add(Box.createRigidArea(new Dimension(191, 0)));
		taxCodePanel.add(taxField);

		formPanel.add(taxCodePanel);

		// Name Panel
		JPanel namePanel = new JPanel();
		namePanel.setOpaque(false);
		namePanel.setPreferredSize(new Dimension(700, 30));
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
		JLabel nameLabel = new JLabel("Name *");
		//nameLabel.setForeground(Color.white);
		AppResources.changeFont(nameLabel, Font.PLAIN, 25);
		namePanel.add(nameLabel);
		nameField = new JTextField(10);
		namePanel.add(Box.createRigidArea(new Dimension(225, 0)));
		namePanel.add(nameField);

		formPanel.add(namePanel);

		// SurName Panel
		JPanel surnamePanel = new JPanel();
		surnamePanel.setOpaque(false);
		surnamePanel.setLayout(new BoxLayout(surnamePanel, BoxLayout.X_AXIS));
		JLabel surnameLabel = new JLabel("Surname *");
		//surnameLabel.setForeground(Color.white);
		AppResources.changeFont(surnameLabel, Font.PLAIN, 25);
		surnamePanel.add(surnameLabel);
		surnameField = new JTextField(10);
		surnamePanel.add(Box.createRigidArea(new Dimension(189, 0)));
		surnamePanel.add(surnameField);

		formPanel.add(surnamePanel);

		// Street and civic Panel
		JPanel streetAndCivicPanel = new JPanel();
		streetAndCivicPanel.setOpaque(false);
		streetAndCivicPanel.setLayout(new BoxLayout(streetAndCivicPanel, BoxLayout.X_AXIS));
		JLabel streetLabel = new JLabel("Street and civic number *");
		//streetLabel.setForeground(Color.white);
		AppResources.changeFont(streetLabel, Font.PLAIN, 25);
		streetAndCivicPanel.add(streetLabel);
		streetField = new JTextField(10);
		civicNumberField = new JTextField(2);
		streetAndCivicPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		streetAndCivicPanel.add(streetField);
		streetAndCivicPanel.add(Box.createRigidArea(new Dimension(40, 0)));
		streetAndCivicPanel.add(civicNumberField);

		formPanel.add(streetAndCivicPanel);

		// Postcode and city Panel
		JPanel postcodeAndCityPanel = new JPanel();
		postcodeAndCityPanel.setOpaque(false);
		postcodeAndCityPanel.setLayout(new BoxLayout(postcodeAndCityPanel, BoxLayout.X_AXIS));
		JLabel postcodeLabel = new JLabel("Postcode and city *");
		//postcodeLabel.setForeground(Color.white);
		AppResources.changeFont(postcodeLabel, Font.PLAIN, 25);
		postcodeAndCityPanel.add(postcodeLabel);
		cityField = new JTextField(18);
		postcodeField = new JTextField(2);
		postcodeAndCityPanel.add(Box.createRigidArea(new Dimension(86, 0)));
		postcodeAndCityPanel.add(postcodeField);
		postcodeAndCityPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		postcodeAndCityPanel.add(cityField);

		formPanel.add(postcodeAndCityPanel);

		// Nation Panel
		JPanel nationPanel = new JPanel();
		nationPanel.setOpaque(false);
		nationPanel.setLayout(new BoxLayout(nationPanel, BoxLayout.X_AXIS));
		JLabel nationLabel = new JLabel("Nation *");
		//nationLabel.setForeground(Color.white);
		AppResources.changeFont(nationLabel, Font.PLAIN, 25);
		nationPanel.add(nationLabel);
		nationField = new JTextField(10);
		nationPanel.add(Box.createRigidArea(new Dimension(213, 0)));
		nationPanel.add(nationField);

		formPanel.add(nationPanel);

		// Email dynamic Panel

		emailPanel = new JPanel();
		emailPanel.setOpaque(false);
		emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));

		JPanel currentEmail = new JPanel();
		currentEmail.setOpaque(false);
		currentEmail.setLayout(new BoxLayout(currentEmail, BoxLayout.X_AXIS));

		JLabel mailLabel = new JLabel("Email *");
		//mailLabel.setForeground(Color.white);
		AppResources.changeFont(mailLabel, Font.PLAIN, 25);
		currentEmail.add(mailLabel);

		JButton newEmail = new JButton("Add another");
		newEmail.addActionListener(new AnotherEmail());
		firstMail = new JTextField(10);
		emails.add(firstMail);
		currentEmail.add(Box.createRigidArea(new Dimension(229, 0)));
		currentEmail.add(firstMail);
		currentEmail.add(Box.createRigidArea(new Dimension(10, 0)));
		currentEmail.add(newEmail);

		emailPanel.add(currentEmail);

		formPanel.add(emailPanel);

		// Phone dynamic Panel

		phonePanel = new JPanel();
		phonePanel.setOpaque(false);
		phonePanel.setLayout(new BoxLayout(phonePanel, BoxLayout.Y_AXIS));

		JPanel currentPhone = new JPanel();
		currentPhone.setOpaque(false);
		currentPhone.setLayout(new BoxLayout(currentPhone, BoxLayout.X_AXIS));

		JLabel phoneLabel = new JLabel("Phone *");
		//phoneLabel.setForeground(Color.white);
		AppResources.changeFont(phoneLabel, Font.PLAIN, 25);
		currentPhone.add(phoneLabel);

		JButton newPhone = new JButton("Add another");
		newPhone.addActionListener(new AnotherPhone());
		firstPhone = new JTextField(10);
		phoneNumbers.add(firstPhone);
		currentPhone.add(Box.createRigidArea(new Dimension(224, 0)));
		currentPhone.add(firstPhone);
		currentPhone.add(Box.createRigidArea(new Dimension(10, 0)));
		currentPhone.add(newPhone);

		phonePanel.add(currentPhone);

		formPanel.add(phonePanel);

		// Fax dynamic Panel

		faxPanel = new JPanel();
		faxPanel.setOpaque(false);
		faxPanel.setLayout(new BoxLayout(faxPanel, BoxLayout.Y_AXIS));

		JPanel currentFax = new JPanel();
		currentFax.setOpaque(false);
		currentFax.setLayout(new BoxLayout(currentFax, BoxLayout.X_AXIS));

		JLabel faxLabel = new JLabel("Fax");
		//faxLabel.setForeground(Color.white);
		AppResources.changeFont(faxLabel, Font.PLAIN, 25);
		currentFax.add(faxLabel);

		JButton newFax = new JButton("Add another");
		newFax.addActionListener(new AddFaxListener());
		firstFax = new JTextField(10);
		faxNumbers.add(firstFax);
		currentFax.add(Box.createRigidArea(new Dimension(268, 0)));
		currentFax.add(firstFax);
		currentFax.add(Box.createRigidArea(new Dimension(10, 0)));
		currentFax.add(newFax);

		faxPanel.add(currentFax);

		formPanel.add(faxPanel);
		
		//END FORM PANEL

		addCustomerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

		addCustomerPanel.add(formPanel);

		addCustomerPanel.add((Box.createRigidArea(new Dimension(0, 80))));

		// Panel for buttons controls
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		// back
		back = AppResources.iconButton("Go back     ", "icons/back.png");
		back.addActionListener(new BackListener());
		buttonPanel.add(back);
		buttonPanel.add((Box.createRigidArea(new Dimension(50, 0))));

		// Add
		add = AppResources.iconButton("Add     ", "icons/user.png");
		add.addActionListener(new AddListener());
		buttonPanel.add(add);

		buttonPanel.setOpaque(false);
		addCustomerPanel.add(buttonPanel);
		addCustomerPanel.add(Box.createRigidArea(new Dimension(0, 40)));
		// end

		add(addCustomerPanel);
	}

	// listener to go back to the stakeholder panel
	private class BackListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			MainPanel.getMainPanel().swapPanel(new StakeholdersPanel());

		}

	}

	// listener for adding a new phone
	private class AnotherPhone implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			JPanel currentPhone = new JPanel();
			currentPhone.setOpaque(false);
			currentPhone.setLayout(new BoxLayout(currentPhone, BoxLayout.X_AXIS));

			JLabel phoneLabel = new JLabel("Phone ");
			AppResources.changeFont(phoneLabel, Font.PLAIN, 25);
			//phoneLabel.setForeground(Color.white);
			currentPhone.add(phoneLabel);

			JButton newPhone = new JButton("Add another");
			newPhone.addActionListener(new AnotherPhone());
			JTextField tempPhone = new JTextField(10);
			phoneNumbers.add(tempPhone);
			currentPhone.add(Box.createRigidArea(new Dimension(237, 0)));
			currentPhone.add(tempPhone);
			currentPhone.add(Box.createRigidArea(new Dimension(10, 0)));
			currentPhone.add(newPhone);
			phonePanel.add(currentPhone);

			phonePanel.repaint();
			phonePanel.revalidate();

		}

	}

	// listener for adding a new email
	private class AnotherEmail implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			JPanel currentEmail = new JPanel();
			currentEmail.setOpaque(false);
			currentEmail.setLayout(new BoxLayout(currentEmail, BoxLayout.X_AXIS));

			JLabel mailLabel = new JLabel("Email ");
			//mailLabel.setForeground(Color.white);
			AppResources.changeFont(mailLabel, Font.PLAIN, 25);
			currentEmail.add(mailLabel);

			JButton newEmail = new JButton("Add another");
			newEmail.addActionListener(new AnotherEmail());
			JTextField tempEmail = new JTextField(10);
			emails.add(tempEmail);
			currentEmail.add(Box.createRigidArea(new Dimension(242, 0)));
			currentEmail.add(tempEmail);
			currentEmail.add(Box.createRigidArea(new Dimension(10, 0)));
			currentEmail.add(newEmail);
			emailPanel.add(currentEmail);

			emailPanel.repaint();
			emailPanel.revalidate();

		}

	}
	
	//listener for adding a new fax
	private class AddFaxListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			JPanel currentFax = new JPanel();
			currentFax.setOpaque(false);
			currentFax.setLayout(new BoxLayout(currentFax, BoxLayout.X_AXIS));

			JLabel faxLabel = new JLabel("Fax");
			//faxLabel.setForeground(Color.white);
			AppResources.changeFont(faxLabel, Font.PLAIN, 25);
			currentFax.add(faxLabel);

			JButton newFax = new JButton("Add another");
			newFax.addActionListener(new AddFaxListener());
			JTextField tempFax = new JTextField(10);
			faxNumbers.add(tempFax);
			currentFax.add(Box.createRigidArea(new Dimension(268, 0)));
			currentFax.add(tempFax);
			currentFax.add(Box.createRigidArea(new Dimension(10, 0)));
			currentFax.add(newFax);

			faxPanel.add(currentFax);
			
			faxPanel.repaint();
			faxPanel.revalidate();
			
		}
		
	}
	
	// listener for the add customer button
	private class AddListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			// First we check if the user respected the constraints (all fields are required
			// and the respective lengths)

			if (taxField.getText().equals("") || taxField.getText().length() > 16) {
				JOptionPane.showMessageDialog(addCustomerPanel, "Taxcode cannot be empty or longer than 16 characters","CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
				return;
			}

			if (nameField.getText().equals("") || nameField.getText().length() > 20) {
				JOptionPane.showMessageDialog(addCustomerPanel, "Name cannot be empty or longer than 20 characters", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
				return;
			}

			if (surnameField.getText().equals("") || surnameField.getText().length() > 30) {
				JOptionPane.showMessageDialog(addCustomerPanel, "Surname cannot be empty or longer than 30 characters", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
				return;
			}

			if (postcodeField.getText().equals("") || postcodeField.getText().length() > 5) {
				JOptionPane.showMessageDialog(addCustomerPanel, "Postcode cannot be empty or longer than 5 characters", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
				return;
			}
			if (streetField.getText().equals("") || streetField.getText().length() > 30) {
				JOptionPane.showMessageDialog(addCustomerPanel, "Street cannot be empty or longer than 30 characters", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
				return;
			}
			if (cityField.getText().equals("") || cityField.getText().length() > 20) {
				JOptionPane.showMessageDialog(addCustomerPanel, "City cannot be empty or longer than 20 characters", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
				return;
			}
			if (civicNumberField.getText().equals("")) {
				JOptionPane.showMessageDialog(addCustomerPanel, "Civic number cannot be empty", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
				return;
			}
			if (nationField.getText().equals("") || nationField.getText().length() > 30) {
				JOptionPane.showMessageDialog(addCustomerPanel, "Nation cannot be empty or longer than 30 characters", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
				return;
			}
			
			Pattern p = Pattern.compile("^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+");
			Matcher m = p.matcher(firstMail.getText());
			
			if (firstMail.getText().equals("") || firstMail.getText().length() > 40 || !m.matches() ) {
				JOptionPane.showMessageDialog(addCustomerPanel, "There must be at least one email and this cannot be longer than 40 characters or invalid", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
				return;
			}
			if (firstPhone.getText().equals("") || firstPhone.getText().length() > 25) {
				JOptionPane.showMessageDialog(addCustomerPanel, "There must be at least one phone and this cannot be longer than 25 characters", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
				return;
			}
			
			
			// We check if the address of our new customer is already present, if yes we
			// have
			// the foreign key for creating the customer, otherwise we insert it.
			String checkAddress = "SELECT * FROM address WHERE UPPER(postcode) = UPPER(?) and UPPER(street) = UPPER(?) and UPPER(city) = UPPER(?) and civic_number = ? and UPPER(nation) = UPPER(?) ";
			try {
				PreparedStatement stat = conn.prepareStatement(checkAddress);
				stat.setString(1, postcodeField.getText());
				stat.setString(2, streetField.getText());
				stat.setString(3, cityField.getText());
				stat.setInt(4, Integer.parseInt(civicNumberField.getText()));
				stat.setString(5, nationField.getText());

				ResultSet rs = stat.executeQuery();

				// At the end of our if we have this value set
				int addressForeignKey = 0;

				if (rs.next()) {
					// We have a result (only one, we cannot have duplicate addresses)
					addressForeignKey = rs.getInt("address_id");
				} else {
					// No data, we have to insert a new address

					String addAddressQuery = "INSERT INTO address (postcode, street, city,civic_number,nation) VALUES(?,?,?,?,?)";

					PreparedStatement addAddress = conn.prepareStatement(addAddressQuery,
							Statement.RETURN_GENERATED_KEYS);

					addAddress.setString(1, postcodeField.getText());
					addAddress.setString(2, streetField.getText());
					addAddress.setString(3, cityField.getText());
					addAddress.setInt(4, Integer.parseInt(civicNumberField.getText()));
					addAddress.setString(5, nationField.getText());

					addAddress.executeUpdate();
					ResultSet key = addAddress.getGeneratedKeys();

					if (key.next())
						addressForeignKey = key.getInt(1);
					
					addAddress.close();
					key.close();
				}
				
				rs.close();
				stat.close();
				// Check if the

				// Now that we have the address foreign key we can add the new customer (if the
				// customer already exist just do nothing)
				String addCustomerQuery = "INSERT INTO customer (tax_code,c_name,c_surname, address) VALUES (?,?,?,?) ON CONFLICT (tax_code) DO NOTHING";
				PreparedStatement addCustomer = conn.prepareStatement(addCustomerQuery);

				addCustomer.setString(1, taxField.getText());
				addCustomer.setString(2, nameField.getText());
				addCustomer.setString(3, surnameField.getText());
				addCustomer.setInt(4, addressForeignKey);

				int affected = addCustomer.executeUpdate();

				if (affected > 0) {
					// we have added it so now we can add its contacts
					Pattern p1 = Pattern.compile("^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+");
					//we start with its emails....
					for(JTextField currentEmail : emails) {
						
						Matcher m1 = p1.matcher(currentEmail.getText());
						if(!currentEmail.getText().equals("") && currentEmail.getText().length() < 40 && m1.matches()) {
						String addEmail = "INSERT INTO mail_contact (mail, owner_customer) VALUES (?,?) ON CONFLICT (mail) DO NOTHING";
						PreparedStatement addMail = conn.prepareStatement(addEmail);
						
						addMail.setString(1, currentEmail.getText());
						//addMail.setString(2, "null");
						addMail.setString(2, taxField.getText());
						
						addMail.executeUpdate();
						
						addMail.close();
						}
					}
					
					
					//... we continue with its phone numbers ...
					for(JTextField currentPhone : phoneNumbers) {
						
						if(!currentPhone.getText().equals("") && currentPhone.getText().length() < 25) {
						String addPhone = "INSERT INTO phone_contact (phone_number, owner_customer) VALUES (?,?) ON CONFLICT (phone_number) DO NOTHING";
						PreparedStatement add = conn.prepareStatement(addPhone);
						
						add.setString(1, currentPhone.getText());
						//addMail.setString(2, "null");
						add.setString(2, taxField.getText());
						
						add.executeUpdate();
						
						add.close();
						}
					}
					
					//... and we finish with its fax numbers!
					for(JTextField currentFax : faxNumbers) {
						
						if(!currentFax.getText().equals("") && currentFax.getText().length() < 30) {
						String addFax = "INSERT INTO fax_contact (fax, owner_customer) VALUES (?,?) ON CONFLICT (fax) DO NOTHING";
						PreparedStatement add = conn.prepareStatement(addFax);
						
						add.setString(1, currentFax.getText());
						//addMail.setString(2, "null");
						add.setString(2, taxField.getText());
						
						add.executeUpdate();
						
						add.close();
						}
					}
					
					JOptionPane.showMessageDialog(addCustomerPanel, "Customer was inserted successfully!");
					MainPanel.getMainPanel().swapPanel(new StakeholdersPanel());
				} else {
					// Already present
					JOptionPane.showMessageDialog(addCustomerPanel, "Customer already present");
					return;
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}