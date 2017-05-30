package xml;

/**
 * Just a simple wrapper exception for the many exceptions from parsing the XML
 * @author Reece
 *
 */
public class XMLException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public XMLException(String message) {
		super(message);
	}

	public XMLException(Throwable cause) {
		super(cause);
	}

	public XMLException(String message, Throwable cause) {
		super(message, cause);
	}

}
