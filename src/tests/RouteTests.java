/**
 * Scrum 1
 *  log/save to xml <---- ask how (leave last)
	view current business event <--- cant test.. thats for the gui

	Scrum 2
	delete mail
	view event log
	view business history
	view routes on google map
	day -> date
	load event xml <---- ask how
	store companies in xml <--- ask how
	implement priority route choices


	//	 num,  company,  origin, destination,  gram,  cubicCm,  maxGrams,  maxCubicCm,  hours,  departureHours
 */

package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import events.Company;
import events.DiscontinueRouteEvent;
import events.EventHistory;
import events.Location;
import events.Route;
import events.TransportCostUpdateEvent;
import events.TransportType;

public class RouteTests {

	/**
	 * Tests if route can be created
	 */
	@Test
	public void createRouteTest() throws IOException {
		createTestRoutes(1, "Dick Smith", "America", "Canada", 5, 5, 5, 5, 5, 5);

		if (Route.getAllRoutesOut(new Location("America0", false)).size() == 0)
			fail("route was not made");
	}

	/**
	 * Tests whether exisiting route is created
	 */
	@Test
	public void createExistingRouteTest() throws IOException {
		createTestRoutes(1, "Dick Smith", "India", "Canada", 5, 5, 5, 5, 5, 5);
		createTestRoutes(1, "Dick Smith", "India", "Canada", 5, 5, 5, 5, 5, 5);
		int count = 0;

		for (@SuppressWarnings("unused") Route i : Route.getAllRoutes(new Location("India0", false),
				new Location("Canada0", false)))
			count++;

		if (count > 1){
			System.out.println(count);
			fail("route was made twice");
		}
	}

	/**
	 * Tests whether a route can be deleted
	 */
	@Test
	public void deleteRouteTest() throws IOException {
		createTestRoutes(1, "Dick Smith", "iraq", "Canada", 5, 5, 5, 5, 5, 5);
		Route.removeRoute(new Location("iraq0", false), new Location("Canada0",
				false), TransportType.AIR, new Company("Dick Smith0"));

		if (Route.getAllRoutesOut(new Location("iraq0", false)).size() > 0)
			fail("route was not removed");
	}

	/**
	 * Tests if a current route can be updated with new values (CubicCentimetre
	 * was only changed from 5 to 6)
	 */
	@Test
	public void updateRouteTest() throws IOException {
		createTestRoutes(1, "P", "I", "C", 5, 5, 5, 5, 5, 5);
		createTestRoutes(1, "P", "I", "C", 5, 6, 5, 5, 5, 5);
		List<Route> rL = Route.getAllRoutes(new Location("I0", false),
				new Location("C0", false));
		if (rL.size() >= 2) {
			if (rL.get(0).dollarsPerCubicCentimetre != 6)
				fail("Did not update the route (CubicCentimetre was used to test this)");
		}
	}

	/**
	 * Tests whether cost along a route is calculated correctly
	 */
	@Test
	public void costCaluclationTest() throws IOException {
		createTestRoutes(1, "Dick Smith", "y", "z", 2, 3, 4, 5, 6, 7);
		Route r = Route.getAllRoutesOut(new Location("y0", false)).get(0);
		double cost = r.calculateCost(10, 20);
		double costVolume = r.dollarsPerCubicCentimetre * 10;
		double costWeight = r.dollarsPerGram * 20;

		if (cost != costVolume && cost != costWeight) {
			fail("Calucatling the cost of a deal on a package was incorrect");
		}
	}

	/**
	 *
	 */
	@Test
	public void discontinueRouteTest() throws IOException {
		createTestRoutes(1, "Dick Smith", "A", "C", 5, 5, 5, 5, 5, 5);
		Route.removeRoute(new Location("A0", false), new Location("C0", false),
				TransportType.AIR, new Company("Dick Smith0"));

		if (Route.getAllRoutes(new Location("A0", false),
				new Location("C0", false)).size() != 0)
			fail("route was not made");
	}
	
	@Test
	public void discontinueRouteEventTest() throws IOException {
		setUp();
		TransportCostUpdateEvent tcue = createTestRoutes(1, "people", "h", "g", 1, 1, 1, 1, 1, 1);
		DiscontinueRouteEvent dre = createTestDiscontinueRouteEvent(
				"people0", "h0", "g0");
		
		if (Route.getAllRoutes(new Location("h0", false),
				new Location("g0", false)).size() != 0)
			fail("route was not made");
	}

	/**
	 *
	 */
	@Test
	public void getCorrectNextDepartureAfterTest() throws IOException {
	}

	/**
	 *
	 */
	@Test
	public void addHoursTest() throws IOException {
	}

	public TransportCostUpdateEvent createTestRoutes(int num, String company, String origin,
			String destination, int gram, int cubicCm, int maxGrams,
			int maxCubicCm, int hours, int departureHours) throws IOException {

		TransportCostUpdateEvent.Builder tcue = new TransportCostUpdateEvent.Builder();

		for (int i = 0; i < num; i++) {
			tcue.setCompany(new Company(company + i));
			tcue.setOrigin(new Location(origin + i, false));
			tcue.setDestination(new Location(destination + i, false));
			tcue.setTransportType(TransportType.AIR);
			tcue.setDollarsPerGram(gram + i);
			tcue.setDollarsPerCubicCentimetre(cubicCm + i);
			tcue.setMaxGrams(maxGrams + i);
			tcue.setMaxCubicCentimetres(maxCubicCm + i);
			tcue.setDurationHours(hours + i);
			tcue.setDepartureIntervalHours(departureHours + i);
			TransportCostUpdateEvent cur = tcue.build();
			Route.updateCost(cur);
			if(num == 1){
				return cur;
			}
		}
		return null;
	}
	
	public DiscontinueRouteEvent createTestDiscontinueRouteEvent(
			String company, String orig, String des) throws IOException {

		DiscontinueRouteEvent.Builder builder = new DiscontinueRouteEvent.Builder();
		builder.setOrigin(new Location(orig, false));
		builder.setDestination(new Location(des, false));
		builder.setCompany(new Company(company));
		builder.setTransportType(TransportType.AIR);
		DiscontinueRouteEvent dre = builder.build();
		dre.process();
		return dre;
	}
	
	public void setUp() {
		EventHistory.readyEventHistoryFromXML(true, false);
	}
}
