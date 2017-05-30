package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class RibbonPanel extends JPanel implements MouseMotionListener, MouseListener{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private GUI gui;

	private ArrayList<RButton> rButtons;

	private Image back = new ImageIcon("res/ribbonPanel/ribbonBackground.png").getImage();

	@Override
	public void paint(Graphics gr){
		Graphics2D g = (Graphics2D) gr.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(back, 0, 0, this.gui.getWidth(), 100, null);

		updateSize();

		//System.out.println(this.getWidth()+" "+this.getHeight());

		// draw Buttons
		for(RButton r : this.rButtons){
			r.draw(g);
			
		}
	}

	private void updateSize() {
		double perButton = (double)this.gui.getWidth()/this.rButtons.size();
		perButton -= 10;

		int x = 5;
		for(int i=0;i<this.rButtons.size();i++){
			Rectangle rec = new Rectangle(100,100);
			rec.x = x;
			rec.y = 7;
			rec.width = (int)perButton;
			rec.height = 80;
			this.rButtons.get(i).setRectangle(rec);

			x += perButton;
			x += 5;
		}
	}

	public RibbonPanel(GUI gui){
		this.gui = gui;

		setupPanel();
	}

	private void setupPanel() {
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		setupButtons();
	}

	private void setupButtons() {
		this.rButtons = new ArrayList<RButton>();
		this.rButtons.add(new RButton(RButton.TYPE_MAIL));
		this.rButtons.add(new RButton(RButton.TYPE_CUSTOMERPRICE));
		this.rButtons.add(new RButton(RButton.TYPE_DISCONTINUECUSTOMERPRICE));
		this.rButtons.add(new RButton(RButton.TYPE_ROUTE));
		this.rButtons.add(new RButton(RButton.TYPE_DISCONTINUEROUTE));
		double perButton = (double)this.gui.getWidth()/this.rButtons.size();
		perButton -= 10;

		int x = 5;
		for(int i=0;i<this.rButtons.size();i++){
			Rectangle rec = new Rectangle(100,100);
			rec.x = x;
			rec.y = 7;
			rec.width = (int)perButton;
			rec.height = 80;
			this.rButtons.get(i).setRectangle(rec);

			x += perButton;
			x += 5;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		for(RButton r : this.rButtons){
			if(r.isOn(e.getPoint())){
				r.action();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// unused

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// unused

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
		for(RButton r : this.rButtons){
			r.isOn(e.getPoint());
		}
		this.repaint();
	}
}
