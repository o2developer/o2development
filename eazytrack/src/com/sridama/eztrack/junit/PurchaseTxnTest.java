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

import com.sridama.eztrack.bo.PurchaseTxn;
import com.sridama.eztrack.bo.SaleTxn;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class PurchaseTxnTest {

	/**
	 * Test the purchase transaction
	 * @throws  
	 */
	@Test
	public void testPurReq()   {
		
		FileReader fr ;
		BufferedReader br = null ;
		try {
		fr = new FileReader("resources/purchaseInvLatest.json");
	
		br = new BufferedReader(fr);

		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		RequestResponse req = new RequestResponse(sb.toString());
		SessionManager ses = new SessionManager("a93c665f-9t8a-42f2-89a7-898e663c94e7","admin",1);
		PurchaseTxn pur = new PurchaseTxn(ses);
		RequestResponse res = pur.purReq(req);
		System.out.println(res.getParam("request"));
		assertTrue("response should not be null", res.getParam("request")!=null);
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
				try {
					if ( br!=null )	br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
