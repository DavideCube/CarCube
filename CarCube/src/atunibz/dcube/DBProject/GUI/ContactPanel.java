package atunibz.dcube.DBProject.GUI;

import java.awt.*;
import javax.swing.*;

public class ContactPanel extends BackgroundedPanel {
	
	private IconLabel nameLbl, surnameLbl, taxLbl, phoneLbl, mailLbl, faxLbl;
	private JLabel nameTF, surnameTF, taxTF, phoneTF, mailTF, faxTF;
	private JButton backBtn, statsBtn;
	private String[] results = {"Default", "Default", "Default", "Default", "Default", "Default"};
	
	public ContactPanel() {
		
		initComponents();
		applyLayout();
		getQueryResults();
		
	}
	
	private void initComponents() {
		//name
		nameLbl = new IconLabel("icons/contacts/tax.png","Name:", false);
		nameTF = new JLabel();
		//surname
		surnameLbl = new IconLabel("icons/contacts/tax.png", "Surname:", false);
		surnameTF = new JLabel();
		//taxcode
		taxLbl = new IconLabel("icons/contacts/tax.png","Taxcode:", false);
		taxTF = new JLabel();
		//phone
		phoneLbl = new IconLabel("icons/contacts/phone.png","Phone:", false);
		phoneTF = new JLabel();
		//mail
		mailLbl = new IconLabel("icons/contacts/mail.png","Mail:", false);
		mailTF = new JLabel();
		//fax
		faxLbl = new IconLabel("icons/contacts/fax.png","Fax:", false);
		faxTF = new JLabel();
		//go back
		backBtn = new JButton("Back");
		statsBtn = new JButton("Stats");
	}
	
	private void applyLayout() {
		
		GridBagLayout l = new GridBagLayout();
		this.setLayout(l);
		GridBagConstraints c = new GridBagConstraints();
		//set constraints and add name
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, -24, 0, 0);
		this.add(nameLbl, c);
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		c.anchor = GridBagConstraints.LINE_START;
		this.add(nameTF, c);
		
		//set constraints and add surname
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(surnameLbl, c);
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(surnameTF, c);
		
		//set constraints and add taxcode
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(taxLbl, c);
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(taxTF, c);
		
		//set constraints and add phone number
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(phoneLbl, c);
		c.gridx = 1;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(phoneTF, c);
		
		//set constraints and add phone number
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(phoneLbl, c);
		c.gridx = 1;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(phoneTF, c);
		
		//set constraints and add mail
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(mailLbl, c);
		c.gridx = 1;
		c.gridy = 4;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(mailTF, c);
		
		//set constraints and add fax
		c.gridx = 0;
		c.gridy = 5;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(faxLbl, c);
		c.gridx = 1;
		c.gridy = 5;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(faxTF, c);
		
	}
	
	private void getQueryResults() {
		nameTF.setText(results[0]);
		surnameTF.setText(results[1]);
		taxTF.setText(results[2]);
		phoneTF.setText(results[3]);
		mailTF.setText(results[4]);
		faxTF.setText(results[5]);
	}
	
	

}
