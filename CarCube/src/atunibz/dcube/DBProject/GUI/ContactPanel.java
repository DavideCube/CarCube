package atunibz.dcube.DBProject.GUI;

import javax.swing.*;

public class ContactPanel extends BackgroundedPanel {
	
	private JLabel nameLbl, surnameLbl, taxLbl, phoneLbl, mailLbl, faxLbl;
	private JTextField nameTF, surnameTF, taxTF, phoneTF, mailTF, faxTF;
	private JButton backBtn, statsBtn;
	
	public ContactPanel() {
		
	}
	
	private void initComponents() {
		//name
		nameLbl = new JLabel("Name:");
		nameTF = new JTextField(10);
		//surname
		surnameLbl = new JLabel("Surname:");
		surnameTF = new JTextField(15);
		//taxcode
		taxLbl = new JLabel("Taxcode:");
		taxTF = new JTextField(16);
		//phone
		phoneLbl = new JLabel("Phone:");
		phoneTF = new JTextField(15);
		
		
	}
	
	

}
