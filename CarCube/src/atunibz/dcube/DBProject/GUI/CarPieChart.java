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

public class CarPieChart extends ChartJPanel{
	
	
	
	/*Query to get make of the car and number of units sold
	 * 
	* select make, count(all_cars.sold)
	  from (select make, sold from new_car where sold = 1 union select make, sold from used_car where sold = 1) as all_cars
	  group by(make)
	  
	 */
	//for every make of cars, returns the make along with the number of units sold of that make
	private final String unitsSoldPerMake = "select make, count(all_cars.sold) as quantity\n" + 
			"				  from (select make, sold from new_car where sold = 1 union all select make, sold from used_car where sold = 1) as all_cars \n" + 
			"				  group by(make)\n" + 
			"                  order by quantity desc";	//returns total number of cars sold so far
	private final String totalUnitSold = "select count(*) from (select make, sold from new_car where sold = 1 union all select make, sold from used_car where sold = 1) as all_cars";
	//private DefaultPieDataset pieDataset;
	
	public CarPieChart() {
		dataset = new DefaultPieDataset();
		//pieDataset = (DefaultPieDataset)pieDataset;
		this.populateDataset();
		chart = ChartFactory.createPieChart3D("Most sold auto", (DefaultPieDataset)dataset, true, true, false);
		chart.setBackgroundPaint(new Color(0,0,0, 0));
		chartPanel = new ChartPanel(chart);
		chartPanel.setOpaque(false);
		chartPanel.setPreferredSize(new Dimension(600, 400));
		
		PiePlot3D plot = (PiePlot3D)chart.getPlot();
		plot.setForegroundAlpha(0.5f);
		
		this.setOpaque(false);
		this.add(chartPanel);
				
				
	}
	
	
	public void populateDataset() {
		DefaultPieDataset dpd = (DefaultPieDataset)dataset;
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
				dpd.setValue(rs.getString(1), new Double((rs.getInt(2)*100)/totalSale));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
	}
	
	
}