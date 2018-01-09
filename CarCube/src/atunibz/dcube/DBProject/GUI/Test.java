package atunibz.dcube.DBProject.GUI;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Test {

	public static void main(String[] args) {
			ColorsPanel p = new ColorsPanel();
			JFrame f = new JFrame();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.getContentPane().add(p);
			f.pack();
			f.setVisible(true);
	}

}
