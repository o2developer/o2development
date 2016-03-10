package com.sridama.eztrack.junit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.sridama.eztrack.bo.SessionManager;

public class SessionManagerTest {

	@Test
	public final void testSessionManager4CreateSession() {
		SessionManager ses = new SessionManager("a93c665f-9c8a-42f2-89a7-898fd63b94ef","admin",1);
		
		String sid =	ses.createSession();
		  assertTrue( "Session id is not null", sid!=null) ;
	}

	@Test
	public final void testSessionManager4checkingTheSession() {
		SessionManager ses = new SessionManager("a93c665f-9c8a-42f2-89a7-898fd63b94ef","admin",1);
		
		  boolean sid =	ses.checkSession();
		  assertTrue( "Session Exists", sid==true) ;
	}

	@Test
	public final void testSessionManager4InvalidateSession() {
		SessionManager ses = new SessionManager("f53b25d0-c575-4cd2-9025-be60f3ec6e35","admin",1);
		
		  boolean sid =	ses.invalidateSession();
		  assertTrue( "Session deleted", sid==true) ;
	}

	@Test
	public final void testCookieResult() {
		fail("Not yet implemented"); // TODO
	}


}
