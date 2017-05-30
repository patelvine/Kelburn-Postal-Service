package events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

public class Location {
	/**
	 * The name of the location
	 */
	public final String name;
	/**
	 * Whether the location is internation (eg. not domestic)
	 */
	public final boolean international;

	/**
	 * A map of all location names to their location objects
	 */
	private static Map<String, Location> allLocations = new HashMap<String, Location>();

	public Location(String name, boolean isInternational) {
		this.name = name;
		this.international = isInternational;
		if (!allLocations.containsKey(name)) {
			allLocations.put(name, this);
		}
	}

	/**
	 * Get the location with the given name
	 *
	 * @param name
	 *            The name of the location
	 * @return The locaiton
	 */
	public static Location getLocation(String name) {
		return allLocations.get(name);
	}

	/**
	 * Get the list of all locations in the system
	 * @return  The list of all locations in the system.
	 */
	public static List<Location> allLocations() {
		return new ArrayList<Location>(allLocations.values());
	}

	/**
	 * Gets the array of all locations in the system
	 * @return  The array of all locaitons in the system.
	 */
	public static Location[] allLocationsArray() {
		Location[] arr = new Location[allLocations.values().size()];
		int idx = 0;
		for (Location loc : allLocations.values())
			arr[idx++] = loc;
		return arr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.name;

	}
	/**
	 * Generate a location from an XML element and put it in the list of locaitons
	 * @param eRoot  The Location element
	 */
	public static void generateFromXML(Element eRoot) {
		Element eName = (Element) eRoot.getElementsByTagName("Name").item(0);
		Element eIsInternational = (Element) eRoot.getElementsByTagName(
				"isInternational").item(0);
		if (eIsInternational == null) {
			//defaulty create a domestic one
			new Location(eName.getTextContent(),false);
		} else {
			new Location(eName.getTextContent(),
					Boolean.parseBoolean(eIsInternational.getTextContent()));
		}
	}
}