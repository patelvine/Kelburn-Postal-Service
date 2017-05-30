package events;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DiscontinueCustomerPriceEvent implements BusinessEvent{
	/**
	 * The origin of the path to discontinue price for
	 */
	public final Location origin;

	/**
	 * The destination of the path to discontinue price for
	 */
	public final Location destination;

	/**
	 * The priority of the path to discontinue price for
	 */
	public final Priority priority;

	/**
	 * The date this event was processed
	 */
	public final Date processDate;

	private DiscontinueCustomerPriceEvent(Location origin,
			Location destination, Priority priority, Date processDate) {
		this.origin = origin;
		this.destination = destination;
		this.priority = priority;
		this.processDate = processDate;

	}

	@Override
	public Element saveToXML(Document doc) {
		Element eRoot = doc.createElement(this.getClass().getSimpleName());

		Element eProcessDate = doc.createElement("ProcessDate");
		eProcessDate.appendChild(doc.createTextNode(DateUtility
				.formatOutputDate(processDate)));
		eRoot.appendChild(eProcessDate);

		Element eOrigin = doc.createElement("Origin");
		eOrigin.appendChild(doc.createTextNode(origin.toString()));
		eRoot.appendChild(eOrigin);

		Element eDestination = doc.createElement("Destination");
		eDestination.appendChild(doc.createTextNode(destination.toString()));

		eRoot.appendChild(eDestination);

		Element ePriority = doc.createElement("Priority");
		ePriority.appendChild(doc.createTextNode(priority.toString()));

		eRoot.appendChild(ePriority);
		return eRoot;
	}

	/**
	 * Generates the DiscontinueRouteEvent from the XML event
	 * @param eRoot  The XML event for the event
	 * @return The DiscontinueRouteEvent
	 * @throws DOMException
	 * @throws ParseException
	 */
	public static DiscontinueCustomerPriceEvent generateFromXML(Element eRoot)
			throws DOMException, ParseException {
		Builder builder = new Builder();

		Element eProcessDate = (Element) eRoot.getElementsByTagName(
				"ProcessDate").item(0);
		builder.setProcessDate(DateUtility.parseInputdate(eProcessDate
				.getTextContent()));

		Element eOrigin = (Element) eRoot.getElementsByTagName("Origin")
				.item(0);
		builder.setOrigin(Location.getLocation(eOrigin.getTextContent()));

		Element eDestination = (Element) eRoot.getElementsByTagName(
				"Destination").item(0);
		builder.setDestination(Location.getLocation(eDestination.getTextContent()));

		Element ePriority = (Element) eRoot.getElementsByTagName("Priority")
				.item(0);
		builder.setPriority(Priority.getPriorityFromString(ePriority
				.getTextContent()));

		return builder.build();
	}


	public String toString() {
		String str = DateUtility.formatDisplayDate(this.processDate) + " - "
				+ this.getClass().getSimpleName();
		return str;
	}


	@Override
	public String toBusinessLogString() {
		String str = "Customer Price Update Event (";
		str += "origin:" + origin.toString() + ", destination:"
				+ destination.toString() + ", priority:" + priority.toString()
				+ ", process date:"
				+ DateUtility.formatDisplayDate(processDate) + ")\n";
		return str;
	}

	@Override
	public void process() {
		// TODO Auto-generated method stub
		CustomerDeal.removeDeal(this.origin,this.destination,this.priority);
		EventHistory.getEventHistory().logEvent(this);
	}

	@Override
	public Date getProcessDate() {
		return this.processDate;
	}


	/**
	 * Builder class used to construct CustomerPriceUpdateEvent piece wise
	 *
	 * @author Chris
	 *
	 */
	public static class Builder {
		private Location origin;
		private Location destination;
		private Priority priority;
		private Date processDate;

		/**
		 * Create a new builder
		 */
		public Builder() {

		}

		/**
		 * Create a new Builder from an existing CustomerPriceUpdateEvent
		 *
		 * @param cpue
		 */
		public Builder(CustomerPriceUpdateEvent cpue) {
			this.origin = cpue.origin;
			this.destination = cpue.destination;
			this.priority = cpue.priority;
		}

		public DiscontinueCustomerPriceEvent build() {
			// TODO check properties;
			if (processDate == null) {
				processDate = Calendar.getInstance().getTime();
			}
			return new DiscontinueCustomerPriceEvent(origin, destination, priority,
					 processDate);
		}

		public Location getOrigin() {
			return origin;
		}

		public void setOrigin(Location origin) {
			this.origin = origin;
		}

		public Location getDestination() {
			return destination;
		}

		public void setDestination(Location destination) {
			this.destination = destination;
		}

		public Priority getPriority() {
			return priority;
		}

		public void setPriority(Priority priority) {
			this.priority = priority;
		}

		public Date getProcessDate() {
			return processDate;
		}

		public void setProcessDate(Date processDate) {
			this.processDate = processDate;
		}

	}


}
