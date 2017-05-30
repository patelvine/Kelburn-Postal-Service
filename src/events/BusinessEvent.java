package events;

import java.util.Date;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class represents a business event. Business events are all events that
 * the sytem users create to change the system in some way.
 * 
 * @author Chris
 * 
 */
public interface BusinessEvent {
	/**
	 * Processes the event. Applying the effects to the system.
	 */
	public void process();

	/**
	 * Gets a string that can be outputted in the event log 
	 * @return The event log string for this Business Event
	 */
	public String toBusinessLogString();

	/**
	 * Gets an XML Element which can represent this class in XML
	 * @return The XML Element for this business event
	 */
	public Element saveToXML(Document doc);
	
	/**
	 * Gets the date the event was processed on
	 */
	public Date getProcessDate();
		
	
}
