package tests;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import events.BusinessEvent;
import events.Company;
import events.CustomerPriceUpdateEvent;
import events.DiscontinueCustomerPriceEvent;
import events.DiscontinueRouteEvent;
import events.EventHistory;
import events.Location;
import events.MailEvent;
import events.Priority;
import events.Route;
import events.TransportCostUpdateEvent;
import events.TransportType;

public class EventHisTester {
	// allEventsArray

	@Test
	public void logEvent() throws IOException {
		setUp();
		// this event gets loged in while it gets created
		CustomerPriceUpdateEvent cpue = createTestCustomerPrices(1, 1, 1,
				"testo", "testd");

		int count = 0;
		for (BusinessEvent be : EventHistory.allEventsArray()) {
			if (be == cpue) {
				count++;
			}
		}
		if (count != 1) {
			fail("event was not found within the log");
		}
	}

	@Test
	public void allEventHisCorrect() throws IOException {
		setUp();
		CustomerPriceUpdateEvent cpue1 = createTestCustomerPrices(1, 1, 1,
				"testo1", "testd1");
		CustomerPriceUpdateEvent cpue2 = createTestCustomerPrices(2, 2, 2,
				"testo2", "testd2");

		BusinessEvent[] bb = EventHistory.allEventsArray();

		if (bb[0] != cpue1 && bb[1] != cpue2) {
			fail("Event history was not correct (out of order)");
		}
	}

	// 1,2 test, add 3 test
	@Test
	public void allEventHisCorrect2() throws IOException {
		setUp();
		CustomerPriceUpdateEvent cpue1 = createTestCustomerPrices(1, 1, 1,
				"testo1", "testd1");
		CustomerPriceUpdateEvent cpue2 = createTestCustomerPrices(2, 2, 2,
				"testo2", "testd2");

		BusinessEvent[] bb = EventHistory.allEventsArray();
		if (bb[0] != cpue1 || bb[1] != cpue2) {
			fail("Event history was not correct (out of order)");
		}

		CustomerPriceUpdateEvent cpue3 = createTestCustomerPrices(3, 3, 3,
				"testo3", "testd3");

		bb = EventHistory.allEventsArray();
		if (bb[0] != cpue1 || bb[1] != cpue2 || bb[2] != cpue3) {
			fail("Event history was not correct (out of order)");
		}
	}

	// out of order check
	@Test
	public void eventHisFalse1() throws IOException {
		setUp();
		CustomerPriceUpdateEvent cpue3 = createTestCustomerPrices(3, 3, 3,
				"testo3", "testd3");
		CustomerPriceUpdateEvent cpue1 = createTestCustomerPrices(1, 1, 1,
				"testo1", "testd1");
		CustomerPriceUpdateEvent cpue2 = createTestCustomerPrices(2, 2, 2,
				"testo2", "testd2");

		BusinessEvent[] bb = EventHistory.allEventsArray();
		if (bb[0] != cpue3 || bb[1] != cpue1 || bb[2] != cpue2) {
			fail("Event history was not correct (out of order)");
		}
	}

	// with all types of events new cus price/ dis route / new mail / dis route
	// / dis cus price
	@Test
	public void allEventHisCorrect3() throws IOException {
		setUp();
		CustomerPriceUpdateEvent cpue3 = createTestCustomerPrices(1, 1, 1,
				"testo3", "testd3");
		TransportCostUpdateEvent tcue = createTestRoutes("people", "testo3",
				"testd3", 1, 1, 1, 1, 1, 1);
		DiscontinueCustomerPriceEvent dcpe = createTestDiscontinueCustomerPrices(
				1, 1, 1, "testo3", "testd3");
		DiscontinueRouteEvent dre = createTestDiscontinueRouteEvent(
				"people", "testo3", "testd3");

		BusinessEvent[] bb = EventHistory.allEventsArray();
		System.out.println(bb.length);
		if (bb[0] != cpue3 || bb[1] != tcue || bb[2] != dcpe || bb[3] != dre) {
			fail("Event history was not correct (out of order)");
		}
	}

	public void setUp() {
		EventHistory.readyEventHistoryFromXML(true,false);
	}

	public DiscontinueCustomerPriceEvent createTestDiscontinueCustomerPrices(
			int num, double dpcc, double dpkg, String orig, String des)
			throws IOException {

		DiscontinueCustomerPriceEvent.Builder builder = new DiscontinueCustomerPriceEvent.Builder();
		builder.setOrigin(new Location("t0", false));
		builder.setDestination(new Location("h0", false));
		builder.setPriority(Priority.INTERNATIONAL_AIR_PRIORITY);
		DiscontinueCustomerPriceEvent dcpe = builder.build();
		dcpe.process();
		return dcpe;
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

	public CustomerPriceUpdateEvent createTestCustomerPrices(int num,
			double dpcc, double dpkg, String orig, String des)
			throws IOException {

		CustomerPriceUpdateEvent.Builder builder = new CustomerPriceUpdateEvent.Builder();
		builder.setDollarsPerCubicCentimetre(dpcc);
		builder.setDollarsPerGram(dpkg);
		builder.setOrigin((new Location(orig, false)));
		builder.setDestination((new Location(des, false)));
		builder.setPriority(Priority.INTERNATIONAL_AIR_PRIORITY);
		CustomerPriceUpdateEvent cpue = builder.build();
		cpue.process();
		return cpue;
	}

	public static TransportCostUpdateEvent createTestRoutes(String company,
			String origin, String destination, int gram, int cubicCm,
			int maxGrams, int maxCubicCm, int hours, int departureHours)
			throws IOException {

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
		cur.process();
		return cur;
	}
}
