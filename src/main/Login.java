
package main;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JOptionPane;
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

import xml.XMLManager;

import org.apache.commons.codec.binary.Base64;

//Security help from http://www.javacodegeeks.com/2012/05/secure-password-storage-donts-dos-and.html
/**
 * Class that handles creation, reading and writing of the login XML file. Also contains the static
 * variable loginPrivilege so that the program knows 
 * @author Reece
 *
 */
public class Login {

	/**
	 * Enum for telling what the privileges the currently logged in user has
	 * @author Reece
	 *
	 */
	public enum AccessPrivilege {
		clerk, manager, none;
		
		/**
		 * Method to get the equivalent Accessprivilege fromt h entered string.
		 * @param string name of the privilege level you want returned
		 * @return AccessPrivilege related to string, null if no match.
		 */
		public static AccessPrivilege getAccessPrivilegeFromString(String string){
			if (string.equalsIgnoreCase("none")){
				return none;
			}
			else if (string.equalsIgnoreCase("clerk")){
				return clerk;
			}
			else if (string.equalsIgnoreCase("manager")){
				return manager;
			}
			return null;
		}

		/**
		 * Method that returns a string of the AccessPrivilege. Can be used with
		 * getAccessPrivilegeFromString to get the enum again.
		 * @return string equivalent to the enum
		 */
		public String toString(){
			if (this.equals(none)){
				return "none";
			}
			else if (this.equals(clerk)){
				return "clerk";
			}
			else if (this.equals(manager)){
				return "manager";
			}
			return null;
		}


	}

	private static AccessPrivilege loginPrivilege = AccessPrivilege.none; //current user's access level
	private static char[] loginUser = new char[0]; //name of the current user

	/**
	 * Get the username of the currently logged in user
	 * @return username of currently logged in user
	 */
	public static char[] getLoggedInUsername(){
		return loginUser;
	}

	/**
	 * Get the access level of the currently logged in user
	 * @return AccessPrivilege of the current user
	 */
	public static AccessPrivilege getLoginPrivilege(){
		return loginPrivilege;
	}
	
	/**
	 * Sets the access level of the current session
	 * @param privilege Access level to set to
	 */
	public static void setLoginPrivilege(AccessPrivilege privilege){
		loginPrivilege = privilege;
	}

	private static final String loginFile = XMLManager.xmlFolder + "/users.xml";

	/**
	 * Method to attempt to authenticate a user using the stored XMLfile and supplied username and password.
	 *
	 * @param username Username entered by the user to try authenticate
	 * @param attemptedPassword Password entered by the user to try authenticate
	 * @return True is login successful, false otherwise.
	 */
	public static boolean authenticate(char[] username, char[] attemptedPassword){
		try{
			DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFac.newDocumentBuilder();
			File xmlFile = new File(loginFile);
			while (!xmlFile.exists()){
				generateNewLoginFile();
			}
			Document logDoc = docBuilder.parse(xmlFile);
			logDoc.normalizeDocument();
			//encrypt the entered username so that it can be found in the XML
			byte[] encryptedUsername = getEncryptedUsername(username, logDoc);
			if (!checkUsernameExists(encryptedUsername, logDoc)){
				return false;
			}
			//use the encrypted username to find the encrypted password from XML
			byte[] encryptedPassword = getEncryptedPasswordFromXML(encryptedUsername, logDoc);
			//use the encrypted username to get the salt for encrypting the entered password
			byte[] salt = getSaltFromXML(encryptedUsername, logDoc);
			//use the salt to encrypt the entered password
			byte[] encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);

			if (Arrays.equals(encryptedPassword, encryptedAttemptedPassword)){
				loginPrivilege = getUserPrivilege(encryptedUsername, logDoc);
				loginUser = username;
				return true;
			}

		} catch (NoSuchAlgorithmException e) {//coding error! this should never happen!
			errorMessage("Unable to authenticate user, please contact you system administrator", "Encryption Error");
		} catch (InvalidKeySpecException e) {//coding error! this should never happen
			errorMessage("Unable to authenticate user, please contact you system administrator", "Encryption Error");
		} catch (ParserConfigurationException e) {//unable to create the documentBuilder with configuration, should not happen as all is default
			errorMessage("Unable to build XML Parser, please try again. If problem persists please contact your system administrator", "XML Document Error");
		} catch (SAXException e) { //XML file is in the incorrect format
			errorMessage("XML file is incorrectly formatted, please check or remove the login file", "XML File Error");
		} catch (IOException e) { //This should never happen as it is checked the line before!!!
			errorMessage("XML login file could not be found, please try again to generate file.", "XML File Error");
		} catch (TransformerException e) {
			errorMessage("Error when attempting to create new login file, If problem persists please contact your system administrator" , "XML Building Error");
		} 
		return false;
	}



	/**
	 * Resets the login privileges to none.
	 */
	public static void logOut(){
		loginUser = new char[0];
		loginPrivilege = AccessPrivilege.none;
	}


	/**
	 * Registers a new user for the program with the entered username, passwor dand access privileges.
	 * Only works if the current user has manager privileges
	 * @param username username of the new user
	 * @param password password to be used by the new user
	 * @param privilegeLevel the AccessPrivilege that the new user will have.
	 */
	public static boolean registerNewUser(char[] username, char[] password, AccessPrivilege privilegeLevel){
		if (loginPrivilege != AccessPrivilege.manager) return false;
		try{
			DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFac.newDocumentBuilder();
			File xmlFile = new File(loginFile);
			Document logDoc = docBuilder.parse(xmlFile);
			logDoc.normalizeDocument();
			byte[] encryptedUsername = getEncryptedUsername(username, logDoc);
			if(checkUsername(username)){
				return false;
			}
			byte[] salt = generateSalt();
			byte[] encryptedPassword = getEncryptedPassword(password, salt);
			Element eUser = logDoc.createElement("User");

			Element eUsername = logDoc.createElement("Username");
			eUsername.appendChild(logDoc.createTextNode(Base64.encodeBase64String(encryptedUsername)));
			eUser.appendChild(eUsername);

			Element ePassword = logDoc.createElement("Password");
			ePassword.appendChild(logDoc.createTextNode(Base64.encodeBase64String(encryptedPassword)));
			eUser.appendChild(ePassword);

			Element eAccess = logDoc.createElement("Access");
			eAccess.appendChild(logDoc.createTextNode(privilegeLevel.toString()));
			eUser.appendChild(eAccess);

			Element eSalt = logDoc.createElement("Salt");
			eSalt.appendChild(logDoc.createTextNode(Base64.encodeBase64String(salt)));
			eUser.appendChild(eSalt);

			logDoc.getDocumentElement().appendChild(eUser);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(logDoc);
			File output = new File(loginFile);
			StreamResult result = new StreamResult(output);
			transformer.transform(source, result);
			return true;
		} catch (NoSuchAlgorithmException e) {//coding error! this should never happen!
			errorMessage("Unable to authenticate user, please contact you system administrator", "Encryption Error");
		} catch (InvalidKeySpecException e) {//coding error! this should never happen
			errorMessage("Unable to authenticate user, please contact you system administrator", "Encryption Error");
		} catch (ParserConfigurationException e) {//unable to create the documentBuilder with configuration, should not happen as all is default
			errorMessage("Unable to build XML Parser, please try again. If problem persists please contact your system administrator", "XML Document Error");
		} catch (SAXException e) { //XML file is in the incorrect format
			errorMessage("XML file is incorrectly formatted, please check or remove the login file", "XML File Error");
		} catch (IOException e) { //This should never happen as it is checked the line before!!!
			errorMessage("XML login file could not be found, please try again to generate file.", "XML File Error");
		} catch (TransformerException e) {
			errorMessage("Error when attempting to create new login file, If problem persists please contact your system administrator" , "XML Building Error");
		} 
		return false;
	}

	/**
	 *Deletes the user of the given name from the XML. Requires manager access privileges to be active
	 * @param username username to delete
	 * @return True if user is successfully deleted, false othewise.
	 */
	public static boolean removeUser(char[] username){
		if (!loginPrivilege.equals(AccessPrivilege.manager)){
			errorMessage("You must have manager access privileges to remove another user", "Insufficient Permissions");
			return false;
		}
		else if (Arrays.equals(username, loginUser)){
			errorMessage("You cannot remove your own login.", "Cannot Delete User");
			return false;
		}
		try{
			DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFac.newDocumentBuilder();
			File xmlFile = new File(loginFile);
			Document logDoc = docBuilder.parse(xmlFile);
			logDoc.normalizeDocument();

			byte[] usernameToDelete = getEncryptedUsername(username, logDoc);
			Element eUser = getUserElement(usernameToDelete, logDoc);
			if (eUser == null){
				errorMessage("Cannot delete user as they do not exist", "Cannot Delete user");
				return false;
			}
			logDoc.getDocumentElement().removeChild(eUser);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(logDoc);
			File output = new File(loginFile);
			File outputFolder = new File(XMLManager.xmlFolder);
			outputFolder.mkdirs();
			output.createNewFile();
			StreamResult result = new StreamResult(output);
			transformer.transform(source, result);
			return true;
		} catch (NoSuchAlgorithmException e) {//coding error! this should never happen!
			errorMessage("Unable to authenticate user, please contact you system administrator", "Encryption Error");
		} catch (InvalidKeySpecException e) {//coding error! this should never happen
			errorMessage("Unable to authenticate user, please contact you system administrator", "Encryption Error");
		} catch (ParserConfigurationException e) {//unable to create the documentBuilder with configuration, should not happen as all is default
			errorMessage("Unable to build XML Parser, please try again. If problem persists please contact your system administrator", "XML Document Error");
		} catch (SAXException e) { //XML file is in the incorrect format
			errorMessage("XML file is incorrectly formatted, please check or remove the login file", "XML File Error");
		} catch (IOException e) { //This should never happen as it is checked the line before!!!
			errorMessage("XML login file could not be found, please try again to generate file.", "XML File Error");
		} catch (TransformerException e) {
			errorMessage("Error when attempting to create new login file, If problem persists please contact your system administrator" , "XML Building Error");
		} 
		return false;
	}

	public static boolean changePassword(char[] username, char[] oldPassword, char[] newPassword){
		if (!loginPrivilege.equals(AccessPrivilege.manager) && !Arrays.equals(username, loginUser)){ //to change password must have manager privileges or is current user
			errorMessage("You must either have manager permissions or owner of the account to change its password", "Insufficient Permissions");
			return false;
		}
		try{
			DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFac.newDocumentBuilder();
			File xmlFile = new File(loginFile);
			Document logDoc = docBuilder.parse(xmlFile);
			logDoc.normalizeDocument();

			byte[] encryptedUsername = getEncryptedUsername(username, logDoc);
			Element eUser = getUserElement(encryptedUsername, logDoc);
			if (eUser == null){
				errorMessage("User does not exist.", "Unable to Change Password");
				return false;
			}
			//use the encrypted username to find the encrypted password from XML
			byte[] encryptedPassword = getEncryptedPasswordFromXML(encryptedUsername, logDoc);
			//use the encrypted username to get the salt for encrypting the entered password
			byte[] salt = getSaltFromXML(encryptedUsername, logDoc);
			//use the salt to encrypt the entered password
			byte[] encryptedAttemptedPassword = getEncryptedPassword(oldPassword, salt);

			if (!Arrays.equals(encryptedPassword, encryptedAttemptedPassword)){
				errorMessage("Please enter the old password correctly" , "Incorrect Password");
				return false;
			}
			byte[] encryptedNewPassword = getEncryptedPassword(newPassword, salt);
			Element ePassword = (Element) eUser.getElementsByTagName("Password").item(0);
			ePassword.setTextContent(Base64.encodeBase64String(encryptedNewPassword));

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(logDoc);
			File output = new File(loginFile);
			File outputFolder = new File(XMLManager.xmlFolder);
			outputFolder.mkdirs();
			output.createNewFile();
			StreamResult result = new StreamResult(output);
			transformer.transform(source, result);
			return true;
		} catch (NoSuchAlgorithmException e) {//coding error! this should never happen!
			errorMessage("Unable to authenticate user, please contact you system administrator", "Encryption Error");
		} catch (InvalidKeySpecException e) {//coding error! this should never happen
			errorMessage("Unable to authenticate user, please contact you system administrator", "Encryption Error");
		} catch (ParserConfigurationException e) {//unable to create the documentBuilder with configuration, should not happen as all is default
			errorMessage("Unable to build XML Parser, please try again. If problem persists please contact your system administrator", "XML Document Error");
		} catch (SAXException e) { //XML file is in the incorrect format
			errorMessage("XML file is incorrectly formatted, please check or remove the login file", "XML File Error");
		} catch (IOException e) { //This should never happen as it is checked the line before!!!
			errorMessage("XML login file could not be found, please try again to generate file.", "XML File Error");
		} catch (TransformerException e) {
			errorMessage("Error when attempting to create new login file, If problem persists please contact your system administrator" , "XML Building Error");
		} 
		return false;
	}

	private static boolean checkUsernameExists(byte[] encryptedUsername, Document logDoc) {
		NodeList nodes = logDoc.getDocumentElement().getElementsByTagName("User");
		for (int i = 0; i < nodes.getLength(); i++){
			Element eUser = (Element) nodes.item(i);
			Element eUsername = (Element) eUser.getElementsByTagName("Username").item(0);
			byte[] xmlUsername = Base64.decodeBase64(eUsername.getTextContent());
			if (Arrays.equals(encryptedUsername, xmlUsername)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkUsername(char[] username){
		try{
			DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFac.newDocumentBuilder();
			File xmlFile = new File(loginFile);
			Document logDoc = docBuilder.parse(xmlFile);
			logDoc.normalizeDocument();
			//encrypt the entered username so that it can be found in the XML
			byte[] encryptedUsername = getEncryptedUsername(username, logDoc);
			if (checkUsernameExists(encryptedUsername, logDoc)){
				return true;
			}
		} catch (NoSuchAlgorithmException e) {//coding error! this should never happen!
			errorMessage("Unable to authenticate user, please contact you system administrator", "Encryption Error");
		} catch (InvalidKeySpecException e) {//coding error! this should never happen
			errorMessage("Unable to authenticate user, please contact you system administrator", "Encryption Error");
		} catch (ParserConfigurationException e) {//unable to create the documentBuilder with configuration, should not happen as all is default
			errorMessage("Unable to build XML Parser, please try again. If problem persists please contact your system administrator", "XML Document Error");
		} catch (SAXException e) { //XML file is in the incorrect format
			errorMessage("XML file is incorrectly formatted, please check or remove the login file", "XML File Error");
		} catch (IOException e) { //This should never happen as it is checked the line before!!!
			errorMessage("XML login file could not be found, please try again to generate file.", "XML File Error");
		} 
			return false;
	}

	private static Element getUserElement(byte[] encryptedUsername, Document logDoc) {
		NodeList nodes = logDoc.getDocumentElement().getElementsByTagName("User");
		for (int i = 0; i < nodes.getLength(); i++){
			Element eUser = (Element) nodes.item(i);
			Element eUsername = (Element) eUser.getElementsByTagName("Username").item(0);
			byte[] xmlUsername = Base64.decodeBase64(eUsername.getTextContent());
			if (Arrays.equals(encryptedUsername, xmlUsername)){
				return eUser;
			}
		}
		return null;
	}

	private static void generateNewLoginFile() throws NoSuchAlgorithmException, InvalidKeySpecException, ParserConfigurationException, IOException, TransformerException{
			DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFac.newDocumentBuilder();
			Document logDoc = docBuilder.newDocument();
			Element rootElement = logDoc.createElement("Users");

			Element usernameSalt = logDoc.createElement("Salt");
			usernameSalt.appendChild(logDoc.createTextNode(Base64.encodeBase64String(generateSalt())));
			rootElement.appendChild(usernameSalt);

			logDoc.appendChild(rootElement);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(logDoc);
			File output = new File(loginFile);
			File outputFolder = new File(XMLManager.xmlFolder);
			outputFolder.mkdirs();
			output.createNewFile();
			StreamResult result = new StreamResult(output);
			transformer.transform(source, result);

			loginPrivilege = AccessPrivilege.manager;
			char[] username = new char[]{'a','d','m','i','n'};
			char[] password = new char[]{'p','a','s','s','w','o','r','d'};
			registerNewUser(username, password, AccessPrivilege.manager);
	}

	private static byte[] generateSalt() throws NoSuchAlgorithmException {
		//Code help from http://www.javacodegeeks.com/2012/05/secure-password-storage-donts-dos-and.html
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		return salt;
	}

	private static byte[] getEncryptedPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		//From http://www.javacodegeeks.com/2012/05/secure-password-storage-donts-dos-and.html
		// PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
		// specifically names SHA-1 as an acceptable hashing algorithm for PBKDF2
		String algorithm = "PBKDF2WithHmacSHA1";
		int derivedKeyLength = 160;
		int iterations = 20000;

		KeySpec spec = new PBEKeySpec(password,salt, iterations, derivedKeyLength);

		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

		return f.generateSecret(spec).getEncoded();
	}

	private static byte[] getEncryptedPasswordFromXML(byte[] encryptedUsername, Document logDoc){
		//open XML
		//use username to find salt for password
		NodeList nodes = logDoc.getDocumentElement().getElementsByTagName("User");
		for (int i = 0; i < nodes.getLength(); i++){
			Element eUser = (Element) nodes.item(i);
			Element eUsername = (Element) eUser.getElementsByTagName("Username").item(0);
			byte[] xmlUsername = Base64.decodeBase64(eUsername.getTextContent());
			if (Arrays.equals(encryptedUsername, xmlUsername)){
				Element ePassword = (Element) eUser.getElementsByTagName("Password").item(0);
				byte[] password = Base64.decodeBase64(ePassword.getTextContent());
				return password;
			}
		}
		return null;

	}

	private static byte[] getEncryptedUsername(char[] username, Document logDoc) throws NoSuchAlgorithmException, InvalidKeySpecException {
		//open XML
		//get salt from file
		Element eSalt = (Element) logDoc.getDocumentElement().getElementsByTagName("Salt").item(0);
		byte[] salt = Base64.decodeBase64(eSalt.getTextContent());
		//From http://www.javacodegeeks.com/2012/05/secure-password-storage-donts-dos-and.html
		// PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
		// specifically names SHA-1 as an acceptable hashing algorithm for PBKDF2
		String algorithm = "PBKDF2WithHmacSHA1";
		int derivedKeyLength = 160;
		int iterations = 20000;

		KeySpec spec = new PBEKeySpec(username,salt, iterations, derivedKeyLength);

		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

		return f.generateSecret(spec).getEncoded();
	}

	private static byte[] getSaltFromXML(byte[] encryptedUsername, Document logDoc) {
		//open XML
		//use username to find salt for password
		NodeList nodes = logDoc.getDocumentElement().getElementsByTagName("User");
		for (int i = 0; i < nodes.getLength(); i++){
			Element eUser = (Element) nodes.item(i);
			Element eUsername = (Element) eUser.getElementsByTagName("Username").item(0);
			byte[] xmlUsername = Base64.decodeBase64(eUsername.getTextContent());
			if (Arrays.equals(encryptedUsername, xmlUsername)){
				Element eSalt = (Element) eUser.getElementsByTagName("Salt").item(0);
				byte[] salt = new byte[8];
				salt = Base64.decodeBase64(eSalt.getTextContent());
				return salt;
			}
		}
		return null;
	}

	private static AccessPrivilege getUserPrivilege(byte[] encryptedUsername, Document logDoc) {
		NodeList nodes = logDoc.getDocumentElement().getElementsByTagName("User");
		for (int i = 0; i < nodes.getLength(); i++){
			Element eUser = (Element) nodes.item(i);
			Element eUsername = (Element) eUser.getElementsByTagName("Username").item(0);
			byte[] xmlUsername = Base64.decodeBase64(eUsername.getTextContent());
			if (Arrays.equals(encryptedUsername, xmlUsername)){
				Element eAccess = (Element) eUser.getElementsByTagName("Access").item(0);
				return AccessPrivilege.getAccessPrivilegeFromString(eAccess.getTextContent());
			}
		}
		return AccessPrivilege.none; //failsafe
	}
	
	private static void errorMessage(String message, String title){
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}

	private Login(){
	}
}

