package events;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This represents a business event which discontinues a specified route.
 * @author Chris
 *
 */
public class DiscontinueRouteEvent implements BusinessEvent {
	/**
	 * The company whos route will be discontinued
	 */
	public final Company company;

	/**
	 * The origin of the route that willl be discontinued
	 */
	public final Location origin;

	/**
	 * The destination of the route that willl be discontinued
	 */
	public final Location destination;

	/**
	 * The transport type of the route that willl be discontinued
	 */
	public final TransportType transportType;

	/**
	 * The date teh event was processed on
	 */
	public final Date processDate;
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

		return eRoot;
	}

	public static DiscontinueRouteEvent generateFromXML(Element eRoot) throws ParseException{
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

		return builder.build();
	}

	@Override
	public String toBusinessLogString() {
		String str = "Discontinue Route Event (Origin"+origin.toString()+
				", Destination:"+destination.toString()+
				", Company"+company.toString()+
				", Transport Type:"+transportType.toString()+
				", Process Date:"+DateUtility.formatDisplayDate(processDate)+
				")\n";
		return str;
	}

	public String toString(){
		String str = DateUtility.formatDisplayDate(this.processDate) + " - "
				+ this.getClass().getSimpleName();
		return str;
	}

	@Override
	public void process() {
		Route.removeRoute(origin, destination, transportType, company);
		EventHistory.getEventHistory().logEvent(this);;
	}
	/**
	 * Private constructor. Use Builder to instantiate
	 */
	private DiscontinueRouteEvent( Company company, Location origin,
			Location destination, TransportType transportType, Date processDate) {
		this.company = company;
		this.origin = origin;
		this.destination = destination;
		this.transportType = transportType;
		this.processDate = processDate;
	}

	/**
	 * Builder class used to construct DiscontinueRouteEvent piece wise
	 *
	 * @author Chris
	 *
	 */
	public static class Builder {

		private Company company;
		private Location origin;
		private Location destination;
		private TransportType transportType;
		private Date processDate;

		/**
		 * Create a new Builder
		 */
		public Builder() {

		}

		/**
		 * Create a new Builder from an existing DiscontinueRouteEvent
		 * @param dre
		 */
		public Builder(DiscontinueRouteEvent dre) {
			this.company = dre.company;
			this.origin = dre.origin;
			this.destination = dre.destination;
			this.transportType = dre.transportType;
		}

		public DiscontinueRouteEvent build() {
			// TODO check Validity
			if (processDate == null){
				processDate = Calendar.getInstance().getTime();
			}
			return new DiscontinueRouteEvent( company, origin, destination,
					transportType, processDate);
		}

		// Getters and Setters for all values

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

		public Date getProcessDate(){
			return processDate;
		}

		public void setProcessDate(Date processDate){
			this.processDate = processDate;
		}

	}

}
