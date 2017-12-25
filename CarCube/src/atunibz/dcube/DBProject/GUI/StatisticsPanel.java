package atunibz.dcube.DBProject.GUI;

import java.awt.Dimension;
import java.sql.Connection;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import atunibz.dcube.DBProject.configuration.AppResources;

public class StatisticsPanel extends JPanel{
	private JPanel statPanel, titlePanel;
	private Connection conn;
	
	public StatisticsPanel () {
		conn = DatabaseConnection.getDBConnection().getConnection();
		statPanel = new JPanel();
		statPanel.setLayout(new BoxLayout(statPanel, BoxLayout.Y_AXIS));
		statPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		// Panel containing the beautiful logo
		titlePanel = AppResources.carCubePanel();
		statPanel.add(titlePanel);
		statPanel.setOpaque(false);
		
		
		
		
		add(statPanel);
	}
	
	
}
