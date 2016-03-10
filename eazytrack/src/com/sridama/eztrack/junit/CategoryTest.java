/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import com.sridama.eztrack.bo.Category;
import com.sridama.eztrack.bo.Item;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class CategoryTest {

	/**
	 * Test method for saving the new category
	 */
	@Test
	public void testSave() throws IOException {
		FileReader fr = new FileReader("resources/category") ;
		BufferedReader br = new BufferedReader(fr);

		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		RequestResponse req = new RequestResponse(sb.toString());
		SessionManager ses = new SessionManager("dhskjh-jdhsd-djkhksdjhf", "admin", 1);
		Category  cat = new Category(req);
		RequestResponse res = cat.save() ;
		System.out.println(res.getParam("request") );
		assertTrue("Response from the class cannot be null", res.getParam("request")!=null );
	}

	/**
	 * Test method for deleting the category
	 * @throws IOException 
	 */
	@Test
	public void testDelete() throws IOException {
		FileReader fr = new FileReader("resources/category") ;
		BufferedReader br = new BufferedReader(fr);

		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		RequestResponse req = new RequestResponse(sb.toString());
		SessionManager ses = new SessionManager("dhskjh-jdhsd-djkhksdjhf", "admin", 1);
		Category  cat = new Category(req);
		RequestResponse res = cat.delete() ;
		System.out.println(res.getParam("request") );
		assertTrue("Response from the class cannot be null", res.getParam("request")!=null );
	}

}
