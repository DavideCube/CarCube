package atunibz.dcube.DBProject.GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import atunibz.dcube.DBProject.configuration.AppResources;

public class StakeholdersPanel extends JPanel{
	private JPanel shPanel, titlePanel, comboPanel;
	private String[] caseCusCus, caseSup, caseBoth;
	private JComboBox cus_sup, criteria;
	
	public StakeholdersPanel() {
		shPanel = new JPanel();
		shPanel.setLayout(new BoxLayout(shPanel, BoxLayout.Y_AXIS));
		shPanel.add((Box.createRigidArea(new Dimension(0, 30))));
		// Panel containing the beautiful logo
		titlePanel = AppResources.carCubePanel();
		shPanel.add(titlePanel);
		shPanel.setOpaque(false);
		
		shPanel.add((Box.createRigidArea(new Dimension(0, 50))));
		
		// combo box to choose supplier or customer
		comboPanel = new JPanel();
		comboPanel.setLayout(new BoxLayout(comboPanel, BoxLayout.X_AXIS));
		
		String [] stakeH = {"Suppliers", "Customers", "Both"};
		cus_sup = new JComboBox (stakeH);
		comboPanel.add(cus_sup);
		cus_sup.addActionListener(new SHlistener());
		shPanel.add(comboPanel);
		//combo box to choose criteria
		String [] machecazzoserve = {"Select stakeholder first"};
		caseCusCus = new String[3];
		caseCusCus[0] = "Name";
		caseCusCus[1] = "Surname";
		caseCusCus[2] = "Tax Code";
		
		caseSup = new String[2];
		caseSup[0] = "Vat";
		caseSup[1] = "Name";
		//caseSup[2] = "";
		
		criteria = new JComboBox (machecazzoserve);
		criteria.setEnabled(false);
		comboPanel.add(criteria);
		
		add(shPanel);
	}
	
	private class SHlistener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			
			String selected = (String) cus_sup.getSelectedItem();
			
			if(selected.compareTo("Suppliers") == 0) {
				criteria.setModel(new DefaultComboBoxModel(caseSup));
			} else if(selected.compareTo("Customers") == 0) {
				criteria.setModel(new DefaultComboBoxModel(caseCusCus));
			} else {
				
			}
			
			criteria.setEnabled(true);
		}
		
	}

}
