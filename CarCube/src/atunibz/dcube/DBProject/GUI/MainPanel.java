package atunibz.dcube.DBProject.GUI;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Singleton: Class which is responsible for switching between different panels in the program. Its private 
 * constructor allows only one time the creation of an instance of this class, so that another
 * main panel cannot be created (singleton). It contains also get method in order to access to 
 * the panel object and switch method for switching between two panels.
 * This is also the class which is added at the frame of the program in the main method.
 */
public class MainPanel extends BackgroundedPanel{
	// create ONE TIME the object
	private static final MainPanel mainPanel = new MainPanel();

	/**
	 * Constructor of the main panel. The constructor is private so that the main panel 
	 * will be created only at the beginning and another main panel cannot be created (singleton).
	 * At the beginning the logo panel is the first one which is added to the main panel.
	 */
	private MainPanel() {
		Image bg = null;
		try {
			bg = ImageIO.read(new File(""));
		} catch (IOException e) {
			System.err.println("Cannot load background!");
		}
		this.setImage(bg);
		this.setBackgroundLayout(BackgroundLayout.SCALED);
		LogoPanel intro = new LogoPanel();
		add(intro);
	}

	/**
	 * Method that allows to access to the main panel object.
	 * @return the MainPanel object.
	 */
	public static MainPanel getMainPanel() {
		return mainPanel;
	}

	/**
	 * Method that allows the switching between two panels in the program. The panel
	 * passed as parameter is that one which is now added to the main panel and, consequently,
	 * to the frame of the program and will be also shown to the user.
	 * @param panel the JPanel which is now added to the main panel and also visible in the program.
	 */
	public void swapPanel(JPanel panel) {
		mainPanel.removeAll();
		mainPanel.add(panel);
		mainPanel.revalidate();
		mainPanel.repaint();
	}
	
}
