package events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This is a search node used for finding series of transport routes between two
 * locations.
 *
 * @author Chris
 *
 */
public class PathFindNode {
	public PathFindNode previousNode;
	public Route routeHere;
	public Location destination;
	public Location currentLocation;

	/**
	 * Cost spent to get this far in dollars
	 */
	public double currentCostDollars;

	/**
	 * Current cost in time
	 */
	public double currentCostTime;

	/**
	 * Priority of the path
	 */
	public Priority priority;

	public PathFindNode(Route routeHere, Location loc, Location dest,
			double currentCostDollars, double currentCostTime,
			Priority priority, PathFindNode previousNode) {
		this.routeHere = routeHere;
		this.currentLocation = loc;
		this.destination = dest;
		this.currentCostDollars = currentCostDollars;
		this.currentCostTime = currentCostTime;
		this.priority = priority;
		this.previousNode = previousNode;
	}

	public static class CostComparator implements Comparator<PathFindNode> {

		@Override
		public int compare(PathFindNode o1, PathFindNode o2) {
			if (o1.currentCostDollars < o2.currentCostDollars)
				return -1;
			else
				return 1;
		}
	}

	public static class TimeComparator implements Comparator<PathFindNode> {

		@Override
		public int compare(PathFindNode pfn1, PathFindNode pfn2) {
			if (pfn1.currentCostTime < pfn2.currentCostTime)
				return -1;
			else
				return 1;
		}

	}

	/**
	 * This comparator is for stqandard international priority. It will always
	 * take land based route unless it can't. After this it orders based on
	 * cheapest
	 *
	 * @author Chris
	 *
	 */
	public static class StandardPriorityComparator implements
			Comparator<PathFindNode> {
		private CostComparator costComp = new CostComparator();

		@Override
		public int compare(PathFindNode pfn1, PathFindNode pfn2) {
			// if pfn2 is air and pfn1 isn't put pfn1 first
			if (pfn2.routeHere.transportType == TransportType.AIR
					&& pfn1.routeHere.transportType != TransportType.AIR)
				return -1;

			// if pfn1 is air and pfn2 isn't put pfn2 first
			if (pfn1.routeHere.transportType == TransportType.AIR
					&& pfn2.routeHere.transportType != TransportType.AIR)
				return 1;

			// otherwise compare based on price
			return costComp.compare(pfn1, pfn2);
		}
	}

	/**
	 * This comparator is for priority international priority. It will always
	 * take air based route unless it can't. After this it orders based on time
	 *
	 * @author Chris
	 *
	 */
	public static class AirPriorityComparator implements
			Comparator<PathFindNode> {
		private TimeComparator timeComp = new TimeComparator();

		@Override
		public int compare(PathFindNode pfn1, PathFindNode pfn2) {
			// if pfn2 is air and pfn1 isn't put pfn1 first
			if (pfn1.routeHere.transportType == TransportType.AIR
					&& pfn2.routeHere.transportType != TransportType.AIR)
				return -1;

			// if pfn1 is air and pfn2 isn't put pfn2 first
			if (pfn2.routeHere.transportType == TransportType.AIR
					&& pfn1.routeHere.transportType != TransportType.AIR)
				return 1;

			// otherwise compare based on price
			return timeComp.compare(pfn1, pfn2);
		}
	}

	public List<Route> getRoutes() {
		PathFindNode pfn = this;
		List<Route> routes = new ArrayList<Route>();
		while (pfn.routeHere != null) {
			routes.add(pfn.routeHere);
			pfn = pfn.previousNode;
		}

		Collections.reverse(routes);
		return routes;
	}
}
