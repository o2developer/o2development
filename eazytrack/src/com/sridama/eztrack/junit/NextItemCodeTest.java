/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sridama.eztrack.bo.NextItemCode;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class NextItemCodeTest {

	/**
	 * Test method for {@link com.sridama.eztrack.bo.Report#generateReport(com.sridama.txngw.core.RequestResponse)}.
	 */
	@Test
	public void testGenerateReport() {
		SessionManager session = new SessionManager("xyz", "admin", 1);
		NextItemCode code = new NextItemCode(session);
		RequestResponse req = new RequestResponse("data={}");
		RequestResponse res = code.generateReport(req) ;
		System.out.println(res.getParam("request"));
		assertTrue("Response is not null ??",res.getParam("request")!=null);
	}
}
