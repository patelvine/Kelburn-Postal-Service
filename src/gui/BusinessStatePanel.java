package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import events.BusinessState;

public class BusinessStatePanel extends JPanel implements Observer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private BusinessState visibleState;
	private MyPlotPanel  mpp;
	private JButton backButt = new JButton("<<");
	private JButton prevButt = new JButton("<");
	private JButton nextButt = new JButton(">");
	private JButton frontButt = new JButton(">>");
	private Image back = new ImageIcon("res/ribbonPanel/ribbonBackground.png").getImage();

	public BusinessStatePanel(MyPlotPanel mpp) {
		this.mpp =mpp;
		this.visibleState = BusinessState.currentState;
		this.setUpButtons();

		//The following functions don't work because I don't know
		this.setMinimumSize(new Dimension(500, 200));
		this.setSize(new Dimension(500, 200));
		this.setMaximumSize(new Dimension(500, 200));
	}

	public BusinessState getVisibleState() {
		return visibleState;
	}

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics.create();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.red);
		g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);

		displayBusinessState(g);
		this.paintComponents(graphics);




	}

	private void setUpButtons() {
		// add button panel


		// Add buttons for navigating Business States
		this.add(backButt);
		backButt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				BusinessState bs = BusinessStatePanel.this.visibleState;
				while (bs.previousState != null)
					bs = bs.previousState;
				BusinessStatePanel.this.visibleState = bs;
				BusinessStatePanel.this.updateLabel();
			}
		});

		this.add(prevButt);
		prevButt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (BusinessStatePanel.this.visibleState.previousState != null)
					BusinessStatePanel.this.visibleState = BusinessStatePanel.this.visibleState.previousState;
				BusinessStatePanel.this.updateLabel();
			}
		});

		this.add(nextButt);
		nextButt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				BusinessState bs = BusinessState.currentState;
				BusinessState vs = BusinessStatePanel.this.visibleState;
				if (bs == vs)
					return;
				while (bs.previousState != vs)
					bs = bs.previousState;
				BusinessStatePanel.this.visibleState = bs;
				BusinessStatePanel.this.updateLabel();
			}
		});

		this.add(frontButt);
		frontButt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				BusinessStatePanel.this.visibleState = BusinessState.currentState;
				BusinessStatePanel.this.updateLabel();
			}
		});


	}

	private void updateLabel(){
		this.repaint();
		this.mpp.updatePlots();
	}

	/**
	 * Gets current state of business and displays it
	 *
	 * @param g
	 */
	private void displayBusinessState(Graphics2D g) {
		BusinessState bs = this.visibleState;

		//lop off the end of the date

		String[] sa = new String[6];
		sa[0] = "--- Current Business State ---\n";
		sa[1]="Date/Time: "+bs.date.toString();
		sa[2] = "Total Money In: " + bs.totMoneyIn;
		sa[3] = "\nTotal Money Out: " + bs.totMoneyOut;
		sa[4] = "\nNumber of Deliveries: " + bs.numDeliveries;
		sa[5] = "\nTotal Delivery Time: " + bs.totDeliveryTime;

		g.setColor(Color.black);
		int padding = 15;
		int i = 45;
		for (String s : sa) {
			g.drawString(s, 10, i);
			i += padding;
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// ensure only latest Business state has this as observer
		arg0.deleteObservers();
		this.repaint();
	}
}
