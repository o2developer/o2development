/**
 * 
 */
package com.sridama.eztrack.utils;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

/**
 * @author admin
 *
 */
public class JDBCHelperTest {

	@Test
	public final void testGetConnection() {
		try {
			Connection con = JDBCHelper.getConnection();
			assertTrue("Connection should not be null ", con!=null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	/*
	 * to test error code updation to hash map
	 */
	public final void testUpdateErrorCodeMap() {/*
		try {
	//		EzTrackErrorCodes.loadMap();
			
			
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	*/}
	
	@Test
	/*
	 * to test error code updation to hash map
	 */
	public final void test4GetErrorCodeMapEntry() {/*
	String value =	EzTrackErrorCodes.getErrorCode(0);
	System.out.println(value);
	assertTrue("value should not be null", !value.equals("") );
		
	*/}
	

}
