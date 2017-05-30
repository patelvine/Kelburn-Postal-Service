package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import events.CustomerDeal;
import events.Location;
import events.Priority;

/**
 * AKA the CostQueryPanel
 *
 * @author Emmanuel
 *
 */
public class CustomerCostQueryPanel extends JPanel implements MouseMotionListener{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private GUI gui;
	public static final Image back = new ImageIcon(
			"res/botMidPanelBackground.png").getImage();

	private JComboBox originChoice;// combo
	private JComboBox destinationChoice;// combo
	private JComboBox priorityChoice;

	// table
	private JTable queryTable;
	private String[] columnNames = new String[0];
	private String[][] rowData = new String[0][0];

	@Override
	public void paint(Graphics g){
		updateSize();
		super.paint(g);
	}

	private void updateSize(){
		int x = this.getX();
		int y = this.getY();
		int width = this.gui.getWidth();
		int height = this.gui.getHeight();

		this.setBounds(x,y,width,height);

	}

	public CustomerCostQueryPanel(GUI gui) {
		this.gui = gui;
		setupPanel();
		this.originChoice.setSelectedIndex(0);
		this.destinationChoice.setSelectedIndex(0);
		this.priorityChoice.setSelectedIndex(0);
		generateTable();
		this.addMouseMotionListener(this);
	}

	private void setupPanel() {
		this.setLayout(new BorderLayout());
		JPanel comboPanel = new JPanel();

		JLabel label = new JLabel("Filter by:");
		label.setFont(new Font("Arial",Font.BOLD,12));
		comboPanel.add(label);

		//ORIGIN
		this.originChoice = new JComboBox(Location.allLocationsArray());
		this.originChoice.addActionListener(new MyActionListener());
		originChoice.insertItemAt("---Any---", 0);
		JLabel originChoiceText = new JLabel("Origin");
		comboPanel.add(originChoiceText);
		comboPanel.add(originChoice);
		

		//DESTINATION
		this.destinationChoice = new JComboBox(
				Location.allLocationsArray());
		destinationChoice.insertItemAt("---Any---", 0);
		this.destinationChoice.addActionListener(new MyActionListener());
		JLabel destinationChoiceText = new JLabel("Destination");
		comboPanel.add(destinationChoiceText);
		comboPanel.add(destinationChoice);
//		this.destinationChoice.setSelectedIndex(0);


		//PRIORITY
		this.priorityChoice = new JComboBox(Priority.values());
		this.priorityChoice.insertItemAt("---Any---", 0);
		this.priorityChoice.addActionListener(new MyActionListener());
		JLabel priorityChoiceText = new JLabel("Priority");
		comboPanel.add(priorityChoiceText);
		comboPanel.add(priorityChoice);
		
//		this.priorityChoice.setSelectedIndex(0);
		this.add(comboPanel,BorderLayout.NORTH);
	}

	public void generateTable() {
		if(queryTable!=null)
			this.remove(queryTable);
		Location or = null;
		Location de = null;
		Priority pr = null;

		//If given get the values form the combo boxes
		if (this.originChoice.getSelectedIndex()!=0)
			or = (Location) this.originChoice.getSelectedItem();

		if (this.destinationChoice.getSelectedIndex()!=0)
			de = (Location) this.destinationChoice.getSelectedItem();

		if(this.priorityChoice.getSelectedIndex()!=0)
			pr = (Priority)this.priorityChoice.getSelectedItem();

		//query customer deals for all applicable deals
		CustomerDeal.Query query = new CustomerDeal.Query();
		query.setOrigin(or);
		query.setDestination(de);
		query.setPriority(pr);
		List<CustomerDeal> deals = query.getAllQualifyingDeals();

		//label collumns
		columnNames = new String[5];
		columnNames[0] = "Origin";
		columnNames[1] = "Deestination";
		columnNames[2] = "Dollars Per Gram";
		columnNames[3] = "Dollars Per Cubic Centimetre";
		columnNames[4] = "Priority";

		rowData = new String[deals.size() + 1][5];
		String[] header = { "Origin", "Destination", "Dollars Per Gram",
				"Dollars Per Cubic Centimetre", "Priority" };
		rowData[0] = header;

		//fill data colls
		for (int i = 1; i < deals.size() + 1; i++) {
			CustomerDeal cd = (CustomerDeal) deals.get(i - 1);
			String[] data = { cd.origin.toString(), cd.destination.toString(),
					cd.dollarsPerGram + "", cd.dollarsPerCubicCentimetre + "",
					cd.priority.toString() };
			rowData[i] = data;
		}

		this.queryTable = new JTable(rowData, columnNames);

		this.queryTable.setEnabled(false);
		this.add(queryTable,BorderLayout.CENTER);
		this.revalidate();
		repaint();
	}

	public class MyActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			CustomerCostQueryPanel.this.generateTable();

		}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// unused

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		System.out.println("painting");
		repaint();
	}

}
