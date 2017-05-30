package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.Axis;
import org.math.plot.plotObjects.BaseLabel;

import events.BusinessState;

import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class MyPlotPanel extends JPanel implements Observer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	enum PlotType {
		MONEY_IN, MONEY_OUT, NUMBER_OF_DELIVERIES, AVERAGE_DELIVERY_TIME, TOTAL_DELIVERY_TIME
	};

	Set<PlotType> visiblePlots = Collections.synchronizedSet(EnumSet
			.noneOf(PlotType.class));

	private Plot2DPanel plot;


	private BusinessStatePanel rightPanel;

	// This daata is a gross hack to get an indicator on the plot as to where
	// the Business state panel is at.

	public MyPlotPanel() {
		this.setLayout(new BorderLayout());

		// create your PlotPanel (you can use it as a JPanel)
		plot = new Plot2DPanel();

		// define the legend position
		plot.addLegend("SOUTH");

		// add a line plot to the PlotPanel

		this.add(plot, BorderLayout.CENTER);
		// this.setSize(new Dimension(1000, 800));
		JPanel checkBoxPanel = makeCheckBoxPanel();
		this.add(checkBoxPanel, BorderLayout.WEST);
		this.rightPanel = new BusinessStatePanel(this);
		this.add(rightPanel, BorderLayout.EAST);

		updatePlots();
		
		BaseLabel title = new BaseLabel("Business Graph", Color.RED, 0.5, 1.1);
		title.setFont(new Font("Courier", Font.BOLD, 20));
		plot.addPlotable(title);
	}

	private String getNiceName(PlotType pt) {
		String str = "";
		switch (pt) {
		case MONEY_IN:
			str = "Money In";
			break;
		case MONEY_OUT:
			str = "Money Out";
			break;
		case NUMBER_OF_DELIVERIES:
			str = "Number of Deliveries";
			break;
		case AVERAGE_DELIVERY_TIME:
			str = "Average Delivery Time   ";
			break;
		case TOTAL_DELIVERY_TIME:
			str = "Totsl Delivery Time";
			break;
		}
		return str;

	}

	public void updatePlots() {
		BusinessState bs = BusinessState.currentState;
		plot.removeAllPlots();
		BusinessState highlightedState = this.rightPanel.getVisibleState();
		double maxY =0;
		for (PlotType pt : visiblePlots) {


			switch (pt) {
			case MONEY_IN:
				this.plot.addLinePlot(pt.name(), getMoneyIn(bs));
				if(highlightedState.totMoneyIn>maxY)
					maxY = highlightedState.totMoneyIn;
				break;
			case MONEY_OUT:
				this.plot.addLinePlot(pt.name(), getMoneyOut(bs));
				if(highlightedState.totMoneyOut>maxY)
					maxY = highlightedState.totMoneyOut;
				break;
			case NUMBER_OF_DELIVERIES:
				this.plot.addLinePlot(pt.name(), getNumDeliveries(bs));
				if(highlightedState.numDeliveries>maxY)
					maxY = highlightedState.numDeliveries;
				break;
			case AVERAGE_DELIVERY_TIME:
				this.plot.addLinePlot(pt.name(), getAverageDeliveryTime(bs));
				if(highlightedState.getAverageDeliveryTime()>maxY)
					maxY = highlightedState.getAverageDeliveryTime();
				break;
			case TOTAL_DELIVERY_TIME:
				this.plot.addLinePlot(pt.name(), getTotDeliveryTime(bs));
				if(highlightedState.totDeliveryTime>maxY)
					maxY = highlightedState.totDeliveryTime;
				break;
			}





		}
		//add guide
		int dots=50;
		double[] x = new double[dots];
		double[] y = new double[dots];
		double stepSize = maxY/dots;
		for(int i=0;i<dots;i++){
			x[i]=highlightedState.date.getTime();
			y[i]=stepSize*i;

		}


		// set y axis label
		plot.setAxisLabel(1, getYAxisLabel());

		plot.setName("hey");

		// set x axis label
		plot.setAxisLabel(0, "time");

		// set x axis markers
		Axis axis = plot.getAxis(0);

		axis.setLightLabelAngle(0);

		long[] times = getTimes(bs);
		String[] stringTimes = new String[11];
		if (times != null && times.length > 1) {
			long minDate = times[0];
			long maxDate = times[times.length - 1];
			long dx = (maxDate - minDate) / 10;
			for (int i = 0; i <= 10; i++) {
				if (i % 2 != 0) {
					stringTimes[i] = "";
					continue;
				}
				// TODO make this work with scaled dates
				Date date = new Date(minDate + i * dx);
				String str = date.toString();

				// chop of NZST 2014
				str = str.substring(0, str.length() - 15);

				stringTimes[i] = str;

			}
			axis.setLightLabelText(stringTimes);
		}

	}

	private String getYAxisLabel() {
		String str = "";
		for (PlotType pt : visiblePlots) {
			switch (pt) {
			case MONEY_IN:
				str += "Money In (dollars)";
				break;
			case MONEY_OUT:
				str += "Money Out (dollars)";
				break;
			case NUMBER_OF_DELIVERIES:
				str += "Number of Deliveries";
				break;
			case AVERAGE_DELIVERY_TIME:
				str += "Average Delivery Time(hours)";
				break;
			case TOTAL_DELIVERY_TIME:
				str += "Totsl Delivery Time(hours)";
				break;
			}
			str += "/";
		}
		// cut off last dash
		if (str.length() > 0) {
			str = str.substring(0, str.length() - 1);
		}
		return str;

	}

	private JPanel makeCheckBoxPanel() {
		JPanel jp = new JPanel() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				g.clearRect(0, 0, this.getWidth(), this.getHeight());
				g.setColor(Color.red);
				g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
				this.paintComponents(g);
			}
		};

		PlotType[] plotTypes = PlotType.values();
		jp.setLayout(new GridLayout(plotTypes.length + 1, 1));
		JLabel jl = new JLabel("Values to Plot");
		jl.setFont(new Font("Arial", Font.BOLD, 12));
		jp.add(jl);

		for (PlotType pt : plotTypes) {

			JCheckBox cb = new JCheckBox(getNiceName(pt));
			cb.addItemListener(new MyItemListener(pt));
			jp.add(cb);

		}

		return jp;
	}

	public class MyItemListener implements ItemListener {
		private PlotType pt;

		public MyItemListener(PlotType pt) {
			this.pt = pt;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			visiblePlots.add(pt);
			// System.out.println(e.getSource());
			JCheckBox cb = (JCheckBox) e.getSource();

			if (cb.isSelected()) {
				MyPlotPanel.this.visiblePlots.add(pt);
			} else {
				MyPlotPanel.this.visiblePlots.remove(pt);
			}
			MyPlotPanel.this.updatePlots();
		}

	}

	private double[][] getMoneyIn(BusinessState bs) {
		int stateNum = bs.getStateNum();
		double[][] dubdubdub = new double[2][stateNum + 1];
		BusinessState state = bs;

		while (stateNum >= 0) {
			dubdubdub[0][stateNum] = state.date.getTime();
			dubdubdub[1][stateNum] = state.totMoneyIn;
			stateNum--;
			state = state.previousState;
		}

		return dubdubdub;
	}

	@SuppressWarnings("unused")
	private double[] getMoneyInOnly(BusinessState bs) {
		int stateNum = bs.getStateNum();
		double[] values = new double[stateNum + 1];
		BusinessState state = bs;

		while (stateNum >= 0) {

			values[stateNum] = state.totMoneyIn;
			stateNum--;
			state = state.previousState;
		}

		return values;
	}

	private long[] getTimes(BusinessState bs) {
		int stateNum = bs.getStateNum();
		long[] times = new long[stateNum + 1];
		BusinessState state = bs;

		while (stateNum >= 0) {

			times[stateNum] = state.date.getTime();
			stateNum--;
			state = state.previousState;
		}

		return times;

	}

	private double[][] getMoneyOut(BusinessState bs) {
		int stateNum = bs.getStateNum();
		double[][] dubdubdub = new double[2][stateNum + 1];
		BusinessState state = bs;

		while (stateNum >= 0) {
			dubdubdub[0][stateNum] = state.date.getTime();
			dubdubdub[1][stateNum] = state.totMoneyOut;
			stateNum--;
			state = state.previousState;
		}

		return dubdubdub;
	}

	private double[][] getNumDeliveries(BusinessState bs) {
		int stateNum = bs.getStateNum();
		double[][] dubdubdub = new double[2][stateNum + 1];
		BusinessState state = bs;

		while (stateNum >= 0) {
			dubdubdub[0][stateNum] = state.date.getTime();
			dubdubdub[1][stateNum] = state.numDeliveries;
			stateNum--;
			state = state.previousState;
		}

		return dubdubdub;
	}

	private double[][] getAverageDeliveryTime(BusinessState bs) {
		int stateNum = bs.getStateNum();
		double[][] dubdubdub = new double[2][stateNum + 1];
		BusinessState state = bs;

		while (stateNum >= 0) {
			dubdubdub[0][stateNum] = state.date.getTime();
			dubdubdub[1][stateNum] = state.getAverageDeliveryTime();
			stateNum--;
			state = state.previousState;
		}

		return dubdubdub;
	}

	private double[][] getTotDeliveryTime(BusinessState bs) {
		int stateNum = bs.getStateNum();
		double[][] dubdubdub = new double[2][stateNum + 1];
		BusinessState state = bs;

		while (stateNum >= 0) {
			dubdubdub[0][stateNum] = state.date.getTime();
			dubdubdub[1][stateNum] = state.totDeliveryTime;
			stateNum--;
			state = state.previousState;
		}

		return dubdubdub;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		this.updatePlots();
	}

}
