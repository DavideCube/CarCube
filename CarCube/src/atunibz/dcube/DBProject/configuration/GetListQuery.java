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
	public static Connection conn =  DatabaseConnection.getDBConnection().getConnection();;
	
	// GET MAKES
	public static String[] getMakes(int typeOfQuery) {
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
	
	// Get all models from DBDBDBDBDBDB
	// Commento serio: 0 = new car; 1 = used car; 2 = boat cars;
	public static String[] getModels(int typeOfQuery, String selectedMake) {

		ArrayList<String> tPiccola = new ArrayList<String>();
		String getModels = null;
		//String selectedMake = (String) make.getSelectedItem();
		switch (typeOfQuery) {
		case 0:
			getModels = "SELECT DISTINCT model FROM new_car WHERE make='" + selectedMake + "'";
			break;
		case 1:
			getModels = "SELECT DISTINCT model FROM used_car WHERE make='" + selectedMake + "'";
			break;
		case 2:
			getModels = "SELECT DISTINCT model FROM new_car WHERE make = '" + selectedMake
					+ "' UNION DISTINCT SELECT DISTINCT model FROM used_car WHERE make='" + selectedMake + "'";
			break;
		}

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(getModels);
			while (rs.next()) {
				tPiccola.add(rs.getString("model"));
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
	
	// Get all car types from db (there is no filter)
	public static String[] getCarTypes() {

		ArrayList<String> carTypes = new ArrayList<String>();

		String query = "SELECT car_type from new_car UNION DISTINCT SELECT car_type from used_car ORDER BY car_type";

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()) {
				carTypes.add(rs.getString("car_type"));
			}

			stat.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String [] result = carTypes.toArray(new String[carTypes.size()]);
		Arrays.sort(result);
		return result;
	}
	
	// Get all fuels type from db (there is no filter)
	public static String[] getFuels() {

		ArrayList<String> fuels = new ArrayList<String>();

		String query = "SELECT DISTINCT fuel from engine";

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()) {
				fuels.add(rs.getString("fuel"));
			}

			stat.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fuels.toArray(new String[fuels.size()]);
	}
	
	// Get all fuels type from db (there is no filter)
	public static String[] getTransmissions() {

		ArrayList<String> transmissions = new ArrayList<String>();

		String query = "SELECT DISTINCT transmission from engine";

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()) {
				transmissions.add(rs.getString("transmission"));
			}

			stat.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String [] result = transmissions.toArray(new String[transmissions.size()]);
		Arrays.sort(result);
		return result;
	}
	
	// Get all seats from db (there is no filter)
	public static String[] getSeats() {

		ArrayList<String> seats = new ArrayList<String>();

		String query = "SELECT seats from new_car UNION DISTINCT SELECT seats from used_car ORDER BY seats";

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()) {
				seats.add(rs.getString("seats"));
			}

			stat.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return seats.toArray(new String[seats.size()]);
	}

	// Get all doors from db (there is no filter)
	public static String[] getDoors() {

		ArrayList<String> doors = new ArrayList<String>();

		String query = "SELECT doors from new_car UNION DISTINCT SELECT doors from used_car ORDER BY doors";

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()) {
				doors.add(rs.getString("doors"));
			}

			stat.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return doors.toArray(new String[doors.size()]);
	}
	
	// Get all wheel drive type from db (there is no filter)
	public static String[] getWheelDrive() {

		ArrayList<String> wheeldrive = new ArrayList<String>();

		String query = "SELECT DISTINCT wheel_drive from engine";

		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			while (rs.next()) {
				wheeldrive.add(rs.getString("wheel_drive"));
			}

			stat.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return wheeldrive.toArray(new String[wheeldrive.size()]);
	}

}
