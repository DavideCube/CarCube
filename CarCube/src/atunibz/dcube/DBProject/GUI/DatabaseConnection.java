package atunibz.dcube.DBProject.GUI;


import java.sql.Connection;
import java.sql.DriverManager;



public class DatabaseConnection {
	
	private static final DatabaseConnection myConnection = new DatabaseConnection();
	private Connection conn = null;
	
	private DatabaseConnection() {
		try {
	         Class.forName("org.postgresql.Driver");
	         conn = DriverManager
	            .getConnection("jdbc:postgresql://localhost:5432/Project",
	            "postgres", "postgres");
	      } catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	      }
	}
	
	public Connection getConnection() {
		return conn;
	}
	
	public static DatabaseConnection getDBConnection() {
		return myConnection;
	}
	
}