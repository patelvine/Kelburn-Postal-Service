package gui;

import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MasterFrame extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final Image i = new ImageIcon("res/icon.png").getImage();

	private JMenuBar menuBar;

	private JMenu fileMenu;
	private JMenu newMenu;
	private JMenuItem newMail;
	private JMenuItem newRoute;
	private JMenuItem discontinueRoute;
	private JMenuItem discontinueCusPrice;
	private JMenuItem logout;
	private JMenuItem save;
	private JMenuItem quit;

	private JMenu viewMenu;
	private JMenuItem viewGraph;

	private JMenu optionsMenu;
	private JMenuItem register;
	private JMenuItem passwordChange;
	private JMenuItem removeUser;

	private JMenu helpMenu;
	private JMenuItem hotkeys;

	private ArrayList<JMenuItem> fileMenuItems;
	private ArrayList<JMenuItem> viewMenuItems;
	private ArrayList<JMenuItem> optionsMenuItems;
	private ArrayList<JMenuItem> helpMenuItems;

	private FileMenuListener fileMenuListener;
	private ViewMenuListener viewMenuListener;
	private OptionsMenuListener optionsMenuListener;
	private HelpMenuListener helpMenuListener;

	private JMenuItem newCustomerPrice;

	private JMenuItem findCriticalPathsButton;

	private JMenuItem enableSoundrack;

	@Override
	public void paint(Graphics gr){
		//this.paintAll(gr);


		menuBar.paintComponents(gr);
		this.paintComponents(gr);
	}

	public MasterFrame() {
		this.fileMenuItems = new ArrayList<JMenuItem>();
		this.viewMenuItems = new ArrayList<JMenuItem>();
		this.optionsMenuItems = new ArrayList<JMenuItem>();
		this.helpMenuItems = new ArrayList<JMenuItem>();

		setupFrame();
		setupMenuBar();
	}

	private void setupMenuBar() {
		this.menuBar = new JMenuBar();

		// file
		this.fileMenu = new JMenu("File");
		this.newMenu = new JMenu("New");
		this.newMail = new JMenuItem("New Mail");
		this.newRoute = new JMenuItem("Create/Change Route");
		this.newCustomerPrice = new JMenuItem("Create/Change Customer Price");
		this.discontinueRoute = new JMenuItem("Discontinue Route");
		this.discontinueCusPrice = new JMenuItem("Discontinue Customer Price");
		this.logout = new JMenuItem("Logout");
		this.save = new JMenuItem("Save");
		this.quit = new JMenuItem("Quit");
		this.enableSoundrack = new JMenuItem("Enable Soundtrack");
		

		this.viewGraph = new JMenuItem("Graph");
		this.findCriticalPathsButton = new JMenuItem("Find Critical Paths");

		this.fileMenu.add(newMail);
		this.fileMenu.add(newRoute);
		this.fileMenu.add(newCustomerPrice);
		this.fileMenu.add(discontinueRoute);
		this.fileMenu.add(discontinueCusPrice);
		
		//this.fileMenu.add(newMenu);

		this.fileMenu.addSeparator();
		this.fileMenu.add(logout);
		this.fileMenu.add(save);
		this.fileMenu.addSeparator();
		this.fileMenu.add(enableSoundrack);
		this.fileMenu.addSeparator();
		this.fileMenu.add(quit);
		this.menuBar.add(fileMenu);

		// view
		this.viewMenu = new JMenu("View");
		this.viewGraph = new JMenuItem("Business Info");

		this.viewMenu.add(viewGraph);
		this.viewMenu.add(findCriticalPathsButton);
		this.menuBar.add(viewMenu);

		// options
		this.optionsMenu = new JMenu("Options");
		this.passwordChange = new JMenuItem("Change password");
		this.register = new JMenuItem("Register New User");
		this.removeUser = new JMenuItem("Remove user");

		this.optionsMenu.add(passwordChange);
		this.optionsMenu.add(register);
		this.optionsMenu.add(removeUser);
		
		this.menuBar.add(optionsMenu);

		// help
		this.helpMenu = new JMenu("Help");
		this.hotkeys = new JMenuItem("Hotkeys");

		this.helpMenu.add(hotkeys);
		this.menuBar.add(helpMenu);

		// add to frame
		this.setJMenuBar(menuBar);

		// add list of menu items to file array
		this.fileMenuItems.add(this.newMail);
		this.fileMenuItems.add(this.newRoute);
		this.fileMenuItems.add(this.newCustomerPrice);
		this.fileMenuItems.add(this.discontinueRoute);
		this.fileMenuItems.add(this.discontinueCusPrice);
		//this.fileMenuItems.add(this.load);
		this.fileMenuItems.add(this.logout);
		this.fileMenuItems.add(this.save);
		this.fileMenuItems.add(this.quit);
		this.fileMenuItems.add(enableSoundrack);

		// add list of menu items to view array
		this.viewMenuItems.add(this.viewGraph);
		this.viewMenuItems.add(this.findCriticalPathsButton);

		// add list of menu items to options array
		this.optionsMenuItems.add(this.passwordChange);
		this.optionsMenuItems.add(this.register);
		this.optionsMenuItems.add(this.removeUser);

		// add list of menu items to help array
		this.helpMenuItems.add(this.hotkeys);

		// loop through array to add listener
		this.fileMenuListener = new FileMenuListener();
		this.viewMenuListener = new ViewMenuListener();
		this.optionsMenuListener = new OptionsMenuListener();
		this.helpMenuListener = new HelpMenuListener();

		for (JMenuItem i : this.fileMenuItems) {
			i.addActionListener(this.fileMenuListener);
		}
		for (JMenuItem i : this.viewMenuItems) {
			i.addActionListener(this.viewMenuListener);
		}
		for (JMenuItem i : this.optionsMenuItems) {
			i.addActionListener(this.optionsMenuListener);
		}
		for (JMenuItem i : this.helpMenuItems) {
			i.addActionListener(this.helpMenuListener);
		}
	}

	private void setupFrame() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(width, height);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setTitle("KPSmart");
		setupIcon();
		this.setVisible(false);
	}

	private void setupIcon() {
		this.setIconImage(i);
	}
}
