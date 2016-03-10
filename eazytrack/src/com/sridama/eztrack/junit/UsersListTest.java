/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sridama.eztrack.bo.Report;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.eztrack.bo.UsersList;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class UsersListTest {

	@Test
	public void test4UsersList() {
		RequestResponse req = new RequestResponse("{}");
		SessionManager ses = new SessionManager("sesid", "admin", 1) ;
		Report usrrpt =  new UsersList(ses);
		RequestResponse res = usrrpt.generateReport(req); 
		System.out.println(res.getParam("request"));
		assertTrue("Response cannot be null",res.getParam("request")!=null);
	}

}
