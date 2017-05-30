

package gui;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.Login;

// chris, eman, vinesh
public class LoginPanel {

	private static ImageIcon icon = new ImageIcon(("res/icon.png"), "KPS Logo");
	private GUI gui;

	public LoginPanel(GUI gui) {
		this.gui = gui;
		setupPanel();
	}

	public void setupPanel() {
		JPanel pop = new JPanel();
		pop.setLayout(new GridLayout(2, 1));
		Font font = new Font("Arial", 0, 12);

		JTextField usernameField = new JTextField(12);
		usernameField.setFont(font);
		// usernameField.setUI(new HintTextFieldUI("Username", true));

		JPasswordField passwordField = new JPasswordField(12);
		passwordField.setFont(font);
		// passwordField.setUI(new HintTextFieldUI("Password", true));

		pop.add(new JLabel("Username (default is 'admin')"));
		pop.add(usernameField);
		pop.add(new JLabel("Password (default is 'password')"));
		pop.add(passwordField);

		Object[] options = { "Sign in", "Register", "Quit" };

		int result = JOptionPane.showOptionDialog(null, pop, "Sign in",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				icon, options, options[0]);

		if (result == 0){
			System.out.println("login");
			if(usernameField.getText().length() == 0 || passwordField.getPassword().length == 0){
				JOptionPane.showMessageDialog(null, "Must enter both username and password",
						"Incorrect Login", JOptionPane.ERROR_MESSAGE);
				new LoginPanel(gui);
			}

			if (Login.authenticate(convertToCharArray(usernameField.getText()), passwordField.getPassword())){
			}
			else{
				JOptionPane.showMessageDialog(null, "Incorrect username or password.",
						"Incorrect Login", JOptionPane.ERROR_MESSAGE);
				new LoginPanel(gui);
			}

		}else if(result == 1){
			System.out.println("Register");
			new CreateUserPanel(this.gui);
		}else if(result == 2){
			System.out.println("Quit");
			System.exit(0);
		}
	}

	public char[] convertToCharArray(String detail){
		char[] array = new char[detail.length()];
		for(int i = 0 ; i < detail.length() ; i++){
			array[i] = detail.charAt(i);
		}
		return array;
	}
}
