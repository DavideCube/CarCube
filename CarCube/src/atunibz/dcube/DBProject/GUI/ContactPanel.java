package atunibz.dcube.DBProject.GUI;

import javax.swing.*;

public class ContactPanel extends BackgroundedPanel {
	
	private IconLabel nameLbl, surnameLbl, taxLbl, phoneLbl, mailLbl, faxLbl;
	private JTextField nameTF, surnameTF, taxTF, phoneTF, mailTF, faxTF;
	private JButton backBtn, statsBtn;
	
	public ContactPanel() {
		
	}
	
	private void initComponents() {
		//name
		nameLbl = new IconLabel(null,"Name:", false);
		nameTF = new JTextField(10);
		//surname
		surnameLbl = new IconLabel(null, "Surname:", false);
		surnameTF = new JTextField(15);
		//taxcode
		taxLbl = new IconLabel(null,"Taxcode:", false);
		taxTF = new JTextField(16);
		//phone
		phoneLbl = new IconLabel(null,"Phone:", false);
		phoneTF = new JTextField(15);
		//mail
		mailLbl = new IconLabel(null,"Mail:", false);
		mailTF = new JTextField(20);
		//fax
		faxLbl = new IconLabel(null,"Fax:", false);
		faxTF = new JTextField(15);
		//go back
		backBtn = new JButton("Back");
		statsBtn = new JButton();
		
		
	}
	
	

}
