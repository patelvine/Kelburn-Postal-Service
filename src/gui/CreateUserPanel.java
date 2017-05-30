package gui;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.Login;
import main.Login.AccessPrivilege;

public class CreateUserPanel {

	/**
	 * @param args
	 */

	private static ImageIcon icon = new ImageIcon(("res/icon.png"), "KPS Logo");
	private GUI gui;

	public CreateUserPanel(GUI gui) {
		this.gui = gui;
		createUser();
	}

	public boolean createUser() {
		JPanel pop = new JPanel();
		String message = "New User";
		pop.setLayout(new GridLayout(7, 7));
		Font font = new Font("Arial", 0, 12);
		Font boldFont = new Font("Arial", Font.BOLD, 12);

		JLabel header = new JLabel(message);
		header.setFont(boldFont);

		JTextField usernameField = new JTextField(12);
		usernameField.setFont(font);
		// usernameField.setUI(new HintTextFieldUI("Username", true));

		JTextField usernameFieldAdmin = new JTextField(12);
		usernameFieldAdmin.setFont(font);

		JPasswordField passwordFieldAdmin = new JPasswordField(12);
		passwordFieldAdmin.setFont(font);

		JPasswordField passwordField = new JPasswordField(12);
		passwordField.setFont(font);
		// passwordField.setUI(new HintTextFieldUI("Password", true));

		JPasswordField passwordField2 = new JPasswordField(12);
		passwordField2.setFont(font);

		JCheckBox authorityCheckBox = new JCheckBox();
		// authorityCheckBox.addItemListener(this);

		pop.add(header);
		pop.add(Box.createVerticalStrut(15));
		pop.add(new JLabel("Username:"));
		pop.add(usernameField);
		pop.add(new JLabel("Password:"));
		pop.add(passwordField);
		pop.add(new JLabel("Comfirm Password:"));
		pop.add(passwordField2);
		pop.add(new JLabel("New Admin"));
		pop.add(authorityCheckBox);
		// pop.add(Box.createVerticalStrut(15));
		pop.add(new JLabel("Current Admins Username:"));
		pop.add(usernameFieldAdmin);
		pop.add(new JLabel("Current Admins Password:"));
		pop.add(passwordFieldAdmin);

		Object[] options = { "Register", "Back", "Quit" };

		int result = JOptionPane.showOptionDialog(null, pop, "Sign in",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				icon, options, options[0]);

		int username = usernameField.getText().length();
		int passwordLen = passwordField.getPassword().length;
		int passwordConfirmLen = passwordField2.getPassword().length;
		int adminUsername = usernameFieldAdmin.getText().length();
		int adminPass = passwordFieldAdmin.getPassword().length;

		if (result == 0) {
			if (username > 0) {
				if (Login.checkUsername(convertToCharArray(usernameField
						.getText()))) {
					dialog("Username already exists", "Existing Username");
					new CreateUserPanel(gui);
				} else {
					if (passwordLen >= 8 && passwordConfirmLen >= 8) {
						if (passwordLen == passwordConfirmLen) {
							for (int i = 0; i < passwordLen; i++) {
								if (passwordField.getPassword()[i] != passwordField2
										.getPassword()[i]) {
									dialog("Passwords do not match",
											"Incorrect Passwords");
									new CreateUserPanel(gui);
								}
							}

							if (adminPass > 0 && adminUsername > 0) {
								Login.authenticate(
										convertToCharArray(usernameFieldAdmin
												.getText()), passwordFieldAdmin
												.getPassword());
								if (Login.getLoginPrivilege() == AccessPrivilege.manager) {

									if (Login
											.registerNewUser(
													convertToCharArray(usernameField
															.getText()),
													passwordField.getPassword(),
													getNewUserAuthority(authorityCheckBox))) {
										// register success
										JOptionPane.showMessageDialog(null,
											    "Registration was successful",
											    "Welcome :)",
											    JOptionPane.INFORMATION_MESSAGE);
									} else {
										// register fail
										dialog("Registration failed",
												"Sorry :(");
									}
									Login.logOut();
								} else {
									dialog("Incorrect admin authentication",
											"Incorrect Passwords");
								}

							} else {
								dialog("Requires authentication from an admin\nIncorrect username or password was entered",
										"Incorrect Passwords");
								new CreateUserPanel(gui);
							}
						} else {
							dialog("Passwords do not match",
									"Incorrect Passwords");
							new CreateUserPanel(gui);
						}
					} else {
						dialog("Passwords must be at least 8 characters long",
								"Incorrect Passwords");
						new CreateUserPanel(gui);
					}
				}
			} else if (result == 1) {
				new LoginPanel(this.gui);
			} else if (result == 2) {
				System.exit(0);
			}
		}
		return true;
	}

	private AccessPrivilege getNewUserAuthority(JCheckBox authorityCheckBox) {
		if (authorityCheckBox.isSelected()) {
			return AccessPrivilege.manager;
		}
		return AccessPrivilege.clerk;
	}

	public char[] convertToCharArray(String detail) {
		char[] array = new char[detail.length()];
		for (int i = 0; i < detail.length(); i++) {
			array[i] = detail.charAt(i);
		}
		return array;
	}

	/**
	 * 
	 * @param message
	 * @param title
	 */
	public void dialog(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title,
				JOptionPane.ERROR_MESSAGE);
	}
}