package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.lang.reflect.Field;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import events.BusinessEvent;

public class TopMidPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private GUI gui;

	public static BusinessEvent visibleEvent;

	public static TopMidPanel it;

	private JScrollPane	sp;
	private JTextArea ta;

	private HashMap<String,String> fieldNameMap;

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
		//g.drawImage(GUI.back, 0, 0, this.getWidth() - 1, this.getHeight() - 1, null);

		updateSize();

		if (visibleEvent != null) {
			ta.setText(visibleEvent.toString()+"\n \n");
			Field[] fields = visibleEvent.getClass().getFields();
			for (Field field : fields) {
				String name = parseFieldName(field.getName());
				try {
					String value = field.get(visibleEvent).toString();
					ta.setText(ta.getText() +" "+ name + " : " + value);
					ta.setText(ta.getText() + "\n");
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
		}
		this.paintComponents(g);

	}

	private String parseFieldName(String name) {
		String toReturn = this.fieldNameMap.get(name);
		return toReturn;
	}

	public TopMidPanel(GUI gui) {
		this.setGui(gui);
		it=this;
		setupPanel();
		setupFieldNameMap();

	}

	private void setupFieldNameMap() {
		this.fieldNameMap = new HashMap<String,String>();
		this.fieldNameMap.put("grams","Grams (g)");
		this.fieldNameMap.put("cubicCentimetres","Size (cc)");

		this.fieldNameMap.put("maxGrams","Max Grams (g)");
		this.fieldNameMap.put("maxCubicCentimetres","Max Size (cc)");

		this.fieldNameMap.put("departureIntervalHours","Departure Interval Hours (hrs)");
		this.fieldNameMap.put("durationHours","Duration Hours (hrs)");

		this.fieldNameMap.put("dollarsPerGram", "Dollars Per Gram ($ per G)");
		this.fieldNameMap.put("dollarsPerCubicCentimetre", "Dollars Per Size ($ per cc)");

		this.fieldNameMap.put("processDate", "Process Date");
		this.fieldNameMap.put("transportType", "Transport Type");

		this.fieldNameMap.put("destination","Destination");
		this.fieldNameMap.put("origin","Origin");
		this.fieldNameMap.put("priority","Priority");
		this.fieldNameMap.put("company","Company");


	}

	private void setupPanel() {
		this.setLayout(null);
		this.ta = new JTextArea();
		this.sp = new JScrollPane(this.ta);
		this.sp.setBounds(0, 0, this.getWidth(), this.getHeight());
		this.add(sp);
	}

	private void updateSize() {
		this.sp.setBounds(0, 0, this.getWidth()-15, this.getHeight());
	}

	public GUI getGui() {
		return gui;
	}

	public void setGui(GUI gui) {
		this.gui = gui;
	}
}
