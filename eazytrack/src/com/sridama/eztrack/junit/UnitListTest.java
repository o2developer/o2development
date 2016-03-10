/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sridama.eztrack.bo.SessionManager;
import com.sridama.eztrack.bo.TaxSlabList;
import com.sridama.eztrack.bo.UnitsList;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class UnitListTest {

	/**
	 *  Test for units list , used while creating the item 
	 */
	@Test
	public void testGenerateReport() {
		RequestResponse req = new RequestResponse("{}");
		req.setParam("opcode", "4");
		UnitsList report = new UnitsList(new SessionManager("session_id"," name", 1)) ;
		RequestResponse res = report.generateReport(req);
		String json = res.getParam("request") ;
		System.out.println(json);
		assertTrue("response cannot be null",res.getParam("request")!=null);
				
	}

}
