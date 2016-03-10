/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sridama.eztrack.bo.CustomersReport;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author admin
 *
 */
public class CustomerReportsTest {

	@Test
	public final void testCustomerReports4CustomerList() {
		RequestResponse req = new RequestResponse("{}");
		req.setParam("opcode", "4");
		req.setParam("type_ahead", "delhi");
			CustomersReport cust = new CustomersReport(new SessionManager("session_id"," name", 1));   //brCode1
			RequestResponse res = cust.generateReport(req);
			String json = res.getParam("request") ;
			System.out.println(json);
			assertTrue("report json should not be null", !json.equals(null)||!json.equals(""));
			
	}

}
