/**
 * 
 */
package com.sridama.eztrack.bo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.eztrack.utils.Utils;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author admin
 *
 */
public class User {
	private final static Logger LOGGER = Logger.getLogger(User.class);
	private String loginId ;
	private String name ;
	private String password ;
	private int brCode ;
	
	private SessionManager session;
	

	public User(String loginId, String password, int brCode) {
		this.loginId = loginId;
		this.password = password;
		this.brCode = brCode;
	}
	
	
	public String getName() {
		return loginId;
	}

	public int getBrCode() {
		return brCode;
	}


	public User(String userJson) throws ParseException   {
		
		JSONParser parse = new JSONParser();
		JSONObject obj = (JSONObject) parse.parse(userJson);
		LOGGER.debug("User JSON in User "+userJson);
		this.loginId = obj.get("user").toString();
		this.password = obj.get("password").toString();
		this.brCode = Integer.parseInt(obj.get("branch_code").toString());
		
	}
	
	public User (JSONObject o) {
		this.loginId =	o.get("name").toString();
		this.password = o.get("password").toString();
		this.brCode = Integer.parseInt(o.get("branch_code").toString());
	}
	
	
	public User(RequestResponse req, SessionManager session) {
		this.session = session ;
		
		if (req.getParam("name")!=null)
			this.name = req.getParam("name") ;
		if (req.getParam("login_id")!=null)
			this.loginId  = req.getParam("login_id");
		if (req.getParam("password")!=null)
			this.password = req.getParam("password");
		if (req.getParam("branch")!=null)
			this.brCode = Integer.parseInt(req.getParam("branch"));
	}
	
	/*
	 * Authenticates the user and gives the response
	 */
	public boolean authenticate() {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from user_master where login_id = '");
		sb.append(loginId+"' and password='"+password+"' and");
		sb.append(" branch_id="+brCode );
		
	    return Utils.checkIfEntryExists(sb.toString());
	}
	
	/*
	 *   Save the user
	 */
	public RequestResponse save() {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into user_master ( login_id ,user_name, password ");
		sb.append(", branch_id,modified_by,modified_on) ");
		sb.append(" values ('"+loginId+"','"+name+"','"+password+"',");
		sb.append(brCode+",'"+session.getName()+":"+session.getBrCode()+"','"+Utils.getCurrentDateTime()+"')");
		System.out.println(sb.toString());
		
		JSONObject res = new JSONObject();
		
		StringBuilder sb1 = new StringBuilder();
		sb1.append("select * from user_master where login_id = '");
		sb1.append(loginId+"' and branch_id="+brCode );
		
	    if ( Utils.checkIfEntryExists(sb1.toString())) {
	    	 res.put("result", -1);
			 res.put("desc", "User Exists Already !");
			 return  createResponse(res);
	    }
		
		Connection con = null;
		Statement stmt = null;
		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement() ;
			stmt.execute(sb.toString());
			
		  	
			 res.put("result", 0);
			 res.put("desc", "User Created Successfully !");
					
			return createResponse(res);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (con != null)con.close();
				if (stmt != null)stmt.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	/*
	 *   Updates the user
	 */
	public RequestResponse update() {
		StringBuilder sb = new StringBuilder();
		sb.append("update user_master set login_id='"+loginId+"',user_name='"+name+"'");
		sb.append(",password='"+password+"',modified_by='"+session.getName()+"',");
		sb.append("modified_on='"+Utils.getCurrentDateTime()+"'");
		sb.append(" where login_id='"+loginId+"' and branch_id="+brCode);
		System.out.println(sb.toString());
		Connection con = null;
		Statement stmt = null;
		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement() ;
			stmt.executeUpdate(sb.toString());
			
		 	JSONObject res = new JSONObject();
			 res.put("result", 0);
			 res.put("desc", "User Updated Successfully !");
					
			return createResponse(res);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (con != null)con.close();
				if (stmt != null)stmt.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	/*
	 *   Deletes the user
	 */
	public RequestResponse delete() {
		StringBuilder sb = new StringBuilder();
		sb.append("delete from user_master where login_id='"+loginId+"'");
		sb.append(" and branch_id="+brCode);
		System.out.println(sb.toString());
		Connection con = null;
		Statement stmt = null;
		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement() ;
			stmt.execute(sb.toString());
			
			JSONObject res = new JSONObject();
			 res.put("result", 0);
			 res.put("desc", "User Deleted Successfully !");
					
			return createResponse(res);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				//if (con != null)con.close();
				if (stmt != null)stmt.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Internal helper method that wraps a given object within a response
	 * object.
	 */
	public static RequestResponse createResponse( JSONObject o ) {
		return new RequestResponse(o.toJSONString());
	}
	
}
