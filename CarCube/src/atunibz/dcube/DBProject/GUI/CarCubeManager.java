package atunibz.dcube.DBProject.GUI;
import java.awt.Component;
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

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CarCubeManager {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame ("CarCube");
		JPanel g = new JPanel();
		g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
		JLabel myGIF = new JLabel (new ImageIcon("icons/contacts/contact.gif"));
		//JLabel bought = new JLabel ("STAI ZITTO");
		myGIF.setAlignmentX(Component.CENTER_ALIGNMENT);
		//bought.setAlignmentX(CENTER_ALIGNMENT);
		g.add(myGIF);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1435, 865));
		if(System.getProperty("os.name").contains("Mac")) {
			frame.getContentPane().add(MainPanel.getMainPanel());
			MainPanel.getMainPanel().swapPanel(g);
		}
		else
			frame.getContentPane().add(MainPanel.getMainPanel());
		frame.getContentPane().add(MainPanel.getMainPanel());
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(true);	
		addPath("jars/postgresql-42.1.4.jar");

	}
	
	public static void addPath(String p){
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
	    System.out.println("Loading classes...\n");
		
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.print("Class loaded.\n");
		
		
		
		class SPanel extends JPanel{
			
			public SPanel(){
				this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				JLabel myGIF = new JLabel (new ImageIcon("icons/contacts/contact.gif"));
				//JLabel bought = new JLabel ("STAI ZITTO");
				myGIF.setAlignmentX(CENTER_ALIGNMENT);
				//bought.setAlignmentX(CENTER_ALIGNMENT);
				this.add(myGIF);
			}
		}
		
		
	}
}