package atunibz.dcube.DBProject.configuration;

import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public final class AppResources {
	
	public static final Font TITLE_FONT = null;
	public static final Font DEFAULT_FONT = new Font("Comic Sans MS", Font.PLAIN, 25);
	public static final Font BUTTON_FONT = new Font("Comic Sans MS", Font.BOLD, 30);
	public static final Font LITTLEBUTTON_FONT = new Font("Helvetica", Font.PLAIN, 15);

	
	//background constants
	public static final Image DEFAULT_BACKGROUND = backgroundInit();
	
	//se volete cambiare lo sfondo cambiate solo la stringa qua sotto
	private static final String DEFAULT_BACKGROUND_PATH = "icons/prova2.JPEG";
	
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
		
		if(o instanceof JTextField) {
			JTextField lab = (JTextField) o;
			lab.setFont(myFont);
		}
	}
	
	public static JPanel carCubePanel () {
		JPanel titlePanel = new JPanel();
		JLabel logo = new JLabel();
		logo.setIcon(new ImageIcon ("icons/logo.png"));
		titlePanel.add(logo);
		titlePanel.setOpaque(false);
		return titlePanel;
	}
	
	public static JButton iconButton (String name, String imagePath) {
		JButton button = new JButton();
		button.setText(name);
		button.setIcon(new ImageIcon(imagePath));
		button.setHorizontalTextPosition(SwingConstants.RIGHT);
		button.setFont(AppResources.LITTLEBUTTON_FONT);
		return button;
	}
	public static JLabel iconLabel (String text, String path) {
		JLabel label= new JLabel();
		label.setText(text);
		label.setIcon(new ImageIcon (path));
		label.setHorizontalTextPosition(SwingConstants.RIGHT);
		return label;
	}
	
	public static ImageIcon scaleProfileImage (String name, boolean newCar) {
		BufferedImage buffImg = null;
		try {
			if (newCar)
				buffImg = ImageIO.read(new File("icons/newCars/" + name + ".jpg"));
			else
				buffImg = ImageIO.read(new File("icons/usedCars/" + name + ".jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Image image = buffImg.getScaledInstance(140, 140, Image.SCALE_SMOOTH);
		ImageIcon scaledImage = new ImageIcon(image);
		return scaledImage;
	}
	
}