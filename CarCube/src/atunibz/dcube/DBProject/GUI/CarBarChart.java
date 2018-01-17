package atunibz.dcube.DBProject.GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.jfree.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

public class CarBarChart extends JPanel{
	
	
	
	/*Query to get make of the car and number of units sold
	 * 
	* select make, count(all_cars.sold)
	  from (select make, sold from new_car where sold = 1 union select make, sold from used_car where sold = 1) as all_cars
	  group by(make)
	  
	 */
	
	
	
	private JFreeChart chart;
	private ChartPanel chartPanel;
	private DefaultPieDataset dataset;
	
	private Connection conn;
	private Statement stmnt;
	
	public CarBarChart() {
		this.establishConnection();
		this.populateDataset();
		chart = ChartFactory.createPieChart("Most sold auto", dataset, true, true, false);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(600, 400));
		this.add(chartPanel);
				
				
	}
	
	
	private void populateDataset() {
		dataset = new DefaultPieDataset();
		dataset.setValue("BMW", new Double(20));
		dataset.setValue("Fiat", new Double(10));
		dataset.setValue("Mercedes", new Double(35));
		dataset.setValue("Tesla", new Double(15));
		dataset.setValue("Maserati", new Double(5));
		dataset.setValue("Ferrari", new Double(3));
		dataset.setValue("Lamborghini", new Double(20));
		
		
		
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