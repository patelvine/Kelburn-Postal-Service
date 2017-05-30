package gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.Context;
import main.Login;
import main.Main;
import xml.XMLException;
import xml.XMLManager;
import events.Company;
import events.Location;
import events.Priority;
import events.TransportType;

public class FileMenuListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("New Mail")) {
			System.out.println(">>> New Mail");
			newMail();
		} else if (e.getActionCommand().equals("Create/Change Route")) {
			System.out.println(">>> New Route");
			newRoute();
		} else if (e.getActionCommand().equals("Discontinue Route")) {
			System.out.println(">>> Discontinue Route");
			discontinueRoute();
		} else if (e.getActionCommand().equals("Discontinue Customer Price")) {
			System.out.println(">>> Discontinue Customer Price");
			discontinueCustomerPrice();
		} else if (e.getActionCommand().equals("Create/Change Customer Price")) {
			System.out.println(">>> New Customer Price");
			newCustomerPriceUpdateEvent();
		} else if (e.getActionCommand().equals("Load")) {
			System.out.println(">>> Load");
			load();
		} else if(e.getActionCommand().equals("Logout")){
			System.out.println(">>> Logout");
			logout();
			//new Context();
		} else if (e.getActionCommand().equals("Save")) {
			System.out.println(">>> Save");
			save();
		} else if (e.getActionCommand().equals("Quit")) {
			System.out.println(">>> Quit");
			quit();
		}else if (e.getActionCommand().equals("Enable Soundtrack")){
			Main.importantOperation();
		}

	}

	private static void logout() {
		Login.logOut();
	}

	/**
	 * Brings up some dialog to help create new mail event.
	 */
	public static void newMail() {
		ArrayList<JComponent> components = new ArrayList<JComponent>();

		JTextField grams = new JTextField(); // grams
		grams.setText("0.0");
		grams.setName("Weight (in grams)");
		components.add(grams);

		JTextField size = new JTextField(); // size
		size.setText("0.0");
		size.setName("Size (in cubic centimetres)");
		components.add(size);

		JComboBoxWider priority = new JComboBoxWider(Priority.values());// priority
		priority.setName("Priority");
		components.add(priority);

		JComboBoxWider origin = new JComboBoxWider(Location.allLocationsArray());// origin
		origin.setName("Origin");
		components.add(origin);

		JComboBoxWider destination = new JComboBoxWider(
				Location.allLocationsArray());// destination
		destination.setName("Destination");
		components.add(destination);

		new NewInformationDialog(NewInformationDialog.NEW_MAIL_EVENT,
				components);

	}

	public static void newRoute() {
		if (checkNotManager()) {
			if (promptLogin()) {
				return;
			}
		}
		System.out.println("Got here");

		ArrayList<JComponent> components = new ArrayList<JComponent>();

		JTextField dollarsPerGram = new JTextField();// grams
		dollarsPerGram.setText("0.0");
		dollarsPerGram.setName("Dollars Per Gram");
		components.add(dollarsPerGram);

		JTextField dollarsPerCubicCentimetre = new JTextField();// size
		dollarsPerCubicCentimetre.setText("0.0");
		dollarsPerCubicCentimetre.setName("Dollars Per Cubic Centimetre");
		components.add(dollarsPerCubicCentimetre);

		JTextField maxGrams = new JTextField();
		maxGrams.setText("0.0");
		maxGrams.setName("Max Weight (in grams)");
		components.add(maxGrams);

		JTextField maxSize = new JTextField();
		maxSize.setText("0.0");
		maxSize.setName("Max Size (in cubic centimetres)");
		components.add(maxSize);

		JTextField duration = new JTextField();
		duration.setText("0.0");
		duration.setName("Duration");
		components.add(duration);

		JTextField departureInterval = new JTextField();
		departureInterval.setText("0.0");
		departureInterval.setName("Departure Interval");
		components.add(departureInterval);

		JComboBoxWider origin = new JComboBoxWider(Location.allLocationsArray());// origin
		origin.setName("Origin");
		components.add(origin);

		JComboBoxWider destination = new JComboBoxWider(
				Location.allLocationsArray());// destination
		destination.setName("Destination");
		components.add(destination);

		JComboBoxWider transportType = new JComboBoxWider(
				TransportType.values());
		transportType.setName("Transport Type");
		components.add(transportType);

		JComboBoxWider company = new JComboBoxWider(Company.allCompaniesArray());
		company.setName("Company");
		components.add(company);

		new NewInformationDialog(NewInformationDialog.NEW_ROUTE_EVENT,
				components);

	}

	private static boolean checkNotManager() {
		if (Login.getLoginPrivilege() != Login.AccessPrivilege.manager) {
			return true;
		}
		return false;
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
			if(Login.authenticate(usernameField.getText().toCharArray(), passwordField.getPassword())){
				return false;
			}
		}
		JOptionPane.showMessageDialog(null, "Login Failed");
		return true;
	}

	@SuppressWarnings("unused")
	private static boolean createLogin() {
		String message = "New User";
		JPanel pop = new JPanel();
		pop.setLayout(new GridLayout(5, 2));
		Font font = new Font("Arial", 0, 12);
		Font boldFont = new Font("Arial", Font.BOLD, 12);

		JLabel header = new JLabel(message);
		header.setFont(boldFont);

		JTextField usernameField = new JTextField(12);
		usernameField.setFont(font);
		usernameField.setText("Username");
		// usernameField.setUI(new HintTextFieldUI("Username", true));

		JPasswordField passwordField = new JPasswordField(12);
		passwordField.setFont(font);
		passwordField.setText("Password");
		// passwordField.setUI(new HintTextFieldUI("Password", true));

		JPasswordField passwordField2 = new JPasswordField(12);
		passwordField2.setFont(font);
		passwordField2.setText("Password");

		JCheckBox authorityCheckBox = new JCheckBox();

		pop.add(header);
		pop.add(Box.createVerticalStrut(15));
		pop.add(new JLabel("Admin"));
		pop.add(authorityCheckBox);
		pop.add(new JLabel("Username:"));
		pop.add(usernameField);
		pop.add(new JLabel("Password:"));
		pop.add(passwordField);
		pop.add(new JLabel("Comfirm Password:"));
		pop.add(passwordField2);

		int result = JOptionPane.showConfirmDialog(null, pop,
				"Create a New User", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.CANCEL_OPTION) {

			return false;
		} else {
			System.out.println("p1:" + passwordField.getPassword().toString() + " p2:"
					+ passwordField2.getPassword().toString());
			boolean match = true;
			if (passwordField.getPassword().length == passwordField2
					.getPassword().length) {

				for (int i = 0; i < passwordField.getPassword().length; i++) {
					System.out.println((passwordField.getPassword()[i]));
					System.out.println((passwordField2.getPassword()[i]));
					if (passwordField.getPassword()[i] != passwordField2
							.getPassword()[i]) {
						System.out.println("passwords conflict");
						return false;
					}
				}

				System.out.println("passwords match");

				// create user here

				return true;
			} else {
				System.out.println("passwords conflict");
				return false;
			}

		}

	}

	public static void newCustomerPriceUpdateEvent() {
		if (checkNotManager()) {
			if (promptLogin()) {
				return;
			}
		}

		ArrayList<JComponent> components = new ArrayList<JComponent>();

		JTextField dollarsPerGram = new JTextField();// grams
		dollarsPerGram.setText("0.0");
		dollarsPerGram.setName("Dollars Per Gram");
		components.add(dollarsPerGram);

		JTextField dollarsPerCubicCentimetre = new JTextField();// size
		dollarsPerCubicCentimetre.setText("0.0");
		dollarsPerCubicCentimetre.setName("Dollars Per Cubic Centimetre");
		components.add(dollarsPerCubicCentimetre);

		JComboBoxWider priority = new JComboBoxWider(Priority.values());//
		// priority
		priority.setName("Priority");
		components.add(priority);

		JComboBoxWider origin = new JComboBoxWider(Location.allLocationsArray());// origin
		origin.setName("Origin");
		components.add(origin);

		JComboBoxWider destination = new JComboBoxWider(
				Location.allLocationsArray());// destination
		destination.setName("Destination");
		components.add(destination);

		new NewInformationDialog(NewInformationDialog.NEW_CUSTOMER_PRICE,
				components);
	}

	public static void discontinueRoute() {
		if (checkNotManager()) {
			if (promptLogin()) {
				return;
			}
		}

		ArrayList<JComponent> components = new ArrayList<JComponent>();

		JComboBoxWider origin = new JComboBoxWider(Location.allLocationsArray());// origin
		origin.setName("Origin");
		components.add(origin);

		JComboBoxWider destination = new JComboBoxWider(
				Location.allLocationsArray());// destination
		destination.setName("Destination");
		components.add(destination);

		JComboBoxWider transportType = new JComboBoxWider(
				TransportType.values());
		transportType.setName("Transport Type");
		components.add(transportType);

		JComboBoxWider company = new JComboBoxWider(Company.allCompaniesArray());
		company.setName("Company");
		components.add(company);

		new NewInformationDialog(NewInformationDialog.NEW_DISCONTINUE_ROUTE,
				components);

	}

	@Deprecated
	public static void load() {

	}

	public static void save() {
		try {
			XMLManager.generateLogFile();
		} catch (XMLException e) {
			e.printStackTrace();
		}
	}

	public static void quit() {
		int n = JOptionPane.showConfirmDialog(null,
				"Would you like to close the application?");
		if (n == 0) {
			System.exit(0);
		}
	}

	public static void discontinueCustomerPrice() {
		if (checkNotManager()) {
			if (promptLogin()) {
				return;
			}
		}

		ArrayList<JComponent> components = new ArrayList<JComponent>();

		JComboBoxWider origin = new JComboBoxWider(Location.allLocationsArray());// origin
		origin.setName("Origin");
		components.add(origin);

		JComboBoxWider destination = new JComboBoxWider(
				Location.allLocationsArray());// destination
		destination.setName("Destination");
		components.add(destination);

		JComboBoxWider priority = new JComboBoxWider(Priority.values());// priority
		priority.setName("Priority");
		components.add(priority);

		new NewInformationDialog(NewInformationDialog.NEW_DISCONTINUE_CUSPRICE,
				components);

	}

}
