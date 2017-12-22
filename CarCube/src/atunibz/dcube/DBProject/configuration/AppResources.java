package atunibz.dcube.DBProject.configuration;

import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class AppResources {
	
	public static final Font TITLE_FONT = null;
	public static final Font DEFAULT_FONT = null;
	public static final Font BUTTON_FONT = new Font("Comic Sans MS", Font.BOLD, 30);
	
	//background constants
	public static final Image DEFAULT_BACKGROUND = backgroundInit();
	
	//se volete cambiare lo sfondo cambiate solo la stringa qua sotto
	private static final String DEFAULT_BACKGROUND_PATH = "icons/default_bg.jpeg";
	
	private static Image backgroundInit() {
		Image backgroundImg = null;
		try {
			backgroundImg = ImageIO.read(new File(DEFAULT_BACKGROUND_PATH));
		} catch (IOException e) {
			System.err.println("Error loading background image.\n");
		}

		return backgroundImg;

	}
	
	
	
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
	
	public static JPanel carCubePanel () {
		JPanel titlePanel = new JPanel();
		JLabel logo = new JLabel();
		logo.setIcon(new ImageIcon ("icons/logo.png"));
		titlePanel.add(logo);
		return titlePanel;
	}
	
}