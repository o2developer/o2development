package com.sridama.eztrack.rfidinteg;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;

import org.json.simple.parser.ParseException;
import org.junit.Test;

import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

public class ReadTaglistTest
{

	@Test
	public void test4Readtaglist() throws InterruptedException, IOException, ClassNotFoundException, SQLException, ParseException
	{
		final SessionManager ses = new SessionManager("afjkl", "admin", 1);
		final RequestResponse req = new RequestResponse("{}");
		final ReadTaglist wt = new ReadTaglist(req);
		wt.setSession(ses);
		final RequestResponse res = wt.readTag();
		final String json = res.getParam("request");
		System.out.println(json);
		assertTrue("report json should not be null", !res.equals(null) || !res.equals(""));

	}
}
