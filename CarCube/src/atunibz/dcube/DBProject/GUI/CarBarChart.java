package atunibz.dcube.DBProject.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
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
	
	public CarBarChart() {
		super();
		dataset = new DefaultCategoryDataset();
		this.populateDataset();
		createAndFormatChart();
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(600, 400));
		chartPanel.setOpaque(false);
		this.setOpaque(false);
		this.add(chartPanel);
					
	}
	
	private void createAndFormatChart() {
		chart = ChartFactory.createBarChart("Number of units sold", "Make", "Units sold all-time", (DefaultCategoryDataset)dataset, PlotOrientation.VERTICAL, true, true , false);
		chart.setBackgroundPaint(Color.WHITE);
		final CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.GRAY);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setRangeGridlinePaint(Color.BLACK);
		// set the range axis to display integers only
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // disable bar outlines
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        // set up gradient paints for series
        final GradientPaint gp0 = new GradientPaint(
            0.0f, 0.0f, Color.blue, 
            0.0f, 0.0f, Color.white
        );
        final GradientPaint gp1 = new GradientPaint(
            0.0f, 0.0f, Color.red, 
            0.0f, 0.0f, Color.white
        );
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        //rotate the label che magari fa figo
        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
	}
	

	@Override
	public void populateDataset() {

		DefaultCategoryDataset dcd = (DefaultCategoryDataset)dataset;
		try {
			ResultSet newCarsSet = stmnt.executeQuery(newUnitsSoldPerMake);
			while(newCarsSet.next()) {
				dcd.setValue((double)newCarsSet.getInt(2), "NEW", newCarsSet.getString(1));
			}
		Statement stmnt2 = conn.createStatement();
		ResultSet usedCarSet = stmnt2.executeQuery(usedUnitsSoldPerMake);
		while(usedCarSet.next()) {
			dcd.setValue((double)usedCarSet.getInt(2), "USED", usedCarSet.getString(1));
		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	

}
