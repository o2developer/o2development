/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sridama.eztrack.bo.InvoiceNoPurchase;
import com.sridama.eztrack.bo.PurchasesReport;
import com.sridama.eztrack.bo.Report;
import com.sridama.eztrack.bo.SaleReport;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class PurchaseInvoiceTest {

	/**
	 * Test method for {@link com.sridama.eztrack.bo.Report#generateReport(com.sridama.txngw.core.RequestResponse)}.
	 */
	@Test
	public void testGenerateReport() {
		RequestResponse req = new RequestResponse("	{\"cust_id\":\"50\"}");
		SessionManager ses = new SessionManager("a93c665f-9t8a-42f2-89a7-898e663c94e7","admin",1);
		InvoiceNoPurchase rpt = new InvoiceNoPurchase(ses) ;
		RequestResponse res = rpt.generateReport(req);
		System.out.println(res.getParam("request"));
		assertTrue("Respone is not null ", res.getParam("request")!=null);
		}
}
