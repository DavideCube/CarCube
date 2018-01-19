package atunibz.dcube.DBProject.GUI;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test {

	public static void main(String[] args) {
			JPanel p = new CarBarChart();
			JFrame f = new JFrame();
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.getContentPane().add(p);
			f.pack();
			f.setVisible(true);
	}

}
