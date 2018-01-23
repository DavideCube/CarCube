package atunibz.dcube.DBProject.GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import atunibz.dcube.DBProject.configuration.AppResources;

// panel for charts
public class ChartsPanel extends JPanel{
	JPanel chartPanel, titlePanel;
	JButton back;
	
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
		
		// First chart
		JPanel container1 = new JPanel();
		container1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		container1.setOpaque(false);
		JPanel chart1 = new CarBarChart();
		chart1.setOpaque(false);
		container1.add(chart1);
		
		// Second chart
		JPanel container2 = new JPanel();
		container2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		container2.setOpaque(false);
		JPanel chart2 = new CarPieChart();
		chart2.setOpaque(false);
		container2.add(chart2);
		
		// Third chart
		JPanel container3 = new JPanel();
		container3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		container3.setOpaque(false);
		JPanel chart3 = new GeneralCriteriaChart();
		chart3.setOpaque(false);
		container3.add(chart3);
		
		// Third chart
		JPanel container4 = new JPanel();
		container4.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		container4.setOpaque(false);
		JPanel chart4 = new SalesLineChart();
		chart4.setOpaque(false);
		container4.add(chart4);
		
		// buttonPanel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		back = AppResources.iconButton("Go back     ", "icons/back.png");
		back.addActionListener(new BackListener());
		buttonPanel.add(back);
				
		
		
		chartPanel.add(container1);
		chartPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		chartPanel.add(container2);
		chartPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		chartPanel.add(container3);
		chartPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		chartPanel.add(container4);
		chartPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		chartPanel.add(buttonPanel);
		add(chartPanel);
	}
	
	// listener for back button
	private class BackListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			MainPanel.getMainPanel().swapPanel(new StatisticsPanel());
		}
	}
	
}
