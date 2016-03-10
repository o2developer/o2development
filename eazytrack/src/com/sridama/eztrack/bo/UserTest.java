/**
 * 
 */
package com.sridama.eztrack.bo;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.junit.Test;

import com.sridama.txngw.core.RequestResponse;

/**
 * @author admin
 *
 */
public class UserTest {

	/**
	 * Test method for {@link com.sridama.eztrack.bo.User#authenticate()}.
	 */
	@Test
	public final void testAuthenticate4SuccessFirstBranchSuccess() {
		User user = new User("admin", "admin", 1);
		boolean result = user.authenticate();
		assertTrue("Authenticate with valid credentials should return true.", result == true);
	}

	/**
	 * Test method for {@link com.sridama.eztrack.bo.User#authenticate()}.
	 */
	@Test
	public final void testAuthenticate4SecondBranchSuccess() {
		User user1 = new User("admin", "admin", 2);
		boolean result = false ;
		try {
			 result = user1.authenticate();
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println("branch 2 valid case");
		}
		assertTrue("Authenticate with valid credentials should return true.", result == true);
	}
	
	/**
	 * Test method for {@link com.sridama.eztrack.bo.User#authenticate()}.
	 */
	@Test
	public final void testAuthenticate4ThirdBranchSuccess() {
		User user = new User("admin", "admin", 3);
		boolean result = user.authenticate();
		assertTrue("Authenticate with valid credentials should return true.", result == true);
	}

	/**
	 * Test method for {@link com.sridama.eztrack.bo.User#authenticate()}.
	 */
	@Test
	public final void testAuthenticate4FourthBranchSuccess() {
		User user = new User("admin", "admin", 4);
		boolean result = user.authenticate();
		assertTrue("Authenticate with valid credentials should return true.", result == true);
	}


	
	@Test
	public final void testAuthenticate4InvalidPassword() {
		User user = new User("admin", "fjdkfjek", 1);
		boolean result = user.authenticate();
		assertTrue("Authenticate with invalid password should return false.", result == false);
		
	}

	@Test
	public final void testAuthenticate4InvalidBranchCode() {
		User user = new User("admin", "admin", 100);
		boolean result = user.authenticate();
		assertTrue("Authenticate with invalid branch code should return false.", result == false);

	}
	
	
	
	@Test
	/*
	 * sending  credentials with json object
	 */
	public final void testAuthenticate4validBranchCodeJSON() throws Exception {
		JSONObject o = new JSONObject();
	o.put("name", "bhasker");	
	o.put("password", "admin");
	o.put("branch_code", 1);    
	User user = new User(o);
	boolean result = user.authenticate();
	assertTrue("Authentication should contain true", result==true) ;

	}
	
	@Test
	/*
	 * sending  credentials with json object
	 */
	public final void testAuthenticate4InvalidBranchCodeJSON() {
		JSONObject o = new JSONObject();
	o.put("name", "bhasker");	
	o.put("password", "admin");
	o.put("branch_code", 100);    
	User user = new User(o);
	boolean result = user.authenticate();
	assertTrue("Authentication should contain true", result==false) ;

	}
	
	@Test
	/*
	 * sending  credentials with json String
	 */
	public final void testAuthenticate4validBranchCodeJSONString() {
	User user=null;
	try {
		user = new User("{\"name\":\"admin\",\"password\":\"admin\",\"branch_code\":1}");
		boolean result = user.authenticate();
		assertTrue("Authentication should contain true", result==true) ;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}
	
	
	
	@Test
	/*
	 * sending  credentials with json String
	 */
	public final void testAuthenticate4InvalidBranchCodeJSONString() {
	User user=null;
	try {
		user = new User("{\"name\":\"admin\",\"password\":\"admin\",\"branch_code\":\"100\"}");
		boolean result = user.authenticate();
		assertTrue("Authentication should contain true", result==false) ;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}
	
	
	@Test
	/*
	 * Saving the user
	 */
	public final void testAuthenticate4SaveUser() throws IOException {		
		FileReader fr = new FileReader("resources/user") ;
	    BufferedReader br = new BufferedReader(fr);
	    String line = "" ;
	    StringBuilder sb = new StringBuilder();
	    while ( (line = br.readLine())!=null) {
		sb.append( line );
	    }
	  RequestResponse req = new RequestResponse(sb.toString());
	  SessionManager ses = new SessionManager("dhskjh-jdhsd-djkhksdjhf", "admin", 1);
	  User user = new User(req,ses);
	  RequestResponse res = user.save() ;
	  //System.out.println(res.getParam("request"));
	  assertTrue("the response value cannot be null",res.getParam("request")!=null);
	}
	
	
	@Test
	/*
	 * Delete user
	 */
	public final void testAuthenticate4DeleteUser() throws IOException {		
		FileReader fr = new FileReader("resources/user") ;
	    BufferedReader br = new BufferedReader(fr);
	    String line = "" ;
	    StringBuilder sb = new StringBuilder();
	    while ( (line = br.readLine())!=null) {
		sb.append( line );
	    }
	  RequestResponse req = new RequestResponse(sb.toString());
	  SessionManager ses = new SessionManager("dhskjh-jdhsd-djkhksdjhf", "admin", 1);
	  User user = new User(req,ses);
	  RequestResponse res = user.delete() ;
	  //System.out.println(res.getParam("request"));
	  assertTrue("the response value cannot be null",res.getParam("request")!=null);
	}
	
	@Test
	/*
	 * Update user
	 */
	public final void testAuthenticate4UpdateUser() throws IOException {		
		FileReader fr = new FileReader("resources/user") ;
	    BufferedReader br = new BufferedReader(fr);
	    String line = "" ;
	    StringBuilder sb = new StringBuilder();
	    while ( (line = br.readLine())!=null) {
		sb.append( line );
	    }
	  RequestResponse req = new RequestResponse(sb.toString());
	  SessionManager ses = new SessionManager("dhskjh-jdhsd-djkhksdjhf", "admin", 1);
	  User user = new User(req,ses);
	  RequestResponse res = user.update() ;
	  //System.out.println(res.getParam("request"));
	  assertTrue("the response value cannot be null",res.getParam("request")!=null);
	}
	
	
	
}
