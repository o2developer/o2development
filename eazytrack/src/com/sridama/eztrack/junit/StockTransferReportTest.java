/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sridama.eztrack.bo.Report;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.eztrack.bo.StockTransferReport;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author admin
 *
 */
public class StockTransferReportTest {

	/**
	 * Test method for {@link com.sridama.eztrack.bo.StockTransferReport1#getReport()}.
	 */
	@Test
	public final void testGetReport4AllTransfers() {
		RequestResponse req = new RequestResponse("{}");
	req.setParam("opcode", "1");
	Report rpt = new StockTransferReport(new SessionManager("session_id"," name", 3));
	RequestResponse res =  rpt.generateReport(req);
	System.out.println(res.getParam("request"));
	assertTrue("Response json should not ne null", res.getParam("request")!=null);}

}
