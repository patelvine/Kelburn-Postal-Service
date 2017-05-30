package events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * This class represents a shipping company that can offer mail transport.
 *
 * @author Chris
 *
 */
public class Company {
	public final String name;
	private static Map<String, Company> allCompanies = new HashMap<String, Company>();

	public Company(String name) {
		this.name = name;
		if (!allCompanies.containsKey(name))
			allCompanies.put(name, this);
	}

	/**
	 * This class represents a shipping company that can offer mail transport.
	 *
	 * @author Chris
	 *
	 */
	public static Company getCompany(String name) {
		return Company.allCompanies.get(name);

	}



	/**
	 * Returns a collection of all Companies in the system
	 * @return The collection of all companies
	 */
	public static Collection<Company> allCompanies() {
		return new ArrayList<Company>(allCompanies.values());

	}

	/**
	 * Returns an array of all companies in the system
	 * @return The array of all companies
	 */
	public static Company[] allCompaniesArray() {
		Company[] arr = new Company[allCompanies.values().size()];
		int idx = 0;
		for (Company comp : allCompanies.values())
			arr[idx++] = comp;
		return arr;
	}


	/**
	 * Loads a company from an XML element and puts it in the system
	 * @param eRoot The XML element that represents the company
	 */
	public static void generateFromXML(Element eRoot) {
		Element eName = (Element) eRoot.getElementsByTagName("Name").item(0);
		new Company(eName.getTextContent());

	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Company other = (Company) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

}
