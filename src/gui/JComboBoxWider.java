package gui;

import javax.swing.JComboBox;
import java.awt.Dimension;
//import java.beans.Transient;

public class JComboBoxWider extends JComboBox {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int listWidth = 300;

	public JComboBoxWider(Object[] objs) {
		super(objs);
	}

	public Dimension getSize() {
		Dimension dim = super.getSize();
		if (listWidth > 0)
			dim.width = listWidth;
		return dim;
	}

	public void setListWidth(int listWidth) {
		this.listWidth = listWidth;
	}
}
