package atunibz.dcube.DBProject.GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.jfree.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class SalesLineChart extends JPanel{
	
	private JFreeChart chart;
	private ChartPanel chartPanel;
	private DefaultCategoryDataset dataset;
	
	private Connection conn;
	private Statement stmnt;
	
	public SalesLineChart() {
		this.establishConnection();
		this.populateDataset();
		chart = ChartFactory.createLineChart("Revenues", "Months", "Net income ($)", dataset, PlotOrientation.VERTICAL, true, true, false);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(600, 400));
		this.add(chartPanel);
				
				
	}
	
	
	private void populateDataset() {
		dataset = new DefaultCategoryDataset();
		
		dataset.addValue(10000, "incomes", "january");
		dataset.addValue(8123, "incomes", "february");
		dataset.addValue(12450, "incomes", "march");
		dataset.addValue(6140, "incomes", "april");
		dataset.addValue(4000, "incomes", "may");
		dataset.addValue(9100, "incomes", "juny");
		dataset.addValue(6405, "incomes", "july");
	}
	
	
	
	
	//////////////////////////DATABASE INTERACTION////////////////////////////////////////
	private boolean establishConnection() {
		conn = DatabaseConnection.getDBConnection().getConnection();
		try {
			stmnt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	
}
