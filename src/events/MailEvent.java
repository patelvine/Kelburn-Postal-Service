package events;

import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MailEvent implements BusinessEvent {
	public final Date processDate;
	public final double grams;
	public final double cubicCentimetres;
	public final Priority priority;
	public final Location origin;
	public final Location destination;

	@Override
	public Date getProcessDate() {
		return this.processDate;
	}

	/**
	 * Private constructor. Use the Builder class to instantiate.
	 */
	private MailEvent(Date processDate, double grams, double cubicCentimetres,
			Priority priority, Location origin, Location destination) {
		this.processDate = processDate;
		this.grams = grams;
		this.cubicCentimetres = cubicCentimetres;
		this.priority = priority;
		this.origin = origin;
		this.destination = destination;
	}

	@Override
	public Element saveToXML(Document doc) {
		Element eRoot = doc.createElement(this.getClass().getSimpleName());

		Element eProcessDate = doc.createElement("ProcessDate");
		eProcessDate.appendChild(doc.createTextNode(DateUtility.formatOutputDate(processDate)));
		eRoot.appendChild(eProcessDate);

		Element eGrams = doc.createElement("Grams");
		eGrams.appendChild(doc.createTextNode(Double.toString(grams)));
		eRoot.appendChild(eGrams);

		Element eCubicCentimetres = doc.createElement("CubicCentimetres");
		eCubicCentimetres.appendChild(doc.createTextNode(Double
				.toString(cubicCentimetres)));
		eRoot.appendChild(eCubicCentimetres);

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

		return eRoot;
	}
	/**
	 * Generate a MailEvent from an XML Event
	 * @param eRoot the XML element
	 * @return The Mail Event
	 * @throws ParseException
	 */
	public static MailEvent generateFromXML(Element eRoot) throws ParseException{
		Builder builder = new Builder();

		Element eProcessDate = (Element) eRoot.getElementsByTagName("ProcessDate").item(0);
		builder.setProcessDate(DateUtility.parseInputdate(eProcessDate.getTextContent()));

		Element eGrams = (Element) eRoot.getElementsByTagName("Grams").item(0);
		builder.setGrams(Double.parseDouble(eGrams.getTextContent()));

		Element eCubicCentimetres = (Element) eRoot.getElementsByTagName("CubicCentimetres").item(0);
		builder.setCubicCentimetres(Double.parseDouble(eCubicCentimetres.getTextContent()));

		Element ePriority = (Element) eRoot.getElementsByTagName("Priority").item(0);
		builder.setPriortiy(Priority.getPriorityFromString(ePriority.getTextContent()));

		Element eOrigin = (Element) eRoot.getElementsByTagName("Origin").item(0);
		builder.setOrigin(Location.getLocation(eOrigin.getTextContent()));

		Element eDestination = (Element) eRoot.getElementsByTagName("Destination").item(0);
		builder.setDestination(Location.getLocation(eDestination.getTextContent()));

		return builder.build();
	}

	@Override
	public String toBusinessLogString() {

		String str = "MailEvent (from:" + origin.toString() + ", to:"
				+ destination.toString() + ", weight:" + this.grams + ", size:"
				+ this.cubicCentimetres + ", priority:"
				+ this.priority.toString() + ", Process Date:"
				+ DateUtility.formatDisplayDate(processDate) + ")\n";
		return str;
	}

	@Override
	public void process() {
		// calculate the money spent by summing the cost of all routes on path
		PathFindNode pfn = findPathNode();
		CustomerDeal.Query q = new CustomerDeal.Query();
		q.origin = this.origin;
		q.destination = this.destination;
		q.priority = this.priority;
		CustomerDeal cd = q.getQualifyingDeal();
		double moneyIn = cd.getCost(this.cubicCentimetres, this.grams);

		// update business state with it.
		BusinessState.currentState.updateWithMail(moneyIn,
				pfn.currentCostDollars, pfn.currentCostTime,this.processDate);
		EventHistory.getEventHistory().logEvent(this);
	}



	/**
	 * Using dijkstras algorithm search through all the routes to try and find a
	 * path between origin and destination. IF the priority is air priority it
	 * will only search for air routes. If the priority is not air priority it
	 * will search for the cheaperst route (which will usually be ground routes)
	 *
	 * @return
	 */
	public PathFindNode findPathNode() {
		return Route.findPath(origin, destination, priority, grams, cubicCentimetres, processDate);

	}

	public static class Builder {
		private Date processDate;
		private double grams;
		private double cubicCentimetres;
		private Priority priortiy;
		private Location origin;
		private Location destination;

		/**
		 * Creates a new Builder
		 */
		public Builder() {

		}

		/**
		 * Creates a new builder initialised with the data from an existing Mail
		 * Event
		 *
		 * @param me
		 *            The existing MailEvent
		 */
		public Builder(MailEvent me) {
			this.processDate = me.processDate;
			this.grams = me.grams;
			this.cubicCentimetres = me.cubicCentimetres;
			this.priortiy = me.priority;
			this.origin = me.origin;
			this.destination = me.destination;
		}

		/**
		 * Checks the validity of the entered data then builds an instance of
		 * the MailEvent
		 *
		 * @return The Built MailEvent
		 */
		public MailEvent build() {
			// TODO check all entered data

			//if time not entered use current time
			if(this.processDate==null)
				this.processDate=GregorianCalendar.getInstance().getTime();

			return new MailEvent(processDate, grams, cubicCentimetres,
					priortiy, origin, destination);
		}

		public Date getProcessDate() {
			return processDate;
		}

		public void setProcessDate(Date processDate) {
			this.processDate = processDate;
		}

		public double getGrams() {
			return grams;
		}

		public void setGrams(double grams) {
			this.grams = grams;
		}

		public double getCubicCentimetres() {
			return cubicCentimetres;
		}

		public void setCubicCentimetres(double cubicCentimetres) {
			this.cubicCentimetres = cubicCentimetres;
		}

		public Priority getPriortiy() {
			return priortiy;
		}

		public void setPriortiy(Priority priortiy) {
			this.priortiy = priortiy;
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

		// getters and setters for all fields

	}

	public String toString() {
		String str = DateUtility.formatDisplayDate(this.processDate) + " - "
				+ this.getClass().getSimpleName();
		return str;

	}

}
