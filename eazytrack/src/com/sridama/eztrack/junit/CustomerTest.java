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

import com.sridama.eztrack.bo.Customer;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class CustomerTest {

	/**
	 * test to initialize the constructor and to create the customer
	 * @throws IOException 
	 */
	@Test
	public void testCustomerRequestResponse() throws IOException {
		FileReader fr = new FileReader("resources/customer") ;
		BufferedReader br = new BufferedReader(fr);

		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		RequestResponse req = new RequestResponse(sb.toString());
		Customer cust = new Customer(req);
		RequestResponse  res = cust.save() ;
		System.out.println(res.getParam("request"));
		assertTrue("response josn is not null", req.getParam("request")!=null);
	}

	/**
	 * to test fot updating a customer
	 * @throws IOException 
	 */						
	@Test
	public void testUpdate() throws IOException {
		FileReader fr = new FileReader("resources/customer") ;
		BufferedReader br = new BufferedReader(fr);

		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		RequestResponse req = new RequestResponse(sb.toString());
		Customer cust = new Customer(req);
		RequestResponse  res = cust.update() ;
		System.out.println(res.getParam("request"));
		assertTrue("response josn is not null", req.getParam("request")!=null);
	}
	
	
	/**
	 * to test for deleting the customer
	 * @throws IOException 
	 */						
	@Test
	public void testDelete() throws IOException {
		FileReader fr = new FileReader("resources/customer") ;
		BufferedReader br = new BufferedReader(fr);

		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		RequestResponse req = new RequestResponse(sb.toString());
		Customer cust = new Customer(req);
		RequestResponse  res = cust.delete() ;
		System.out.println(res.getParam("request"));
		assertTrue("response josn is not null", req.getParam("request")!=null);
	}

}
