package tests;

import static org.junit.Assert.fail;

import java.io.IOException;

import main.Login;

import org.junit.Test;

import events.Company;
import events.CustomerDeal;
import events.CustomerPriceUpdateEvent;
import events.DiscontinueCustomerPriceEvent;
import events.DiscontinueRouteEvent;
import events.EventHistory;
import events.Location;
import events.MailEvent;
import events.Priority;
import events.TransportType;
import events.CustomerDeal.Query;
import gui.FileMenuListener;

public class CustomerPriceAndDealTester {

	/**
	 * Tests if a new CustomerPrice can be created
	 */
	@Test
	public void createNewCustomerPrice() throws IOException {
		setUp();
		createTestCustomerPrices(1, 10, 10, "India", "New Zealand");
		CustomerDeal cd;
		CustomerDeal.Query q = new CustomerDeal.Query();
		q.setOrigin(new Location("India0",false));
		q.setDestination(new Location("New Zealand0",false));
		q.setPriority(Priority.INTERNATIONAL_AIR_PRIORITY);
		cd = q.getQualifyingDeal();
		if (cd == null)
			fail("Customer Price was not made");
	}

	/**
	 * Tests if a duplicate CustomerPrice can be created
	 */
	@Test
	public void createDuplicateCustomerPrice() throws IOException {
		setUp();
		createTestCustomerPrices(1, 10, 10, "Fiji", "New Zealand");
		createTestCustomerPrices(1, 10, 10, "Fiji", "New Zealand");
		
		CustomerDeal cd;
		CustomerDeal.Query q = new CustomerDeal.Query();
		q.setOrigin(new Location("Fiji0",false));
		q.setDestination(new Location("New Zealand0",false));
		q.setPriority(Priority.INTERNATIONAL_AIR_PRIORITY);
		cd = q.getQualifyingDeal();
		
		int count = 0;
		for (CustomerDeal deal : CustomerDeal.currentDeals) {
			if (deal == cd) {
				count++;
			}
		}
		if (count > 1)
			fail("Same customer price was made twice");
	}

	/**
	 * Tests if a customer price can be deletled
	 */
	@Test
	public void deleteCustomerPrice() throws IOException {
		setUp();
		createTestCustomerPrices(1, 10, 10, "Hong Kong", "China");
		
		CustomerDeal cd;
		CustomerDeal.Query q = new CustomerDeal.Query();
		q.setOrigin(new Location("Hong Kong0",false));
		q.setDestination(new Location("China0",false));
		q.setPriority(Priority.INTERNATIONAL_AIR_PRIORITY);
		cd = q.getQualifyingDeal();
		
		CustomerDeal.removeDeal(new Location("Hong Kong0", false),
				new Location("China0", false),
				Priority.INTERNATIONAL_AIR_PRIORITY);
		
		int count = 0;
		for (CustomerDeal deal : CustomerDeal.currentDeals) {
			if (deal == cd) {
				count++;
			}
		}
		if (count > 0)
			fail("Deal was not deleted");
	}

	/**
	 * Tests if an existing customer price can be updated with new values
	 */
	@Test
	public void updateCustomerPrice() throws IOException {
		setUp();
		createTestCustomerPrices(1, 10, 10, "France", "Canada");
		createTestCustomerPrices(1, 9, 15, "France", "Canada");
		
		CustomerDeal cd;
		CustomerDeal.Query q = new CustomerDeal.Query();
		q.setOrigin(new Location("France0",false));
		q.setDestination(new Location("Canada0",false));
		q.setPriority(Priority.INTERNATIONAL_AIR_PRIORITY);
		cd = q.getQualifyingDeal();

		if ((cd.dollarsPerCubicCentimetre != 9) || (cd.dollarsPerGram != 15))
			fail("Customer Deal was not updated correctly");
	}

	@Test
	public void getCusPriceByMail() throws IOException {
		setUp();
		createTestCustomerPrices(1, 10, 10, "x", "y");
		
		MailEvent.Builder m = new MailEvent.Builder();
		m.setPriortiy(Priority.INTERNATIONAL_AIR_PRIORITY);
		m.setCubicCentimetres(1);
		m.setOrigin(new Location("x0", false));
		m.setDestination(new Location("y0", false));
		m.setGrams(1);
		MailEvent mail = m.build();
		
		CustomerDeal cd;
		CustomerDeal.Query q = new CustomerDeal.Query();
		q.setOrigin(new Location("x0",false));
		q.setDestination(new Location("y0",false));
		q.setPriority(Priority.INTERNATIONAL_AIR_PRIORITY);
		cd = q.getQualifyingDeal();
		
		if(cd != CustomerDeal.getDeal(mail)){
			fail("Could not find a customer deal(does exist) for mail event");
		}
	}
	
//	@Test
//	public void disContinueCusPrice() throws IOException {
//		setUp();
//		Login.setLoginPrivilege(Login.AccessPrivilege.manager);
//		createTestCustomerPrices(1, 5, 7, "t", "h");
//		
//		DiscontinueCustomerPriceEvent.Builder builder = new DiscontinueCustomerPriceEvent.Builder();
//		builder.setOrigin(new Location("t0",false));
//		builder.setDestination(new Location("h0",false));
//		builder.setPriority(Priority.INTERNATIONAL_AIR_PRIORITY);
//		DiscontinueCustomerPriceEvent dcpe = builder.build();
//		dcpe.process();
//		
//		CustomerDeal cd;
//		CustomerDeal.Query q = new CustomerDeal.Query();
//		q.setOrigin(new Location("t0",false));
//		q.setDestination(new Location("h0",false));
//		q.setPriority(Priority.INTERNATIONAL_AIR_PRIORITY);
//		
//		if (q.getQualifyingDeal() != null)
//			fail("should have no qualifying deals. deal was not discontinued");
//	}

	@Test
	public void costCaluclationTest() throws IOException {
		setUp();
		createTestCustomerPrices(1, 5, 7, "c", "d");
		
		CustomerDeal cd;
		CustomerDeal.Query q = new CustomerDeal.Query();
		q.setOrigin(new Location("c0",false));
		q.setDestination(new Location("d0",false));
		q.setPriority(Priority.INTERNATIONAL_AIR_PRIORITY);
		cd = q.getQualifyingDeal();

		double cost = cd.getCost(12, 2);
		double costVolume = cd.dollarsPerCubicCentimetre * 12;
		double costWeight = cd.dollarsPerGram * 2;

		if (cost != costVolume && cost != costWeight) {
			fail("Calucatling the cost of a deal on a package was incorrect");
		}
	}

	@Test
	public void criticalPathCorrect() throws IOException {
		
	}
	
	@Test
	public void criticalPathIncorrect() throws IOException {
		
	}

	@Test
	public void getCriticalRanges() throws IOException {

	}

	public void setUp() {
		EventHistory.readyEventHistoryFromXML(true, false);
	}

	/**
	 * Helper method used to create a number of test deals
	 */
	public void createTestCustomerPrices(int num, double dpcc, double dpkg,
			String orig, String des) throws IOException {

		CustomerPriceUpdateEvent.Builder builder = new CustomerPriceUpdateEvent.Builder();

		for (int i = 0; i < num; i++) {
			builder.setDollarsPerCubicCentimetre(dpcc + i);
			builder.setDollarsPerGram(dpkg + i);
			builder.setOrigin((new Location(orig + i, false)));
			builder.setDestination((new Location(des + i, false)));
			builder.setPriority(Priority.INTERNATIONAL_AIR_PRIORITY);
			CustomerPriceUpdateEvent cpue = builder.build();
			cpue.process();
		}
	}
}
