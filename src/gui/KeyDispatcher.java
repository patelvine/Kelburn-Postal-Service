package gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.Login;

/**
 * Class for key events and short cuts
 *
 * @author Vinesh Patel
 *
 */

public class KeyDispatcher implements KeyEventDispatcher {
	private ActionEvent ae;
	FileMenuListener fml = new FileMenuListener();
	ViewMenuListener vml = new ViewMenuListener();
	OptionsMenuListener oml = new OptionsMenuListener();
	HelpMenuListener hml = new HelpMenuListener();

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_PRESSED) {
			if (e.isControlDown()) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_M:
					this.ae = new ActionEvent(this, 0, "New Mail");
					fml.actionPerformed(ae);
					break;
				case KeyEvent.VK_R:
					if (checkNotManager()) {
						if (promptLogin()) {
							return false;
						}
					}
					this.ae = new ActionEvent(this, 0, "New Route");
					fml.actionPerformed(ae);
					break;
				case KeyEvent.VK_P:
					if (checkNotManager()) {
						if (promptLogin()) {
							return false;
						}
					}
					this.ae = new ActionEvent(this, 0, "New Customer Price");
					fml.actionPerformed(ae);
					break;
				case KeyEvent.VK_D:
					if (checkNotManager()) {
						if (promptLogin()) {
							return false;
						}
					}
					this.ae = new ActionEvent(this, 0, "Discontinue Route");
					fml.actionPerformed(ae);
					break;
				case KeyEvent.VK_F:
					if (checkNotManager()) {
						if (promptLogin()) {
							return false;
						}
					}
					this.ae = new ActionEvent(this, 0, "Discontinue Customer Price");
					fml.actionPerformed(ae);
					break;
				case KeyEvent.VK_L:
					this.ae = new ActionEvent(this, 0, "Load");
					fml.actionPerformed(ae);
					break;
				case KeyEvent.VK_S:
					this.ae = new ActionEvent(this, 0, "Save");
					fml.actionPerformed(ae);
					break;
				case KeyEvent.VK_Q:
					this.ae = new ActionEvent(this, 0, "Quit");
					fml.actionPerformed(ae);
					break;
				case KeyEvent.VK_G:
					if (checkNotManager()) {
						if (promptLogin()) {
							return false;
						}
					}
					this.ae = new ActionEvent(this, 0, "View Graph");
					vml.actionPerformed(ae);
					break;
				case KeyEvent.VK_O:
					this.ae = new ActionEvent(this, 0, "Find Critical Paths");
					vml.actionPerformed(ae);
					break;
				case KeyEvent.VK_C:
					this.ae = new ActionEvent(this, 0, "Change password");
					oml.actionPerformed(ae);
					break;
				case KeyEvent.VK_H:
					this.ae = new ActionEvent(this, 0, "Hotkeys");
					hml.actionPerformed(ae);
					break;
				}
			} else if (e.getID() == KeyEvent.KEY_RELEASED) {
			} else if (e.getID() == KeyEvent.KEY_TYPED) {
			}
		}
		return false;
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
}
