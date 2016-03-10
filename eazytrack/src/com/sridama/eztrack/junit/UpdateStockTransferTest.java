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

import com.sridama.eztrack.bo.SessionManager;
import com.sridama.eztrack.bo.UpdateStockManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 */
public class UpdateStockTransferTest {

	@Test
	public void test4rejctionOfStockTransfer() throws IOException {
		FileReader fr = new FileReader("resources/updatestocktrans") ;
		BufferedReader br = new BufferedReader(fr);

		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		RequestResponse req = new RequestResponse(sb.toString());
		SessionManager ses = new SessionManager("a93c665f-9t8a-42f2-89a7-898e663c94e7","admin",3);

		UpdateStockManager updatestock = new UpdateStockManager(ses);
		RequestResponse res = updatestock.updateStock(req) ; 
		System.out.println(res.getParam("request"));
		assertTrue("response should not be null",res.getParam("request")!=null) ;
	}

}
