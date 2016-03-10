/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sridama.eztrack.bo.Report;
import com.sridama.eztrack.bo.SaleEditInvoiceDetails;
import com.sridama.eztrack.bo.SaleTxnDetails;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class SaleEditInvoiceDetailsTest {

	/**
	 * Test method for {@link com.sridama.eztrack.bo.Report#generateReport(com.sridama.txngw.core.RequestResponse)}.
	 */
	@Test
	public void testGenerateReport() {
	//	RequestResponse req = new RequestResponse("{\"branch\":\"1\",\"inv_no\":\"14\"}");
	//	RequestResponse req = new RequestResponse("{\"inv_no\":14,\"branch\":\"1\",\"txn_type\":\"sale\"}");
		RequestResponse req = new RequestResponse("{\"inv_no\":168,\"branch\":\"1\",\"txn_type\":\"sale\"}");
		req.setParam("opcode", "4");
		Report rpt = new SaleEditInvoiceDetails(new SessionManager("session_id","admin", 1)) ;
		RequestResponse res = rpt.generateReport(req) ;
		String json = res.getParam("request") ;
		System.out.println(json);
		assertTrue("response cannot be null", res.getParam("request")!=null);
	}

}
