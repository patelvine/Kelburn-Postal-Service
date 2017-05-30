package gui;

import javax.swing.JPanel;

public class MasterPanel extends JPanel{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private GUI gui;

	public MasterPanel(GUI gui) {
		this.setGui(gui);
		this.setLayout(null);
	}

	public GUI getGui() {
		return gui;
	}

	public void setGui(GUI gui) {
		this.gui = gui;
	}

}
