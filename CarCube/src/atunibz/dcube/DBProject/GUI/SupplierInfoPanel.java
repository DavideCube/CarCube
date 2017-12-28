package atunibz.dcube.DBProject.GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import atunibz.dcube.DBProject.configuration.AppResources;

public class SupplierInfoPanel extends BackgroundedPanel {
	
	private String supplierPkey;
	private IconLabel nameLbl, addressLbl, vatLbl;
	private int numberOfPhones, numberOfMails, numberOfFaxes;
	private JLabel nameTF, addressTF, vatTF;
	private JButton backBtn, statsBtn, deleteBtn, modifyBtn;
	public SupplierInfoPanel(String supplierPkey) {
		this.supplierPkey = supplierPkey;
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
			queryPhone = "select count(*) from phone_contact where owner_supplier = " + "'" + supplierPkey + "'";
			queryMail = "select count(*) from mail_contact where owner_supplier = " + "'" + supplierPkey + "'";
			queryFax = "select count(*) from fax_contact where owner_supplier = " + "'" + supplierPkey + "'";
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
			System.out.println("The supplier has " + numberOfPhones + " phone numbers, " + numberOfMails + " mail addresses and " + numberOfFaxes + " fax numbers.");
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
			ResultSet rs = stmnt.executeQuery("select phone_number from phone_contact where owner_supplier = '" + supplierPkey + "'");
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
			ResultSet rs = stmnt.executeQuery("select mail from mail_contact where owner_supplier = '" + supplierPkey + "'");
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
			ResultSet rs = stmnt.executeQuery("select fax from fax_contact where owner_supplier = '" + supplierPkey + "'");
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
	
	private String getSupplierName() {
		Connection conn = DatabaseConnection.getDBConnection().getConnection();
		String name = null;
		try {
			Statement stmnt = conn.createStatement();
			ResultSet rs = stmnt.executeQuery("select name from supplier where vat = '" + supplierPkey + "'");
			while(rs.next()) {
				name = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}
	
	private String getSupplierAddress() {
		Connection conn = DatabaseConnection.getDBConnection().getConnection();
		String address = null;
		try {
			Statement stmnt = conn.createStatement();
			ResultSet rs = stmnt.executeQuery("select a.street, a.civic_number, a.city, a.postcode, a.nation from address a, supplier s where a.address_id = s.address and s.vat = '" + supplierPkey + "'");
			while(rs.next()) {
				address = rs.getString(1) + " Street n." + rs.getInt(2) + " , " + rs.getString(3) + " - " + rs.getString(4) + " , " + rs.getString(5);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return address;
	}
	
	
	private void initComponents() {
		//name
		nameLbl = new IconLabel("icons/contacts/tax.png","Name:", AppResources.DEFAULT_FONT, false);
		nameTF = new JLabel();
		nameTF.setText(this.getSupplierName());
		//surname
		addressLbl = new IconLabel("icons/contacts/address.png", "Address:", AppResources.DEFAULT_FONT, false);
		addressTF = new JLabel();
		addressTF.setText(this.getSupplierAddress());
		//taxcode
		vatLbl = new IconLabel("icons/contacts/tax.png","Taxcode:", AppResources.DEFAULT_FONT, false);
		vatTF = new JLabel();
		vatTF.setText(supplierPkey);
		//buttons
		backBtn = new JButton("Back");
		statsBtn = new JButton("Stats");
		deleteBtn = new JButton("Delete");
		modifyBtn = new JButton("Modify");
	}
	
	private int addPhoneLabels(GridBagConstraints c, JPanel infoPanel) {
		System.out.println("Constraints: " + c.gridx + ", " + c.gridy);
		int offsetX = 0, offsetY = ++c.gridy;
		String[] phones = getPhones();
		c.gridx = offsetX;
		c.gridy = offsetY;
		for(int i = 0; i < phones.length; i++) {
			IconLabel phoneLbl = new IconLabel("icons/contacts/phone.png","Phone #" + (i+1) +":", AppResources.DEFAULT_FONT, false);
			JLabel phoneTF = new JLabel();
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
			JLabel mailTF = new JLabel();
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
			JLabel faxTF = new JLabel();
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
	
	
	private void configLayout() {
		
		this.setLayout(new BorderLayout());
		this.add(AppResources.carCubePanel(), BorderLayout.NORTH);
		JPanel infoPanel = new JPanel();
		
		GridBagLayout l = new GridBagLayout();
		infoPanel.setLayout(l);
		GridBagConstraints c = new GridBagConstraints();
		//c.fill = GridBagConstraints.HORIZONTAL;
		//set constraints and add name
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, -40, 0, 0);
		infoPanel.add(nameLbl, c);
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		c.anchor = GridBagConstraints.LINE_START;
		infoPanel.add(nameTF, c);
		
		//set constraints and add surname
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(addressLbl, c);
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(addressTF, c);
		
		//set constraints and add vatcode
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(vatLbl, c);
		c.gridx = 1;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(vatTF, c);
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
		
		this.add(btnPanel, BorderLayout.SOUTH);
		
		
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
			
			String pkey = vatTF.getText();
			String query = "DELETE from customer WHERE tax_code = ?";
			PreparedStatement stmt;
			try {
				stmt = DatabaseConnection.getDBConnection().getConnection().prepareStatement(query);
				stmt.setString(1, pkey);
				stmt.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			//MainPanel.getMainPanel().swapPanel(new StakeholdersPanel());
			
		}
		
	}

}
