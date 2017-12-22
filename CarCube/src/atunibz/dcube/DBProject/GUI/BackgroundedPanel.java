package atunibz.dcube.DBProject.GUI;

import javax.swing.*;
import java.awt.*;


public class BackgroundedPanel extends JPanel {
	
	/**
	 * JPanel that provides the feature to draw a custom background on the panel. The background
	 * may be any graphical image, i.e an object implementing the {@link Image} interface, or a custom color pattern.
	 * If an image is chosen as background, it is possible to specify its layout.
	 * @author Davide Perez
	 * @since 20/5/2017
	 * @version 2.0
	 *
	 */
	/**
	 * Enumerated type that specifies the layout of the background, i.e how the image will be drawn as background.<p>
	 * @author Davide Perez
	 * @since 20/5/2017
	 * @version 1.0
	 */
	// restrict the choice of the constructor argument to this three unique
	// values
	public enum BackgroundLayout {
		/**
		 * <b>SCALED:</b> causes the image to adapt to the panel size (note that this can result
		 * in a stretched image if the image chosen is not of an adequate size).
		 */
		SCALED, 
		/**
		 * <b>TILED: </b>the image is drawn with its original dimensions as many times as it is necessary
		 * to completely fill the panel (this is valid even when the panel dimensions change).
		 */
		TILED, 
		/**
		 *<b>CENTERED:</b> the image is drawn in the center of the panel. This accounts of the current
		 * panel size and of the eventual constraints specified by its LayoutManager or other components.
		 */
		CENTERED
	}

	/**
	 * Specifies the default central position in horizontal orientation
	 */
	static final float DEFAULT_X = 0.5f;// ensures the central position with
										// respect to x
	/**
	 * Specifies the default central position in vertical horientation
	 */
	static final float DEFAULT_Y = 0.5f;// ensures the central position with
										// respect to y
	/*
	 * the f postponed to the number is because 0.5 is read by Java as a double,
	 * which cannot be assigned to a float. By postponing f you explicitly
	 * declare it is a float value
	 */
	
	/**
	 * The image that will be drawn on the background
	 */
	private Image image; 
	/**
	 * Instance of the enum BackgroundLayout 
	 */
	private BackgroundLayout layout; 
	/**
	 * Paint to allow the creation of a custom painted background 
	 */
	private Paint painter;// this object allows the feature to paint the panel using a color or a set of colors
	/**
	 * Specifies if the component that will be added on this panel will be opaque or transparent
	 */
	private boolean transparentComponents = true; // indicates if the components that will be added should be made transparent or not

	/////CONSTRUCTORS/////
	
	/**
	 * Default constructor with no arguments
	 */
	// default constructor
	public BackgroundedPanel() {
		super();
	}
	
	/**
	 * Constructor to instantiate a BackgroundedPanel with a picture as background
	 * @param image an {@link Image} object to draw on the panel
	 * @param imgLayout the background's layout
	 */
	// define an image as background and a layout for the background
	public BackgroundedPanel(Image image, BackgroundLayout imgLayout) {
		this.setImage(image);
		this.setBackgroundLayout(imgLayout);
		this.setLayout(new BorderLayout());
	}
	
	/**
	 * Constructor to instantiate a panel with a color pattern as background. The pattern is 
	 * specified by the an object implementing the {@link Paint} interface, for instance {@link GradientPaint}.
	 * @param painter a Paint object
	 */
	//
	public BackgroundedPanel(Paint painter) {
		this.setPainter(painter);
		this.setLayout(new BorderLayout());
	}

	////////SETTERS////////
	/**
	 * Setter for the image field. Updates the background image of the panel
	 * @param image the image to set as background
	 */
	public void setImage(Image image) {
		this.image = image;
		repaint();// re-invokes paintComponent() method to update any graphical
					// modification
	}
	
	/**
	 * Setter for the BackgroundLayout property. Changes the background's layout
	 * @param property the new background layout
	 */
	public void setBackgroundLayout(BackgroundLayout property) {
		this.layout = property;
		repaint();
	}
	/**
	 * Setter for the painter object. Changes the painter defining another color pattern
	 * @param painter a new Paint object
	 */
	public void setPainter(Paint painter) {
		this.painter = painter;
		repaint();

	}
	/**
	 * Setter for the transparentComponents field, to specify if an added component should be made transparent
	 * (hence the background is visible) or opaque
	 * @param transparence boolean specifying the transparence property
	 */
	public void setAllComponentTransparent(boolean transparence) {
		this.transparentComponents = transparence;
	}

//////OVERRIDEN AND INHERITED METHODS///////
	
	/**
	 * Paints the background. In this implementation, acts as a convenience method to call the right
	 * paint method of this class depending on the background chosen. 
	 */
	// overrides paintComponent() method to define how the panel object will be
	// represented. It defines
	// how the background will be painted.
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// if there is a painter (i.e, some colors) defined, paints it on the
		// panel
		if (painter != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setPaint(painter);// the call to this method paints the
									// components with the colors specified by
									// the painter
			g2.fill(new Rectangle(0, 0, getSize().width,
					getSize().height));/*
										 * creates a painted rectangle with the
										 * same sizes of the panel
										 */
		}

		if (image == null) {
			return;
		}

		// depending on the enum member passed as parameter, a different method
		// for image drawing is called
		switch (layout) {
		case SCALED:
			drawScaled(g);
			break;
		case TILED:
			drawTiled(g);
			break;
		case CENTERED:
			drawCentered(g);
			break;

		default:
			drawScaled(g);

		}

	}
	
	/**
	 * Convenience method for {@link #add(JComponent)} with no specified costraints.
	 * @param component the component to add
	 */
	public void add(JComponent component) {
		add(component, null);
	}
	
	/**
	 * Convenience method for {@link #add(Component, Object)}. If {@link #transparentComponents} is setted to true,
	 * the component is made transparent before to be added.
	 * @param component the JComponent to be added
	 * @param constraints object espressing layout constraints for the component
	 */
	// checks if the component to be add should be made transparent before been
	// added, then calls the
	// super method
	public void add(JComponent component, Object constraints) {

		if (transparentComponents)
			makeTransparent(component);

		super.add(component, constraints);
	}
	
	/**
	 * Makes the component transparent, i.e non-opaque. For JScrollPane, a
	 * overloaded version of this method is invoked when such an instance is 
	 * passed as parameter.<p>
	 * <b>Note: </b> for Components that use a renderer (JTable, JList..) it is 
	 * necessary to set the rendering transparent in order to obtain a transparent
	 * instance of the Component.
	 * @param component the component to be made opaque
	 */
	// makes the component transparent by setting isOpaque() to false.
	// NOTE: not working for renderized components
	//does not work with components that use a renderer (JList).
	// To make it work, change renderer to be also transparent, for instance
	// changing the background of the table
	private void makeTransparent(JComponent component) {
		component.setOpaque(false);
		if (component instanceof JScrollPane)
			makeTransparent((JScrollPane) component);
	}
	
	
	/**
	 * Helper method for {@link #makeTransparent(JComponent)}. Makes a 
	 * {@link JScrollPane} instance transparent by making the ViewPort
	 * and its content transparent.
	 * @param scrollPane a JScrollPane instance
	 */
	// overloaded version of makeTransparent specific for JScrollPane.
	// Since it is a compounded component, it is necessary to make transparent
	// both the viewport and eventually the component inside the viewport
	private void makeTransparent(JScrollPane scrollPane) {
		JViewport viewport = scrollPane.getViewport();
		viewport.setOpaque(false);
		Component view = viewport.getView();
		if (view instanceof JComponent) {
			makeTransparent((JComponent) view);//makes the viewport view transparent
		}

	}
	
	/**
	 * If the current instance has an image as background, returns its size as preferred size.
	 */
	public Dimension getPreferredSize() {
		if (image != null)
			return getImageSize();
		return super.getPreferredSize();
	}
	
	/**
	 * Helper method to get the size of the background image
	 * @return a Dimension object representing the image size
	 */
	private Dimension getImageSize() {

		return new Dimension(image.getWidth(null), image.getHeight(null));
	}

	
	/////Draw methods to define the image background layout////
	
	/**
	 * Draw the background image as specified by {@link BackgroundLayout#SCALED} layout.
	 * @param g Graphics object. See overridden method
	 */
	// adapts the image to the panel size, so that it always fits also when the
	// panel is redimensioned
	private void drawScaled(Graphics g) {
		// draws the image with a size equals to the panel's size
		g.drawImage(image, 0, 0, getSize().width, getSize().height, null);

	}
	/**
	 * Draw the background image as specified by {@link BackgroundLayout#TILED} layout.
	 * @param g Graphics object. See overridden method
	 */
	// draws the image with its original size as many times as necessary to fill
	// the panel
	// the for loop creates a "matrix" of such an image which fills the whole
	// panel and its counters specify the position. Starts from top left corner (0, 0)
	private void drawTiled(Graphics g) {
		Dimension size = getSize();
		int imgWidth = image.getWidth(null);
		int imgHeight = image.getHeight(null);
		// creates a matrix of images
		for (int i = 0; i < size.width; i = i + imgWidth) {
			for (int j = 0; j < size.height; j = j + imgHeight) {
				g.drawImage(image, i, j, null, null);
			}
		}

	}
	/**
	 * Draw the background image as specified by {@link BackgroundLayout#CENTERED} layout.
	 * @param g Graphics object. See overridden method
	 */
	// the image is drawn at the center of the panel, even with respect though
	// eventual boundaries such as
	// panel layouts, labels or other components which affect the panel layout
	private void drawCentered(Graphics g) {
		Dimension size = getSize();
		Insets borders = getInsets();// get the insets of the panel
		int width = size.width - borders.right - borders.left; // computes the
																// "available
																// space" to
																// draw
																// accounting
																// eventual
																// insets
																// defined by a
																// layout
		int height = size.height - borders.right - borders.left;// same as
																// above. This
																// is done to
																// avoid the
																// image to go
																// "out of
																// bounds" or to
																// cover other
																// stuff
		
		float x = (width - image.getWidth(null)) * DEFAULT_X;// returns the x of
																// the centered
																// image in the
																// panel
		
		float y = (height - image.getHeight(null)) * DEFAULT_Y;// returns the y
																// of the
																// centered
																// image
		
																//the image is drawn with 
																//its top-left corner at (x,y)
		g.drawImage(image, (int) x + borders.left, (int) y + borders.top, this);
	}

}
