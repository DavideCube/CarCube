package atunibz.dcube.DBProject.GUI;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LogoPanel extends JPanel {
	private JLabel logo;
	private JPanel logoPanel, titlePanel;
	
	
	public LogoPanel () {
		logoPanel = new JPanel ();
		titlePanel = new JPanel();
		logo = new JLabel();
		logo.setIcon(new ImageIcon ("logo.png"));
		titlePanel.add(logo);
		logoPanel.add(titlePanel);
		
		
		
		add (logoPanel);
	}

}
