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
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.*;

import atunibz.dcube.DBProject.configuration.AppResources;

public class CustomerInfoPanel extends BackgroundedPanel {
	
	private String customerPkey;
	private IconLabel nameLbl, surnameLbl, taxLbl, addressLbl;
	private int numberOfPhones, numberOfMails, numberOfFaxes;
	private JLabel nameTF, surnameTF, taxTF, addressTF;
	private JTextField modNameTF, modSurnameTF, modAddressTF;
	private JButton backBtn, statsBtn, deleteBtn, modifyBtn;
	private ImageIcon modifyIcon;
	private JButton modNameBtn, modSurnameBtn, modAddressBtn;
	private JPanel infoPanel;
	
	public CustomerInfoPanel(String customerPkey) {
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
		
		modNameBtn = new JButton();
		modNameBtn.setIcon(modifyIcon);
		modSurnameBtn = new JButton();
		modSurnameBtn.setIcon(modifyIcon);
		modAddressBtn = new JButton();
		modAddressBtn.setIcon(modifyIcon);
		
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
		modNameBtn.addActionListener(new ModifyListener());
		infoPanel.add(modNameBtn, c);
		
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
		modSurnameBtn.addActionListener(new ModifyListener());
		infoPanel.add(modSurnameBtn, c);
		
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
		modAddressBtn.addActionListener(new ModifyListener());
		infoPanel.add(modAddressBtn, c);
		
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
		deleteBtn.addActionListener(new DeleteListener());
		c.gridy = offsetY + 2;
		infoPanel.add(btnPanel, c);
		
		
	}
	
	private void addModBtn(GridBagConstraints c) {
		
	}
	
	
	private class ModifyListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	private class BackListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			MainPanel.getMainPanel().swapPanel(new StakeholdersPanel());
			
		}
	}
	
	
	private class DeleteListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			activeModifyMode();
			
		}
		
	}
	

}
