package atunibz.dcube.DBProject.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;


import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;


public class SalesLineChart extends ChartJPanel{
	//TODO set jcombobox options and fetch data
	private Calendar calendar = Calendar.getInstance();
	private Map<String, Double> salesMap;
	private int year = 2017;
	private JComboBox<String> criteriaBox;
	
	
	public SalesLineChart() {
		this.getSalesInfo();
		this.populateDataset();
		chart = ChartFactory.createLineChart("Revenues", "Months", "Net income (€)", (DefaultCategoryDataset)dataset, PlotOrientation.VERTICAL, true, true, false);
		chart.setBackgroundPaint(new Color (0,0,0,0));
		final CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.GRAY);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setRangeGridlinePaint(Color.BLACK);
		chartPanel = new ChartPanel(chart);
		chartPanel.setOpaque(false);
		chartPanel.setPreferredSize(new Dimension(600, 400));
		criteriaBox = new JComboBox<String>(setYearsRange());
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.criteriaBox.addActionListener(new ChoiceListener());
		this.add(criteriaBox);
		this.setOpaque(false);
		this.add(chartPanel);		
	}
	
	private String[] setYearsRange() {
		int upperBound = 0, lowerBound = 0;
		String[] yearsRange;
		try {
			Statement stmnt = conn.createStatement();
			ResultSet rs = stmnt.executeQuery("select max(sell_date)\n" + 
					"from\n" + 
					"(select sell_date from used_sell\n" + 
					"union distinct\n" + 
					"select sell_date from new_sell) as all_sells");
			while(rs.next()) {
				calendar.setTime(rs.getDate(1));
				upperBound = calendar.get(Calendar.YEAR);
			}
			Statement stmnt2 = conn.createStatement();
			ResultSet rs2 = stmnt2.executeQuery("select min(sell_date)\n" + 
					"from\n" + 
					"(select sell_date from used_sell\n" + 
					"union distinct\n" + 
					"select sell_date from new_sell) as all_sells");
			while(rs2.next()) {
				calendar.setTime(rs2.getDate(1));
				lowerBound = calendar.get(Calendar.YEAR);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		yearsRange = new String[(upperBound-lowerBound) + 1];
		int pos = 0;
		for(int i = lowerBound; i <= upperBound; i++) { 
			yearsRange[pos] = "" + i;
			pos++;
		}
		
		return yearsRange;
			
	}
	
	//evaluates the condition required to get the interval in the whole year
	private String evalTimeInterval(int year) {
		String interval = " and sell_date < '" + (year + 1) + "-01-01' and sell_date > '" + (year-1) + "-12-31'";
		return interval;
	}
	
	private String evalQuery(int year) {
		return "select sell_price, sell_date from new_car c, new_sell s where sold = 1 and c.car_id = s.car_id " + evalTimeInterval(year) + 
				"\n" + 
				"union all \n" + 
				"\n" + 
				"select sell_price, sell_date from used_car d, used_sell u where sold = 1 and d.immatriculation = u.immatriculation" + evalTimeInterval(year);
	}
	
	
	private String dateToMonth(int month) {
		String ret = "";
		switch(month) {
		case 0: ret = "January";
		break;
		case 1: ret = "February";
		break;
		case 2: ret = "March";
		break;
		case 3: ret = "April";
		break;
		case 4: ret = "May";
		break;
		case 5: ret = "June";
		break;
		case 6:  ret = "July";
		break;
		case 7: ret = "August";
		break;
		case 8: ret = "September";
		break;
		case 9: ret = "October";
		break;
		case 10: ret = "November";
		break;
		case 11: ret = "December";
		break;
		}
		return ret;
	}
	
	private void initMap() {
		salesMap = new LinkedHashMap<>();
		//put all months in map
		salesMap.put("January", 0.0);
		salesMap.put("February", 0.0);
		salesMap.put("March", 0.0);
		salesMap.put("April", 0.0);
		salesMap.put("May", 0.0);
		salesMap.put("Juny", 0.0);
		salesMap.put("July", 0.0);
		salesMap.put("August", 0.0);
		salesMap.put("September", 0.0);
		salesMap.put("October", 0.0);
		salesMap.put("November", 0.0);
		salesMap.put("December", 0.0);
	}
	
	
	private void getSalesInfo() {
		initMap();
		try {
			ResultSet rs = stmnt.executeQuery(evalQuery(this.year));
			String monthToPut = "";
			double priceToPut = 0.0;
			while(rs.next()) {
				calendar.setTime(rs.getDate(2));
				monthToPut = dateToMonth(calendar.get(Calendar.MONTH));
				priceToPut = rs.getDouble(1);
				priceToPut += salesMap.get(monthToPut);
				salesMap.put(monthToPut, priceToPut);
				
				
			}
		}
		catch(SQLException e) {
			
		}
		
	}	
	
	public void populateDataset() {
		dataset = new DefaultCategoryDataset();
		DefaultCategoryDataset dcd = (DefaultCategoryDataset)dataset;
		for(Map.Entry<String, Double> entry : salesMap.entrySet()) {
			dcd.addValue(entry.getValue(), "incomes", entry.getKey().substring(0, 3));
		}
			
		}
	
	private ChartPanel createProperGraph(int year) {
		this.year = year;
		initMap();
		getSalesInfo();
		populateDataset();
		chart = ChartFactory.createLineChart("Revenues", "Months", "Net income (€)", (DefaultCategoryDataset)dataset, PlotOrientation.VERTICAL, true, true, false);
		chart.setBackgroundPaint(new Color (0,0,0,0));
		final CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.GRAY);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setRangeGridlinePaint(Color.BLACK);
		ChartPanel c = new ChartPanel(chart);
		c.setOpaque(false);
		return c;
	}
	
	
	private class ChoiceListener implements ActionListener{
		
		JComboBox sourceBox;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			sourceBox = (JComboBox) e.getSource();
			int newYear = Integer.parseInt((String)this.sourceBox.getSelectedItem());
			remove(chartPanel);
			dataset = null;
			salesMap = null;
			chartPanel = createProperGraph(newYear);
			add(chartPanel);
			chartPanel.setPreferredSize(new Dimension(600, 400));
			repaint();
			revalidate();
			
		}
		
	}
	
	
	
	
	}
