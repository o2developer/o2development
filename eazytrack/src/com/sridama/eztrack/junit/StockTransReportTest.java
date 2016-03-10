/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sridama.eztrack.bo.Report;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.eztrack.bo.StockTransReport;
import com.sridama.eztrack.bo.StockTransferReport;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class StockTransReportTest {

	/**
	 * Test method for {@link com.sridama.eztrack.bo.Report#generateReport(com.sridama.txngw.core.RequestResponse)}.
	 */
	@Test
	public void testGenerateReport() {

		RequestResponse req = new RequestResponse("{}");
	req.setParam("opcode", "1");
	Report rpt = new StockTransReport(new SessionManager("session_id"," name", 1));
	RequestResponse res =  rpt.generateReport(req);
	System.out.println(res.getParam("request"));
	assertTrue("Response json should not ne null", res.getParam("request")!=null);
	}

}
