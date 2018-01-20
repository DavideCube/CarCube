package atunibz.dcube.DBProject.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class GeneralCriteriaChart extends ChartJPanel {
	
	
	private JComboBox criteriaBox;
	private final String[] criterias = {"Price", "Horsepower", "Capacity", "Mileage (used only)", "Trunk capacity", "Height", "Length", "Width"};
	private String selectedCriteria = "Price";
	private String xAxis, yAxis;
	private String query;
	
	public GeneralCriteriaChart() {
		super();
		criteriaBox = new JComboBox<String>(criterias);
		criteriaBox.setSelectedIndex(0);
		selectedCriteria = (String)criteriaBox.getSelectedItem();
		this.chartPanel = createProperGraph(selectedCriteria);
		chartPanel.setPreferredSize(new Dimension(600, 400));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.criteriaBox.addActionListener(new ChoiceListener());
		this.add(criteriaBox);
		this.add(chartPanel);
	}
	
	
	private ChartPanel createProperGraph(String inputCriteria) {
		populateDataset();
		createAndFormatChart(inputCriteria);
		return new ChartPanel(chart);
	}
	
	private void evaluateAxisByCriteria(String inputCriteria) {
		switch(inputCriteria) {
		case "Price": 
			this.xAxis = "Price";
			this.yAxis = "Price in $";
		break;
		case "Horsepower":
			this.xAxis = "Horsepower";
			this.yAxis = "Number of horses";
		break;
		case "Capacity":
			this.xAxis = "Capacity";
			this.yAxis = "Liters";
		break;
		case "Mileage (used only)":
			this.xAxis = "Mileage";
			this.yAxis = "Kilometers";
		break;
		case "Trunk capacity":
			this.xAxis = "Trunk capacity";
			this.yAxis = "Liters";
		break;
		case "Height":
			this.xAxis = "Heigth";
			this.yAxis = "Centimeters";
		break;
		case "Length":
			this.xAxis = "Length";
			this.yAxis = "Centimeters";
		break;
		case "Width":
			this.xAxis = "Width";
			this.yAxis = "Centimeters";
		break;
		default:
			throw new UnsupportedOperationException("Invalid criteria selected!");
		}
	}
	
	//call after populating dataset
	private void formatAxisUnits(String inputCriteria) {
		final CategoryPlot plot = chart.getCategoryPlot();
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		if(!(inputCriteria.compareTo("Price") == 0)) {
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		}
	}
	
	
	
	
	private void createAndFormatChart(String inputCriteria) {
		evaluateAxisByCriteria(inputCriteria);
		chart = ChartFactory.createBarChart("Car comparison", xAxis, yAxis, (DefaultCategoryDataset)dataset, PlotOrientation.VERTICAL, true, true , false);
		chart.setBackgroundPaint(Color.WHITE);
		final CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.GRAY);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setRangeGridlinePaint(Color.BLACK);
		formatAxisUnits(inputCriteria);
		
	}

	
	private boolean querySelector(String param){
		switch(param) {
		case "Price": 
			query = "select make, model, price\n" + 
			"		 from (select make, model, base_price as price from new_car union all select make, model," +
			"		 net_price as price from used_car) as all_cars \n" + 
			"		 group by(all_cars.make, all_cars.price, all_cars.model)\n";
			return true;
		case "Horsepower":
			query = "select *\n" + 
					"from (select make, model, horsepower\n" + 
					"      from new_car inner join engine\n" + 
					"      on engine = engine_id\n" + 
					"      union all\n" + 
					"      select make, model, horsepower\n" + 
					"      from used_car inner join engine\n" + 
					"      on engine = engine_id) as horsepower";
			return true;
		case "Capacity":
			query = "select *\n" + 
					"from (select make, model, capacity\n" + 
					"      from new_car inner join engine\n" + 
					"      on engine = engine_id\n" + 
					"      union all\n" + 
					"      select make, model, capacity\n" + 
					"      from used_car inner join engine\n" + 
					"      on engine = engine_id) as capacity";
			return true;
		case "Mileage (used only)":
			query = "select make, model, mileage from used_car";
			return true;
		case "Trunk capacity":
			query = "select *\n" + 
					"from (select make, model, trunk_capacity\n" + 
					"      from new_car inner join dimension\n" + 
					"      on dimension = dimension_id\n" + 
					"      union all\n" + 
					"      select make, model, trunk_capacity\n" + 
					"      from used_car inner join dimension\n" + 
					"      on dimension = dimension) as capacity";
			return true;
		case "Height":
			query = "select *\n" + 
					"from (select make, model, car_heigth\n" + 
					"      from new_car inner join dimension\n" + 
					"      on dimension = dimension_id\n" + 
					"      union all\n" + 
					"      select make, model, car_heigth\n" + 
					"      from used_car inner join dimension\n" + 
					"      on dimension = dimension) as height";
			return true;
		case "Length":
			query = "select *\n" + 
					"from (select make, model, car_length\n" + 
					"      from new_car inner join dimension\n" + 
					"      on dimension = dimension_id\n" + 
					"      union all\n" + 
					"      select make, model, car_length\n" + 
					"      from used_car inner join dimension\n" + 
					"      on dimension = dimension) as length";
			return true;
		case "Width":
			query = "select *\n" + 
					"from (select make, model, car_width\n" + 
					"      from new_car inner join dimension\n" + 
					"      on dimension = dimension_id\n" + 
					"      union all\n" + 
					"      select make, model, car_width\n" + 
					"      from used_car inner join dimension\n" + 
					"      on dimension = dimension) as width";
			return true;
		default:
			return false;
		}
	}
	
	//TODO
	@Override
	public void populateDataset() {
		dataset = new DefaultCategoryDataset();
		DefaultCategoryDataset dcd = (DefaultCategoryDataset)dataset;
		if(querySelector(selectedCriteria)) {
			//then valid criteria selected and query defined
			try {
				ResultSet rs = stmnt.executeQuery(query);
				while(rs.next()) {
					System.out.println("RS UPDATED");
						dcd.setValue((double)rs.getInt(3), rs.getString(1) + " " + rs.getString(2), selectedCriteria);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	 private class ChoiceListener implements ActionListener {
		JComboBox sourceBox;
		
		ChoiceListener(){
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Action performed!");
			sourceBox = (JComboBox) e.getSource();
			selectedCriteria = (String)this.sourceBox.getSelectedItem();
			remove(chartPanel);
			dataset = null;
			chartPanel = createProperGraph(selectedCriteria);
			add(chartPanel);
			chartPanel.setPreferredSize(new Dimension(600, 400));
			repaint();
			revalidate();
			/*
			JFrame f = new JFrame();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.getContentPane().add(new GeneralCriteriaChart(selectedCriteria));
			f.pack();
			f.setVisible(true);*/
			
		}
		
		
	}
	 
}
