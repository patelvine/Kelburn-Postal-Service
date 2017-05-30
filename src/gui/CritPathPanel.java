package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.BaseLabel;

import events.CustomerDeal;

public class CritPathPanel extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JList critList;
	private JList nonCritList;
	private CustomerDeal observing = null;
	private Plot2DPanel plot;

	public CritPathPanel() {
		populateList();

		this.critList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				nonCritList.clearSelection();
				CritPathPanel.this.observing = (CustomerDeal) critList
						.getSelectedValue();
				CritPathPanel.this.redoGraph();
				CritPathPanel.this.repaint();
			}
		});

		this.nonCritList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				critList.clearSelection();
				CritPathPanel.this.observing = (CustomerDeal) nonCritList
						.getSelectedValue();
				CritPathPanel.this.redoGraph();
				CritPathPanel.this.repaint();
			}
		});

		// create graph
		this.plot = new Plot2DPanel();
		plot.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

		// add bordewrs
		// critList.setBorder(BorderFactory.createLoweredBevelBorder());
		// nonCritList.setBorder(BorderFactory.createLoweredBevelBorder());

		this.setLayout(new BorderLayout());

		this.add(plot, BorderLayout.CENTER);

		// adjust width of lists
		this.critList.setAlignmentX(LEFT_ALIGNMENT);
		this.critList.setMinimumSize(new Dimension(350,20));
		this.critList.setMaximumSize(new Dimension(350,20));
		this.critList.setPreferredSize(new Dimension(350,20));

		this.nonCritList.setAlignmentX(LEFT_ALIGNMENT);
		this.nonCritList.setMinimumSize(new Dimension(350,20));
		this.nonCritList.setMaximumSize(new Dimension(350,20));
		this.nonCritList.setPreferredSize(new Dimension(350,20));

		// arrange left panel with lists and labesls
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setAlignmentX(LeftPanel.LEFT_ALIGNMENT);

		Font myFont = new Font("Serif",Font.BOLD,16);
		JLabel label1 = new JLabel("Critical Paths");
		label1.setFont(myFont);
		leftPanel.add(new JLabel("Critical Paths"));
		leftPanel.add(critList);

		leftPanel.add(new JLabel("Non Critical Paths"));
		leftPanel.add(nonCritList);
		//
		this.add(leftPanel, BorderLayout.WEST);
		
		plot.setAxisLabel(1, "Cost ($)");
		plot.setAxisLabel(0, "Weight to Volume radio");

	}

	private void populateList() {

		CustomerDeal[] arr = getAllCustomerDealsArray();

		DefaultListModel critListModel = new DefaultListModel();
		DefaultListModel nonCritListModel = new DefaultListModel();
		for (CustomerDeal cd : arr) {
			if (cd.isCritical2()) {
				critListModel.addElement(cd);
			} else {
				nonCritListModel.addElement(cd);
			}
		}

		this.critList = new JList(critListModel);
		this.nonCritList = new JList(nonCritListModel);
	}

	protected void redoGraph() {
		if (this.observing == null)
			return;
		// get y axis data
		double[][] costs = this.observing.getCriticalRanges();

		// get x axis
		double x[] = new double[costs[0].length];
		double divSize = 1.0 / (x.length - 1);
		for (int i = 0; i < x.length; i++) {
			x[i] = i * divSize;
		}
		// clear plot
		this.plot.removeAllPlots();

		double[][] kpsValues = { x, costs[0] };
		double[][] customerValues = { x, costs[1] };

		this.plot
				.addPlot(Plot2DPanel.LINE, "Cost to KPS", Color.red, kpsValues);
		this.plot.addPlot(Plot2DPanel.LINE, "Cost to Custoemr", Color.green,
				customerValues);
		
		plot.setAxisLabel(1, "Cost ($)");
		plot.setAxisLabel(0, "Weight to Volume Ratio");
		
		BaseLabel title = new BaseLabel("Cost and Profit of Paths", Color.RED, 0.5, 1.1);
		title.setFont(new Font("Courier", Font.BOLD, 20));
		plot.addPlotable(title);
		
		plot.addLegend("SOUTH");

	}

	private CustomerDeal[] getAllCustomerDealsArray() {
		List<CustomerDeal> list = CustomerDeal.currentDeals;
		CustomerDeal[] arr = new CustomerDeal[list.size()];
		for (int i = 0; i < list.size(); i++) {
			arr[i] = list.get(i);
		}
		return arr;
	}
}
