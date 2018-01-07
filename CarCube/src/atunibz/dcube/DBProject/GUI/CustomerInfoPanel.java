package atunibz.dcube.DBProject.GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.*;

import atunibz.dcube.DBProject.configuration.AppResources;

public class CustomerInfoPanel extends BackgroundedPanel {
	
	private String customerPkey;
	private IconLabel nameLbl, surnameLbl, taxLbl, addressLbl;
	private int numberOfPhones, numberOfMails, numberOfFaxes;
	private JLabel nameTF, surnameTF, taxTF, addressTF;
	private JButton backBtn, statsBtn, deleteBtn, modifyBtn;
	private ImageIcon modifyIcon;
	private JButton modNameBtn, modSurnameBtn, modAddressBtn;
	private JPanel infoPanel;
	private ArrayList<JButton> buttons;
	
	public CustomerInfoPanel(String customerPkey) {
		System.out.println("THERMHOSIFooooNE");
		this.setOpaque(false);
		this.customerPkey = customerPkey;
		getNumberOfContacts();
		initComponents();
		configLayout();
		//getQueryResults();
		
	}
	//select count(*) from phone_contact where owner_customer = customerPkey;
	private void getNumberOfContacts() {
		Connection conn = DatabaseConnection.getDBConnection().getConnection();
		try {
			Statement stmnt = conn.createStatement();
			String queryPhone, queryMail, queryFax;
			queryPhone = "select count(*) from phone_contact where owner_customer = " + "'" + customerPkey + "'";
			queryMail = "select count(*) from mail_contact where owner_customer = " + "'" + customerPkey + "'";
			queryFax = "select count(*) from fax_contact where owner_customer = " + "'" + customerPkey + "'";
			ResultSet rsPhone = stmnt.executeQuery(queryPhone);
			while(rsPhone.next()) {
				numberOfPhones = rsPhone.getInt("count");
			}
			ResultSet rsMail = stmnt.executeQuery(queryMail);
			while(rsMail.next()) {
				numberOfMails = rsMail.getInt("count");
			}
			ResultSet rsFax = stmnt.executeQuery(queryFax);
			while(rsFax.next()) {
				numberOfFaxes = rsFax.getInt("count");
			}
			System.out.println("The customer has " + numberOfPhones + " phone numbers, " + numberOfMails + " mail addresses and " + numberOfFaxes + " fax numbers.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String[] getPhones() {
		Connection conn = DatabaseConnection.getDBConnection().getConnection();
		String[] phones = new String[numberOfPhones];
		System.out.println("PHONES: " + Arrays.toString(phones));
		int index = 0;
		try {
			Statement stmnt = conn.createStatement();
			ResultSet rs = stmnt.executeQuery("select phone_number from phone_contact where owner_customer = '" + customerPkey + "'");
			while(rs.next()) {
				phones[index] = rs.getString(1);
				index++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println(Arrays.toString(phones));
		return phones;
	}
	
	private String[] getMails() {
		Connection conn = DatabaseConnection.getDBConnection().getConnection();
		String[] mails = new String[numberOfMails];
		int index = 0;
		try {
			Statement stmnt = conn.createStatement();
			ResultSet rs = stmnt.executeQuery("select mail from mail_contact where owner_customer = '" + customerPkey + "'");
			while(rs.next()) {
				mails[index] = rs.getString(1);
				index++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println(Arrays.toString(mails));
		return mails;
	}
	
	private String[] getFaxes() {
		Connection conn = DatabaseConnection.getDBConnection().getConnection();
		String[] faxes = new String[numberOfFaxes];
		int index = 0;
		try {
			Statement stmnt = conn.createStatement();
			ResultSet rs = stmnt.executeQuery("select fax from fax_contact where owner_customer = '" + customerPkey + "'");
			while(rs.next()) {
				faxes[index] = rs.getString(1);
				index++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(faxes.length == 0)
			System.out.println("[No fax]");
		System.out.println(Arrays.toString(faxes));
		return faxes;
	}
	
	private String getCustomerName() {
		Connection conn = DatabaseConnection.getDBConnection().getConnection();
		String name = null;
		try {
			Statement stmnt = conn.createStatement();
			ResultSet rs = stmnt.executeQuery("select c_name from customer where tax_code = '" + customerPkey + "'");
			while(rs.next()) {
				name = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}
	
	private String getCustomerSurname() {
		Connection conn = DatabaseConnection.getDBConnection().getConnection();
		String surname = null;
		try {
			Statement stmnt = conn.createStatement();
			ResultSet rs = stmnt.executeQuery("select c_surname from customer where tax_code = '" + customerPkey + "'");
			while(rs.next()) {
				surname = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return surname;
	}
	
	
	private void initComponents() {
		//name
		nameLbl = new IconLabel("icons/contacts/tax.png","Name:", AppResources.DEFAULT_FONT, false);
		nameLbl.setOpaque(false);
		nameTF = new JLabel();
		nameTF.setOpaque(false);
		nameTF.setText(this.getCustomerName());
		//surname
		surnameLbl = new IconLabel("icons/contacts/tax.png", "Surname:", AppResources.DEFAULT_FONT, false);
		surnameLbl.setOpaque(false);
		surnameTF = new JLabel();
		surnameTF.setOpaque(false);
		surnameTF.setText(this.getCustomerSurname());
		//taxcode
		taxLbl = new IconLabel("icons/contacts/tax.png","Taxcode:", AppResources.DEFAULT_FONT, false);
		taxLbl.setOpaque(false);
		taxTF = new JLabel();
		taxTF.setOpaque(false);
		taxTF.setText(customerPkey);
		//address
		addressLbl = new IconLabel("icons/contacts/address.png", "Address:", AppResources.DEFAULT_FONT, false);
		addressLbl.setOpaque(false);
		addressTF = new JLabel();
		addressTF.setOpaque(false);
		addressTF.setText(this.getCustomerAddress());
		//buttons
		backBtn = new JButton("Back");
		statsBtn = new JButton("Stats");
		deleteBtn = new JButton("Delete");
		modifyBtn = new JButton("Modify");
		
		
		try {
			modifyIcon = new ImageIcon(ImageIO.read(new File("icons/contacts/modify.png")));
			modifyBtn.setIcon(modifyIcon);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		/*
		modNameBtn = new JButton();
		modNameBtn.setIcon(modifyIcon);
		modNameBtn.addActionListener(new ModifyListener(modNameBtn));
		
		modSurnameBtn = new JButton();
		modSurnameBtn.setIcon(modifyIcon);
		modSurnameBtn.addActionListener(new ModifyListener(modSurnameBtn));
		
		modAddressBtn = new JButton();
		modAddressBtn.setIcon(modifyIcon);
		modAddressBtn.addActionListener(new ModifyListener(modAddressBtn));
		*/
		buttons = new ArrayList<>();
		
		infoPanel = new JPanel();
		
		
	}
	
	
	private String getCustomerAddress() {
		Connection conn = DatabaseConnection.getDBConnection().getConnection();
		String address = null;
		try {
			Statement stmnt = conn.createStatement();
			ResultSet rs = stmnt.executeQuery("select a.street, a.civic_number, a.city, a.postcode, a.nation from address a, customer c where a.address_id = c.address and c.tax_code = '" + customerPkey + "'");
			while(rs.next()) {
				address = rs.getString(1) + " Street n." + rs.getInt(2) + " , " + rs.getString(3) + " - " + rs.getString(4) + " , " + rs.getString(5);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return address;
	}
	
	
	private int addPhoneLabels(GridBagConstraints c, JPanel infoPanel) {
		System.out.println("Constraints: " + c.gridx + ", " + c.gridy);
		int offsetX = 0, offsetY = ++c.gridy;
		String[] phones = getPhones();
		c.gridx = offsetX;
		c.gridy = offsetY;
		for(int i = 0; i < phones.length; i++) {
			IconLabel phoneLbl = new IconLabel("icons/contacts/phone.png","Phone #" + (i+1) +":", AppResources.DEFAULT_FONT, false);
			phoneLbl.setOpaque(false);
			JLabel phoneTF = new JLabel();
			phoneTF.setOpaque(false);
			phoneTF.setText(phones[i]);
			infoPanel.add(phoneLbl, c);
			c.gridx = offsetX + 1;
			infoPanel.add(phoneTF, c);
			//modified here
			c.gridx = ++c.gridx;
			JButton modPhoneBtn = new JButton();
			modPhoneBtn.setIcon(modifyIcon);
			modPhoneBtn.addActionListener(new EditListener(modPhoneBtn));
			infoPanel.add(modPhoneBtn, c);
			modPhoneBtn.setName("modPhoneBtn" + (i+1));
			buttons.add(modPhoneBtn);
			//
			c.gridy = ++c.gridy;
			c.gridx = 0;
			System.out.println("Constraints: " + c.gridx + ", " + c.gridy);
			
		}
		
		return c.gridy;
	}
	
	private int addMailLabels(GridBagConstraints c, JPanel infoPanel) {
		System.out.println("Constraints: " + c.gridx + ", " + c.gridy);
		int offsetX = 0, offsetY = ++c.gridy;
		String[] mails = getMails();
		c.gridx = offsetX;
		c.gridy = offsetY;
		for(int i = 0; i < mails.length; i++) {
			IconLabel mailLbl = new IconLabel("icons/contacts/mail.png","Mail #" + (i+1) +":", AppResources.DEFAULT_FONT, false);
			mailLbl.setOpaque(false);
			JLabel mailTF = new JLabel();
			mailTF.setOpaque(false);
			mailTF.setText(mails[i]);
			infoPanel.add(mailLbl, c);
			c.gridx = offsetX + 1;
			infoPanel.add(mailTF, c);
			//
			c.gridx = ++c.gridx;
			JButton modMailBtn = new JButton();
			modMailBtn.setIcon(modifyIcon);
			modMailBtn.addActionListener(new EditListener(modMailBtn));
			infoPanel.add(modMailBtn, c);
			modMailBtn.setName("modMailBtn" + (i+1));
			buttons.add(modMailBtn);
			//
			c.gridy = ++c.gridy;
			c.gridx = 0;
			System.out.println("Constraints: " + c.gridx + ", " + c.gridy);
			
		}
		
		return c.gridy;
	}
	
	private int addFaxLabels(GridBagConstraints c, JPanel infoPanel) {
		System.out.println("Constraints: " + c.gridx + ", " + c.gridy);
		int offsetX = 0, offsetY = ++c.gridy;
		String[] faxes = getFaxes();
		c.gridx = offsetX;
		c.gridy = offsetY;
		//no fax
		if(faxes.length == 0) {
			IconLabel faxLbl = new IconLabel("icons/contacts/fax.png","Fax:", AppResources.DEFAULT_FONT, false);
			faxLbl.setOpaque(false);
			JLabel faxTF = new JLabel();
			faxTF.setOpaque(false);
			faxTF.setText("-");
			infoPanel.add(faxLbl, c);
			c.gridx = offsetX + 1;
			infoPanel.add(faxTF, c);
			//
			c.gridx = ++c.gridx;
			JButton modFaxBtn = new JButton();
			modFaxBtn.setIcon(modifyIcon);
			modFaxBtn.addActionListener(new EditListener(modFaxBtn));
			infoPanel.add(modFaxBtn, c);
			modFaxBtn.setName("modFaxBtn" + 0);
			buttons.add(modFaxBtn);
			//
			c.gridy = ++c.gridy;
			c.gridx = 0;
			System.out.println("Constraints: " + c.gridx + ", " + c.gridy);
			return c.gridy;
		}
		else {
		for(int i = 0; i < faxes.length; i++) {
			IconLabel faxLbl = new IconLabel("icons/contacts/fax.png","Fax #" + (i+1) +":", AppResources.DEFAULT_FONT, false);
			JLabel faxTF = new JLabel();
			faxTF.setOpaque(false);
			faxTF.setText(faxes[i]);
			infoPanel.add(faxLbl, c);
			c.gridx = offsetX + 1;
			infoPanel.add(faxTF, c);
			//
			c.gridx = ++c.gridx;
			JButton modFaxBtn = new JButton();
			modFaxBtn.setIcon(modifyIcon);
			modFaxBtn.setName("modFaxBtn" + (i+1));
			modFaxBtn.addActionListener(new EditListener(modFaxBtn));
			infoPanel.add(modFaxBtn, c);
			buttons.add(modFaxBtn);
			//
			c.gridy = ++c.gridy;
			c.gridx = 0;
			System.out.println("Constraints: " + c.gridx + ", " + c.gridy);
			
		}
		
		return c.gridy;
		}
	}
	
	private void activeModifyMode() {
		//TODO
		infoPanel.remove(nameTF);
		infoPanel.remove(addressTF);
		infoPanel.remove(surnameTF);
		repaint();
		revalidate();
	}
	
	
	private void configLayout() {
			
		this.setLayout(new BorderLayout());
		//add title
		/*
		JPanel shPanel = new JPanel();
		shPanel.setLayout(new BoxLayout(shPanel, BoxLayout.Y_AXIS));
		shPanel.add((Box.createRigidArea(new Dimension(0, 35))));
		// Panel containing the beautiful logo
		JPanel titlePanel = AppResources.carCubePanel();
		shPanel.add(titlePanel);
		shPanel.setOpaque(false);
		//shPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		this.add(shPanel, BorderLayout.NORTH);*/
		infoPanel.setOpaque(false);
		
		GridBagLayout l = new GridBagLayout();
		infoPanel.setLayout(l);
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		//c.fill = GridBagConstraints.HORIZONTAL;
		//set constraints and add name
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(nameLbl, c);
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(nameTF, c);
		c.gridx = 3;
		JButton modNameBtn = new JButton();
		modNameBtn.setIcon(modifyIcon);
		modNameBtn.addActionListener(new EditListener(modNameBtn));
		infoPanel.add(modNameBtn, c);
		modNameBtn.setName("modNameBtn");
		buttons.add(modNameBtn);
		
		//set constraints and add surname
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(surnameLbl, c);
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(surnameTF, c);
		c.gridx = 3;
		JButton modSurnameBtn = new JButton();
		modSurnameBtn.setIcon(modifyIcon);
		modSurnameBtn.addActionListener(new EditListener(modSurnameBtn));
		infoPanel.add(modSurnameBtn, c);
		modSurnameBtn.setName("modSurnameBtn");
		buttons.add(modSurnameBtn);
		
		//set constraints and add taxcode
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(taxLbl, c);
		c.gridx = 1;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(taxTF, c);
		
		
		//set constraints and add address
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(addressLbl, c);
		c.gridx = 1;
		c.gridy = 4;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(addressTF, c);
		c.gridx = 3;
		JButton modAddressBtn = new JButton();
		modAddressBtn.setIcon(modifyIcon);
		modAddressBtn.addActionListener(new EditListener(modAddressBtn));
		infoPanel.add(modAddressBtn, c);
		modAddressBtn.setName("modAddressBtn");
		buttons.add(modAddressBtn);
		
		
		//add phone labels, one for each phone number read directly from DB
		int offsetY = addPhoneLabels(c, infoPanel) + 1;
		//update offsets
		c.gridx = 0;
		c.gridy = offsetY;
		//add mails
		offsetY = addMailLabels(c, infoPanel) + 1;
		//update offsets
		c.gridx = 0;
		c.gridy = offsetY;
		offsetY = addFaxLabels(c, infoPanel) + 1;
		
		this.add(infoPanel, BorderLayout.CENTER);
		
		//set up buttons
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout());
		btnPanel.add(backBtn);
		backBtn.addActionListener(new BackListener());
		btnPanel.add(statsBtn);
		btnPanel.add(modifyBtn);
		btnPanel.add(deleteBtn);
		c.gridy = offsetY + 2;
		c.gridx = 1;
		infoPanel.add(btnPanel, c);
		
		//disable all buttons
		for(JButton curr : buttons) {
			curr.setVisible(false);
		}
		
		modifyBtn.addActionListener(new EnableButtonsListener());
		
	}
	
	private void editSimpleField(String sourceId) {
		String fieldName;
		switch(sourceId) {
		case "modNameBtn": fieldName = "name";
		break;
		case "modSurnameBtn": fieldName = "surname";
		break;
		case "modAddressBtn": fieldName = "address";
		break;
		default:
			if(sourceId.contains("Phone"))
				fieldName = "phone";
			else if(sourceId.contains("Mail"))
				fieldName = "mail";
			else if(sourceId.contains("Fax"))
				fieldName = "fax";
			else
				fieldName = "termosifone";
			break;
		}
			String newValue = null;
			if(fieldName.compareTo("address" )!= 0) {
				newValue = (String)JOptionPane.showInputDialog(null, "Insert new value for customer's " + fieldName + ":", "Edit data", JOptionPane.QUESTION_MESSAGE);
		}
			
		switch(fieldName) {
		case "name": updateValueInDB("c_name", newValue);
		break;
		case "surname": updateValueInDB("c_surname", newValue);
		break;
		case "address":
			AddressEditPanel panel = new AddressEditPanel();
			 int result = JOptionPane.showConfirmDialog(null, panel, 
		               "Please update the address of this customer", JOptionPane.OK_CANCEL_OPTION);
		      if (result == JOptionPane.OK_OPTION) {
		         System.out.println("Postcode: " + panel.zipTF.getText());
		         System.out.println("City: " + panel.cityTF.getText());
		         System.out.println("Street: " + panel.streetTF.getText());
		      }
		      break;
		}
	}
	
	
	private boolean updateValueInDB(String colName, String newVal) {
		Connection con = DatabaseConnection.getDBConnection().getConnection();
		Statement s;
		if(colName.compareTo("c_name") == 0 || colName.compareTo("c_surname") == 0){
			try {
				s = con.createStatement();
				String sql = "UPDATE customer " +
						"SET " + colName + " = " + "'" + newVal + "' " +
						"WHERE tax_code = " + "'" + customerPkey + "'";
				System.out.println("Query: " + sql);
				s.executeUpdate(sql);
				JOptionPane.showMessageDialog(null, "Value updated succesfully!");
				MainPanel.getMainPanel().swapPanel(new CustomerInfoPanel(customerPkey));

			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		else if(colName.compareTo("address") == 0) {
			
		}
		
		
		return true;
	}
	
	
	private void updateAddress(String newZIP, String newStreet, String newCity, int newCivicNumber, String newNation) {
		Connection con = DatabaseConnection.getDBConnection().getConnection();
		Statement s;
		if(newCivicNumber <= 0) {
			JOptionPane.showMessageDialog(null, "Civic number cannot be equal to 0 or less!");
			return;
		}
		try {
			s = con.createStatement();
			String sql = "UPDATE address " + 
						 "SET postcode = " + newZIP + ", street = '" + newStreet + "', city = '" + newCity + "', civic_number = " + newCivicNumber + ", nation = '" + newNation + "' " + 
						 "WHERE address_id = customer.address AND customer.tax_code = " + customerPkey;
			System.out.println(sql);
			s.executeUpdate(sql);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	////////////////////////////////////INNER CLASSES///////////////////////////////////////////////////
	
	private class AddressEditPanel extends JPanel{
		JTextField zipTF, streetTF, cityTF, civNumTF, nationTF;
		
		public AddressEditPanel() {
			this.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = gbc.LINE_START;
			zipTF = new JTextField(5);
			streetTF = new JTextField(10);
			cityTF = new JTextField(10);
			civNumTF = new JTextField(5);
			nationTF = new JTextField(10);
			
			
			gbc.gridx = 0;
			gbc.gridy = 0;
			this.add(new JLabel("Postcode: "), gbc);
			gbc.gridx = 1;
			this.add(zipTF, gbc);
			zipTF.setText("customer's zip");
			
			gbc.gridx = 0;
			gbc.gridy = 1;
			this.add(new JLabel("Street: "), gbc);
			gbc.gridx = 1;
			this.add(streetTF, gbc);
			streetTF.setText("customer's street");
			
			gbc.gridx = 0;
			gbc.gridy = 2;
			this.add(new JLabel("City: "), gbc);
			gbc.gridx = 1;
			this.add(cityTF, gbc);
			cityTF.setText("customer's city");
			
			gbc.gridx = 0;
			gbc.gridy = 3;
			this.add(new JLabel("Civic number: "), gbc);
			gbc.gridx = 1;
			this.add(civNumTF, gbc);
			civNumTF.setText("customer's civ num");
			
			gbc.gridx = 0;
			gbc.gridy = 4;
			this.add(new JLabel("Nation: "), gbc);
			gbc.gridx = 1;
			this.add(nationTF, gbc);
			nationTF.setText("customer's nation");
		}
	}
	
	
	
	
	
	///////////////////////////////////////////LISTENERS/////////////////////////////////////////////////
	
	
	private class EditListener implements ActionListener{
		
		protected JButton source;
		
		protected EditListener(JButton btn){
				this.source = btn;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for(JButton curr : buttons) {
				curr.setVisible(true);
			}
			System.out.println("Button " + source.getName() + " clicked.");
			editSimpleField(source.getName());
		}
		
	}
	
	private class BackListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			MainPanel.getMainPanel().swapPanel(new StakeholdersPanel());
			
		}
	}
	
	private abstract class MutualListener implements ActionListener{

		@Override
		public abstract void actionPerformed(ActionEvent arg0);
		
		protected void swapListener(Object o, ActionListener al) {
			AbstractButton source = (JButton) o;
			for(ActionListener curr : source.getActionListeners()) {
				source.removeActionListener(curr);
				System.out.println("Action listener removed.");
			}
			
			source.addActionListener(al);
			
		}
		
	}
	
	private class EnableButtonsListener extends MutualListener{
		
		protected JButton source;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			source = (JButton) e.getSource();
			for (JButton curr : buttons) {
				curr.setVisible(true);
				System.out.println("enabled");
			}
			
			swapListener(source, new DisableButtonsListener());
		}
		
	}
	
	private class DisableButtonsListener extends MutualListener{
		
		protected JButton source;

		@Override
		public void actionPerformed(ActionEvent e) {
			source = (JButton) e.getSource();
			for (JButton curr : buttons) {
				curr.setVisible(false);
			}
			
			swapListener(source, new EnableButtonsListener());
			
		}
			
		}
		
	}
