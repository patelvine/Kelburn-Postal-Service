package events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * This class represents a route transport may take. This is distinct from what
 * a customer orders. Customers know only the origin and destination, this may
 * be composed of many Routes.
 *
 * @author Chris
 *
 */
public class Route {

	public final Location origin;
	public final Location destination;
	public final TransportType transportType;
	public final Company company;
	public final double dollarsPerGram;
	public final double dollarsPerCubicCentimetre;
	public final double durationHours;
	public final double departureIntervalHours;
	public final double maxGrams;
	public final double maxCubicCentimetres;
	private static List<Route> currentRoutes = new ArrayList<Route>();

	/**
	 * Create a new Route from a TransportCostUpdateEvent
	 *
	 * @param tcue
	 */
	private Route(TransportCostUpdateEvent tcue) {
		this.origin = tcue.origin;
		this.destination = tcue.destination;
		this.transportType = tcue.transportType;
		this.dollarsPerGram = tcue.dollarsPerGram;
		this.dollarsPerCubicCentimetre = tcue.dollarsPerCubicCentimetre;
		this.company = tcue.company;
		this.durationHours = tcue.durationHours;
		this.departureIntervalHours = tcue.departureIntervalHours;
		this.maxGrams = tcue.maxGrams;
		this.maxCubicCentimetres = tcue.maxCubicCentimetres;
	}

	public Date getNextDepartureAfter(Date time) {

		// the cal we are looking for a time after.
		Calendar afterCal = new GregorianCalendar();
		afterCal.setTime(time);

		// assume first departue on 00:00 1st of jan 2014
		Calendar searchCal = new GregorianCalendar(2014, 0, 0, 0, 0, 0);
		while (searchCal.before(afterCal)) {
			addHours(this.departureIntervalHours, searchCal);
		}

		return searchCal.getTime();
	}

	public static void addHours(double hours, Calendar cal) {
		int h = (int) hours;
		cal.add(Calendar.HOUR, h);

		int m = (int) ((hours - h) * 60);
		cal.add(Calendar.MINUTE, m);

		int s = (int) ((((hours - h) * 60) - m) * 60);
		cal.add(Calendar.SECOND, s);
	}

	/**
	 * Updates the current routes using a TransportCostUpdateEvent. If the
	 * current route doesn't exist it is created and entered
	 *
	 * @param tcue
	 *            The business event representing the change in cost
	 */
	public static void updateCost(TransportCostUpdateEvent tcue) {
		Route route = new Route(tcue);

		// if already exists in current routes remove outdated one
		for (int i = 0; i < currentRoutes.size(); i++) {
			Route r = currentRoutes.get(i);
			if (r.origin.equals(tcue.origin)) {
				if (r.destination.equals(tcue.destination)) {
					if (r.company.equals(tcue.company)) {
						if (r.transportType.equals(tcue.transportType)) {
							currentRoutes.remove(r);
						}
					}
				}
			}

		}
		// remove old one if there
//		Route old = null;
//		for (Route r : currentRoutes) {
//			if (r.origin == route.origin && r.destination == route.destination
//					&& r.company == route.company
//					&& r.transportType == route.transportType) {
//				old = r;
//			}
//		}
//		if (old != null) {
//			currentRoutes.remove(old);
//		}
		// add updated route to list
		currentRoutes.add(route);
	}

	/**
	 * Attempts to get from teh list of current Routes the route specified by
	 * the destination, origin and transportType. Returns null if not presetn
	 *
	 *
	 * @param origin
	 *            The origin of the Route to look for
	 * @param destination
	 *            The destination of the Route to look for
	 * @param transportType
	 *            The type of transport of the Route to look for
	 * @return The Route if found or null
	 */
	public static Route getRoute(Location origin, Location destination,
			TransportType transportType, Company company) {

		for (Route route : currentRoutes)
			if (route.origin.equals(origin))
				if (route.destination.equals(destination))
					if (route.transportType.equals(transportType))
						if (route.company.equals(company))
							return route;
		return null;
	}

	/**
	 * Removes the route from the list of currently available routes
	 *
	 * @param origin
	 *            The origin of the route to be removed
	 * @param destination
	 * @param transportType
	 */
	public static void removeRoute(Location origin, Location destination,
			TransportType transportType, Company company) {
		Route toRemove = null;
		for (Route route : currentRoutes)
			if (route.origin.equals(origin))
				if (route.destination.equals(destination))
					if (route.transportType.equals(transportType))
						toRemove = route;
		if (toRemove != null)
			currentRoutes.remove(toRemove);
		else
			System.out.println("WARNING:Tried to delete route("
					+ origin.toString() + " to " + destination.toString()
					+ " by " + transportType.name() + " with "
					+ company.toString());
	}

	public static List<Route> getAllRoutesOut(Location origin,
			TransportType transportType) {
		List<Route> routes = new ArrayList<Route>();
		for (Route route : currentRoutes)
			if (route.origin.equals(origin))
				if (route.transportType.equals(transportType))
					routes.add(route);
		return routes;

	}

	public static List<Route> getAllRoutesOut(Location origin) {
		List<Route> routes = new ArrayList<Route>();
		for (Route route : currentRoutes)
			if (route.origin.equals(origin))
				routes.add(route);
		return routes;

	}

	/**
	 * Gets all the routes from an origin to a destination.
	 *
	 * @param origin
	 *            The origin location
	 * @param destination
	 *            The destination location
	 * @return List of all possible routes from the origin to the destination
	 */
	public static List<Route> getAllRoutes(Location origin, Location destination) {
		List<Route> routes = new ArrayList<Route>();
		for (Route route : currentRoutes)
			if (route.origin.equals(origin))
				if (route.destination.equals(destination))
					routes.add(route);
		return routes;
	}

	public static List<Route> getAllRoutesOut(Location origin, Priority priority) {
		List<Route> routes = new ArrayList<Route>();
		for (Route route : currentRoutes)
			if (route.origin.equals(origin)) {

				// DOMESTIC_AIR_PRIORITY
				if (priority == Priority.DOMESTIC_AIR_PRIORITY) {
					if (route.transportType.equals(TransportType.AIR)
							&& !route.destination.international)
						routes.add(route);
				}
				// INTERNATIONAL_AIR_PRIORITY
				if (priority == Priority.INTERNATIONAL_AIR_PRIORITY) {
					if (route.transportType == TransportType.AIR)
						routes.add(route);
				}

				// DOMESTIC_STANDARD_PRIORITY
				if (priority == Priority.DOMESTIC_STANDARD_PRIORITY) {
					if (!route.destination.international)
						routes.add(route);
				}

				// INTERNATIONAL_STANDARD_PRIORITY
				if (priority == Priority.INTERNATIONAL_STANDARD_PRIORITY) {
					routes.add(route);
				}
			}
		return routes;
	}

	/**
	 * Gets all the routes from an origin to a destination by a specificed
	 * transportation method
	 *
	 * @param origin
	 *            The origin Location
	 * @param destination
	 *            The destination Location
	 * @param transportType
	 *            The required transportType
	 * @return List of all possible routes from the origin to the destination
	 */
	public static List<Route> getAllRoutes(Location origin,
			Location destination, TransportType transportType) {
		List<Route> routes = new ArrayList<Route>();
		for (Route route : currentRoutes)
			if (route.origin.equals(origin))
				if (route.destination.equals(destination))
					if (route.transportType.equals(transportType))
						routes.add(route);
		return routes;
	}

	public double calculateCost(double cubicCentimetres, double grams) {
		double costVolume = dollarsPerCubicCentimetre * cubicCentimetres;
		double costWeight = dollarsPerGram * grams;
		if (costVolume > costWeight)
			return costVolume;
		else
			return costWeight;

	}

	/**
	 * Gets the final path node for the best path between two points given a
	 * priority and process date
	 *
	 * @param origin
	 * @param dest
	 * @param priority
	 * @param processDate
	 * @return
	 */
	public static PathFindNode findPath(Location origin, Location destination,
			Priority priority, double grams, double cubicCentimetres,
			Date processDate) {

		Set<Location> visitedLocs = new HashSet<Location>();

		Comparator<PathFindNode> comparator;
		// based on priority decide whether to charge
		if (priority == Priority.DOMESTIC_AIR_PRIORITY
				|| priority == Priority.INTERNATIONAL_AIR_PRIORITY)
			comparator = new PathFindNode.AirPriorityComparator();
		else
			comparator = new PathFindNode.StandardPriorityComparator();
		PriorityQueue<PathFindNode> fringe = new PriorityQueue<PathFindNode>(5,
				comparator);
		PathFindNode pfn1 = new PathFindNode(null, origin, destination, 0, 0,
				Priority.DOMESTIC_AIR_PRIORITY, null);
		fringe.add(pfn1);
		while (!fringe.isEmpty()) {
			PathFindNode pfn = fringe.poll();
			if (!visitedLocs.contains(pfn.currentLocation)) {
				visitedLocs.add(pfn.currentLocation);
				if (pfn.currentLocation.equals(destination)) {
					return pfn;
				}

				for (Route route : Route.getAllRoutesOut(pfn.currentLocation,
						priority)) {
					Location neighbour = route.destination;
					if (!visitedLocs.contains(neighbour)) {

						// get calendar for time at pfn
						Calendar cal = new GregorianCalendar();
						cal.setTime(processDate);
						addHours(pfn.currentCostTime, cal);

						double costDollars = route.calculateCost(
								cubicCentimetres, grams);
						double durHours = route.durationHours;
						Date nextDeparture = route.getNextDepartureAfter(cal
								.getTime());
						long waitMillis = nextDeparture.getTime()
								- cal.getTimeInMillis();
						double waitHours = waitMillis / (60 * 60 * 1000);

						double costTime = pfn.currentCostTime + waitHours
								+ durHours;

						PathFindNode newPfn = new PathFindNode(route,
								route.destination, destination,
								pfn.currentCostDollars + costDollars, costTime,
								priority, pfn);
						fringe.add(newPfn);
					}

				}

			}

		}

		return null;
	}
	/**
	 * This class is used to query the list of all Routes.
	 * @author hawkinchri
	 *
	 */
	public static class Query {
		Location origin = null;
		Location destination = null;
		TransportType transportType = null;
		Company company = null;

		public void setOrigin(Location origin) {
			this.origin = origin;
		}

		public void setDestination(Location destination) {
			this.destination = destination;
		}

		public void setTransportType(TransportType tt) {
			this.transportType = tt;
		}

		public void setCompany(Company comp) {
			this.company = comp;
		}

		public List<Route> getAllQualifyingRoutes() {
			List<Route> routes = new ArrayList<Route>();
			for (Route route : currentRoutes)
				if (this.origin == null || route.origin.equals(this.origin)) {
					if (this.destination == null
							|| route.destination.equals(this.destination)) {
						if (this.transportType == null
								|| route.transportType == this.transportType) {
							if (this.company == null
									|| route.company.equals(this.company))
								routes.add(route);
						}
					}
				}
			return routes;
		}

		public Route getQualifyingRoute(){
			List<Route> routes = getAllQualifyingRoutes();
			if(routes.isEmpty())
				return null;
			else
				return routes.get(0);
		}

	}
	public static List<Route> getCurrentRoutes() {
		return currentRoutes;
	}
	
	

}
