package events;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Observable;
import xml.XMLException;
import xml.XMLManager;

/**
 * This class keeps the history of all events processed
 *
 * @author hawkinchri
 *
 */
public class EventHistory extends Observable {
	private static EventHistory eventHist;

	private static List<BusinessEvent> events; //<-- made static
	private static boolean startUp = true;

	private EventHistory() {
		eventHist = this;
		events = new ArrayList<BusinessEvent>();
	}

	/**
	 * Reads all the existing event history from XML. This method will reprocess
	 * all the events in order to get the System up to date
	 */
	public static void readyEventHistoryFromXML(boolean test, boolean test2) {
		eventHist = new EventHistory();
		try {
			XMLManager.loadLocations(test, test2);

			XMLManager.loadCompanies(test, test2);
			List<BusinessEvent> list = XMLManager.loadLogToList(test);

			// create the first businessState
			Date earliestDate = GregorianCalendar.getInstance().getTime();
			if (list.size() > 0)
				earliestDate = list.get(0).getProcessDate();
			BusinessState.currentState = BusinessState.startState(earliestDate);

			// reprocess all the events
			for (int i = 0; i < list.size(); i++) {
				list.get(i).process();
			}
			startUp = false;
		} catch (XMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the EventHistory object for the system.
	 * @return
	 */
	public static EventHistory getEventHistory() {
		if (eventHist == null) {
			// eventHist = new EventHistory();
			throw new IllegalStateException(
					"EventHistory not yet initialised!!!");
		}
		return eventHist;
	}

	/**
	 * Prints to the business Log and stores the event in memory
	 */
	public void logEvent(BusinessEvent be) {
		events.add(be);
		// don't write stuff to xml on start up (as it is being read from there)
		if (!startUp) {
			try {
				XMLManager.addEventToLog(be);
			} catch (XMLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Get all events in an ordered array from least to most recent
	 *
	 * @return The ordered array of events from least to most recent
	 */
	public static BusinessEvent[] allEventsArray() {   //<-- made static
		BusinessEvent[] arr = new BusinessEvent[events.size()];
		for (int i = 0; i < events.size(); i++)
			arr[i] = events.get(i);
		return arr;
	}

}
