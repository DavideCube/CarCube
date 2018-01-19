package atunibz.dcube.DBProject.GUI;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class CarBarChart extends ChartJPanel {
	 
	//for every make of cars, returns the make along with the number of units sold of that make
	private final String unitsSoldPerMake = "select make, count(all_cars.sold) as quantity\n" + 
			"				  from (select make, sold from new_car where sold = 1 union all select make, sold from used_car where sold = 1) as all_cars \n" + 
			"				  group by(make)\n" + 
			"                  order by quantity desc";
	private final String newUnitsSoldPerMake = "select make, count(cars_sold.sold) as quantity\n" + 
			" from (select make, sold from new_car where sold = 1) as cars_sold group by(make) order by quantity desc";
	private final String usedUnitsSoldPerMake = "select make, count(cars_sold.sold) as quantity\n" + 
			" from (select make, sold from used_car where sold = 1) as cars_sold group by(make) order by quantity desc";
	private String[] soldCarMakes;
	
	
	public CarBarChart() {
		super();
		dataset = new DefaultCategoryDataset();
		this.populateDataset();
		chart = ChartFactory.createBarChart("Number of units sold", "Make", "Units sold all-time", (DefaultCategoryDataset)dataset);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(600, 400));
		
		this.add(chartPanel);
					
	}
	
	public void getCarMakes() {
		soldCarMakes = new String[5];
		int index = 0;
		try {
			ResultSet rs = stmnt.executeQuery(unitsSoldPerMake);
			while(rs.next() && index < 5) {
				soldCarMakes[index] = rs.getString(1);
				index++;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	

	@Override
	public void populateDataset() {
		getCarMakes();
		DefaultCategoryDataset dcd = (DefaultCategoryDataset)dataset;
		int columnIndex = 3;
		for(int i = 0; i < soldCarMakes.length; i++) {
			dcd.setValue(1, soldCarMakes[i], Integer.valueOf(columnIndex));
			columnIndex += 3;
		}
		
	}
	
	
	
	
	

}
