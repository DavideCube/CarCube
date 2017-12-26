package atunibz.dcube.DBProject.GUI;

import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import atunibz.dcube.DBProject.configuration.AppResources;

public class StatisticsPanel extends JPanel{
	private JPanel statPanel, titlePanel, dataPanel;
	private Connection conn;
	private JLabel lab1, lab2, lab3, lab4, sales1, sales2, sales3, sales4, sales5;
	
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
		info.setFont(new Font("Helvetica", Font.BOLD, 18));
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
		dataPanel.add(support);
		statPanel.add(dataPanel);
		statPanel.add(Box.createRigidArea(new Dimension(0, 40)));
		
		// information about the sales
		JLabel info1 = new JLabel ("Information about the sales");
		info1.setFont(new Font("Helvetica", Font.BOLD, 18));
		info1.setAlignmentX(CENTER_ALIGNMENT);
		statPanel.add(info1);
		JPanel salesPanel = new JPanel ();
		JPanel support2 = new JPanel();
		support2.setLayout(new BoxLayout(support2, BoxLayout.Y_AXIS));
		sales1 = AppResources.iconLabel("Total revenues: " + totalRevenues() + ".00 €", "icons/money-bag.png");
		sales1.setAlignmentX(LEFT_ALIGNMENT);
		sales1.setFont(new Font("Helvetica", Font.PLAIN, 17));
		sales2 = AppResources.iconLabel("New cars sold: " + carSold (false), "icons/racing.png");
		sales2.setAlignmentX(LEFT_ALIGNMENT);
		sales2.setFont(new Font("Helvetica", Font.PLAIN, 17));
		sales3 = AppResources.iconLabel("Used cars sold: " + carSold (true), "icons/icon.png");
		sales3.setAlignmentX(LEFT_ALIGNMENT);
		sales3.setFont(new Font("Helvetica", Font.PLAIN, 17));
		sales4 = AppResources.iconLabel("Most sold car make: " + mostSold ("make"), "icons/pedestal.png");
		sales4.setAlignmentX(LEFT_ALIGNMENT);
		sales4.setFont(new Font("Helvetica", Font.PLAIN, 17));
		sales5 = AppResources.iconLabel("Most sold car type: " + mostSold ("car_type"), "icons/pedestal.png");
		sales5.setAlignmentX(LEFT_ALIGNMENT);
		sales5.setFont(new Font("Helvetica", Font.PLAIN, 17));
		
		support2.add(sales1);
		support2.add(sales2);
		support2.add(sales3);
		support2.add(sales4);
		support2.add(sales5);
		support2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		salesPanel.add(support2);
		statPanel.add(salesPanel);
		
		
		

		
		add(statPanel);
	}
	
	public int numberOf (String table) {
		int count = 0;
		try {
			Statement st = conn.createStatement();
			String sql = "SELECT count(*) FROM " + table;
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			count = rs.getInt("count");
			
	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	// query that executes the total number of revenues
	public int totalRevenues () {
		int count = 0;
		try {
			Statement st = conn.createStatement();
			String sql = "select sum (sum) from (select sum(sell_price) from new_sell \n" + 
					"union\n" + 
					"select sum(sell_price) from used_sell) as support";
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			count = rs.getInt("sum");
			
	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	// query that calculate the total number of used/new car sold
	public int carSold (boolean used) {
		int count = 0;
		try {
			Statement st = conn.createStatement();
			String sql = "select count(*) from " + (used ? "used" : "new") + "_sell";
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			count = rs.getInt("count");
			
	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	// query that calculates the most sold car make/type
	public String mostSold (String attribute) {
		String result = null;
		try {
			Statement st = conn.createStatement();
			String sql = "select " + attribute +", count(*) from (select c." + attribute + " from new_sell s inner join new_car c on s.car_id = c.car_id \n" + 
					"union all\n" + 
					"select c." + attribute + " from used_sell s inner join used_car c on s.immatriculation = c.immatriculation) as sales\n" + 
					"group by " + attribute + " \n" + 
					"order by count desc";
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			result = rs.getString(attribute) + " with " + rs.getInt("count") + " sales";
			
	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
