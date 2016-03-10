/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sridama.eztrack.bo.Report;
import com.sridama.eztrack.bo.SaleReport;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.eztrack.xl.ExportReportSale;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class ExportReportXlTest {

	/**
	 *  Test method for {@link com.sridama.eztrack.xl.ExportReportSale#generateXl(com.sridama.txngw.core.RequestResponse)}.
	 */
	@Test
	public void testGenerateXl() {
		//\"fdate\":\"01/02/2014\",\"tdate\":\"04/02/2014\",
	RequestResponse req = new RequestResponse("{\"cust_id\":\"\",\"cust_name\":\"\",\"inv_from\":\"\",\"inv_to\":\"\",\"branch\":-1}");
	req.setParam("opcode", "4");
	Report rpt = new SaleReport(new SessionManager("session_id","admin", 1)) ;
	ExportReportSale exp =  new ExportReportSale(new SessionManager("session_id","admin", 1));
	RequestResponse res = exp.generateXl(req); ;
	String json = res.getParam("request") ;
	System.out.println(json);
	assertTrue("response cannot be null",res.getParam("request")!=null);
	
	}

}
