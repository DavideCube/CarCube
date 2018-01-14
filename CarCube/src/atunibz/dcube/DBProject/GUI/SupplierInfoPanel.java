package atunibz.dcube.DBProject.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import atunibz.dcube.DBProject.configuration.AppResources;





public class SupplierInfoPanel extends BackgroundedPanel {
	
	private String supplierPkey;
	private AddressEditPanel addressEditPanel;
	private IconLabel nameLbl, addressLbl, vatLbl;
	private int numberOfPhones, numberOfMails, numberOfFaxes;
	private JLabel nameTF, addressTF, vatTF;
	private JButton backBtn, statsBtn, addBtn, modifyBtn;
	private ArrayList<JButton> buttons;
	private JPanel infoPanel;
	private JScrollPane scrollPane;
	private ImageIcon modifyIcon;
	
	public SupplierInfoPanel(String supplierPkey) {
		this.supplierPkey = supplierPkey;
		this.addressEditPanel = new AddressEditPanel();
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
		nameLbl.setOpaque(false);
		nameTF = new JLabel();
		nameTF.setText(this.getSupplierName());
		//address
		addressLbl = new IconLabel("icons/contacts/address.png", "Address:", AppResources.DEFAULT_FONT, false);
		addressLbl.setOpaque(false);
		addressTF = new JLabel();
		addressTF.setText(this.getSupplierAddress());
		//taxcode
		vatLbl = new IconLabel("icons/contacts/tax.png","Taxcode:", AppResources.DEFAULT_FONT, false);
		vatLbl.setOpaque(false);
		vatTF = new JLabel();
		vatTF.setText(supplierPkey);
		//buttons
		backBtn = new JButton("Back");
		backBtn.setIcon(new ImageIcon("icons/back.png"));
		statsBtn = new JButton("Stats");
		statsBtn.setIcon(new ImageIcon("icons/graph.png"));
		addBtn = new JButton("Add contact");
		addBtn.setIcon(new ImageIcon("icons/plus.png"));
		modifyBtn = new JButton("Modify");
		try {
			modifyIcon = new ImageIcon(ImageIO.read(new File("icons/contacts/modify.png")));
			modifyBtn.setIcon(modifyIcon);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		buttons = new ArrayList<>();
		
		infoPanel = new JPanel();
		
		scrollPane = new JScrollPane(infoPanel);
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
			/*
			c.gridx = ++c.gridx;
			JButton modFaxBtn = new JButton();
			modFaxBtn.setIcon(modifyIcon);
			modFaxBtn.addActionListener(new EditListener(modFaxBtn));
			infoPanel.add(modFaxBtn, c);
			modFaxBtn.setName("modFaxBtn" + 0);
			buttons.add(modFaxBtn);
			*/
			c.gridy = ++c.gridy;
			c.gridx = 0;
			return c.gridy;
		}
		else {
		for(int i = 0; i < faxes.length; i++) {
			IconLabel faxLbl = new IconLabel("icons/contacts/fax.png","Fax #" + (i+1) +":", AppResources.DEFAULT_FONT, false);
			faxLbl.setOpaque(false);
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
			
		}
		
		return c.gridy;
		}
	}
	
	
	private void configLayout() {
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//add title
		
		JPanel shPanel = new JPanel();
		shPanel.setLayout(new BoxLayout(shPanel, BoxLayout.Y_AXIS));
		shPanel.add((Box.createRigidArea(new Dimension(0, 35))));
		// Panel containing the beautiful logo
		JPanel titlePanel = AppResources.carCubePanel();
		shPanel.add(titlePanel);
		shPanel.setOpaque(false);
		shPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		this.add(shPanel);
		
		scrollPane.setPreferredSize(new Dimension(100, 400));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//temporary border to spot jscrollpane dimension
		scrollPane.setBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.RED));
		this.add(scrollPane);
		
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
		c.anchor = GridBagConstraints.LINE_START;
		infoPanel.add(nameTF, c);
		JButton modNameBtn = new JButton();
		modNameBtn.setIcon(modifyIcon);
		modNameBtn.addActionListener(new EditListener(modNameBtn));
		c.gridx = 2;
		infoPanel.add(modNameBtn, c);
		modNameBtn.setName("modNameBtn");
		buttons.add(modNameBtn);
		
		//set constraints and add address
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(addressLbl, c);
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(addressTF, c);
		JButton modAddressBtn = new JButton();
		modAddressBtn.setIcon(modifyIcon);
		modAddressBtn.addActionListener(new EditListener(modAddressBtn));
		c.gridx = 2;
		infoPanel.add(modAddressBtn, c);
		modAddressBtn.setName("modAddressBtn");
		buttons.add(modAddressBtn);
		
		
		//set constraints and add vatcode
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(vatLbl, c);
		c.gridx = 1;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 0, 0);
		infoPanel.add(vatTF, c);
		
		//add labels, one for each phone number read directly from DB
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
		
		//this.add(infoPanel);
		
		//this.add((Box.createRigidArea(new Dimension(0, 30))));
	//	this.add((Box.createRigidArea(new Dimension(0, 30))));
		//disable all buttons
		for(JButton curr : buttons) {
			curr.setVisible(false);
		}
		
		modifyBtn.addActionListener(new EnableButtonsListener());
		
		addBtn.addActionListener(new AddContactListener());
		
		//set up buttons
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout());
		btnPanel.add(backBtn);
		backBtn.addActionListener(new BackListener());
		btnPanel.add(statsBtn);
		btnPanel.add(modifyBtn);
		btnPanel.add(addBtn);
		addBtn.addActionListener(new AddContactListener());
		this.add(btnPanel);
		this.add((Box.createRigidArea(new Dimension(0, 30))));
		this.add((Box.createRigidArea(new Dimension(0, 30))));
	}
	
	
	////////////////////////////////////INNER CLASSES///////////////////////////////////////////////////
	
	private class AddressEditPanel extends JPanel{
		JTextField zipTF, streetTF, cityTF, civNumTF, nationTF;
		
		public AddressEditPanel() {
			this.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.LINE_START;
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
			
			gbc.gridx = 0;
			gbc.gridy = 1;
			this.add(new JLabel("Street: "), gbc);
			gbc.gridx = 1;
			this.add(streetTF, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 2;
			this.add(new JLabel("City: "), gbc);
			gbc.gridx = 1;
			this.add(cityTF, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 3;
			this.add(new JLabel("Civic number: "), gbc);
			gbc.gridx = 1;
			this.add(civNumTF, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 4;
			this.add(new JLabel("Nation: "), gbc);
			gbc.gridx = 1;
			this.add(nationTF, gbc);
		}
		
		public boolean sanitizeInput() {
			
			if((!zipTF.getText().matches("\\d+")) || zipTF.getText().length() > 5) {
				zipTF.setText("");
				return false;
			}
			if(!streetTF.getText().matches("[A-Za-z0-9\\s]+") || streetTF.getText().length() > 30) {
				streetTF.setText("");
				return false;
			}
			if(!cityTF.getText().matches("[A-Za-z0-9\\s]+") || cityTF.getText().length() > 20) {
				cityTF.setText("");
				return false;
			}
			if(!civNumTF.getText().matches("\\d+") || civNumTF.getText().length() <= 0 || Integer.parseInt(civNumTF.getText()) <= 0) {
				civNumTF.setText("");
				return false;
			}
			if(!nationTF.getText().matches("[A-Za-z0-9\\s]+") || nationTF.getText().length() > 30) {
				nationTF.setText("");
				return false;
			}
			
			return true;
		}
	}
	
	
	private class AddContactPanel extends JPanel{
		protected JComboBox comboBox;
		protected JTextField inputTF;
		final String[] choices = {"Phone contact", "Mail contact", "Fax contact"};
		
		protected AddContactPanel() {
			comboBox = new JComboBox(choices);
			inputTF = new JTextField(10);
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(comboBox);
			this.add(Box.createRigidArea(new Dimension(100, 10)));
			this.add(inputTF);
		}
		
		protected String getContactType() {
			return (String) comboBox.getSelectedItem();
		}
		
		protected String getUserInput() {
			return (String)inputTF.getText();
		}
		
		protected boolean sanitizeInput() {
			String input = inputTF.getText();
			if(input.compareTo("") == 0)
				return false;
			if(getContactType().compareTo("Phone contact") == 0) {
				return (!input.matches("[A-Za-z]+") && (input.length() <= 25)) ? true : false;
			}
			if(getContactType().compareTo("Mail contact") == 0) {
				return (input.matches("^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+") && (input.length() <= 40)) ? true : false;
			}
			if(getContactType().compareTo("Fax contact") == 0) {
				return (!input.matches("[A-Za-z]+") && (input.length() <= 30)) ? true : false;
			}
			return false;
		}
		
		
	}
	

/////////////////////////////////////Listeners///////////////////////////////////////////////
	
	
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
			//editSimpleField(source.getName());
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
	
	
	
	
	
	private class AddContactListener implements ActionListener{
		private AddContactPanel addContactPanel;
		private Connection conn;
		private Statement stmnt;
		private String sql;
		
		public AddContactListener() {
			addContactPanel = new AddContactPanel();
			conn = DatabaseConnection.getDBConnection().getConnection();
			try {
				stmnt = conn.createStatement();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			 int answer = JOptionPane.showConfirmDialog(null, addContactPanel, 
		               "Add new contact", JOptionPane.OK_CANCEL_OPTION);
		      if (answer == JOptionPane.OK_OPTION) {
		    	 if(addContactPanel.sanitizeInput()) {

		    		 String choice = addContactPanel.getContactType();
		    		 switch(choice) {
		    		 case("Phone contact"):
		    			 //TODO
		    			 //sql = "INSERT INTO phone_contact VALUES ('" + addContactPanel.getUserInput() + "', NULL, '" + customerPkey + "')";
		    		 	 try {
							stmnt.executeUpdate(sql);
							JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Phone contact added!", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
				    		MainPanel.getMainPanel().swapPanel(new SupplierInfoPanel(supplierPkey));
				    		return;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    			 
		    		 break;
		    		 
		    		 case("Mail contact"):
		    			 
		    			 //TODO sql = "INSERT INTO mail_contact VALUES ('" + addContactPanel.getUserInput() + "', NULL, '" + supplierPkey + "')";
		    		 	 try {
							stmnt.executeUpdate(sql);
							JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Mail contact added!", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
				    		MainPanel.getMainPanel().swapPanel(new SupplierInfoPanel(supplierPkey));
				    		return;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    		 	 
		    		 case("Fax contact"):
		    			 //TODO
		    			 //sql = "INSERT INTO fax_contact VALUES ('" + addContactPanel.getUserInput() + "', NULL, '" + customerPkey + "')";
		    		 	 try {
							stmnt.executeUpdate(sql);
							JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Fax contact added!", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
				    		MainPanel.getMainPanel().swapPanel(new SupplierInfoPanel(supplierPkey));
				    		return;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    			 
		    		 break;
		    		 }
		    		 
		    		 
		    		 JOptionPane.showMessageDialog(MainPanel.getMainPanel(), " added!", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
		    		 MainPanel.getMainPanel().swapPanel(new SupplierInfoPanel(supplierPkey));
		    	 }
		    	 else {
		    		 JOptionPane.showMessageDialog(MainPanel.getMainPanel(), "Please insert a valid contact information.\nField cannot be left blank.", "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));
		    		 return;
		    	 }
		      }
		      else {
		    	  return;
		      }
		}
		
	}

}
