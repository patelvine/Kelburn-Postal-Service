package events;
/**
 * The mailing priority of a package.
 * @author Chris
 *
 */
public enum Priority {
	INTERNATIONAL_AIR_PRIORITY, INTERNATIONAL_STANDARD_PRIORITY, DOMESTIC_AIR_PRIORITY, DOMESTIC_STANDARD_PRIORITY;
	
	public static Priority getPriorityFromString(String input){
		if(input.equalsIgnoreCase("INTERNATIONAL_AIR_PRIORITY")){
			return Priority.INTERNATIONAL_AIR_PRIORITY;
		}
		else if(input.equalsIgnoreCase("INTERNATIONAL_STANDARD_PRIORITY")){
			return Priority.INTERNATIONAL_STANDARD_PRIORITY;
		}
		else if(input.equalsIgnoreCase("DOMESTIC_AIR_PRIORITY")){
			return Priority.DOMESTIC_AIR_PRIORITY;
		}
		else if(input.equalsIgnoreCase("DOMESTIC_STANDARD_PRIORITY")){
			return Priority.DOMESTIC_STANDARD_PRIORITY;
		}
		return null;
	}
}
