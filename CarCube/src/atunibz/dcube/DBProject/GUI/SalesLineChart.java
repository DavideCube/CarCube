package atunibz.dcube.DBProject.GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.jfree.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class SalesLineChart extends JPanel{
	
	private JFreeChart chart;
	private ChartPanel chartPanel;
	private DefaultCategoryDataset dataset;
	private final String carSalesInfo = "select sell_price, sell_date from new_car c, new_sell s where sold = 1 and c.car_id = s.car_id \n" + 
			"\n" + 
			"union all \n" + 
			"\n" + 
			"select sell_price, sell_date from used_car d, used_sell u where sold = 1 and d.immatriculation = u.immatriculation";
	private double[] salesTable;
	private Map<Integer, Double> salesMap;
	
	private Connection conn;
	private Statement stmnt;
	
	public SalesLineChart() {
		this.establishConnection();
		this.getSalesInfo();
		//this.populateDataset();
		chart = ChartFactory.createLineChart("Revenues", "Months", "Net income (€)", dataset, PlotOrientation.VERTICAL, true, true, false);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(600, 400));
		this.add(chartPanel);
				
				
	}
	
	private void getSalesInfo() {
		salesTable = new double[11];
		salesMap = new HashMap<>();
		Calendar cal = Calendar.getInstance();
		double price = 0.0;
		int month = 0;
		try {
			ResultSet rs = stmnt.executeQuery(carSalesInfo);
			while(rs.next()) {
				cal.setTime(rs.getDate(2));
				month = cal.get(Calendar.MONTH);
				price = rs.getDouble(1);
				//no car sold that month, add month along with the price
				if(!salesMap.containsKey(month)) {
					salesMap.put(month, price);
				}
				//at least a car has already been sold that month, get the price, increase it by the new value and add it again
				else {
					price += salesMap.get(month);
					salesMap.put(month, price);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Map.Entry<Integer, Double> entry : salesMap.entrySet()) {
			System.out.println(entry.getKey() + " - " + entry.getValue().toString());
		}
		
		
		
		
	}
	
	
	private void populateDataset() {
		dataset = new DefaultCategoryDataset();
		for(int i = 0; i < 12; i++) {
			if(!salesMap.containsKey(i))
				salesMap.put(i, 0.0);
			dataset.addValue(salesMap.get(i), "incomes", "month");
		}/*
		dataset.addValue(10000, "incomes", "january");
		dataset.addValue(8123, "incomes", "february");
		dataset.addValue(12450, "incomes", "march");
		dataset.addValue(6140, "incomes", "april");
		dataset.addValue(4000, "incomes", "may");
		dataset.addValue(9100, "incomes", "juny");
		dataset.addValue(6405, "incomes", "july");*/
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
