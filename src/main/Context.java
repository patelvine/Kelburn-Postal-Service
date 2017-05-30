package main;

import gui.GUI;

public class Context {

	private GUI gui;

	public Context() {
		this.setGui(new GUI());
	}

	public GUI getGui() {
		return gui;
	}

	public void setGui(GUI gui) {
		this.gui = gui;
	}
}
