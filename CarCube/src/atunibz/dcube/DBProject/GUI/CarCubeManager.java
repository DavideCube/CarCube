package atunibz.dcube.DBProject.GUI;
import java.awt.Dimension;
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
		Connection conn = DatabaseConnection.getDBConnection().getConnection();
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
}