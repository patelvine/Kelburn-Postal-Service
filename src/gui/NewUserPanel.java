package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NewUserPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JTextField username;
	private JTextField password;

	private EButton login;
	private EButton close;

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		this.login.draw(g);
		this.close.draw(g);

		this.paintComponents(g);
	}

	public NewUserPanel( ){
		setupPanel();
	}

	private void setupPanel() {
		this.setBounds(0, 0, 400,400);
		this.setLayout(null);

		int buffer = 70;
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;

		// username field
		this.username = new JTextField();
		this.username.setSize(250, 30);
		x = (this.getWidth()/2)-(this.username.getWidth()/2);
		y = (this.getHeight()/4)+(buffer*1);
		width = this.username.getWidth();
		height = this.username.getHeight();
		System.out.println("Username: "+x+","+y+","+width+","+height);
		this.username.setBounds(x,y,width,height);
		this.username.setToolTipText("Username");
		this.add(this.username);

		// password field
		this.password = new JTextField();
		this.password.setSize(250, 30);
		x = (this.getWidth()/2)-(this.password.getWidth()/2);
		y = (this.getHeight()/4)+(buffer*2);
		width = this.password.getWidth();
		height = this.password.getHeight();
		System.out.println("Password: "+x+","+y+","+width+","+height);
		this.password.setBounds(x,y,width,height);
		//this.password.set.setInputPrompt("hi");
		this.add(this.password);

		// login button
		x = (this.getWidth()/2)+(150);
		y = (this.getHeight()/4)+(buffer*3);
		width = 50;
		height = 30;
		this.login = new EButton(new Rectangle(x,y,width,height), "Login");


		// close button
		x = (this.getWidth()/2)-(150+50);
		y = (this.getHeight()/4)+(buffer*3);
		width = 50;
		height = 30;
		this.close = new EButton(new Rectangle(x,y,width,height), "Close");

		this.setMinimumSize(new Dimension(400, 400));
		this.setMaximumSize(new Dimension(400, 400));
		this.setSize(new Dimension(400, 400));

	}


	public static void main(String []args){
		JFrame jf = new JFrame();
		NewUserPanel nup = new NewUserPanel();
		jf.add(nup);
		jf.pack();
		jf.setVisible(true);


	}

}
