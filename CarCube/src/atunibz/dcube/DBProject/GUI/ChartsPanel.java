package atunibz.dcube.DBProject.GUI;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import atunibz.dcube.DBProject.configuration.AppResources;

// panel for charts
public class ChartsPanel extends JPanel{
	JPanel chartPanel, titlePanel;
	
	public ChartsPanel () {
		chartPanel = new JPanel();
		// create panel which will be the general container of all others GUI objects
		chartPanel = new JPanel();
		chartPanel.setLayout(new BoxLayout(chartPanel, BoxLayout.Y_AXIS));
		chartPanel.add((Box.createRigidArea(new Dimension(0, 30))));

		// Panel containing the beautiful logo
		titlePanel = AppResources.carCubePanel();
		chartPanel.add(titlePanel);
		chartPanel.setOpaque(false);
		chartPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		
		// JPanel matrioska
		JPanel container1 = new JPanel();
		container1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		container1.setOpaque(false);
		JPanel chart1 = new CarBarChart();
		container1.add(chart1);
		
		
		chartPanel.add(container1);
		add(chartPanel);
	}
	
}
