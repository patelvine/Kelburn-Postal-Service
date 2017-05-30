package gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.Login;

public class OptionsMenuListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Change password")) {
			System.out.println(">>> Change Password");
			changePassword();
		} else if (e.getActionCommand().equals("Register New User")) {
			if (Login.getLoginPrivilege() == Login.AccessPrivilege.manager) {
				new CreateUserPanel(null);
			}
		} else if (e.getActionCommand().equals("Remove user")) {
			if (Login.getLoginPrivilege() == Login.AccessPrivilege.manager) {
				removeUser();
			}
		}
	}

	public void changePassword() {
		JPanel pop = new JPanel();
		String message = "Change password";
		pop.setLayout(new GridLayout(4, 1));
		Font font = new Font("Arial", 0, 12);
		Font boldFont = new Font("Arial", Font.BOLD, 12);

		JLabel header = new JLabel(message);
		header.setFont(boldFont);

		JTextField usernameField = new JTextField(12);
		usernameField.setFont(font);

		JPasswordField passwordField2 = new JPasswordField(12);
		passwordField2.setFont(font);

		JPasswordField passwordField3 = new JPasswordField(12);
		passwordField3.setFont(font);

		pop.add(header);
		pop.add(Box.createVerticalStrut(15));
		pop.add(new JLabel("Current Username:"));
		pop.add(usernameField);
		pop.add(new JLabel("Old Password:"));
		pop.add(passwordField2);
		pop.add(new JLabel("New Password:"));
		pop.add(passwordField3);

		int result = JOptionPane.showConfirmDialog(null, pop,
				"Password Change", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		int usernameLen = usernameField.getText().length();
		int newPassLen = passwordField2.getPassword().length;
		int newPassConfirmLen = passwordField3.getPassword().length;

		if (result == 0) {
			if (usernameLen == 0 || newPassLen == 0 || newPassConfirmLen == 0) {
				dialog("Must complete each section", "Insufficient information");
				changePassword();
			} else if (newPassLen <= 7 || newPassConfirmLen <= 7) {
				dialog("Passwords must be at least 8 characters long",
						"Incorrect Password Length");
				changePassword();
			} else {
				Login.changePassword(
						convertToCharArray(usernameField.getText()),
						passwordField2.getPassword(),
						passwordField3.getPassword());

			}
		}
	}

	public void removeUser() {
		JPanel pop = new JPanel();
		String message = "Enter user to be removed";
		pop.setLayout(new GridLayout(2, 1));
		Font font = new Font("Arial", 0, 12);
		Font boldFont = new Font("Arial", Font.BOLD, 12);

		JLabel header = new JLabel(message);
		header.setFont(boldFont);

		JTextField usernameField = new JTextField(12);
		usernameField.setFont(font);

		pop.add(header);
		pop.add(Box.createVerticalStrut(15));
		pop.add(new JLabel("Username:"));
		pop.add(usernameField);

		int result = JOptionPane.showConfirmDialog(null, pop,
				"Password Change", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (result == 0) {
			int confirm = JOptionPane.showConfirmDialog(null,
					"User will be permanently deleted are you sure?",
					"Confirmation", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (confirm == 0) {
				if (!Login.checkUsername(convertToCharArray(usernameField
						.getText()))) {
					dialog("User does not exist", "Non-Existing User");
					removeUser();
				} else {
					if (Login.removeUser(convertToCharArray(usernameField
							.getText()))) {
						JOptionPane.showMessageDialog(null,
								"User successfully removed", "User removed",
								JOptionPane.INFORMATION_MESSAGE);
					}

				}
			}
		}
	}

	public char[] convertToCharArray(String detail) {
		char[] array = new char[detail.length()];
		for (int i = 0; i < detail.length(); i++) {
			array[i] = detail.charAt(i);
		}
		return array;
	}

	public void dialog(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title,
				JOptionPane.ERROR_MESSAGE);
	}
}
