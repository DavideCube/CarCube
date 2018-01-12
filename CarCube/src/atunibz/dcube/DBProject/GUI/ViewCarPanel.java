package atunibz.dcube.DBProject.GUI;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JPanel;

import atunibz.dcube.DBProject.configuration.AppResources;

public class ViewCarPanel extends JPanel {
	
	
	JPanel viewCarPanel;
	JPanel titlePanel;
	
	public ViewCarPanel(String id, boolean newCar) {

		//Main Panel
		viewCarPanel = new JPanel();
		viewCarPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		viewCarPanel.setOpaque(false);
		
		// Add title Panel
		titlePanel = AppResources.carCubePanel();
		viewCarPanel.add(titlePanel);
		

		viewCarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		
		//End of the panel
		add(viewCarPanel);
	}
}
