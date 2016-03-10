/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sridama.eztrack.bo.Report;
import com.sridama.eztrack.bo.SaleReport;
import com.sridama.eztrack.bo.SaleTxnDetails;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class SaleTxnDetailsTest {

	@Test
	public void test4SaleTxnDetails() {
		RequestResponse req = new RequestResponse("{\"txn_type\":\"purchase\",\"branch\":\"1\",\"inv_no\":\"9\"}");
		req.setParam("opcode", "4");
		Report rpt = new SaleTxnDetails(new SessionManager("session_id","admin", 1)) ;
		RequestResponse res = rpt.generateReport(req) ;
		String json = res.getParam("request") ;
		System.out.println(json);
		assertTrue("response cannot be null",res.getParam("request")!=null);
	}

}
