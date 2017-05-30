package gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.Login;

import events.CustomerDeal;
import events.Route;

public class ViewMenuListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Business Info")) {
			System.out.println(">>> View Graph");
			
			viewGraph();
		} else if (e.getActionCommand().equals("Find Critical Paths")) {
			if(CustomerDeal.currentDeals.isEmpty()){
				JOptionPane.showMessageDialog(null, "There are no current deals in the database");
				return;
			}
			if(Route.getCurrentRoutes().isEmpty()){
				JOptionPane.showMessageDialog(null, "There are no routes in the database");
				return;
			}
			
			JFrame jf = new JFrame("Critical Path Finder");
			CritPathPanel cpp = new CritPathPanel();
			jf.add(cpp);
			jf.setVisible(true);
			jf.setSize(1000, 800);
			printCriticalPaths();
		}
	}

	private void printCriticalPaths() {
		for (CustomerDeal cd : CustomerDeal.currentDeals) {
			System.out
					.println("-----------------------------------------------");
			String str = "Critical path between " + cd.origin.toString()
					+ " and " + cd.destination.toString() + " with priority "
					+ cd.priority;
			System.out.println(str);
			//
			System.out.println();
			double[][] costs = cd.getCriticalRanges();
			double divisionSize = 1.0 / ((double) costs[0].length - 1);
			for (int i = 0; i < costs[0].length; i++) {
				double weight = i * divisionSize;
				double vol = 1 - weight;
				System.out.println();
				System.out.println("-PACKAGE " + i + " (" + weight + " grams, "
						+ vol + " cc)\n");
				System.out.println("Cost to KPS: " + costs[0][i]);
				System.out.println("Cost to customer: " + costs[1][i]);
			}

		}

	}

	public static void viewGraph() {
		if (checkNotManager()) {
			if (promptLogin()) {
				return;
			}
		}
		JFrame jf = new JFrame("Graph Frame");
		MyPlotPanel mpp = new MyPlotPanel();
		jf.add(mpp);
		jf.setVisible(true);
		jf.setSize(1000, 800);
	}

	private static boolean promptLogin() {
		String message = "The following action requires authentication from the admin.";
		JPanel pop = new JPanel();
		pop.setLayout(new GridLayout(5, 1));
		Font font = new Font("Arial", 0, 12);

		JTextField usernameField = new JTextField(12);
		usernameField.setFont(font);
		usernameField.setText("Username");
		// usernameField.setUI(new HintTextFieldUI("Username", true));

		JPasswordField passwordField = new JPasswordField(12);
		passwordField.setFont(font);
		passwordField.setText("Password");
		// passwordField.setUI(new HintTextFieldUI("Password", true));

		pop.add(new JLabel(message));
		pop.add(Box.createVerticalStrut(15));
		pop.add(usernameField);
		pop.add(Box.createVerticalStrut(15));
		pop.add(passwordField);

		int result = JOptionPane.showConfirmDialog(null, pop, "Login",
				JOptionPane.OK_CANCEL_OPTION);
		if (result == 0) {
			// check if password is good
			return false;
		}
		return true;
	}

	private static boolean checkNotManager() {
		if (Login.getLoginPrivilege() != Login.AccessPrivilege.manager) {
			return true;
		}
		return false;
	}
}
