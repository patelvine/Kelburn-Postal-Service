package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import events.Company;
import events.Location;
import events.MailEvent;
import events.PathFindNode;
import events.Priority;
import events.Route;
import events.TransportCostUpdateEvent;
import events.TransportType;

public class MailEventTester {

	/**
	 * Tests whether a mail can be created
	 */
	@Test
	public void testNewMailNotNull() throws IOException {
		MailEvent mail = createTestMail("India", "China", 5, 5);
		if (mail == null)
			fail("new mail was not created");
	}

	/**
	 * Tests whether the find path algorithm returns a path
	 */
	@Test
	public void testFindPathNotNull() throws IOException {
		createTestRoutes("Dick Smith", "Canada", "China", 5, 5, 5, 5, 5, 5);
		createTestRoutes("Dick Smith", "China", "India", 5, 5, 5, 5, 5, 5);
		createTestRoutes("Dick Smith", "India", "New Zealand", 5, 5, 5, 5, 5, 5);
		createTestRoutes("Dick Smith", "New Zealand", "Aussie", 5, 5, 5, 5, 5,
				5);

		MailEvent mail = createTestMail("Canada", "Aussie", 5, 5);

		if (mail.findPathNode() == null)
			fail("path was not found");
	}

	/**
	 * Tests whether a false path can be found
	 */
	@Test
	public void testNonExistentPath() throws IOException {
		createTestRoutes("Dick Smith", "Canada", "China", 5, 5, 5, 5, 5, 5);
		createTestRoutes("Dick Smith", "China", "India", 5, 5, 5, 5, 5, 5);
		createTestRoutes("Dick Smith", "India", "New Zealand", 5, 5, 5, 5, 5, 5);
		createTestRoutes("Dick Smith", "New Zealand", "Aussie", 5, 5, 5, 5, 5,
				5);

		MailEvent mail = createTestMail("India", "China", 5, 5);

		if (mail.findPathNode() != null)
			fail("false path was found");
	}

	/**
	 * Tests whether the find path found is correct
	 */
	@Test
	public void testFindPathIsCorrect() throws IOException {
		createTestRoutes("Dick Smith", "Canada", "China", 5, 5, 5, 5, 5, 5);
		createTestRoutes("Dick Smith", "China", "India", 5, 5, 5, 5, 5, 5);
		createTestRoutes("Dick Smith", "India", "New Zealand", 5, 5, 5, 5, 5, 5);

		MailEvent mail = createTestMail("Canada", "New Zealand", 5, 5);

		List<Route> path = mail.findPathNode().getRoutes();
		if (path != null) {
			if (path.get(0).origin.name == "Canada"
					&& path.get(0).destination.name == "China") {
				if (path.get(1).origin.name == "China"
						&& path.get(1).destination.name == "India") {
					if (path.get(2).origin.name == "India"
							&& path.get(2).destination.name == "New Zealand") {
						return;
					}
				}
			}
		} else {
			fail("Path was null");
		}
		fail("Path returned was not correct");
	}
	
	// remove mail event??

	public MailEvent createTestMail(String origin, String destination,
			int gram, int cubicCm) throws IOException {

		MailEvent.Builder m = new MailEvent.Builder();

		m.setPriortiy(Priority.INTERNATIONAL_AIR_PRIORITY);
		m.setCubicCentimetres(1);
		m.setDestination(new Location(destination, false));
		m.setGrams(1);
		m.setOrigin(new Location(origin, false));
		MailEvent mail = m.build();
		return mail;
	}

	public static void createTestRoutes(String company, String origin,
			String destination, int gram, int cubicCm, int maxGrams,
			int maxCubicCm, int hours, int departureHours) throws IOException {

		TransportCostUpdateEvent.Builder tcue = new TransportCostUpdateEvent.Builder();

		tcue.setCompany(new Company(company));
		tcue.setOrigin(new Location(origin, false));
		tcue.setDestination(new Location(destination, false));
		tcue.setTransportType(TransportType.AIR);
		tcue.setDollarsPerGram(gram);
		tcue.setDollarsPerCubicCentimetre(cubicCm);
		tcue.setMaxGrams(maxGrams);
		tcue.setMaxCubicCentimetres(maxCubicCm);
		tcue.setDurationHours(hours);
		tcue.setDepartureIntervalHours(departureHours);
		TransportCostUpdateEvent cur = tcue.build();
		Route.updateCost(cur);
	}
}