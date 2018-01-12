package atunibz.dcube.DBProject.configuration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import atunibz.dcube.DBProject.GUI.DatabaseConnection;

// CLASS WHICH CONTAINS METHODS FOR RETRIEVING LIST OF DATA FROM DATABASE
public class GetListQuery {
	public static Connection conn;
	
	
	public static String[] getMakes(int typeOfQuery) {
		conn = DatabaseConnection.getDBConnection().getConnection();
		ArrayList<String> tPiccola = new ArrayList<String>();
		String getMakes = null;
		switch(typeOfQuery) {
		case 0: getMakes = "SELECT DISTINCT make FROM new_car"; break;
		case 1: getMakes = "SELECT DISTINCT make FROM used_car"; break;
		case 2: getMakes = "SELECT DISTINCT make FROM new_car UNION DISTINCT SELECT DISTINCT make FROM used_car"; break;
		}
		
		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(getMakes);
			while(rs.next()) {
				tPiccola.add(rs.getString("make"));
			}
			stat.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String [] result = tPiccola.toArray(new String[tPiccola.size()]);
		Arrays.sort(result);
		return result;

	}

}
