package atunibz.dcube.DBProject.GUI;

import java.awt.*;
import java.awt.image.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.BevelBorder;;

public class ColorsPanel extends JPanel {
	
	private ArrayList<ColorCheckBox> boxes;
	private ArrayList<String> colorKeys;
	private Map<String, Color> colorMap;
	private int numberOfColors = 0;
	private String giveMeColors = "select * from color";
	private GridBagConstraints c;
	private Connection conn = DatabaseConnection.getDBConnection().getConnection();
	
	public ColorsPanel(){
		colorKeys = new ArrayList<>();
		colorMap = new LinkedHashMap<>();
		boxes = new ArrayList<>();
		c = new GridBagConstraints();
		this.populateColorMap();
		numberOfColors = colorMap.size();
		this.populateColorBoxes();
		this.setLayout(new GridBagLayout());
		this.populateGrid(7 , 19);
	//	this.add(new ColorCheckBox(15, 150, 0, "Green"));
		
	}
	
	private void populateGrid(int rows, int cols) {
		int i = 0, j = 0;
		for(ColorCheckBox currBox : boxes) {
			c.gridx = i;
			c.gridy = j;
			c.insets = new Insets(3, 3, 3, 3);
			this.add(currBox, c);
			if(i < rows) {
				++i;
			}
			else {//go to the line below
				i = 0;
				j++;
			}
			
		}
	}
	
	private void populateColorMap() {
		
		try {
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(giveMeColors);
			while(rs.next()) {
				Color c = this.getColor(rs.getString(3));
				String name = rs.getString(2);
				colorMap.put(name, c);
				colorKeys.add(rs.getString("color_code"));
				//get the color from the string
				//get the color name
				//insert color in map
			}
			s.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private void populateColorBoxes() {
		Iterator it = colorMap.entrySet().iterator();
		int index = 0;
		while(it.hasNext()) {
			Map.Entry<String, Color> pair = (Map.Entry)it.next();
			System.out.println(pair.getKey() + " - " + pair.getValue().toString());
			ColorCheckBox colorBox = new ColorCheckBox(pair.getValue(), pair.getKey(), colorKeys.get(index));
			//this.add(colorBox);
			boxes.add(colorBox);

		}
	}
	
	
	//returns the color given the string as in DB, i.e in form (int, int, int)
	private Color getColor(String colorDesc) {
		//form "(r, g, b)"
		String temp = colorDesc.replaceAll("\\s+", "");//remove whitespaces
		String temp2 = temp.substring(1, temp.length()-1);//strip parentheses
		//now I have "r, g, b"
		String[] params = temp2.split(",");//split depending on ,
		return new Color(Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]));
	}
	
	
	public ArrayList<ColorCheckBox> getColorCheckBoxes(){
		return boxes;
	}
	
	public ArrayList<String> getColorKeys(){
		return colorKeys;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public class ColorCheckBox extends JPanel{
		private ColorBox color;
		private JCheckBox cb;
		private String colorCode = null;
		public ColorCheckBox(int r, int g, int b, String colorName, String colorCodeInit){
			colorCode = colorCodeInit;
			color = new ColorBox(r, g, b);
			cb = new JCheckBox();
			FlowLayout layout = new FlowLayout();
			layout.setHgap(0);
			layout.setVgap(0);
			this.setLayout(layout);
			this.add(color);
			this.add(cb);
			this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true), BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
			color.setColorName(colorName);
			color.setToolTipText(colorName);
		}
		
		public ColorCheckBox(Color color, String colorName, String colorCodeInit) {
			this(color.getRed(), color.getGreen(), color.getBlue(), colorName,colorCodeInit);
		}
		
		public String getColorName() {
			return color.getColorName();
		}
		
		public JCheckBox getCheckBox() {
			return cb;
		}
		
		public String getColorCode() {
			return colorCode;
		}
	}
	
	
	private class ColorBox extends JCheckBox{
		
		private BufferedImage img;
		private Graphics2D g2d;
		private ImageIcon colorImage;
		private String color;
		
		public ColorBox(int r, int g, int b) {
			super();
			img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
			g2d = img.createGraphics();
			g2d.setPaint(new Color(r, g, b));
			g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
			colorImage = new ImageIcon(img);
			this.setSelectedIcon(colorImage);
			this.setIcon(colorImage);
			this.setBackground(new Color(r, g, b));
			this.setOpaque(true);
		}
		
		public void setColorName(String colorName) {
			this.color = colorName;
		}
		
		public String getColorName() {
			return color;
		}
	}


	private static void main(String[] args) {
		ColorsPanel p = new ColorsPanel();
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(p);
		f.pack();
		f.setVisible(true);
	}


}