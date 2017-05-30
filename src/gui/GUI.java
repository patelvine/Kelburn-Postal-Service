package gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.KeyboardFocusManager;

import javax.swing.ImageIcon;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import main.Login;
import main.Login.AccessPrivilege;

/**
 * Master class of the GUI section for KPSmart.
 *
 * @author Emmanuel
 *
 */
public class GUI {

	private MasterFrame frame;

	//private LoginPanel loginPanel;

	private MasterPanel masterPanel;
	private RibbonPanel ribbonPanel;

	private LeftPanel lp;
	private MidPanel mp;

	private TopMidPanel tmp;

	//Bottom panel
	private JTabbedPane jtp = new JTabbedPane();
	private CustomerCostQueryPanel ccqp;
	private RoutePriceQueryPanel rpqp;

	private JSplitPane midSplitPane;
	private JSplitPane leftSplitPane;

	public static final Image back = new ImageIcon("res/mainPanelBackground.png").getImage();

	public GUI() {
		setupGui();
	}

	private void setupGui() {
		setupFrame();
		lookAndFeelSetup();


		while(Login.getLoginPrivilege() == AccessPrivilege.none){
			setupLogin();
		}

		setupPanels();
		setupKeyListener();
		this.frame.setVisible(true);
	}

	private void setupLogin(){
		new LoginPanel(this);
	}

	public void setupKeyListener() {
		KeyboardFocusManager manager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyDispatcher());
	}

	private void lookAndFeelSetup() {
		LookAndFeelInfo[] laf = UIManager.getInstalledLookAndFeels();
        int index = 1;
		try {
            UIManager.setLookAndFeel(laf[index].getClassName());
            SwingUtilities.updateComponentTreeUI(this.frame);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
	}

	private void setupPanels() {
		// login panel
		//this.loginPanel = new LoginPanel(this);
		//this.loginPanel.setBounds(0, 0, this.frame.getWidth(), this.frame.getHeight());

		// -- mid panels setup --
		this.tmp = new TopMidPanel(this);
		this.rpqp = new RoutePriceQueryPanel(this);
		this.ccqp = new CustomerCostQueryPanel(this);
		this.jtp = new JTabbedPane(){
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g){
				int widthSomething = GUI.this.lp.getWidth();
				this.setBounds(this.getX(), this.getY(), GUI.this.getWidth()-widthSomething, this.getHeight());
				super.paint(g);
			}
		};



		this.jtp.add(ccqp,"Customer Prices");
		this.jtp.add(rpqp,"Transport Costs");

		this.mp = new MidPanel(this);

		// -- master panels setup --
		this.lp = new LeftPanel(this);
		this.ribbonPanel = new RibbonPanel(this);
		this.masterPanel = new MasterPanel(this);

		// WARNING!!! Make sure ordering is understood

		this.midSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.tmp,
				this.jtp);
		this.midSplitPane.setDividerLocation(this.frame.getHeight() / 3);
		this.leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				this.lp, this.midSplitPane);
		this.leftSplitPane.setDividerLocation(this.frame.getWidth() / 4);

		// not needed at the moment
		// this.rightSplitPane = new
		// JSplitPane(JSplitPane.HORIZONTAL_SPLIT,this.leftSplitPane,this.rp);
		// this.rightSplitPane.setDividerLocation((3*this.frame.getWidth())/4);

		this.ribbonPanel.setBounds(0, 0, this.frame.getWidth(), 100);
		this.masterPanel.add(this.ribbonPanel);
		this.leftSplitPane.setBounds(0, 100, this.frame.getWidth(),
				this.frame.getHeight() - 100);
		this.masterPanel.add(leftSplitPane);

		this.frame.add(this.ribbonPanel);
		this.frame.add(this.masterPanel);

		//this.frame.add(this.loginPanel);
	}

	private void setupFrame() {
		this.frame = new MasterFrame();
	}

	// ----- Standard Getters -----

	public MasterFrame getFrame() {
		return frame;
	}

	public MasterPanel getMasterPanel() {
		return masterPanel;
	}

	public LeftPanel getLp() {
		return lp;
	}

	public MidPanel getMp() {
		return mp;
	}


	public TopMidPanel getTmp() {
		return tmp;
	}

	public int getWidth() {
		return this.frame.getWidth();
	}

	public int getHeight() {
		return this.frame.getHeight();
	}

	public void repaint() {
		//resize();
		this.frame.repaint();

	}

	public void resize(){
		int midValue = this.midSplitPane.getDividerLocation()*3;
		this.midSplitPane.setDividerLocation(midValue);

		int leftValue = this.leftSplitPane.getDividerLocation()*4;
		this.leftSplitPane.setDividerLocation(leftValue);
	}

}
