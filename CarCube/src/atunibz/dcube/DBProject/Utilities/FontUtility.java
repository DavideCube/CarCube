package atunibz.dcube.DBProject.Utilities;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;

public class FontUtility {
	
	
	public static void changeFont(Object o, int FontType, int dimension) {
		
		Font myFont = new Font("Comic Sans MS", FontType, dimension);
		
		if (o instanceof JButton) {
			JButton but = (JButton) o;
			but.setFont(myFont);
		}
		
		if(o instanceof JLabel) {
			JLabel lab = (JLabel) o;
			lab.setFont(myFont);
		}
	}
	
}