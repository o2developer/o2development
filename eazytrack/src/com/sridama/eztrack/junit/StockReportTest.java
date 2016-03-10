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
import com.sridama.eztrack.bo.StockReport;
import com.sridama.eztrack.utils.Utils;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author admin
 *
 */
public class StockReportTest {

	/**
	 * Test method for {@link com.sridama.eztrack.bo.StockReport#StockReport(com.sridama.txngw.core.RequestResponse)}.
	 */
	@Test
	public final void testStockReport4CompleteStockReport() {
		RequestResponse req = new RequestResponse("{}");
		req.setParam("opcode", "3");
		StockReport s = new StockReport(new SessionManager("session_id"," name", 1));
		RequestResponse res =  s.generateReport(req);
		
		String json = res.getParam("request");
		System.out.println(json + Utils.brCode);
		assertTrue("Sale Report JSON cannot be null ", !json.equals(null) || !json.equals("") );
	}

	/**
	 * Test method for {@link com.sridama.eztrack.bo.StockReport#getReport()}.
	 */
	@Test
	public final void testStockReport4StockBasedOnCategory() {
		RequestResponse req = new RequestResponse("{\"category_name\":\"balls\"}");
		req.setParam("opcode", "3");
		StockReport s = new StockReport(new SessionManager("session_id"," name", 1));
		RequestResponse res =  s.generateReport(req);
		
		String json = res.getParam("request");
		System.out.println(json + Utils.brCode);
		assertTrue("Sale Report JSON cannot be null ", !json.equals(null) || !json.equals("") );
		
	}
	
	/**
	 * Test method for {@link com.sridama.eztrack.bo.StockReport#getReport()}.
	 */
	@Test
	public final void testStockReport4StockBasedOnModel() {
		RequestResponse req = new RequestResponse("{\"modal\":\"ymg123\"}");
		req.setParam("opcode", "3");
		req.setParam("branch","1");
		StockReport s = new StockReport(new SessionManager("session_id"," name", 1));
		RequestResponse res =  s.generateReport(req);
		
		String json = res.getParam("request");
		System.out.println(json + Utils.brCode);
		assertTrue("Sale Report JSON cannot be null ", !json.equals(null) || !json.equals("") );
		
	}
	
	
	/**
	 *    Gives the Response of stock , Based on the type ahead values 
	 * @throws IOException 
	 */
	@Test
	public final void testStockReport4StockBasedOnTypeAHead() throws IOException {
		
		FileReader fr = new FileReader("resources/stockList.json") ;   //  sale_invoice_reject.json
		BufferedReader br = new BufferedReader(fr);

		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}

		RequestResponse req = new RequestResponse(sb.toString());
		req.setParam("opcode", "3");
		req.setParam("branch","1");
		req.setParam("sSearch","702");
		StockReport s = new StockReport(new SessionManager("session_id"," name", 1));
		RequestResponse res =  s.generateReport(req);
		
		String json = res.getParam("request");
		System.out.println(json + Utils.brCode);
		assertTrue("Sale Report JSON cannot be null ", !json.equals(null) || !json.equals("") );
		
	}
}
