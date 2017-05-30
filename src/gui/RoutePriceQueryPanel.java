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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import events.Company;
import events.Location;
import events.Priority;
import events.Route;

/**
 * AKA the CostQueryPanel
 *
 * @author Emmanuel
 *
 */
public class RoutePriceQueryPanel extends JPanel implements MouseMotionListener{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private GUI gui;
	public static final Image back = new ImageIcon(
			"res/botMidPanelBackground.png").getImage();

	private JComboBoxWider originChoice;// combo
	private JComboBoxWider destinationChoice;// combo


	// table
	private JTable queryTable;
	private String[] columnNames = new String[0];
	private String[][] rowData = new String[0][0];
	private JComboBoxWider transportTypeChoice;
	private JComboBoxWider companyChoice;

	@Override
	public void paint(Graphics g){
		this.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		super.paint(g);
	}

	public RoutePriceQueryPanel(GUI gui) {
		this.setGui(gui);
		setupPanel();
		this.originChoice.setSelectedIndex(0);
		this.destinationChoice.setSelectedIndex(0);
		this.companyChoice.setSelectedIndex(0);
		this.transportTypeChoice.setSelectedIndex(0);
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
		this.originChoice = new JComboBoxWider(Location.allLocationsArray());
		this.originChoice.addActionListener(new MyActionListener());
		originChoice.insertItemAt("---Any---", 0);
		JLabel originChoiceText = new JLabel("Origin");
		comboPanel.add(originChoiceText);
		comboPanel.add(originChoice);
//		this.originChoice.setSelectedIndex(0);

		
		//DESTINATION
		this.destinationChoice = new JComboBoxWider(
				Location.allLocationsArray());
		destinationChoice.insertItemAt("---Any---", 0);
		this.destinationChoice.addActionListener(new MyActionListener());
		JLabel destinationChoiceText = new JLabel("Destination");
		comboPanel.add(destinationChoiceText);
		comboPanel.add(destinationChoice);
//		this.destinationChoice.setSelectedIndex(0);

		
		//TRANSPORT TYPE
		this.transportTypeChoice = new JComboBoxWider(Priority.values());
		this.transportTypeChoice.insertItemAt("---Any---", 0);
		this.transportTypeChoice.addActionListener(new MyActionListener());
		JLabel transportTypeChoiceText = new JLabel("Transport Type");
		comboPanel.add(transportTypeChoiceText);
		comboPanel.add(transportTypeChoice);
//		this.transportTypeChoice.setSelectedIndex(0);
		
		//COMPANY
		this.companyChoice = new JComboBoxWider(Company.allCompaniesArray());
		this.companyChoice.insertItemAt("---Any---", 0);
		this.companyChoice.addActionListener(new MyActionListener());
		JLabel companyChoiceText = new JLabel("Company");
		comboPanel.add(companyChoiceText);
		comboPanel.add(companyChoice);
//		this.destinationChoice.setSelectedIndex(0);
		this.add(comboPanel,BorderLayout.NORTH);

	}

	public void generateTable() {
		if(queryTable!=null)
			this.remove(queryTable);
		Location or = null;
		Location de = null;
		if (!this.originChoice.getSelectedItem().equals("---Any---"))
			or = (Location) this.originChoice.getSelectedItem();
		if (!this.destinationChoice.getSelectedItem().equals("---Any---"))
			de = (Location) this.destinationChoice.getSelectedItem();

		Route.Query query = new Route.Query();
		query.setOrigin(or);
		query.setDestination(de);
		List<Route> routes = query.getAllQualifyingRoutes();

		columnNames = new String[8];
		columnNames[0] = "Dollars Per Gram";
		columnNames[1] = "Dollars Per Cubic Centimetre";
		columnNames[2] = "Max Weight";
		columnNames[3] = "Max Size";
		columnNames[4] = "Duration";
		columnNames[5] = "Departure Interval";
		columnNames[6] = "Transport Type";
		columnNames[7] = "Company";

		rowData = new String[routes.size() + 1][8];
		String[] header = { "Dollars Per Gram",
				"Dollars Per Cubic Centimetre", "Max Weight", "Max Size",
				"Duration", "Departure Interval", "Transport Type",
				"Company" };
		rowData[0] = header;

		for (int i = 1; i < routes.size() + 1; i++) {
			Route r = (Route) routes.get(i - 1);
			String[] data = { r.dollarsPerGram + "",
					r.dollarsPerCubicCentimetre + "", r.maxGrams + "",
					r.maxCubicCentimetres + "", r.durationHours + "",
					r.departureIntervalHours + "",
					r.transportType.toString(), r.company.toString() };
			rowData[i] = data;
		}

		this.queryTable = new JTable(rowData, columnNames);
		this.queryTable.setEnabled(false);
		this.add(queryTable,BorderLayout.CENTER);

		this.revalidate();
		this.repaint();
	}

	public class MyActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			RoutePriceQueryPanel.this.generateTable();
		}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// unused

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		repaint();
	}

	public GUI getGui() {
		return gui;
	}

	public void setGui(GUI gui) {
		this.gui = gui;
	}

}