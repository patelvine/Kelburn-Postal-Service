package events;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class to syncronise how the date of logs is handled in the program.
 * Dates are saved and loaded in the form dd/MM/yyyy HH:mm (e.g. 07/04/2014 13:27)
 *  but displayed to the user in the form EEE, d MMM, yyyy HH:mm (e.g. Mon, 7 Apr, 2014 13:27)
 * 
 * @author Reece
 *
 */
public class DateUtility {

	/**
	 * Method to return a date from a string in the form dd/MM/yyyy HH:mm
	 * (e.g. 07/04/2014 13:27)
	 * @param input Input string to generate date
	 * @return Date produced from string if valid, null otherwise
	 * @throws ParseException 
	 */
	public static Date parseInputdate(String input) throws ParseException{
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date date;
		date = formatter.parse(input);
		return date;
	}

	/**
	 * Method to return a string from a date for use in saving so that the output
	 * can be put into parseInputdate() and return a valid date.
	 * Returned format is dd/MM/yyyy HH:mm (e.g. 07/04/2014 13:27)
	 * @param date Date to convert to a string
	 * @return String of date in form dd/MM/yyyy HH:mm
	 */
	public static String formatOutputDate(Date date){
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return formatter.format(date);
	}

	/**
	 * Method to return a sting from a date for use in displaying to the user
	 * cannot be used to produce date from parseInputdate(), please use 
	 * formatOutputDate().
	 * Returned format is EEE, d MMM, yyyy HH:mm (e.g. Mon, 7 Apr, 2014 13:27)
	 * @param date Date to convert to a string
	 * @return String of date in form EEE, d MMM, yyyy HH:mm
	 */
	public static String formatDisplayDate(Date date){
		DateFormat formatter = new SimpleDateFormat("EEE, d MMM, yyyy HH:mm");
		return formatter.format(date);
	}

}
