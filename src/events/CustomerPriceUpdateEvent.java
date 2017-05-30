package events;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This represents a business event that changes a customer price.
 *
 * @author Chris
 *
 */
public class CustomerPriceUpdateEvent implements BusinessEvent {

	public final Location origin;
	public final Location destination;
	public final Priority priority;
	public final double dollarsPerGram;
	public final double dollarsPerCubicCentimetre;
	public final Date processDate;

	private CustomerPriceUpdateEvent(Location origin, Location destination,
			Priority priority, double dollarsPerGram,
			double dollarsPerCubicCentimetre, Date processDate) {
		this.origin = origin;
		this.destination = destination;
		this.priority = priority;
		this.dollarsPerGram = dollarsPerGram;
		this.dollarsPerCubicCentimetre = dollarsPerCubicCentimetre;
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
		eOrigin.appendChild(doc.createTextNode(origin.toString())); // TODO
																	// confirm
																	// this is
																	// how we
																	// want to
																	// save
		eRoot.appendChild(eOrigin);

		Element eDestination = doc.createElement("Destination");
		eDestination.appendChild(doc.createTextNode(destination.toString())); // TODO
																				// confirm
																				// this
																				// is
																				// how
																				// we
																				// want
																				// to
																				// save
																				// this
		eRoot.appendChild(eDestination);

		Element ePriority = doc.createElement("Priority");
		ePriority.appendChild(doc.createTextNode(priority.toString())); // TODO
																		// confirm
																		// this
																		// is
																		// how
																		// we
																		// want
																		// to
																		// save
																		// this
		eRoot.appendChild(ePriority);

		Element eDollarsPerGram = doc.createElement("DollarsPerGram");
		eDollarsPerGram.appendChild(doc.createTextNode(Double
				.toString(dollarsPerGram)));
		eRoot.appendChild(eDollarsPerGram);

		Element eDollarsPerCubicCentimetre = doc
				.createElement("DollarsPerCubicCentimetre");
		eDollarsPerCubicCentimetre.appendChild(doc.createTextNode(Double
				.toString(dollarsPerCubicCentimetre)));
		eRoot.appendChild(eDollarsPerCubicCentimetre);

		return eRoot;
	}

	public static CustomerPriceUpdateEvent generateFromXML(Element eRoot) throws ParseException {
		Builder builder = new Builder();
		
		Element eProcessDate = (Element) eRoot.getElementsByTagName("ProcessDate").item(0);
		builder.setProcessDate(DateUtility.parseInputdate(eProcessDate.getTextContent()));
		
		Element eOrigin = (Element) eRoot.getElementsByTagName("Origin").item(0);
		builder.setOrigin(Location.getLocation(eOrigin.getTextContent()));

		Element eDestination = (Element) eRoot.getElementsByTagName("Destination").item(0);
		builder.setDestination(Location.getLocation(eDestination.getTextContent()));

		Element eDollarsPerGrams = (Element) eRoot.getElementsByTagName("DollarsPerGram").item(0);
		builder.setDollarsPerGram(Double.parseDouble(eDollarsPerGrams.getTextContent()));

		Element eDollarsPerCubicCentimetre = (Element) eRoot.getElementsByTagName("DollarsPerCubicCentimetre").item(0);
		builder.setDollarsPerCubicCentimetre(Double.parseDouble(eDollarsPerCubicCentimetre.getTextContent()));

		Element ePriority = (Element) eRoot.getElementsByTagName("Priority").item(0);
		builder.setPriority(Priority.getPriorityFromString(ePriority.getTextContent()));

		return builder.build();
	}

	@Override
	public String toBusinessLogString() {
		String str = "Customer Price Update Event (";
		str += "origin:" + origin.toString() + ", destination:"
				+ destination.toString() + ", priority:" + priority.toString()
				+ ", dollars per gram:" + dollarsPerGram
				+ ", dollars per cubic centimetre:" + dollarsPerCubicCentimetre
				+ ", process date:"
				+ DateUtility.formatDisplayDate(processDate) + ")\n";
		return str;
	}

	public String toString() {
		String str = DateUtility.formatDisplayDate(this.processDate) + " - "
				+ this.getClass().getSimpleName();
		return str;
	}

	@Override
	public void process() {
		// TODO Auto-generated method stub
		CustomerDeal.updateCost(this);
		EventHistory.getEventHistory().logEvent(this);
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
		private double dollarsPerGram;
		private double dollarsPerCubicCentimetre;
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
			this.dollarsPerGram = cpue.dollarsPerGram;
			this.dollarsPerCubicCentimetre = cpue.dollarsPerCubicCentimetre;
		}

		public CustomerPriceUpdateEvent build() {
			// TODO check properties;
			if (processDate == null) {
				processDate = Calendar.getInstance().getTime();
			}
			return new CustomerPriceUpdateEvent(origin, destination, priority,
					dollarsPerGram, dollarsPerCubicCentimetre, processDate);
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

		public double getDollarsPerKilogram() {
			return dollarsPerGram;
		}

		public void setDollarsPerGram(double dollarsPerGram) {
			this.dollarsPerGram = dollarsPerGram;
		}

		public double getDollarsPerCubicMetre() {
			return dollarsPerCubicCentimetre;
		}

		public void setDollarsPerCubicCentimetre(double dollarsPerCubicCentimetre) {
			this.dollarsPerCubicCentimetre = dollarsPerCubicCentimetre;
		}

		public Date getProcessDate() {
			return processDate;
		}

		public void setProcessDate(Date processDate) {
			this.processDate = processDate;
		}

	}

	@Override
	public Date getProcessDate() {
		return this.processDate;
	}

}
