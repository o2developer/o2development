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

import com.sridama.eztrack.bo.Item;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class ItemTest {

	/**
	 * Test method for {@link com.sridama.eztrack.bo.Item#Item(com.sridama.txngw.core.RequestResponse)}.
	 */
	@Test
	public void testItem() {
	}
	

	/**
	 * to create the stock item
	 * @throws IOException 
	 */
	@Test
	public void testItemSave() throws IOException {
		FileReader fr = new FileReader("resources/item2") ;
		BufferedReader br = new BufferedReader(fr);

		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		RequestResponse req = new RequestResponse(sb.toString());
		SessionManager ses = new SessionManager("dhskjh-jdhsd-djkhksdjhf", "admin", 1);
		Item  item = new Item(ses);
		item.loadValues(req);
		RequestResponse res = item.save() ;
		System.out.println(res.getParam("request") );
		assertTrue("Response from the class cannot be null", res.getParam("request")!=null );
	}
	
	/**
	 * update the stock item
	 * @throws IOException 
	 */
	@Test
	public void testItemUpdate() throws IOException {
		FileReader fr = new FileReader("resources/item") ;
		BufferedReader br = new BufferedReader(fr);

		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		RequestResponse req = new RequestResponse(sb.toString());
		SessionManager ses = new SessionManager("dhskjh-jdhsd-djkhksdjhf", "admin", 1);
		Item  item = new Item(ses);
		item.loadValues(req);
		RequestResponse res = item.updateItem() ;
		System.out.println(res.getParam("request") );
		assertTrue("Response from the class cannot be null", res.getParam("result")!=null );
	}
	
	/**
	 * to create the stock item
	 * @throws IOException 
	 */
	@Test
	public void testDeleteItem() throws IOException {
		FileReader fr = new FileReader("resources/item") ;
		BufferedReader br = new BufferedReader(fr);

		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		RequestResponse req = new RequestResponse(sb.toString());
		SessionManager ses = new SessionManager("dhskjh-jdhsd-djkhksdjhf", "admin", 1);
		Item  item = new Item(ses);
		item.loadValues(req);
		RequestResponse res = item.delete();
		System.out.println(res.getParam("request") );
		assertTrue("Response from the class cannot be null", res.getParam("request")!=null );
	}
}
