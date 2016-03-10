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
import com.sridama.eztrack.bo.StockManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class StockManagerTest {
	@Test
	public void test4StockTransfer() throws IOException {
		FileReader fr = new FileReader("resources/stocktransfer") ;
		BufferedReader br = new BufferedReader(fr);
		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		System.out.println(sb.toString());
		RequestResponse req = new RequestResponse(sb.toString());
		SessionManager ses = new SessionManager("a93c665f-9t8a-42f2-89a7-898e663c94e7","admin",1);
		
		StockManager  stock = new StockManager(ses);
		RequestResponse res =  stock.transferStock(req) ;
		System.out.println(res.getParam("request"));
		assertTrue("", res.getParam("request")!=null); 
	}

}
