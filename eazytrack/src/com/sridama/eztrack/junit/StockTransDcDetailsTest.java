package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sridama.eztrack.bo.Report;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.eztrack.bo.StockTransDcDetails;
import com.sridama.eztrack.bo.StockTransReport;
import com.sridama.txngw.core.RequestResponse;

public class StockTransDcDetailsTest {

	@Test
	public void testGenerateReport() {


		RequestResponse req = new RequestResponse("{ \"to_branch\" : \"1\" ,\"dc_no\" : \"1\" , \"from_branch\" : \"4\"}");
	req.setParam("opcode", "1");
	Report rpt = new StockTransDcDetails(new SessionManager("session_id"," name", 1));
	RequestResponse res =  rpt.generateReport(req);
	System.out.println(res.getParam("request"));
	assertTrue("Response json should not ne null", res.getParam("request")!=null);
	
	}

}
