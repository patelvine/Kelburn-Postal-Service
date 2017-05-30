package tests;

import static org.junit.Assert.fail;
import events.EventHistory;
import gui.CreateUserPanel;
import gui.GUI;
import gui.LoginPanel;

import java.io.IOException;

import main.Login;
import main.Login.AccessPrivilege;

import org.junit.Test;

public class AccountTests {

	@Test
	public void newClient() throws IOException {
		Login.setLoginPrivilege(Login.AccessPrivilege.manager);
		Login.registerNewUser(convertToCharArray("testClient1"),convertToCharArray("password"),AccessPrivilege.clerk);
		Login.authenticate(convertToCharArray("testClient1"),convertToCharArray("password"));
		
		if (Login.getLoginPrivilege() != Login.AccessPrivilege.clerk) {
			fail("new clerk was not made");
		}
	}

	@Test
	public void newAdmin() throws IOException {
		Login.setLoginPrivilege(Login.AccessPrivilege.manager);
		Login.registerNewUser(convertToCharArray("testAdmin1"),convertToCharArray("password"),AccessPrivilege.manager);
		Login.authenticate(convertToCharArray("testAdmin1"),convertToCharArray("password"));
		
		if (Login.getLoginPrivilege() != Login.AccessPrivilege.manager) {
			fail("new manager was not made");
		}
	}

	@Test
	public void newClientExistingUsername() throws IOException {
		Login.setLoginPrivilege(Login.AccessPrivilege.manager);
		Login.registerNewUser(convertToCharArray("testClient2"),convertToCharArray("password"),AccessPrivilege.clerk);
		if (Login.registerNewUser(convertToCharArray("testClient2"),convertToCharArray("password"),AccessPrivilege.clerk)) {
			fail("Existing username was used to make another clerks account");
		}
	}

	@Test
	public void newAdminExistingUsername() throws IOException {
		Login.setLoginPrivilege(Login.AccessPrivilege.manager);
		Login.registerNewUser(convertToCharArray("testAdmin2"),convertToCharArray("password"),AccessPrivilege.manager);
		if (Login.registerNewUser(convertToCharArray("testAdmin2"),convertToCharArray("password"),AccessPrivilege.manager)) {
			fail("Existing username was used to make another managers account");
		}
	}

	@Test
	public void logInClient() throws IOException {
		Login.setLoginPrivilege(Login.AccessPrivilege.manager);
		Login.registerNewUser(convertToCharArray("testClient3"),convertToCharArray("password"),AccessPrivilege.clerk);
		Login.authenticate(convertToCharArray("testClient3"),convertToCharArray("password"));
		
		if (Login.getLoginPrivilege() != Login.AccessPrivilege.clerk) {
			fail("clerk could not log in");
		}
		if(!String.copyValueOf(Login.getLoggedInUsername()).equals("testClient3")){
			fail("clerk could not log in");
		}
	}
	
	@Test
	public void logInAdmin() throws IOException {
		Login.setLoginPrivilege(Login.AccessPrivilege.manager);
		Login.registerNewUser(convertToCharArray("testAdmin3"),convertToCharArray("password"),AccessPrivilege.manager);
		Login.authenticate(convertToCharArray("testAdmin3"),convertToCharArray("password"));
		
		if (Login.getLoginPrivilege() != Login.AccessPrivilege.manager) {
			fail("manager could not log in");
		}
		if(!String.copyValueOf(Login.getLoggedInUsername()).equals("testAdmin3")){
			fail("manager could not log in");
		}
	}

	@Test
	public void logInFail() throws IOException {
		if (Login.authenticate(convertToCharArray("noOne1"),convertToCharArray("password"))) {
			fail("Non existing user logged in");
		}
	}
	
	// admin logged in
	@Test
	public void deleteAccount1() throws IOException {
		Login.setLoginPrivilege(Login.AccessPrivilege.manager);
		Login.registerNewUser(convertToCharArray("dummyClient1"),convertToCharArray("password"),AccessPrivilege.clerk);
		Login.registerNewUser(convertToCharArray("testAdmin4"),convertToCharArray("password"),AccessPrivilege.manager);
		Login.authenticate(convertToCharArray("testAdmin4"),convertToCharArray("password"));
		
		if(!Login.removeUser(convertToCharArray("dummyClient1"))){
			fail("Client was not removed");
		}
		if(Login.authenticate(convertToCharArray("dummyClient1"),convertToCharArray("password"))){
			fail("Double check client was not removed by logging in");
		}
	}
	
	// admin not logged in
	@Test
	public void deleteAccount2() throws IOException {
		Login.setLoginPrivilege(Login.AccessPrivilege.manager);
		Login.registerNewUser(convertToCharArray("dummyClient2"),convertToCharArray("password"),AccessPrivilege.clerk);
		Login.registerNewUser(convertToCharArray("dummyClient3"),convertToCharArray("password"),AccessPrivilege.clerk);
		Login.registerNewUser(convertToCharArray("testAdmin5"),convertToCharArray("password"),AccessPrivilege.manager);
		Login.authenticate(convertToCharArray("dummyClient2"),convertToCharArray("password"));
		
		if(Login.removeUser(convertToCharArray("dummyClient3"))){
			fail("Client was able to remove a user");
		}
		if(!Login.authenticate(convertToCharArray("dummyClient3"),convertToCharArray("password"))){
			fail("Client attempted to be removed cannot login");
		}
	}
	
	// remove user that does not exist
	@Test
	public void deleteAccount3() throws IOException {
		Login.setLoginPrivilege(Login.AccessPrivilege.manager);
		Login.registerNewUser(convertToCharArray("testAdmin6"),convertToCharArray("password"),AccessPrivilege.manager);
		Login.authenticate(convertToCharArray("testAdmin6"),convertToCharArray("password"));
		
		if(Login.removeUser(convertToCharArray("someone"))){
			fail("Outcome true when non-existing user was given to be removed");
		}
	}
		
	// admin not logged in
	@Test
	public void deleteAccount4() throws IOException {
	}
	
	// clerk changes password
	@Test
	public void changePasswordClerk() throws IOException {
		Login.setLoginPrivilege(Login.AccessPrivilege.manager);
		Login.registerNewUser(convertToCharArray("dummyClient4"),convertToCharArray("password"),AccessPrivilege.clerk);
		Login.authenticate(convertToCharArray("dummyClient4"),convertToCharArray("password"));
		Login.changePassword(convertToCharArray("dummyClient4"), convertToCharArray("password"), convertToCharArray("password2"));
		
		if(!Login.authenticate(convertToCharArray("dummyClient4"),convertToCharArray("password2"))){
			fail("Password was not changed for a clerk");
		}
	}
	
	// admin changes password
		@Test
		public void changePasswordAdmin() throws IOException {
			Login.setLoginPrivilege(Login.AccessPrivilege.manager);
			Login.registerNewUser(convertToCharArray("testAdmin7"),convertToCharArray("password"),AccessPrivilege.clerk);
			Login.authenticate(convertToCharArray("testAdmin7"),convertToCharArray("password"));
			Login.changePassword(convertToCharArray("testAdmin7"), convertToCharArray("password"), convertToCharArray("password2"));
			
			if(!Login.authenticate(convertToCharArray("testAdmin7"),convertToCharArray("password2"))){
				fail("Password was not changed for a admin");
			}
		}
		
		@Test
		public void logout() throws IOException {
			Login.setLoginPrivilege(Login.AccessPrivilege.manager);
			Login.removeUser(convertToCharArray("testAdmin8"));
			Login.registerNewUser(convertToCharArray("testAdmin8"),convertToCharArray("password"),AccessPrivilege.manager);
			Login.authenticate(convertToCharArray("testAdmin8"),convertToCharArray("password"));
			
			if(String.copyValueOf(Login.getLoggedInUsername()).equals("testAdmin8")){
				if(Login.getLoginPrivilege() == Login.AccessPrivilege.manager){
					Login.logOut();
					if(!String.copyValueOf(Login.getLoggedInUsername()).isEmpty() || Login.getLoginPrivilege() != Login.AccessPrivilege.none){
						fail("Logout was not successful");
					}
				}
			}
		}

	// tests for admin changes to cus prices etc?
	// tests for client attempts to change cus prices etc?
	
	public char[] convertToCharArray(String detail){
		char[] array = new char[detail.length()];
		for(int i = 0 ; i < detail.length() ; i++){
			array[i] = detail.charAt(i);
		}
		return array;
	}

}
