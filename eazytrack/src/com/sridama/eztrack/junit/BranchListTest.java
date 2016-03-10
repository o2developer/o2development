/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sridama.eztrack.bo.BranchList;
import com.sridama.eztrack.bo.DiscountSlabList;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class BranchListTest {

	@Test
	public void test() {
		RequestResponse req = new RequestResponse("{}");
		req.setParam("opcode", "4");
		BranchList report = new BranchList(new SessionManager("session_id"," name", 1)) ;
		RequestResponse res = report.generateReport(req);
		String json = res.getParam("request") ;
		System.out.println(json);
		assertTrue("response cannot be null",res.getParam("request")!=null);
	}

}
