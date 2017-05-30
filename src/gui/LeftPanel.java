package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import events.BusinessEvent;
import events.EventHistory;

public class LeftPanel extends JPanel implements Observer{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private GUI gui;
	private JScrollPane jsp;
	private JList jList = new JList();
	private BusinessEvent[] eventArray;
	public static LeftPanel it;

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
		//g.drawImage(GUI.back, 0, 0, this.getWidth() - 1, this.getHeight() - 1, null);

		updateSize();

		this.paintComponents(g);
	}

	private void updateSize(){
		int width = 375;
		int height = this.getHeight()-75;

		int x = (this.getWidth()/2) - (width/2);
		int y = 10;

		this.jsp.setBounds(x, y, width, height);
	}

	public LeftPanel(GUI gui) {
		this.gui = gui;
		this.eventArray = EventHistory.getEventHistory().allEventsArray();
		EventHistory.getEventHistory().addObserver(this);

		this.setLayout(null);

		//System.out.println(this.getX()+" "+this.getY()+" "+this.getWidth()+" "+this.getHeight());

		jList = new JList(eventArray);
		this.jList.addListSelectionListener(new myListener());
		//this.jList.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());

		this.jsp = new JScrollPane(jList);
		this.jsp.setBounds(this.getX(), this.getY(), 100, gui.getHeight());

		this.add(jsp);

		generateList();

		it = this;
	}

	private class myListener implements ListSelectionListener {

		@SuppressWarnings("static-access")
		@Override
		public void valueChanged(ListSelectionEvent e) {

			TopMidPanel.it.visibleEvent=(BusinessEvent)jList.getSelectedValue();
			TopMidPanel.it.repaint();
		}

	}

	@Override
	public void update(Observable o, Object arg) {
		generateList();
		this.validate();
		this.repaint();
		this.jsp.repaint();
	}

	private void generateList() {
		//remember previously selected item
		int idx = jList.getSelectedIndex();
		Object prev=null;

		if(idx>=0)
			prev=eventArray[jList.getSelectedIndex()];

		BusinessEvent[]arr = EventHistory.getEventHistory().allEventsArray();

		//reverse array and put into events
		this.eventArray = new BusinessEvent[arr.length];
		for(int i=0;i<arr.length;i++)
			eventArray[i]=arr[arr.length-i-1];

		this.remove(jsp);
		jList = new JList(eventArray);
		jList.addListSelectionListener(new myListener());
		//this.jList.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());

		this.jsp = new JScrollPane(jList);
		this.jsp.setBorder(BorderFactory.createEmptyBorder());
		this.jsp.setBounds(this.getX(), this.getY(), 100, gui.getHeight());

		this.add(jsp);

		if(prev!=null)
			jList.setSelectedValue(prev, true);

	}

}
