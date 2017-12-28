package atunibz.dcube.DBProject.GUI;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Test {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame ("CarCube");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1435, 800));
		frame.getContentPane().add(new CustomerInfoPanel("ZZAYNN81R10Z336E"));
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(true);

	}

}
