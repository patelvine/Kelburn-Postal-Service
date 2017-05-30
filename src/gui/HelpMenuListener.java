package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * This represents a business event that changes a customer price.
 *
 * @author Vinesh, Emmanuel
 *
 */

public class HelpMenuListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Hotkeys")){
			System.out.println(">>> Hotkeys");
			hotkeyHelp();
		}else if(e.getActionCommand().equals("_")){

		}
	}

	private void hotkeyHelp() {
		//String message = "Hotkeys";
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(25, 2));
		//panel.setBackground(Color.blue);
		//Font font = new Font("Arial", 0, 12);

		panel.add(new JLabel("File"));
		panel.add(Box.createVerticalStrut(15));
		panel.add(new JSeparator());
		panel.add(Box.createVerticalStrut(15));
		panel.add(new JLabel("New Mail"));
		panel.add(new JLabel("        Ctrl + M"));
		panel.add(new JLabel("New Route"));
		panel.add(new JLabel("        Ctrl + R"));
		panel.add(new JLabel("Customer Price"));
		panel.add(new JLabel("        Ctrl + P"));
		panel.add(new JLabel("Discontinue Route"));
		panel.add(new JLabel("        Ctrl + D"));
		panel.add(new JLabel("Discontinue Customer Price"));
		panel.add(new JLabel("        Ctrl + F"));
		panel.add(new JLabel("Load"));
		panel.add(new JLabel("        Ctrl + L"));
		panel.add(new JLabel("Save"));
		panel.add(new JLabel("        Ctrl + S"));
		panel.add(new JLabel("Quit"));
		panel.add(new JLabel("        Ctrl + Q"));
		panel.add(Box.createVerticalStrut(15));
		panel.add(Box.createVerticalStrut(15));
		panel.add(new JLabel("View"));
		panel.add(Box.createVerticalStrut(15));
		panel.add(new JSeparator());
		panel.add(Box.createVerticalStrut(15));
		panel.add(new JLabel("View Graph"));
		panel.add(new JLabel("        Ctrl + G"));
		panel.add(new JLabel("Find Critical Paths"));
		panel.add(new JLabel("        Ctrl + O"));
		panel.add(Box.createVerticalStrut(15));
		panel.add(Box.createVerticalStrut(15));
		panel.add(new JLabel("Options"));
		panel.add(Box.createVerticalStrut(15));
		panel.add(new JSeparator());
		panel.add(Box.createVerticalStrut(15));
		panel.add(new JLabel("Change Password"));
		panel.add(new JLabel("        Ctrl + C"));
		panel.add(Box.createVerticalStrut(15));
		panel.add(Box.createVerticalStrut(15));
		panel.add(new JLabel("Help"));
		panel.add(Box.createVerticalStrut(15));
		panel.add(new JSeparator());
		panel.add(Box.createVerticalStrut(15));
		panel.add(new JLabel("Hotkeys"));
		panel.add(new JLabel("        Ctrl + H"));

//		JOptionPane o = new JOptionPane();
//		o.setBackground(new Color(255,0,0));
		JOptionPane.showMessageDialog(null,panel,"Hotkeys",JOptionPane.PLAIN_MESSAGE);
	}
}