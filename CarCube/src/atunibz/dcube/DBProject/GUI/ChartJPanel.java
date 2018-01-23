package atunibz.dcube.DBProject.GUI;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;

public abstract class ChartJPanel extends JPanel {
	
	protected JFreeChart chart;
	protected ChartPanel chartPanel;
	protected Dataset dataset;
	protected Connection conn;
	protected Statement stmnt;
	//for every make of cars, returns the make along with the number of units sold of that make
	
	/*
	public ChartJPanel() {
		throw new UnsupportedOperationException("Cannot instantiate an istance of this class without a valid databse connection");
	}*/
	
	public ChartJPanel() {
		super();
		this.setOpaque(false);
		this.establishConnection();
		
	}
	
	private boolean establishConnection() {
		this.conn = DatabaseConnection.getDBConnection().getConnection();
		try {
			stmnt = this.conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	
	public abstract void populateDataset();
	

}
