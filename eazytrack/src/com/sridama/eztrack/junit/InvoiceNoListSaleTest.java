package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sridama.eztrack.bo.InvoiceNoListSale;
import com.sridama.eztrack.bo.Report;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

public class InvoiceNoListSaleTest {

	@Test
	public void testGenerateReport() {
		RequestResponse req = new RequestResponse("{}");
		req.setParam("opcode", "4");
		Report rpt = new InvoiceNoListSale(new SessionManager("session_id","admin", 4)) ;
		RequestResponse res = rpt.generateReport(req) ;
		String json = res.getParam("request") ;
		System.out.println(json);
		assertTrue("response cannot be null",res.getParam("request")!=null);
		
	}

}
