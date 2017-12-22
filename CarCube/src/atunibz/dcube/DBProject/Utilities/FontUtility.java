package atunibz.dcube.DBProject.Utilities;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;

public class FontUtility {
	
	
	public static void changeFont(FontInterface o, int FontType, int dimension) {
		
		Font myFont = new Font("Comic Sans MS", FontType, dimension);
		
		o.setFont(myFont);
	}
	
}