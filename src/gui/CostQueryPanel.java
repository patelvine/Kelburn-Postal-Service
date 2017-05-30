package gui;

import javax.swing.*;

public class CostQueryPanel extends JPanel{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private GUI gui;

	public CostQueryPanel(GUI gui){
		this.setGui(gui);

	}

	public GUI getGui() {
		return gui;
	}

	public void setGui(GUI gui) {
		this.gui = gui;
	}
}
