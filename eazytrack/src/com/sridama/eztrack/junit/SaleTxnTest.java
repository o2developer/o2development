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

import com.sridama.eztrack.bo.SaleTxn;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class SaleTxnTest {
	/**
	 * Test method for {@link com.sridama.eztrack.bo.SaleTxn#saleReq(com.sridama.txngw.core.RequestResponse)}.
	 * @throws IOException 
	 */
	@Test
	public void testSaleReq4MakingSale() throws IOException {
		
		FileReader fr = new FileReader("resources/saleinvoice1") ;   //  sale_invoice_reject.json
		BufferedReader br = new BufferedReader(fr);

		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		RequestResponse req = new RequestResponse(sb.toString());
		SessionManager ses = new SessionManager("a93c665f-9t8a-42f2-89a7-898e663c94e7","admin",1);
		SaleTxn sale = new SaleTxn(ses);
		RequestResponse res = sale.saleReq(req);
		System.out.println(res.getParam("request"));
		assertTrue("response should not be null", res.getParam("request")!=null);
	}
	
	/**
	 * Test method for {@link com.sridama.eztrack.bo.SaleTxn#saleReq(com.sridama.txngw.core.RequestResponse)}.
	 * @throws IOException 
	 */
	@Test
	public void testSaleReq4RejectingSaleInvoice() throws IOException {
		
		FileReader fr = new FileReader("resources/sale_invoice_reject.json") ;   //  sale_invoice_reject.json
		BufferedReader br = new BufferedReader(fr);

		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		RequestResponse req = new RequestResponse(sb.toString());
		SessionManager ses = new SessionManager("a93c665f-9t8a-42f2-89a7-898e663c94e7","admin",1);
		SaleTxn sale = new SaleTxn(ses);
		RequestResponse res = sale.saleReq(req);
		System.out.println(res.getParam("request"));
		assertTrue("response should not be null", res.getParam("request")!=null);

	}

}
