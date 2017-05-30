package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class MidPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private GUI gui;

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);

		this.paintComponents(g);
	}

	public MidPanel(GUI gui) {
		this.setGui(gui);
	}

	public GUI getGui() {
		return gui;
	}

	public void setGui(GUI gui) {
		this.gui = gui;
	}
}
