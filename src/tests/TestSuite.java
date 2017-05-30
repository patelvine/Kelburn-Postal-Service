package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AccountTests.class, CustomerPriceAndDealTester.class,
		EventHisTester.class, MailEventTester.class, RouteTests.class, })
public class TestSuite {
}