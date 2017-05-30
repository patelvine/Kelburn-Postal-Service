package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.swing.ImageIcon;
import java.awt.Image;


import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import events.BusinessState;
import events.Company;
import events.CustomerDeal;
import events.CustomerPriceUpdateEvent;
import events.DiscontinueCustomerPriceEvent;
import events.DiscontinueRouteEvent;
import events.Location;
import events.MailEvent;
import events.Priority;
import events.Route;
import events.TransportCostUpdateEvent;
import events.TransportType;

/**
 * MAIL EVENT = 3x2 ROUTE_EVENT = 4x3 CUSTOMER_PRICE = 3x2 DISCONTINUE = 3x2
 *
 * @author Emmanuel
 *
 */
public class NewInformationDialog extends JFrame implements MouseListener,
		MouseMotionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public final static int NEW_MAIL_EVENT = 0;
	public final static int NEW_ROUTE_EVENT = 1;
	public final static int NEW_CUSTOMER_PRICE = 2;
	public final static int NEW_DISCONTINUE_ROUTE = 3;
	public final static int NEW_DISCONTINUE_CUSPRICE = 4;
	private Image back = new ImageIcon("res/mainPanelBackground.png").getImage();
	private Image botImg = new ImageIcon("res/ribbonPanel/ribbonBackground.png").getImage();

	private int type;

	private JPanel panel;
	private ArrayList<JComponent> components;

	private ArrayList<JTextField> jtf;
	private ArrayList<JComboBoxWider> jcb;

	private String[] s;

	private Font font;

	// colors
	private Color background = new Color(51, 171, 249);
	private Color quadBoundries = new Color(47, 86, 233);
	private Color bottom = new Color(174, 234, 255);

	// my buttons
//	private EButton cancel;
//	private EButton approve;
	private EButton cancel;
	private EButton approve;

	public NewInformationDialog(int type, JComponent... components) {
		if (components.length == 0) {
			System.err.println("ERROR : No components to display");
			return;
		}
		this.components = new ArrayList<JComponent>();
		for (JComponent c : components) {
			this.components.add(c);
		}
		this.type = type;
		setupDialog();
	}

	public NewInformationDialog(int type, ArrayList<JComponent> components) {
		if (components.isEmpty()) {
			System.err.println("ERROR : No components to display");
			return;
		}
		this.components = components;
		this.type = type;
		setupDialog();

		// buttons
		this.cancel = new EButton(new Rectangle(this.getWidth() - 200,
				this.getHeight() - 70, 80, 30), "Cancel");
		this.approve = new EButton(new Rectangle(this.getWidth() - 100,
				this.getHeight() - 70, 80, 30), "Approve");
	}

	/**
	 * The Mail Dialog will be a small dialog (3x2)
	 */
	private void setupDialog() {
		// frame
		this.setIconImage(MasterFrame.i);
		this.setResizable(false);

		this.jtf = new ArrayList<JTextField>();
		this.jcb = new ArrayList<JComboBoxWider>();

		if (type == NEW_ROUTE_EVENT) {
			this.setSize(800, 560);
		} else {
			this.setSize(600, 400);
		}
		this.setLocation(150, 150);
		if (type == NEW_MAIL_EVENT) {
			this.setTitle("New Mail");
		} else if (type == NEW_ROUTE_EVENT) {
			this.setTitle("New Route");
		} else if (type == NEW_CUSTOMER_PRICE) {
			this.setTitle("New Customer Price");
		} else if (type == NEW_DISCONTINUE_ROUTE) {
			this.setTitle("Discontinue Route");
		} else if (type == NEW_DISCONTINUE_CUSPRICE) {
			this.setTitle("Discontinue Customer Price");
		} else {
			this.setTitle("Unknown dialog");
		}
		// this.setResizable(false);
		this.setVisible(true);

		// component names
		this.s = new String[this.components.size()];
		for (int i = 0; i < this.components.size(); i++) {
			s[i] = this.components.get(i).getName();
		}

		// font
		this.font = new Font("Arial", Font.PLAIN, 18);

		// panel
		if (type == NEW_ROUTE_EVENT) {
			this.panel = this.getBigPanel();
		} else {
			this.panel = this.getSmallPanel();
		}

		// panel's stuff
		this.panel.setLayout(null);
		this.panel.addMouseListener(this);
		this.panel.addMouseMotionListener(this);
		setPanelComponents();

		this.add(this.panel);
	}

	// === utilites ===

	private void setPanelComponents() {
		int width = 160; // leaves 20 each side
		int height = 40; // leaves 60 each side

		int x = 20;
		int y = 30;

		int limit = 0;
		if (type == NEW_ROUTE_EVENT) {
			// 4x3 setup
			limit = 800;
		} else {
			limit = 600;
		}

		System.out.println(limit);
		// 3x2 setup
		for (JComponent comp : this.components) {
			// check what type of component
			if (comp instanceof JTextField) {
				JTextField jtf = (JTextField) comp;
				jtf.setFont(font);
				jtf.setText("0.0");

				jtf.setBounds(x, y, width, height);
				this.panel.add(jtf);
				this.jtf.add(jtf);

			} else if (comp instanceof JComboBoxWider) {
				JComboBoxWider jcb = (JComboBoxWider) comp;
				jcb.setFont(font);

				// fill with appropriate content
				if (jcb.getName().toLowerCase().contains("origin")
						|| jcb.getName().toLowerCase().contains("destination")) {
					// get locations
					jcb = new JComboBoxWider(Location.allLocationsArray());
				} else if (jcb.getName().toLowerCase()
						.contains("transport type")) {
					jcb = new JComboBoxWider(TransportType.values());
				} else if (jcb.getName().toLowerCase().contains("company")) {
					jcb = new JComboBoxWider(Company.allCompaniesArray());
				} else {
					jcb = new JComboBoxWider(Priority.values());
				}

				jcb.setBounds(x, y, width, height);
				this.panel.add(jcb);
				this.jcb.add(jcb);
			}

			x += 200;
			if (x > limit) {
				x = 20;
				y += 160;
			}
		}
	}

	private JPanel getBigPanel() {
		JPanel p = new JPanel() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics graphics) {
				// paint here
				Graphics2D g = (Graphics2D) graphics.create();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);

				// background
//				g.setColor(background);
//				g.fillRect(0, 0, 800, 560);
				
				g.clearRect(0, 0, this.getWidth(), this.getHeight());
				g.drawImage(back, 0, 0,this.getWidth(),this.getHeight(), null);

				g.drawImage(botImg,0, 480, 800, 80,null);
//				// bottom
//				g.setColor(bottom);
//				g.fillRect(0, 480, 800, 80);

				// quadrants
				g.setColor(quadBoundries);

				g.drawLine(0, 0, 800, 0); // horizontal
				g.drawLine(0, 160, 800, 160);
				g.drawLine(0, 320, 800, 320);
				g.drawLine(0, 480, 800, 480);

				g.drawLine(0, 0, 0, 480); // vertical
				g.drawLine(200, 0, 200, 480);
				g.drawLine(400, 0, 400, 480);
				g.drawLine(600, 0, 600, 480);
				g.drawLine(800, 0, 800, 480);

				// text
				g.setColor(Color.black);
				int x = 10;
				int y = 20;
				for (int i = 0; i < s.length; i++) {
					g.drawString(s[i], x, y);
					x += 200;
					if (x > 800) {
						x = 10;
						y += 160;
					}
				}

				cancel.draw(g);
				approve.draw(g);

				this.paintComponents(g);
			}
		};
		return p;
	}

	private JPanel getSmallPanel() {
		JPanel p = new JPanel() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics graphics) {
				// paint here
				Graphics2D g = (Graphics2D) graphics.create();
//				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//						RenderingHints.VALUE_ANTIALIAS_ON);

				// background
//				g.setColor(background);
//				g.fillRect(0, 0, 600, 400);
				g.clearRect(0, 0, this.getWidth(), this.getHeight());
				g.drawImage(back, 0, 0,this.getWidth(),this.getHeight(),null);
				g.drawImage(botImg,0, 320, 600, 80,null);

				// bottom
//				g.setColor(bottom);
//				g.fillRect(0, 320, 600, 80);

				// quadrants
				g.setColor(quadBoundries);

				g.drawLine(0, 0, 600, 0); // horizontal
				g.drawLine(0, 160, 600, 160);
				g.drawLine(0, 320, 600, 320);

				g.drawLine(0, 0, 0, 320); // vertical
				g.drawLine(200, 0, 200, 320);
				g.drawLine(400, 0, 400, 320);
				g.drawLine(600, 0, 600, 320);

				// text
				g.setColor(Color.black);
				int x = 10;
				int y = 20;
				for (int i = 0; i < s.length; i++) {
					g.drawString(s[i], x, y);
					x += 200;
					if (x > 600) {
						x = 10;
						y += 160;
					}
				}

				cancel.draw(g);
				approve.draw(g);

				this.paintComponents(g);
			}
		};
		return p;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();

		if (this.cancel.isOn(p)) {
			cancel();
		} else if (this.approve.isOn(p)) {
			if (!this.approve.isActive())
				return;
			approve();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// unused

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// unused

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// unused

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point p = e.getPoint();
		this.cancel.setMouseOn(false);
		this.approve.setMouseOn(false);

		if (this.cancel.isOn(p)) {
			this.cancel.setMouseOn(true);
		} else if (this.approve.isOn(p)) {
			this.approve.setMouseOn(true);
		}

		this.panel.repaint();
	}

	private void approve() {
		if (!eventCheck()) {
			return;
		}
		if (type == NEW_MAIL_EVENT) {
			mailApprove();
		} else if (type == NEW_ROUTE_EVENT) {
			routeApprove();
		} else if (type == NEW_CUSTOMER_PRICE) {
			cusPriceApprove();
		} else if (type == NEW_DISCONTINUE_ROUTE) {
			disRouteApprove();
		} else if (type == NEW_DISCONTINUE_CUSPRICE) {
			desCusPriceApprove();
		}
		this.dispose();
	}

	private void desCusPriceApprove() {
		DiscontinueCustomerPriceEvent.Builder builder = new DiscontinueCustomerPriceEvent.Builder();

		builder.setOrigin((Location) this.jcb.get(0).getSelectedItem());
		builder.setDestination((Location) this.jcb.get(1).getSelectedItem());
		builder.setPriority((Priority) this.jcb.get(2).getSelectedItem());

		DiscontinueCustomerPriceEvent dcpe = builder.build();
		System.out.println(dcpe.toBusinessLogString());
		dcpe.process();
	}

	private void disRouteApprove() {
		DiscontinueRouteEvent.Builder builder = new DiscontinueRouteEvent.Builder();

		builder.setOrigin((Location) this.jcb.get(0).getSelectedItem());
		builder.setDestination((Location) this.jcb.get(1).getSelectedItem());
		builder.setTransportType((TransportType) this.jcb.get(2)
				.getSelectedItem());
		builder.setCompany((Company) this.jcb.get(3).getSelectedItem());

		DiscontinueRouteEvent dre = builder.build();

		System.out.println(dre.toBusinessLogString());

		dre.process();
	}

	private void cusPriceApprove() {
		CustomerPriceUpdateEvent.Builder builder = new CustomerPriceUpdateEvent.Builder();

		builder.setDollarsPerGram(Double.parseDouble(this.jtf.get(0).getText()));
		builder.setDollarsPerCubicCentimetre(Double.parseDouble(this.jtf.get(1)
				.getText()));

		builder.setPriority((Priority) this.jcb.get(0).getSelectedItem());
		builder.setOrigin((Location) this.jcb.get(1).getSelectedItem());
		builder.setDestination((Location) this.jcb.get(2).getSelectedItem());

		CustomerPriceUpdateEvent cpue = builder.build();

		System.out.println(cpue.toBusinessLogString());

		cpue.process();
		System.out.println("Made new customer price update event: "
				+ cpue.toString());
	}

	private void routeApprove() {
		TransportCostUpdateEvent.Builder builder = new TransportCostUpdateEvent.Builder();

		builder.setDollarsPerGram(Double.parseDouble(this.jtf.get(0).getText()));
		builder.setDollarsPerCubicCentimetre(Double.parseDouble(this.jtf.get(1)
				.getText()));

		builder.setMaxGrams(Double.parseDouble(this.jtf.get(2).getText()));
		builder.setMaxCubicCentimetres(Double.parseDouble(this.jtf.get(3)
				.getText()));

		builder.setDurationHours(Double.parseDouble(this.jtf.get(4).getText()));
		builder.setDepartureIntervalHours(Double.parseDouble(this.jtf.get(5)
				.getText()));

		builder.setOrigin((Location) this.jcb.get(0).getSelectedItem());

		builder.setDestination((Location) this.jcb.get(1).getSelectedItem());

		builder.setTransportType((TransportType) this.jcb.get(2)
				.getSelectedItem());
		builder.setCompany((Company) this.jcb.get(3).getSelectedItem());

		builder.setProcessDate(Calendar.getInstance().getTime());

		TransportCostUpdateEvent tcue = builder.build();

		System.out.println(tcue.toBusinessLogString());

		tcue.process();
		System.out.println("Made new mail event: " + tcue.toString());
	}

	private void mailApprove() {
		MailEvent.Builder builder = new MailEvent.Builder();

		builder.setGrams(Double.parseDouble(this.jtf.get(0).getText()));
		builder.setCubicCentimetres(Double.parseDouble(this.jtf.get(1)
				.getText()));

		builder.setPriortiy((Priority) this.jcb.get(0).getSelectedItem());
		builder.setOrigin((Location) this.jcb.get(1).getSelectedItem());
		builder.setDestination((Location) this.jcb.get(2).getSelectedItem());

		builder.setProcessDate(Calendar.getInstance().getTime());

		MailEvent me = builder.build();

		System.out.println(me.toBusinessLogString());

		me.process();
		// BusinessState.currentState.notifyObservers();
		System.out.println(BusinessState.currentState);

		System.out.println("Made new mail event: " + me.toString());
	}

	private void cancel() {
		this.dispose();
	}

	/**
	 * This is a big as method so be careful of where you are. READ THE COMMENTS
	 *
	 * @return
	 */
	private boolean eventCheck() {
		String msg = "";
		boolean pass = true;

		if (type == NEW_MAIL_EVENT) { // +++++=========================
			// weight must be number
			try {
				// weight must be greater than 0
				double weight = Double.parseDouble(this.jtf.get(0).getText());
				if (weight <= 0) {
					msg = msg + "Weight must be greater of 0\n";
					pass = false;
				}
			} catch (NumberFormatException nfe) {
				msg = msg + "Weight must be a number\n";
				pass = false;
			}

			// size must be number
			try {
				// size must be greater than 0
				double size = Double.parseDouble(this.jtf.get(1).getText());
				if (size <= 0) {
					msg = msg + "Size must be greater than 0\n";
					pass = false;
				}
			} catch (NumberFormatException nfe) {
				msg = msg + "Size must be a number\n";
				pass = false;
			}

			// origin - destination
			Location or = (Location) this.jcb.get(1).getSelectedItem();
			Location de = (Location) this.jcb.get(2).getSelectedItem();
			if (or.name.equals(de.name)) {
				msg = msg + "Origin cannot be the same as the destination";
				pass = false;
			}
			if (or.international) {
				msg = msg + "Origin cannot be an international location\n";
			}

			// the path must have an existing path
			if (!checkIfCusDealExists(or, de)) {
				pass = false;
				msg = msg + "There is no customer price for this path\n";
			}

			// the path must have an existing path
			if (!checkIfRouteExists(or, de)) {
				pass = false;
				msg = msg + "There is no route specified for this path\n";
			}

			if (!pass) {
				JOptionPane.showMessageDialog(null,
						"Please check your input. \n\n" + msg);
				return false;
			}
		} else if (type == NEW_ROUTE_EVENT) { // +++++=========================
			// dollars per gram must be number
			try {
				// weight must be greater than 0
				double dollarsPerGram = Double.parseDouble(this.jtf.get(0)
						.getText());
				if (dollarsPerGram < 0) {
					msg = msg + "Dollars per Gram must be greater of 0\n";
					pass = false;
				}
			} catch (NumberFormatException nfe) {
				msg = msg + "Dollars per Gram must be a number\n";
				pass = false;
			}

			// Dollars Per Cubic Centimetre must be number
			try {
				// Dollars Per Cubic Centimetre must be greater than 0
				double dollarPerCC = Double.parseDouble(this.jtf.get(1)
						.getText());
				if (dollarPerCC < 0) {
					msg = msg
							+ "Dollars Per Cubic Centimetre must be greater of 0\n";
					pass = false;
				}
			} catch (NumberFormatException nfe) {
				msg = msg + "Dollars Per Cubic Centimetre must be a number\n";
				pass = false;
			}

			// max weight must be number
			try {
				// max weight must be greater than 0
				double maxWeight = Double
						.parseDouble(this.jtf.get(2).getText());
				if (maxWeight <= 0) {
					msg = msg + "Max Weight must be greater of 0\n";
					pass = false;
				}
			} catch (NumberFormatException nfe) {
				msg = msg + "Max Weight must be a number\n";
				pass = false;
			}

			// max size must be number
			try {
				// max weight must be greater than 0
				double maxSize = Double.parseDouble(this.jtf.get(3).getText());
				if (maxSize <= 0) {
					msg = msg + "Max Size must be greater of 0\n";
					pass = false;
				}
			} catch (NumberFormatException nfe) {
				msg = msg + "Max Size must be a number\n";
				pass = false;
			}

			// duration must be number
			try {
				// duration must be greater than 0
				double duration = Double.parseDouble(this.jtf.get(4).getText());
				if (duration <= 0) {
					msg = msg + "Duration must be greater of 0\n";
					pass = false;
				}
			} catch (NumberFormatException nfe) {
				msg = msg + "Duration must be a number\n";
				pass = false;
			}

			// departure interval must be number
			try {
				// departure interval must be greater than 0
				double depInt = Double.parseDouble(this.jtf.get(4).getText());
				if (depInt <= 0) {
					msg = msg + "Departure Interval must be greater of 0\n";
					pass = false;
				}
			} catch (NumberFormatException nfe) {
				msg = msg + "Departure Interval must be a number\n";
				pass = false;
			}

			// origin - destination
			Location or = (Location) this.jcb.get(0).getSelectedItem();
			Location de = (Location) this.jcb.get(1).getSelectedItem();
			if (or.name.equals(de.name)) {
				msg = msg + "Origin cannot be the same as the destination";
				pass = false;
			}

			// the path must have an existing path
			if (!checkIfCusDealExists(or, de)) {
				pass = false;
				msg = msg + "There is no customer price for this path\n";
			}

			if (!pass) {
				JOptionPane.showMessageDialog(null,
						"Please check your input. \n\n" + msg);
				return false;
			}
		} else if (type == NEW_CUSTOMER_PRICE) { // +++++=========================
			// dollars per gram must be number
			try {
				// weight must be greater than 0
				double dollarsPerGram = Double.parseDouble(this.jtf.get(0)
						.getText());
				if (dollarsPerGram < 0) {
					msg = msg + "Dollars per Gram must be greater of 0\n";
					pass = false;
				}
			} catch (NumberFormatException nfe) {
				msg = msg + "Dollars per Gram must be a number\n";
				pass = false;
			}

			// Dollars Per Cubic Centimetre must be number
			try {
				// Dollars Per Cubic Centimetre must be greater than 0
				double dollarPerCC = Double.parseDouble(this.jtf.get(1)
						.getText());
				if (dollarPerCC < 0) {
					msg = msg
							+ "Dollars Per Cubic Centimetre must be greater of 0\n";
					pass = false;
				}
			} catch (NumberFormatException nfe) {
				msg = msg + "Dollars Per Cubic Centimetre must be a number\n";
				pass = false;
			}

			// origin - destination
			Location or = (Location) this.jcb.get(1).getSelectedItem();
			Location de = (Location) this.jcb.get(2).getSelectedItem();
			if (or.name.equals(de.name)) {
				msg = msg + "Origin cannot be the same as the destination\n";
				pass = false;
			}
			
			if(or.international){
				msg = msg+"Origin must be a domestic location\n";
				pass = false;
			}
			// the path must have an existing path
			// checkIfPathExists(or, de, msg, pass);

			if (!pass) {
				JOptionPane.showMessageDialog(null,
						"Please check your input. \n\n" + msg);
				return false;
			}
		} else if (type == NEW_DISCONTINUE_ROUTE) { // +++++=========================
			// origin - destination
			Location or = (Location) this.jcb.get(0).getSelectedItem();
			Location de = (Location) this.jcb.get(1).getSelectedItem();
			if (or.name.equals(de.name)) {
				msg = msg + "Origin cannot be the same as the destination";
				pass = false;
			}

			// the path must have an existing path
			if (!checkIfCusDealExists(or, de)) {
				pass = false;
				msg = msg + "There is no customer price for this path\n";
			}

			// the path must have an existing path
			if (!checkIfRouteExists(or, de)) {
				pass = false;
				msg = msg + "There is no route specified for this path\n";
			}

			if (!pass) {
				JOptionPane.showMessageDialog(null,
						"Please check your input. \n\n" + msg);
				return false;
			}
		} else if (type == NEW_DISCONTINUE_CUSPRICE) {
			// origin - destination
			Location or = (Location) this.jcb.get(0).getSelectedItem();
			Location de = (Location) this.jcb.get(1).getSelectedItem();
			if (or.name.equals(de.name)) {
				msg = msg + "Origin cannot be the same as the destination";
				pass = false;
			}

			// the path must have an existing path
			if (!checkIfCusDealExists(or, de)) {
				pass = false;
				msg = msg + "There is no customer price for this path\n";
			}

			if (!pass) {
				JOptionPane.showMessageDialog(null,
						"Please check your input. \n\n" + msg);
				return false;
			}
		}
		return true;
	}

	private boolean checkIfCusDealExists(Location or, Location de) {
		CustomerDeal.Query q = new CustomerDeal.Query();
		q.setOrigin(or);
		q.setDestination(de);

		Collection<CustomerDeal> cds = q.getAllQualifyingDeals();

		if (!cds.isEmpty())
			return true;
		else
			return false;
	}

	private boolean checkIfRouteExists(Location or, Location de) {

		ArrayList<Route> r = (ArrayList<Route>) Route.getAllRoutes(or, de);

		if (r.isEmpty()) {
			return false;
		}

		return true;
	}
}
