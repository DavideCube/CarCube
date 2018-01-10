package atunibz.dcube.DBProject.GUI;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import atunibz.dcube.DBProject.configuration.AppResources;

public class StatisticsPanel extends JPanel{
	private JPanel statPanel, titlePanel, dataPanel;
	private Connection conn;
	private JLabel lab1, lab2, lab3, lab4, sales1, sales2, sales3, sales4, sales5;
	private JButton start, back;
	private JDatePickerImpl datePicker, datePicker2;
	
	public StatisticsPanel () {
		conn = DatabaseConnection.getDBConnection().getConnection();
		statPanel = new JPanel();
		statPanel.setLayout(new BoxLayout(statPanel, BoxLayout.Y_AXIS));
		statPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		// Panel containing the beautiful logo
		titlePanel = AppResources.carCubePanel();
		statPanel.add(titlePanel);
		statPanel.setOpaque(false);
		statPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		
		// panel that contains information about the car dealership
		JLabel info = new JLabel ("Information about the car dealership");
		info.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
		info.setAlignmentX(CENTER_ALIGNMENT);
		statPanel.add(info);
		dataPanel = new JPanel();
		JPanel support = new JPanel ();
		support.setLayout(new BoxLayout(support, BoxLayout.Y_AXIS));
		lab1 = AppResources.iconLabel("Total number of customers: " + numberOf("customer"), "icons/users.png");
		lab1.setAlignmentX(LEFT_ALIGNMENT);
		lab1.setFont(new Font("Helvetica", Font.PLAIN, 17));
		lab2 = AppResources.iconLabel("Total number of suppliers: " + numberOf("supplier"), "icons/trucking.png");
		lab2.setAlignmentX(LEFT_ALIGNMENT);
		lab2.setFont(new Font("Helvetica", Font.PLAIN, 17));
		lab3 = AppResources.iconLabel("Total number of new cars: " + numberOf("new_car"), "icons/racing.png");
		lab3.setAlignmentX(LEFT_ALIGNMENT);
		lab3.setFont(new Font("Helvetica", Font.PLAIN, 17));
		lab4 = AppResources.iconLabel("Total number of used cars: " + numberOf("used_car"), "icons/icon.png");
		lab4.setAlignmentX(LEFT_ALIGNMENT);
		lab4.setFont(new Font("Helvetica", Font.PLAIN, 17));
		support.add(lab1);
		support.add(lab2);
		support.add(lab3);
		support.add(lab4);
		support.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		support.setOpaque(false);
		dataPanel.setOpaque(false);
		dataPanel.add(support);
		statPanel.add(dataPanel);
		statPanel.add(Box.createRigidArea(new Dimension(0, 40)));
		
		// information about the sales
		JLabel info1 = new JLabel ("Information about the sales");
		info1.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
		info1.setAlignmentX(CENTER_ALIGNMENT);
		statPanel.add(info1);
		statPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		// date picker
		JPanel selectDatePanel = new JPanel();
		selectDatePanel.setLayout(new BoxLayout(selectDatePanel, BoxLayout.X_AXIS));
		UtilDateModel model = new UtilDateModel();
		UtilDateModel model2 = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		datePicker.setOpaque(false);
		datePicker.addActionListener(new DatePicker1Listener());
		selectDatePanel.add(datePicker);
		selectDatePanel.add(Box.createRigidArea(new Dimension(20, 0)));
		JDatePanelImpl datePanel2 = new JDatePanelImpl(model2, p);
		datePicker2 = new JDatePickerImpl(datePanel2, new DateLabelFormatter());
		datePicker2.addActionListener(new DatePicker2Listener());
		datePicker2.setOpaque(false);
		start = new JButton ("Select an interval of time");
		start.setEnabled(false);
		start.addActionListener(new SalesListener());
		selectDatePanel.add(start);
		selectDatePanel.add(Box.createRigidArea(new Dimension(20, 0)));
		selectDatePanel.add(datePicker2);
		selectDatePanel.setOpaque(false);
		statPanel.add(selectDatePanel);
		statPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		
		
		JPanel salesPanel = new JPanel ();
		JPanel support2 = new JPanel();
		support2.setLayout(new BoxLayout(support2, BoxLayout.Y_AXIS));
		sales1 = AppResources.iconLabel("Total incomes: " , "icons/money-bag.png");
		sales1.setAlignmentX(LEFT_ALIGNMENT);
		sales1.setFont(new Font("Helvetica", Font.PLAIN, 17));
		sales2 = AppResources.iconLabel("New cars sold: " , "icons/racing.png");
		sales2.setAlignmentX(LEFT_ALIGNMENT);
		sales2.setFont(new Font("Helvetica", Font.PLAIN, 17));
		sales3 = AppResources.iconLabel("Used cars sold: ", "icons/icon.png");
		sales3.setAlignmentX(LEFT_ALIGNMENT);
		sales3.setFont(new Font("Helvetica", Font.PLAIN, 17));
		sales4 = AppResources.iconLabel("Most sold car make: ", "icons/pedestal.png");
		sales4.setAlignmentX(LEFT_ALIGNMENT);
		sales4.setFont(new Font("Helvetica", Font.PLAIN, 17));
		sales5 = AppResources.iconLabel("Most sold car type: ", "icons/pedestal.png");
		sales5.setAlignmentX(LEFT_ALIGNMENT);
		sales5.setFont(new Font("Helvetica", Font.PLAIN, 17));
		
		support2.add(sales1);
		support2.add(sales2);
		support2.add(sales3);
		support2.add(sales4);
		support2.add(sales5);
		support2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		support2.setOpaque(false);
		salesPanel.setOpaque(false);
		salesPanel.add(support2);
		statPanel.add(salesPanel);
		
		// back button
		statPanel.add(Box.createRigidArea(new Dimension(0, 40)));
		back = AppResources.iconButton("Go back     ", "icons/back.png");
		back.addActionListener(new BackListener());
		back.setAlignmentX(CENTER_ALIGNMENT);
		statPanel.add(back);
		
		add(statPanel);
	}
	
	// query that returns the total number of tuples in the table passed as parameter
	public int numberOf (String table) {
		int count = 0;
		try {
			Statement st = conn.createStatement();
			String sql = "SELECT count(*) FROM " + table;
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			count = rs.getInt("count");
			
			st.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	// query that executes the total number of revenues
	public int totalRevenues (String from, String to) {
		int count = 0;
		try {
			Statement st = conn.createStatement();
			String sql = "select sum (sum) from (select sum(sell_price) from new_sell where sell_date >= \'" +from+ "\' and sell_date <= \'" + to + "\'\n" + 
					"union  \n" + 
					"select sum(sell_price) from used_sell where sell_date >= \'" + from + "\' and sell_date <= \'" + to +"\') as support\n";
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			count = rs.getInt("sum");
			
			st.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	// query that calculate the total number of used/new car sold
	public int carSold (boolean used, String from, String to) {
		int count = 0;
		try {
			Statement st = conn.createStatement();
			String sql = "select count(*) from " + (used ? "used" : "new") + "_sell where sell_date >= \'" + from + "\' and sell_date <= \'" + to + "\'";
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			count = rs.getInt("count");
			
			st.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	// query that calculates the most sold car make/type
	public String mostSold (String attribute, String from, String to) {
		String result = null;
		try {
			Statement st = conn.createStatement();
			String sql = "select " + attribute +", count(*) from (select c." + attribute + " from new_sell s inner join new_car c on s.car_id = c.car_id and s.sell_date >= \'" + from + "\' and s.sell_date <=\'" + to + "\'\n" + 
					"union all\n" + 
					"select c." + attribute + " from used_sell s inner join used_car c on s.immatriculation = c.immatriculation and s.sell_date >= \'" + from + "\' and s.sell_date <=\'" + to + "\') as sales\n" + 
					"group by " + attribute + " \n" + 
					"order by count desc";
			ResultSet rs = st.executeQuery(sql);
			if (rs.next())
				result = rs.getString(attribute) + " with " + rs.getInt("count") + " sales";
			else
				result = "No car was sold";
			
			
			st.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	// formatter for the date Picker
	public class DateLabelFormatter extends AbstractFormatter {

	    private String datePattern = "yyyy-MM-dd";
	    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

	    @Override
	    public Object stringToValue(String text) throws ParseException {
	        return dateFormatter.parseObject(text);
	    }

	    @Override
	    public String valueToString(Object value) throws ParseException {
	        if (value != null) {
	            Calendar cal = (Calendar) value;
	            return dateFormatter.format(cal.getTime());
	        }

	        return "";
	    }
	}
	
	// listener for the first date picker
	private class DatePicker1Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (datePicker2.getModel().getValue() != null) {
				start.setText("Search in this period of time");
				start.setEnabled(true);
			}
		}
	}
	
	// listener for the second date picker
	private class DatePicker2Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (datePicker.getModel().getValue() != null) {
				start.setText("Search in this period of time");
				start.setEnabled(true);
			}
		}
	}
	
	// listener for the button the start searching into the database for information about the sales
	private class SalesListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dateFrom = (Date) datePicker.getModel().getValue();
			Date dateTo = (Date) datePicker2.getModel().getValue();
			if (!dateFrom.after(dateTo)) {
				String from = sdf.format(datePicker.getModel().getValue());
				String to = sdf.format(datePicker2.getModel().getValue());
				NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
				String priceFormatted = currencyFormat.format(totalRevenues(from,to));
				sales1.setText("Total incomes: " + priceFormatted);
				sales2.setText("New cars sold: " + carSold (false, from, to));
				sales3.setText("Used cars sold: " + carSold (true, from, to));
				sales4.setText("Most sold car make: " + mostSold ("make", from, to));
				sales5.setText("Most sold car type: " + mostSold ("car_type", from, to));
			}
			else {
				String message = "Invalid period of time, the left Date cannot be after the right Date.";
				JOptionPane.showMessageDialog(MainPanel.getMainPanel(), message, "CarCube", JOptionPane.INFORMATION_MESSAGE, new ImageIcon ("icons/minilogo.png"));	
			}
		}
		
	}
	
	// listener for back button
	private class BackListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			MainPanel.getMainPanel().swapPanel(new StakeholdersPanel());
		}
	}
}

