package atunibz.dcube.DBProject.GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.jfree.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

public class CarPieChart extends JPanel{
	
	
	
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
	//for every make of cars, returns the make along with the number of units sold of that make
	private final String unitsSoldPerMake = "select make, count(all_cars.sold) as quantity\n" + 
			"				  from (select make, sold from new_car where sold = 1 union all select make, sold from used_car where sold = 1) as all_cars \n" + 
			"				  group by(make)\n" + 
			"                  order by quantity desc";	//returns total number of cars sold so far
	private final String totalUnitSold = "select count(*) from (select make, sold from new_car where sold = 1 union all select make, sold from used_car where sold = 1) as all_cars";
	public CarPieChart() {
		this.establishConnection();
		this.populateDataset();
		chart = ChartFactory.createPieChart3D("Most sold auto", dataset, true, true, false);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(600, 400));
		
		PiePlot3D plot = (PiePlot3D)chart.getPlot();
		plot.setForegroundAlpha(0.5f);
		
		this.add(chartPanel);
				
				
	}
	
	
	private void populateDataset() {
		dataset = new DefaultPieDataset();
		/*
		dataset.setValue("BMW", new Double(20));
		dataset.setValue("Fiat", new Double(10));
		dataset.setValue("Mercedes", new Double(35));
		dataset.setValue("Tesla", new Double(15));
		dataset.setValue("Maserati", new Double(5));
		dataset.setValue("Ferrari", new Double(3));
		dataset.setValue("Lamborghini", new Double(20));
		*/
		try {
			int totalSale = 0;
			ResultSet rs1 = stmnt.executeQuery(totalUnitSold);
			int i = 0;
			while(rs1.next() && i <= 5) {
				totalSale = rs1.getInt(1);
				i++;
			}
			System.out.println("Total sales: " + totalSale);
			ResultSet rs = stmnt.executeQuery(unitsSoldPerMake);
			while(rs.next()) {
				dataset.setValue(rs.getString(1), new Double((rs.getInt(2)*100)/totalSale));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
	}
	
	
	
	
	//////////////////////////////////DATABASE INTERACTION////////////////////////////////////////
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