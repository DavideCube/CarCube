package atunibz.dcube.DBProject.GUI;
import java.awt.Dimension;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;

public class CarCubeManager {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame ("CarCube");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1435, 800));
		frame.getContentPane().add(new StakeholdersPanel());
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);	
		String currentUser = System.getProperty("os.name");
		//addPath(currentUser);
		Connection conn = DatabaseConnection.getDBConnection().getConnection();
		
		//dperez, Davide Sbetti, Davide Cremonini
		try {
			Statement st = conn.createStatement();
			String sql = "SELECT * FROM color";
			ResultSet rs = st.executeQuery(sql);
			
			System.out.println("Color_Code\tColor_name\tColor_value");
			while(rs.next()) {
				System.out.println(rs.getString("color_code") + "\t\t" + rs.getString("color_name") + "\t\t\t" + rs.getString("color_value"));
			}
	
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();

	}

	}
	
	/*public static void addPath(String os){
		System.out.println("User " + System.getProperty("os.name") + " is running the application\n");
		String p = "";
		if(os.compareTo("Linux") == 0) {
			System.out.println("Ciao perez\n");
			p = "/home/dperez/Desktop/Java/external libs/postgresql-42.1.4.jar";
		}
		else if(os.compareTo("Mac OS X") == 0) {
			System.out.println("Ciao cremo\n");
			p = "/Users/DavideCremonini/DocumentiDavide/UniBZ/Database_System/DavideCube/postgresql-42.1.4.jar";
		}
		else if(os.compareTo("Windows 10") == 0) {
			System.out.println("Ciao sbetti\n");
			p = "C:\\Users\\Davide\\Documents\\unibz\\DatabaseSystems\\Project\\postgresql-42.1.4.jar";
		}
		else
			throw new RuntimeException("Ziocane non funziona. Porco termosifone.\n");
	    File f = new File(p);
	    @SuppressWarnings("deprecation")
		URL u = null;
		try {
			u = f.toURL();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	    Class urlClass = URLClassLoader.class;
	    Method method = null;
		try {
			method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
		} catch (NoSuchMethodException | SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    method.setAccessible(true);
	    try {
			method.invoke(urlClassLoader, new Object[]{u});
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    System.out.println("Classpath updated from " + os + "\n");
	    System.out.println("Loading classes...\n");
		
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.print("Class loaded.\n");
	}*/
}