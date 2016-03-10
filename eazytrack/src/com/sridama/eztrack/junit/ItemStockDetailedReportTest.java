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

import com.sridama.eztrack.bo.ItemStockDetailedReport;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class ItemStockDetailedReportTest {
	/*
	 * to test whether proper json is coming or not 
	 */
	@Test
	public void test() throws IOException {

		FileReader fr = new FileReader("resources/stockListReport.json") ;   //saleinvoice1
		BufferedReader br = new BufferedReader(fr);
		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		RequestResponse req = new RequestResponse(sb.toString());
		SessionManager ses = new SessionManager("a93c665f-9t8a-42f2-89a7-898e663c94e7","admin", 1);
		ItemStockDetailedReport itemstock =  new ItemStockDetailedReport(ses);
		RequestResponse res =  itemstock.getStockReport(req);
		System.out.println( res.getParam("request") );
		assertTrue ("response cannot be null ", res.getParam("request")!=null);
	}
}
