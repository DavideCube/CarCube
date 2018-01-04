package atunibz.dcube.DBProject.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import atunibz.dcube.DBProject.configuration.AppResources;
import atunibz.dcube.DBProject.configuration.AppResources;

public class LogoPanel extends JPanel {
	private JPanel logoPanel, titlePanel, controlPanel;
	private JButton manage, car;
	
	
	public LogoPanel () {
		logoPanel = new JPanel ();
		logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
		logoPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		// Panel containing the beautiful logo
		titlePanel = AppResources.carCubePanel();
		logoPanel.add(titlePanel);
		logoPanel.setOpaque(false);
		
		// control panel
		logoPanel.add((Box.createRigidArea(new Dimension(0, 150))));
		
		controlPanel = new JPanel();
		controlPanel.setOpaque(false);
		controlPanel.setPreferredSize(new Dimension(1000,150));
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		manage = new JButton ();
		manage.setIcon(new ImageIcon ("icons/management.png"));
		manage.setText("Management");
		manage.setFont(AppResources.BUTTON_FONT);
		manage.addActionListener(new StakeholdersListener());
		car = new JButton ();
		car.setIcon(new ImageIcon ("icons/car-2.png"));
		car.setText("Search Car");
		car.addActionListener(new SearchCarListener());
		car.setFont(AppResources.BUTTON_FONT);
		car.setHorizontalTextPosition(SwingConstants.LEFT);
		controlPanel.add((Box.createRigidArea(new Dimension(50, 0))));
		controlPanel.add(manage);
		controlPanel.add((Box.createRigidArea(new Dimension(150, 0))));
		controlPanel.add(car);
		controlPanel.add((Box.createRigidArea(new Dimension(50, 0))));
		logoPanel.add(controlPanel);
		
		add (logoPanel);
	}
	
	// listener class to swap to the stakeholder panel
	private class StakeholdersListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			MainPanel.getMainPanel().swapPanel(new StakeholdersPanel());
		}
	}
	
	// listener class to swap to the search car panel
	private class SearchCarListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			MainPanel.getMainPanel().swapPanel(new SearchCarPanel());
		}
	}

}
