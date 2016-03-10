package com.sridama.eztrack.bo ;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sridama.eztrack.controller.EzTrackServlet;
import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.eztrack.utils.Utils;

public class SessionManager {
	
	private final static Logger LOGGER = Logger.getLogger(SessionManager.class .getName()); 
	private String session_id = null ;
	private String name = null ;
	private  int brCode = -1 ;
	
	//private Connection con = null ;
	//private Statement stmt = null ;
	//private ResultSet rs = null ;
	
//	private User user ;

	Cookie[] cookies = null ;
	private boolean cookieResult = false ;
	
	
	public SessionManager(User user) {
		this.name = user.getName() ;
		this.brCode = user.getBrCode() ;
	}
	
	//String session_id , String name , int brCode
	
	public int getBrCode() {
		return brCode;
	}
	
	public String getName() {
		return name;
	}

	public SessionManager(String session_id , String name , int brCode1) {
		this.session_id = session_id ;
		this.name = name ;
		this.brCode = brCode1 ;
		Utils.brCode = brCode1 ;
	}
	
	
	public  SessionManager(HttpServletRequest request) {
		 
		cookies = request.getCookies();

		if ( cookies != null) {
			for (Cookie c: cookies) {	
				LOGGER.debug(c.getName() + ":" + c.getValue());
			
				if (c.getName().equals("name")) {
					this.name = c.getValue();
				} else if (c.getName().equals("session_id")) {
					this.session_id = c.getValue();
				} else if (c.getName().equals("brCode")) {
					this.brCode = Integer.parseInt(c.getValue());
				}
			}
			cookieResult = true ;
		}else {
			cookieResult = false ;
		}
		
	}
	

	public HttpServletResponse getCookieResponse (HttpServletResponse response , String type) {
		
		if (type.equals("validate")) {
		Cookie uname = new Cookie("name", name);
		Cookie jsession_id = new Cookie("session_id", session_id);
		Cookie branch_id = new Cookie("brCode", ""+brCode);
		response.addCookie(uname);
		response.addCookie(jsession_id);
		response.addCookie(branch_id) ;
		} else {
			Cookie uname = new Cookie("name", null);
			uname.setMaxAge(0);
			Cookie jsession_id = new Cookie("session_id", null);
			jsession_id.setMaxAge(0);
			Cookie branch_id = new Cookie("brCode", null);
			branch_id.setMaxAge(0);
			response.addCookie(uname);
			response.addCookie(jsession_id);
			response.addCookie(branch_id) ;	
		}
	
		return response ;
	}
	
	public boolean cookieResult() {
		return cookieResult ;
	}
	
	
	
	
	public String isSessionExistsAlready() {
		String sql = "select * from session_master where  login_id='"+name+"' AND branch_id="+brCode ;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			if( rs.isFirst() )
				return rs.getString("jsession_id");
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
				try {
					if(rs!=null)rs.close() ;
					if (stmt!=null)stmt.close();
					//if (con!=null)con.close() ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return null ;
	}
	
	public String createSession() {
		// IF session exists already for user update session
		Connection con = null;
		Statement stmt = null;
		//ResultSet rs = null;
		
		String sessionId =  isSessionExistsAlready();
		if ( sessionId!=null )  {
			this.session_id = sessionId ;
			LOGGER.debug("Invalidating the existing session with session id "+sessionId);
			invalidateSession();
		}
		
		// TODO Auto-generated method stub
		String jsessionId = UUID.randomUUID().toString();
		LOGGER.debug("Creating New Session session id  : user : branch "+sessionId +" : "+name+" : "+brCode);
		StringBuilder sb = new StringBuilder();
		sb.append("insert into session_master values (");
		sb.append("'" + name + "'");
		sb.append(" ,"+ brCode );
		sb.append(", '"+jsessionId+"'");
		sb.append(", '" +Utils.getCurrentJavaSqlTimestamp() + "')"); 
		this.session_id = jsessionId ;
		
		try {
		con = JDBCHelper.getConnection();
		stmt = con.createStatement() ;
		stmt.execute( sb.toString() );
		
		} catch (Exception e) {
			e.printStackTrace() ;
		} finally {
			try {
			//if (rs!=null) rs.close();
			if (stmt != null) stmt.close();
//			if (con != null) con.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
			} catch (SQLException e) {	}
		}
		
		return jsessionId;
	}
	
	
	/*
	 * Checking for valid session
	 */

	public boolean checkSession() {
		String sql = "select * from session_master where jsession_id = '"
				+ session_id + "'";
		LOGGER.info("Query  :" + sql);
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.isBeforeFirst() == false ) {    //   Removed the negation condition
				LOGGER.info("Entry Doesnot Exists in the database rs.isBeforeFirst()  "+rs.isBeforeFirst());
				LOGGER.info("Entry Doesnot Exists in the database  ");
				return false;
			}
		

			if (!sessionTimeValidate()) {
				LOGGER.info("Session Time vaidation Failed");
				invalidateSession();
				return false;
			}
			// Update the last access time.
			sql = "UPDATE session_master SET la_time = '"
					+ Utils.getCurrentJavaSqlTimestamp() + "'"
					+ "where jsession_id ='" + session_id + "'";
			stmt.executeUpdate(sql);
			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}
	
	
	public boolean invalidateSession() {

		Connection con = null;
		Statement stmt = null;

		String sql = "DELETE from session_master  where jsession_id = '"
				+ session_id + "'";
		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			stmt.execute(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				// if (con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}	
	

	/*
	 * This is for session validation
	 */
	public boolean sessionTimeValidate() {
		String laTime = "";

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		String sqlQuery = "select  la_time  from session_master where jsession_id='"
				+ session_id + "'";

		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			while (rs.next()) {
				laTime = rs.getString("la_time");
			}

			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-DD hh:mm:ss");

			String currentTime = "";
			java.util.Date date = new java.util.Date();
			currentTime = new java.sql.Timestamp(date.getTime()).toString();
			Date d1 = null;
			Date d2 = null;
			d1 = format.parse(laTime);
			d2 = format.parse(currentTime);
			LOGGER.info("Session Time  Difference between last Access"
					+ ((d2.getTime()) - (d1.getTime())));
			LOGGER.info("Session Time out duration "
					+ (JDBCHelper.SESSION_TIME_OUT * 60 * 1000));
			if (((d2.getTime()) - (d1.getTime())) <= (JDBCHelper.SESSION_TIME_OUT * 60 * 1000))
				return true;

		} catch (Exception e) {
			//System.out.println(e);
			e.printStackTrace();
			//return false;
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				// if (con != null) con.close();
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
