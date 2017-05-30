package events;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class represents an event which will alter the cost ro other properties
 * of a certain transport route.
 *
 * @author Chris
 *
 */
public class TransportCostUpdateEvent implements BusinessEvent {
	public final Date processDate;
	public final Company company;
	public final Location origin;
	public final Location destination;
	public final TransportType transportType;
	public final double dollarsPerGram;
	public final double dollarsPerCubicCentimetre;
	public final double maxGrams;
	public final double maxCubicCentimetres;
	public final double durationHours;
	public final double departureIntervalHours;


	@Override
	public Date getProcessDate() {
		return this.processDate;
	}

	@Override
	public Element saveToXML(Document doc) {
		Element eRoot = doc.createElement(this.getClass().getSimpleName());

		Element eProcessDate = doc.createElement("ProcessDate");
		eProcessDate.appendChild(doc.createTextNode(DateUtility.formatOutputDate(processDate)));
		eRoot.appendChild(eProcessDate);

		Element eCompany = doc.createElement("Company");
		eCompany.appendChild(doc.createTextNode(company.toString())); //TODO confirm this is how we want to save this
		eRoot.appendChild(eCompany);

		Element eOrigin = doc.createElement("Origin");
		eOrigin.appendChild(doc.createTextNode(origin.toString())); //TODO confirm this is how we want to save
		eRoot.appendChild(eOrigin);

		Element eDestination = doc.createElement("Destination");
		eDestination.appendChild(doc.createTextNode(destination.toString())); //TODO confirm this is how we want to save this
		eRoot.appendChild(eDestination);

		Element eTransportType = doc.createElement("TransportType");
		eTransportType.appendChild(doc.createTextNode(transportType.toString()));
		eRoot.appendChild(eTransportType);

		Element eDollarsPerGram = doc.createElement("DollarsPerGram");
		eDollarsPerGram.appendChild(doc.createTextNode(Double.toString(dollarsPerGram)));
		eRoot.appendChild(eDollarsPerGram);

		Element eDollarsPerCubicCentimetre= doc.createElement("DollarsPerCubicCentimetre");
		eDollarsPerCubicCentimetre.appendChild(doc.createTextNode(Double.toString(dollarsPerCubicCentimetre)));
		eRoot.appendChild(eDollarsPerCubicCentimetre);

		Element eMaxGrams= doc.createElement("MaxGrams");
		eMaxGrams.appendChild(doc.createTextNode(Double.toString(maxGrams)));
		eRoot.appendChild(eMaxGrams);

		Element eMaxCubicCentimetres= doc.createElement("MaxCubicCentimetres");
		eMaxCubicCentimetres.appendChild(doc.createTextNode(Double.toString(maxCubicCentimetres)));
		eRoot.appendChild(eMaxCubicCentimetres);

		Element eDurationHours= doc.createElement("DurationHours");
		eDurationHours.appendChild(doc.createTextNode(Double.toString(durationHours)));
		eRoot.appendChild(eDurationHours);

		Element eDepartureIntervalHours= doc.createElement("DepartureIntervalHours");
		eDepartureIntervalHours.appendChild(doc.createTextNode(Double.toString(departureIntervalHours)));
		eRoot.appendChild(eDepartureIntervalHours);

		return eRoot;
	}

	/**
	 * Genertates a transport Cost update event from an XML element
	 * @param eRoot  The XML Element
	 * @return  The Transport Cost Update Event
	 * @throws ParseException
	 */
	public static TransportCostUpdateEvent generateFromXML(Element eRoot) throws ParseException{
		Builder builder = new Builder();

		Element eProcessDate = (Element) eRoot.getElementsByTagName("ProcessDate").item(0);
		builder.setProcessDate(DateUtility.parseInputdate(eProcessDate.getTextContent()));

		Element eCompany = (Element) eRoot.getElementsByTagName("Company").item(0);
		builder.setCompany(Company.getCompany(eCompany.getTextContent()));

		Element eOrigin = (Element) eRoot.getElementsByTagName("Origin").item(0);
		builder.setOrigin(Location.getLocation(eOrigin.getTextContent()));

		Element eDestination = (Element) eRoot.getElementsByTagName("Destination").item(0);
		builder.setDestination(Location.getLocation(eDestination.getTextContent()));

		Element eTransportType = (Element) eRoot.getElementsByTagName("TransportType").item(0);
		builder.setTransportType(TransportType.valueOf(eTransportType.getTextContent()));

		Element eDollarsPerGrams = (Element) eRoot.getElementsByTagName("DollarsPerGram").item(0);
		builder.setDollarsPerGram(Double.parseDouble(eDollarsPerGrams.getTextContent()));

		Element eDollarsPerCubicCentimetre = (Element) eRoot.getElementsByTagName("DollarsPerCubicCentimetre").item(0);
		builder.setDollarsPerCubicCentimetre(Double.parseDouble(eDollarsPerCubicCentimetre.getTextContent()));

		Element eMaxGrams= (Element) eRoot.getElementsByTagName("MaxGrams").item(0);
		builder.setMaxGrams(Double.parseDouble(eMaxGrams.getTextContent()));

		Element eMaxCubicCentimetres= (Element) eRoot.getElementsByTagName("MaxCubicCentimetres").item(0);
		builder.setMaxCubicCentimetres(Double.parseDouble(eMaxCubicCentimetres.getTextContent()));

		Element eDurationHours= (Element) eRoot.getElementsByTagName("DurationHours").item(0);
		builder.setDurationHours(Double.parseDouble(eDurationHours.getTextContent()));

		Element eDepartureIntervalHours= (Element) eRoot.getElementsByTagName("DepartureIntervalHours").item(0);
		builder.setDepartureIntervalHours(Double.parseDouble(eDepartureIntervalHours.getTextContent()));

		return builder.build();
	}

	@Override
	public String toBusinessLogString() {
		String str = "Transport Cost Update Event (Origin:" + origin.toString()
				+ ", Destination:" + destination.toString() + ",Process Date:"
				+ DateUtility.formatOutputDate(processDate) + ", Company:" + company.toString()
				+ ", Transport Type:" + transportType.toString()
				+ ", Dollars Per Gram:" + dollarsPerGram
				+ ", Dollars Per Cubic Centimetre:"
				+ this.dollarsPerCubicCentimetre + ", Max Grams:" + maxGrams
				+ ", Max Cubic Centimetres:" + this.maxCubicCentimetres
				+ ", Duration Hours:"+durationHours+", Departure Interval Hours:"+durationHours+")\n";
		return str;
	}

	public String toString() {
		String str = DateUtility.formatDisplayDate(this.processDate) + " - "
				+ this.getClass().getSimpleName();
		return str;

	}

	@Override
	public void process() {
		Route.updateCost(this);
		EventHistory.getEventHistory().logEvent(this);
	}

	/**
	 * Private constructor. Use Builder to instantiate.
	 */
	private TransportCostUpdateEvent(Date processDate, Company company, Location origin,
			Location destination, TransportType transportType,
			double dollarsPerGram, double dollarsPerCubicCentimetre,
			double maxGrams, double maxCubicCentimetres, double durationHours,
			double departureIntervalHours) {
		this.processDate = processDate;
		this.company = company;
		this.origin = origin;
		this.destination = destination;
		this.transportType = transportType;
		this.dollarsPerGram = dollarsPerGram;
		this.dollarsPerCubicCentimetre = dollarsPerCubicCentimetre;
		this.maxGrams = maxGrams;
		this.maxCubicCentimetres = maxCubicCentimetres;
		this.durationHours = durationHours;
		this.departureIntervalHours = departureIntervalHours;

	}

	/**
	 * Builder class used to construct TransportCostUpdateEvent piece wise
	 *
	 * @author Chris
	 *
	 */
	public static class Builder {
		private Date processDate;
		private Company company;
		private Location origin;
		private Location destination;
		private TransportType transportType;
		private double dollarsPerGram;
		private double dollarsPerCubicCentimetre;
		private double maxGrams;
		private double maxCubicCentimetres;
		private double durationHours;
		private double departureIntervalHours;

		/**
		 * Creates a new Builder
		 */
		public Builder() {

		}

		/**
		 * Creates a new builder from an existing TransportCostUpdateEvent
		 *
		 * @param tcue
		 *            The existing TransportCostUpdateEvent
		 */
		public Builder(TransportCostUpdateEvent tcue) {
			this.processDate = tcue.processDate;
			this.company = tcue.company;
			this.origin = tcue.origin;
			this.destination = tcue.destination;
			this.transportType = tcue.transportType;
			this.dollarsPerGram = tcue.dollarsPerGram;
			this.dollarsPerCubicCentimetre = tcue.dollarsPerCubicCentimetre;
			this.maxGrams = tcue.maxGrams;
			this.maxCubicCentimetres = tcue.maxCubicCentimetres;
			this.durationHours = tcue.durationHours;
			this.departureIntervalHours = tcue.departureIntervalHours;
		}

		public TransportCostUpdateEvent build() {
			// TODO check validity of all data
			// create TransportCostUpdateEvent from builder data
			if(processDate == null){
				processDate = Calendar.getInstance().getTime();
			}
			return new TransportCostUpdateEvent(processDate, company, origin,
					destination, transportType, dollarsPerGram,
					dollarsPerCubicCentimetre, maxGrams, maxCubicCentimetres,
					durationHours, departureIntervalHours);
		}

		public Date getProcessDate() {
			return processDate;
		}

		public void setProcessDate(Date processDate) {
			this.processDate = processDate;
		}

		public Company getCompany() {
			return company;
		}

		public void setCompany(Company company) {
			this.company = company;
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

		public TransportType getTransportType() {
			return transportType;
		}

		public void setTransportType(TransportType transportType) {
			this.transportType = transportType;
		}

		public double getDollarsPerGram() {
			return dollarsPerGram;
		}

		public void setDollarsPerGram(double dollarsPerGram) {
			this.dollarsPerGram = dollarsPerGram;
		}

		public double getDollarsPerCubicCentimetre() {
			return dollarsPerCubicCentimetre;
		}

		public void setDollarsPerCubicCentimetre(
				double dollarsPerCubicCentimetre) {
			this.dollarsPerCubicCentimetre = dollarsPerCubicCentimetre;
		}

		public double getMaxGrams() {
			return maxGrams;
		}

		public void setMaxGrams(double maxGrams) {
			this.maxGrams = maxGrams;
		}

		public double getMaxCubicCentimetres() {
			return maxCubicCentimetres;
		}

		public void setMaxCubicCentimetres(double maxCubicCentimetres) {
			this.maxCubicCentimetres = maxCubicCentimetres;
		}

		public double getDurationHours() {
			return durationHours;
		}

		public void setDurationHours(double durationHours) {
			this.durationHours = durationHours;
		}

		public double getDepartureIntervalHours() {
			return departureIntervalHours;
		}

		public void setDepartureIntervalHours(double departureIntervalHours) {
			this.departureIntervalHours = departureIntervalHours;
		}

		// Getters amd Setters fpr all properties

	}

}
