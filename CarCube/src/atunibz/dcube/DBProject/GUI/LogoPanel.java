package atunibz.dcube.DBProject.GUI;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LogoPanel extends JPanel {
	private JLabel logo;
	private JPanel logoPanel, titlePanel, controlPanel;
	private JButton manage, car;
	
	
	public LogoPanel () {
		logoPanel = new JPanel ();
		logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
		// Panel containing the beautiful logo
		titlePanel = new JPanel();
		logo = new JLabel();
		logo.setIcon(new ImageIcon ("logo.png"));
		titlePanel.add(logo);
		logoPanel.add(titlePanel);
		
		// control panel
		logoPanel.add((Box.createRigidArea(new Dimension(0, 150))));
		
		controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(1000,150));
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		manage = new JButton ();
		manage.setIcon(new ImageIcon ("icons/management.png"));
		manage.setText("Management");
		manage.setFont((new Font ("Comic Sans MS", Font.BOLD, 30)));
		
		car = new JButton ();
		car.setIcon(new ImageIcon ("icons/car-2.png"));
		car.setText("Search Car");
		car.setFont((new Font ("Comic Sans MS", Font.BOLD, 30)));
		car.setHorizontalTextPosition(SwingConstants.LEFT);
		controlPanel.add((Box.createRigidArea(new Dimension(50, 0))));
		controlPanel.add(manage);
		controlPanel.add((Box.createRigidArea(new Dimension(150, 0))));
		controlPanel.add(car);
		controlPanel.add((Box.createRigidArea(new Dimension(50, 0))));
		controlPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		logoPanel.add(controlPanel);
		
		
		
		
		add (logoPanel);
	}

}
