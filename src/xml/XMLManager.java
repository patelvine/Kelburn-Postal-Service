package xml;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

//XML help from http://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import events.BusinessEvent;
import events.Company;
import events.CustomerPriceUpdateEvent;
import events.DiscontinueRouteEvent;
import events.Location;
import events.MailEvent;
import events.TransportCostUpdateEvent;

/**
 * Class to manage the reading and writing of all XML documents for this
 * program. Documents are the log file for events, the companies list and the
 * locations list.
 * 
 * @author Reece Patterson
 * 
 */
public class XMLManager {

	private static boolean isTest = false;
	private static Document eventDoc;
	public static final String testFolder = "test";
	public static final String xmlFolder = "xml";
	private static final String logFile = "log.xml";
	private static final String locationsFile = "loc.xml";
	private static final String companiesFile = "com.xml";

	/**
	 * Private constructor as all methods are static
	 */
	private XMLManager() {
	}

	/**
	 * Method to return the list of BusinessEvents from the log file located at
	 * log/log.xml Returns an empty stack if no file exists and starts a new XML
	 * log file.
	 * 
	 * @return List of BusinessEvents from log file
	 * @throws XMLException
	 *             Thrown if any error occurs in parsing the file. Contains
	 *             string message of error and original source exception
	 */
	public static List<BusinessEvent> loadLogToList(boolean test)
			throws XMLException {
		try {
			DocumentBuilderFactory docFac = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFac.newDocumentBuilder();

			File fle = null;
			if (!test) {
				fle = new File(xmlFolder + "/" + logFile);
			} else {
				fle = new File(testFolder + "/" + logFile);
			}

			if (fle.exists()) {
				eventDoc = docBuilder.parse(fle);
				eventDoc.normalizeDocument();
				List<BusinessEvent> eventList = new ArrayList<BusinessEvent>();
				NodeList nodes = eventDoc.getDocumentElement()
						.getElementsByTagName("*"); // Get all elements, we want
													// to proccess them all in
													// order, not by grouping
				for (int i = 0; i < nodes.getLength(); i++) {
					Element currElem = (Element) nodes.item(i);
					String currElemName = currElem.getNodeName();
					if (currElemName == "CustomerPriceUpdateEvent") {
						eventList.add(CustomerPriceUpdateEvent
								.generateFromXML(currElem));
					} else if (currElemName == "DiscontinueRouteEvent") {
						eventList.add(DiscontinueRouteEvent
								.generateFromXML(currElem));
					} else if (currElemName == "MailEvent") {
						eventList.add(MailEvent.generateFromXML(currElem));
					} else if (currElemName == "TransportCostUpdateEvent") {
						eventList.add(TransportCostUpdateEvent
								.generateFromXML(currElem));
					}
				}
				return eventList;
			} else {
				eventDoc = docBuilder.newDocument(); // create a new log file
				eventDoc.appendChild(eventDoc.createElement("log"));
				return new Stack<BusinessEvent>(); // return empty stack
			}
		} catch (ParserConfigurationException e) {// unable to create the
													// documentBuilder with
													// configuration, should not
													// happen as all is default
			throw new XMLException(
					"Unable to build XML Parser, please try again. If problem persists please contact your system administrator",
					e);
		} catch (SAXException e) { // XML file is in the incorrect format
			throw new XMLException(
					"XML file is incorrectly formatted, please check or remove the log file",
					e);
		} catch (IOException e) { // This should never happen as it is checked
									// the line before!!!
			throw new XMLException(
					"XML log file could not be found, please try again to generate file.",
					e);
		} catch (ParseException e) {
			throw new XMLException(
					"XML log file contains a date incorrectly formatted, please check or remove the log file.",
					e);
		}
	}

	/**
	 * Call when a new event is created. Calls the saveToXML method on the event
	 * and adds it to the log document then generates a new log file
	 * 
	 * @param event
	 *            Event that has needs to be added to the log.
	 * @throws XMLException
	 */
	public static void addEventToLog(BusinessEvent event) throws XMLException {
		eventDoc.getDocumentElement().appendChild(event.saveToXML(eventDoc));
		generateLogFile();
	}

	/**
	 * Generates a new log file according to the stored XML data
	 * 
	 * @throws XMLException
	 *             When an error occurs when attempting to generate the file.
	 *             Contains message about error and source exception
	 */
	public static void generateLogFile() throws XMLException {
		try {
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(eventDoc);
			if (isTest) {
				File output = new File(testFolder + "/" + logFile);
				if (!output.exists()) {
					File outputFolder = new File(testFolder);
					outputFolder.mkdirs();
					output.createNewFile();
				}
				StreamResult result = new StreamResult(output);
				transformer.transform(source, result);
			} 
			else {
				File output = new File(xmlFolder + "/" + logFile);
				if (!output.exists()) {
					File outputFolder = new File(xmlFolder);
					outputFolder.mkdirs();
					output.createNewFile();
				}
				StreamResult result = new StreamResult(output);
				transformer.transform(source, result);
			}

		} catch (IOException e) {
			throw new XMLException(
					"I/O error when attempting to create latest log file, please try a manual save.",
					e);
		} catch (TransformerConfigurationException e) {
			throw new XMLException(
					"Unable to build XML Transformer, please try again. If problem persists please contact your system administrator",
					e);
		} catch (TransformerException e) {
			throw new XMLException(
					"Error when attempting to create latest log file, please try a manual save.",
					e);
		}
	}

	/**
	 * Loads the locations stored in the xml file to the static list of
	 * locations
	 * 
	 * @throws XMLException
	 *             When unable to load from the XML file. Contains message about
	 *             exception and original source of the exception.
	 */
	public static void loadLocations(boolean test, boolean test2)
			throws XMLException {
		try {
			DocumentBuilderFactory docFac = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFac.newDocumentBuilder();

			File fle = null;
			if (!test) {
				fle = new File(xmlFolder + "/" + locationsFile);
			} else {
				isTest = true;
				fle = new File(testFolder + "/" + locationsFile);
			}

			if (fle.exists() && !test || fle.exists() && test2) {
				Document locDoc = docBuilder.parse(fle);
				locDoc.normalizeDocument();
				NodeList nodes = locDoc.getDocumentElement()
						.getElementsByTagName("Location"); // Get all elements,
															// we want to
															// proccess them all
															// in order, not by
															// grouping
				for (int i = 0; i < nodes.getLength(); i++) { // First event in
																// XML = first
																// event =
																// bottom of
																// stack
					Element currElem = (Element) nodes.item(i);
					Location.generateFromXML(currElem);
				}
				return;
			} else {
				// secretly make dummy values
				System.out.println("generating dummy values");

				generateLocationsAndComapnies(test, test2);
				loadLocations(test, true);
			}
		} catch (ParserConfigurationException e) {// unable to create the
													// documentBuilder with
													// configuration, should not
													// happen as all is default
			throw new XMLException(
					"Unable to build XML Parser, please try again. If problem persists please contact your system administrator",
					e);
		} catch (SAXException e) { // XML file is in the incorrect format
			throw new XMLException(
					"XML file is incorrectly formatted, please check or remove the log file",
					e);
		} catch (IOException e) { // This should never happen as it is checked
									// the line before!!!
			throw new XMLException(
					"XML log file could not be found, please try again to generate file.",
					e);
		}
	}

	/**
	 * Loads the companies stored in the xml file to the static list of
	 * companies
	 * 
	 * @throws XMLException
	 *             When unable to load from the XML file. Contains message about
	 *             exception and original source of the exception.
	 */
	public static void loadCompanies(boolean test, boolean test2)
			throws XMLException {
		try {
			DocumentBuilderFactory docFac = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFac.newDocumentBuilder();
			File fle = null;
			if (!test) {
				fle = new File(xmlFolder + "/" + companiesFile);
			} else {
				fle = new File(testFolder + "/" + companiesFile);
			}

			if (fle.exists() && !test || fle.exists() && test2) {
				Document comDoc = docBuilder.parse(fle);
				comDoc.normalizeDocument();
				NodeList nodes = comDoc.getDocumentElement()
						.getElementsByTagName("Company"); // Get all elements,
															// we want to
															// proccess them all
															// in order, not by
															// grouping
				for (int i = 0; i < nodes.getLength(); i++) { // First event in
																// XML = first
																// event =
																// bottom of
																// stack
					Element currElem = (Element) nodes.item(i);
					Company.generateFromXML(currElem);
				}
				return;
			} else {
				// secretly make dummy values
				System.out.println("generating dummy values");

				generateLocationsAndComapnies(test, test2);
				loadCompanies(test, true);
			}
		} catch (ParserConfigurationException e) {// unable to create the
													// documentBuilder with
													// configuration, should not
													// happen as all is default
			throw new XMLException(
					"Unable to build XML Parser, please try again. If problem persists please contact your system administrator",
					e);
		} catch (SAXException e) { // XML file is in the incorrect format
			throw new XMLException(
					"XML file is incorrectly formatted, please check or remove the log file",
					e);
		} catch (IOException e) { // This should never happen as it is checked
									// the line before!!!
			throw new XMLException(
					"XML log file could not be found, please try again to generate file.",
					e);
		}
	}

	/**
	 * Generate locations and companies from hardcoded lists
	 * 
	 * @throws XMLException
	 */
	private static void generateLocationsAndComapnies(boolean test,
			boolean test2) throws XMLException {
		try {
			DocumentBuilderFactory docFac = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFac.newDocumentBuilder();
			Document locDoc = docBuilder.newDocument();
			Element rootElement = locDoc.createElement("Locations");
			// dummy values
			rootElement.appendChild(genLocElem("Bermuda", true, locDoc));
			rootElement.appendChild(genLocElem("Bahama", true, locDoc));
			rootElement.appendChild(genLocElem("Jamaica", true, locDoc));
			rootElement.appendChild(genLocElem("Aruba", true, locDoc));
			rootElement.appendChild(genLocElem("Key Largo", true, locDoc));
			rootElement.appendChild(genLocElem("Montego", true, locDoc));

			locDoc.appendChild(rootElement);
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(locDoc);

			if (!test) {
				File output = new File(xmlFolder + "/" + locationsFile);
				if (!output.exists()) {
					File outputFolder = new File(xmlFolder);
					outputFolder.mkdirs();
					output.createNewFile();
				}
				StreamResult result = new StreamResult(output);
				transformer.transform(source, result);
			} else {
				File output = new File(testFolder + "/" + locationsFile);
				if (!output.exists()) {
					File outputFolder = new File(testFolder);
					outputFolder.mkdirs();
					output.createNewFile();
				}
				StreamResult result = new StreamResult(output);
				transformer.transform(source, result);
			}
		} catch (IOException e) {
			throw new XMLException(
					"I/O error when attempting to create latest log file, please try a manual save.",
					e);
		} catch (TransformerConfigurationException e) {
			throw new XMLException(
					"Unable to build XML Transformer, please try again. If problem persists please contact your system administrator",
					e);
		} catch (TransformerException e) {
			throw new XMLException(
					"Error when attempting to create latest log file, please try a manual save.",
					e);
		} catch (ParserConfigurationException e) {// unable to create the
													// documentBuilder with
													// configuration, should not
													// happen as all is default
			throw new XMLException(
					"Unable to build XML Parser, please try again. If problem persists please contact your system administrator",
					e);
		}

		try {
			DocumentBuilderFactory docFac = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFac.newDocumentBuilder();
			Document comDoc = docBuilder.newDocument();
			Element rootElement = comDoc.createElement("Companies");
			// dummy values
			rootElement.appendChild(genComElem("FedEx", comDoc));
			rootElement.appendChild(genComElem("UPS", comDoc));
			rootElement.appendChild(genComElem("NZ Post", comDoc));
			comDoc.appendChild(rootElement);

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(comDoc);
			if (!test) {
				File output = new File(xmlFolder + "/" + companiesFile);
				if (!output.exists()) {
					File outputFolder = new File(xmlFolder);
					outputFolder.mkdirs();
					output.createNewFile();
				}
				StreamResult result = new StreamResult(output);
				transformer.transform(source, result);
			} else {
				File output = new File(testFolder + "/" + companiesFile);
				if (!output.exists()) {
					File outputFolder = new File(testFolder);
					outputFolder.mkdirs();
					output.createNewFile();
				}
				StreamResult result = new StreamResult(output);
				transformer.transform(source, result);
			}
		} catch (IOException e) {
			throw new XMLException(
					"I/O error when attempting to create latest log file, please try a manual save.",
					e);
		} catch (TransformerConfigurationException e) {
			throw new XMLException(
					"Unable to build XML Transformer, please try again. If problem persists please contact your system administrator",
					e);
		} catch (TransformerException e) {
			throw new XMLException(
					"Error when attempting to create latest log file, please try a manual save.",
					e);
		} catch (ParserConfigurationException e) {// unable to create the
													// documentBuilder with
													// configuration, should not
													// happen as all is default
			throw new XMLException(
					"Unable to build XML Parser, please try again. If problem persists please contact your system administrator",
					e);
		}
	}

	/**
	 * Create a Location element for use in the locations XML file
	 * 
	 * @param name
	 *            Name of the location
	 * @param doc
	 *            Document builder to create element with
	 * @return Location element for use in locations xml
	 */
	private static Element genLocElem(String name, boolean isInternational,
			Document doc) {
		Element eRoot = doc.createElement("Location");
		Element eName = doc.createElement("Name");
		Element eIsInternational = doc.createElement("isInternational");
		eName.appendChild(doc.createTextNode(name));
		eIsInternational.appendChild(doc.createTextNode(Boolean
				.toString(isInternational)));
		eRoot.appendChild(eName);
		eRoot.appendChild(eIsInternational);
		return eRoot;
	}

	/**
	 * Create a Company element for use in the companies XML file
	 * 
	 * @param name
	 *            Name of the company
	 * @param doc
	 *            Document builder to create element with
	 * @return Company element for use in companies xml
	 */
	private static Element genComElem(String name, Document doc) {
		Element eRoot = doc.createElement("Company");
		Element eName = doc.createElement("Name");
		eName.appendChild(doc.createTextNode(name));
		eRoot.appendChild(eName);
		return eRoot;
	}
}
