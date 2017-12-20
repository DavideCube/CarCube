package atunibz.dcube.DBProject.GUI;
import java.awt.Dimension;

import javax.swing.JFrame;

public class CarCubeManager {

	public static void main(String[] args) {
		JFrame frame = new JFrame ("CarCube");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1435, 800));
		frame.getContentPane().add(MainPanel.getMainPanel());
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		//Prova commit Sbetti--- Ciao sono Sbetti
		
	}

}
