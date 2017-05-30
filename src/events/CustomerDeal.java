package events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * This class represents the deal offered to customers for a shipment. This is
 * the public facing price for some shipment.
 *
 * @author Chris
 *
 */
public class CustomerDeal {
	public final Location origin;
	public final Location destination;
	public final Priority priority;
	public final double dollarsPerGram;
	public final double dollarsPerCubicCentimetre;
	public static List<CustomerDeal> currentDeals = new ArrayList<CustomerDeal>();

	/**
	 * Create a new CustomerDeal from a CustomerPriceUpdateEvent
	 *
	 * @param tcue
	 */
	private CustomerDeal(CustomerPriceUpdateEvent cpue) {
		this.origin = cpue.origin;
		this.destination = cpue.destination;
		this.dollarsPerGram = cpue.dollarsPerGram;
		this.dollarsPerCubicCentimetre = cpue.dollarsPerCubicCentimetre;
		this.priority = cpue.priority;
	}

	/**
	 * Updates the current deals using a TransportCostUpdateEvent. If the
	 * configureation doesn't exist it is created and entered
	 *
	 * @param tcue
	 *            The business event representing the change in cost
	 */
	public static void updateCost(CustomerPriceUpdateEvent cpue) {
		CustomerDeal deal = new CustomerDeal(cpue);

		// if already exists in current routes remove outdated one
		for (int i = 0; i < currentDeals.size(); i++) {
			CustomerDeal cd = currentDeals.get(i);
			if (cd.origin.equals(cpue.origin)) {
				if (cd.destination.equals(cpue.destination)) {
					if (cd.priority == cpue.priority) {
						currentDeals.remove(cd);
						break;
					}
				}
			}

		}
		// add updated route to list
		currentDeals.add(deal);

	}

	/**
	 * Gets the appropriate customer deal for the specified mail event or null
	 * if not found
	 *
	 * @param me
	 *            The mail event
	 * @return The CustomerDeal for the mail event or null if not found
	 */
	public static CustomerDeal getDeal(MailEvent me) {
		Query q = new Query();
		q.origin = me.origin;
		q.destination = me.destination;
		q.priority = me.priority;

		return q.getQualifyingDeal();
	}

	public static boolean removeDeal(Location orign, Location destination,
			Priority priority) {
		for (int i = 0; i < currentDeals.size(); i++) {
			CustomerDeal cd = currentDeals.get(i);
			if (orign.equals(cd.origin)) {
				if (destination.equals(cd.destination)) {
					if (priority == cd.priority) {
						currentDeals.remove(i);
						return true;
					}
				}

			}
		}
		return false;
	}

	/**
	 * For a given packages volume and weight will return the cost to the
	 * consumer
	 *
	 * @param cubicCentimetres
	 *            The volume of the package in cubic centimetres
	 * @param grams
	 *            The weight of the package in grams
	 * @return The cost of sending the package
	 */
	public double getCost(double cubicCentimetres, double grams) {
		double costVolume = dollarsPerCubicCentimetre * cubicCentimetres;
		double costWeight = dollarsPerGram * grams;
		if (costVolume < costWeight)
			return costWeight;
		else
			return costVolume;
	}

	/**
	 * Finds all the customer prices where KPS pays more than it makes
	 *
	 * @return Collection of all critical deals
	 */
	public static Collection<CustomerDeal> getCriticalDeals() {
		Collection<CustomerDeal> criticals = new ArrayList<CustomerDeal>();
		for (CustomerDeal cd : currentDeals) {
			if (cd.isCritical2())
				criticals.add(cd);
		}
		return criticals;

	}

	@SuppressWarnings("unused")
	private boolean isCritical() {
		Date dummyDate = GregorianCalendar.getInstance().getTime();
		// Test with weight
		// TODO check assumpiton of todays date correct
		PathFindNode pfn;
		pfn = Route.findPath(origin, destination, priority, 1, 0, dummyDate);

		if (this.dollarsPerGram < pfn.currentCostDollars) {
			System.out.println("Critical path due to cost per gram!");
			return true;
		}

		// test with ccs
		pfn = Route.findPath(origin, destination, priority, 0, 1, dummyDate);

		if (this.dollarsPerCubicCentimetre < pfn.currentCostDollars) {
			System.out.println("Cricital path due to cost per cc!");
			return true;
		}

		return false;
	}

	public boolean isCritical2() {
		double[][] costs = getCriticalRanges();
		for (int i = 0; i < costs.length; i++) {
			if (costs[0][i] > costs[1][i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * For a range of weights and volumes (that sum to 1) find all their prices
	 * on this deal
	 *
	 * @return returns a mapping from a grams
	 */
	public double[][] getCriticalRanges() {

		Date dummyDate = GregorianCalendar.getInstance().getTime();

		int divisions = 100;
		double[][] dubdubdub = new double[2][divisions + 1];
		double stepSize = 1.0 / ((double) divisions);

		for (int i = 0; i <= divisions; i++) {
			double grams = i * stepSize;
			double cc = 1 - grams;

			if (grams > 1)
				grams = 1;
			if (cc < 0)
				cc = 0;

			PathFindNode pfn = Route.findPath(origin, destination, priority,
					grams, cc, dummyDate);

			double customerCost = this.getCost(cc, grams);
			double kpsCost = pfn.currentCostDollars;
			dubdubdub[0][i] = kpsCost;
			dubdubdub[1][i] = customerCost;

		}

		return dubdubdub;

	}

	@Override
	public String toString() {
		String str = this.origin.toString() + " to "
				+ this.destination.toString() + " - " + this.priority.name();
		return str;
	}

	public static class Query {
		Location origin = null;
		Location destination = null;
		Priority priority = null;

		public void setPriority(Priority priority) {
			this.priority = priority;
		}

		public void setOrigin(Location origin) {
			this.origin = origin;
		}

		public void setDestination(Location destination) {
			this.destination = destination;
		}

		public List<CustomerDeal> getAllQualifyingDeals() {
			List<CustomerDeal> deals = new ArrayList<CustomerDeal>();
			for (CustomerDeal deal : currentDeals)
				if (this.origin == null || deal.origin.equals(this.origin)) {
					if (this.destination == null
							|| deal.destination.equals(this.destination)) {
						if (this.priority == null
								|| deal.priority == this.priority) {
							deals.add(deal);
						}
					}
				}
			return deals;
		}

		public CustomerDeal getQualifyingDeal(){
			List<CustomerDeal>deals = getAllQualifyingDeals();
			if(deals.size() < 0)
				return null;
			else
				return deals.get(0);

		}

	}

}
