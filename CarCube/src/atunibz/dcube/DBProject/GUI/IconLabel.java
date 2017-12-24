package atunibz.dcube.DBProject.GUI;

import java.awt.Font;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
/**
 * Panel that shows a text next to a picture, or a text between two pictures. Useful for
 * titles or labelled icons.
 * @author Davide Perez
 * @since 24/12/2017
 *
 */
public class IconLabel extends JPanel {
	/**
	 * Image to show near the text
	 */
	ImageIcon image;
	/**
	 * JLabel with text content
	 */
	JLabel contentLbl,
	/**
	 * JLabel with ImageIcon
	 */
			imageLbl;
	
	
	/**
	 * Constructor. With the doubleIcon parameter is possible to specify if the IconLabel
	 * should have one or two icons.
	 * @param iconPath path of the image file
	 * @param content the text content to show
	 * @param doubleIcon if false, the IconLabel shows the text next to the picture. If true,
	 * it shows the text between two identical pictures
	 */
	public IconLabel(String iconPath, String content, boolean doubleIcon){
			image = new ImageIcon(iconPath);
			imageLbl = new JLabel(image);
			contentLbl = new JLabel(content);
			//empty border used only to specify insets
			contentLbl.setBorder(new EmptyBorder(new Insets(0, 0, 5, 5)));
			this.add(imageLbl);
			this.add(contentLbl);
			if(doubleIcon){
				JLabel imageLbl = new JLabel(image);
				this.add(imageLbl);
			}
	}
	
	/**
	 * 
	 * @param iconPattern the path of the image file
	 * @param content the text content to show
	 * @param font the font for the content
	 * @param doubleIcon specifies if it is a double icon label
	 */
	public IconLabel(String iconPattern, String content, Font font, boolean doubleIcon){
		image = new ImageIcon(iconPattern);
		imageLbl = new JLabel(image);
		contentLbl = new JLabel(content);
		contentLbl.setBorder(new EmptyBorder(new Insets(0, 0, 5, 5)));
		contentLbl.setFont(font);
		this.add(imageLbl);
		this.add(contentLbl);
		if(doubleIcon){
			JLabel imageLbl = new JLabel(image);
			this.add(imageLbl);
		}
	}
	
	/**
	 * 
	 * @param icon the ImageIcon to use as icon
	 * @param content the text content to show
	 * @param font the font for the text content
	 * @param doubleIcon specifies if it is a double icon label
	 */
	public IconLabel(ImageIcon icon, String content, Font font, boolean doubleIcon){
		image = icon;
		imageLbl = new JLabel(image);
		contentLbl = new JLabel(content);
		contentLbl.setFont(font);
		this.add(imageLbl);
		this.add(contentLbl);
		if(doubleIcon){
			JLabel imageLbl = new JLabel(image);
			this.add(imageLbl);
		}
	}

	
	
	/**
	 * Getter for the ImageIcon image
	 * @return the IconLabel image
	 */
	public ImageIcon getImage() {
		return image;
	}
	
	/**
	 * Setter for the ImageIcon image
	 * @param image ImageIcon to set as icon
	 */
	public void setImage(ImageIcon image) {
		this.image = image;
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Getter for the text content
	 * @return the text in the JLabel
	 */
	public JLabel getContentLbl() {
		return contentLbl;
	}
	
	/**
	 * Setter for the text content
	 * @param contentLbl the string to set as text content
	 */
	public void setContentLbl(JLabel contentLbl) {
		this.contentLbl = contentLbl;
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Getter for the image JLabel
	 * @return the JLabel containing the picture
	 */
	public JLabel getImageLbl() {
		return imageLbl;

	}
	
	/**
	 * Setter for the image JLabel
	 * @param imageLbl the JLabel to put as picture label
	 */
	public void setImageLbl(JLabel imageLbl) {
		this.imageLbl = imageLbl;
		this.revalidate();
		this.repaint();
	}
	
	
	
	
}