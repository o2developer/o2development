package com.sridama.eztrack.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.sridama.txngw.core.RequestResponse;

public class Utils {

	 static HashMap< Integer, String>  map ;
	public static java.sql.Timestamp getCurrentJavaSqlTimestamp() {
		java.util.Date date = new java.util.Date();
		return new java.sql.Timestamp(date.getTime());
	}

	public static int brCode = 0;

	/**
	 * Handy utility function to generate a common json success response.
	 * 
	 * @return
	 */
	private static JSONObject successJson = null;

	public static synchronized String getSuccessResponse() {

		if (successJson == null) {
			successJson = new JSONObject();
			successJson.put("result", 0);
			successJson.put("desc", "success");
		}
		return successJson.toJSONString();
	}

	public static String getInvalidSessionResponse() {

		return "{ \"result\" :  \"1\",  \"desc\": \"Invalid session\" } ";
		//return null;
	}
	
	public static String getInvalidLicenseResponse() {

		return "{ \"result\" :  \"1\",  \"desc\": \"License Expired !\" } ";
		//return null;
	}

	
	/*
	 * Gives the current date time value in the mysql format
	 */
	public static String getCurrentDateTime()  {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss");
        java.util.Date date = new java.util.Date();
        return new java.sql.Timestamp(date.getTime()).toString();
	}
	
	/*
	 * checks if entry already exists in the database
	 */
	public static boolean checkIfEntryExists(String sql) {
	
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();

			rs = stmt.executeQuery(sql);
			if (rs.isBeforeFirst()) {
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				//if (con != null)
					//con.close();
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return false;
	}
	
	
	/*
	 * Gives the latest insert id of the perticular connection
	 */
	public static int getRecentId(Connection con) {
	
		String sql = "select last_insert_id()";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();

			rs = stmt.executeQuery(sql);
			if (rs.isBeforeFirst()) {
				rs.next();
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				//if (con != null) con.close();
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return -1;
	}
	
	/**
	 * Internal helper method that wraps a given object within a response
	 * object.
	 */
 public static	RequestResponse createResponse( JSONObject o ) {
		return new RequestResponse(o.toJSONString());
	}
 
 
	/**
	 * Utility method to send response to browser.
	 * @param response
	 * @param successResponse
	 */
	public static void sendResponse(HttpServletResponse response, String data) {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		PrintWriter out;
		try {
			out = response.getWriter();
			out.print(data);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getBranchName(int branch) {
		String[] branches = {"Main", "DSL", "GSL", "MSL"};
       
//		if (map == null) {
//		map   =  new HashMap<Integer, String>();
//       
//       map.put(1, "Main");
//       map.put(2, "DSL");
//       map.put(3, "GSL");
//       map.put(4, "MSL");
//       
//		}
       
 		//return map.get(branch);
		return (branch >= 0 && branch < branches.length)?branches[branch]:null;
	}

	public static boolean isNumeric(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch(NumberFormatException nfe) {
			return false;
		}
		
	}
	
	/*
	 *  to check the date is valid
	 */
	public static boolean isThisDateValid ( String dateToValidate , String dateFormat ) {
		
		if (dateToValidate == null ) {
			return false ;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		sdf.setLenient(false);
		
		try {
			     //if not valid, it will throw ParseException
			 Date date = sdf.parse(dateToValidate);
			 //System.out.println(date);

		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}

