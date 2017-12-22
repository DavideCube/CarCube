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

import atunibz.dcube.DBProject.configuration.Resources;

public class LogoPanel extends JPanel {
	private JPanel logoPanel, titlePanel, controlPanel;
	private JButton manage, car;
	
	
	public LogoPanel () {
		logoPanel = new JPanel ();
		logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
		logoPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		// Panel containing the beautiful logo
		titlePanel = Resources.carCubePanel();
		logoPanel.add(titlePanel);
		
		// control panel
		logoPanel.add((Box.createRigidArea(new Dimension(0, 150))));
		
		controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(1000,150));
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		manage = new JButton ();
		manage.setIcon(new ImageIcon ("icons/management.png"));
		manage.setText("Management");
		Resources.changeFont(manage, Font.BOLD, 30);
		
		car = new JButton ();
		car.setIcon(new ImageIcon ("icons/car-2.png"));
		car.setText("Search Car");
		Resources.changeFont(car, Font.BOLD, 30);
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
