/**
 * 
 */
package com.sridama.eztrack.bo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.eztrack.utils.Utils;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 */
public class Units {
	private SessionManager session;
	private int id ;
	private String name ;
	private String desc ;
	/**
	 *  Updates the class variables with incoming request variables 
	 */

	public Units(RequestResponse req, SessionManager sessionObj) {
		this.session = sessionObj ;
		if (req.getParam("id")!=null && !req.getParam("id").isEmpty())
			this.id = Integer.parseInt(req.getParam("id")) ;
		if (req.getParam("name")!=null)
			this.name = req.getParam("name") ;
		if (req.getParam("description")!=null)
			this.desc = req.getParam("description") ;
	}
	
	/*
	 * Returns recently created id  , for item creation 
	 */
	public int getId() {
		return id;
	}

	/*
	 * new unit will be created while creating the item
	 */
	public Units(String name ) {
		this.name = name ;
	}

	/*
	 * saves the units to database
	 */
	public RequestResponse save ()   {
		Connection con = null ;
		Statement stmt = null ;
		
		JSONObject obj = new JSONObject() ;
		if (checkIfUnitExists()) {
			obj.put("result_code", "-1");
			obj.put("desc", "unit exists Already") ;
			return createResponse(obj) ;
		}
		StringBuilder sb = new StringBuilder() ;
		sb.append("insert into unit_master (name , description) values ");
		sb.append("('"+name+"','"+desc+"')");
		try {
			con = JDBCHelper.getConnection() ;
			stmt = con.createStatement() ;
			stmt.execute(sb.toString()) ;
			
			getRecentId(con) ;
			
			obj.put("result_code", "0");
			obj.put("desc", "unit created successfully") ;
			return createResponse(obj) ;
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace() ;
		}catch (IOException e) {
			e.printStackTrace() ;
		}catch (SQLException e) {
			e.printStackTrace() ;
		} finally {
			try {
				//if (con!=null) con.close() ;
				if (stmt!=null) stmt.close() ;
			}catch (SQLException e) {
				e.printStackTrace() ;
			}
		}
		return null ;
	}

	
	/*
	 * sets the id to the latest inserted id  
	 */
	private void getRecentId(Connection con) {
		Statement stmt = null ;
		ResultSet rs  = null ;
		try {
			con = JDBCHelper.getConnection() ;
			stmt = con.createStatement() ;
			
			String sql = "select last_insert_id() from unit_master";
			rs = stmt.executeQuery(sql) ;
			if (rs.isBeforeFirst()) {
				rs.next();
				this.id = rs.getInt(1);
			}
			
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace() ;
		}catch (IOException e) {
			e.printStackTrace() ;
		}catch (SQLException e) {
			e.printStackTrace() ;
		} finally {
			try {
				//if (con!=null) con.close() ;
				if (rs != null) rs.close();
				if (stmt!=null) stmt.close() ;
			}catch (SQLException e) {
				e.printStackTrace() ;
			}
		}
		
	}


	private boolean checkIfUnitExists() {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from unit_master where name='" + name + "'");

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();

			rs = stmt.executeQuery(sb.toString());
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
				// if (con != null) con.close();
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}
	
	/*
	 * deletes the unit and puts items having this
	 * units under the no unit field which is 0
	 */
	public RequestResponse delete() {

		String sql = "delete from unit_master where id="+id;
		
		StringBuilder sb = new StringBuilder() ;
		sb.append("update item_master set units=0 where ");
		sb.append("units="+id);

		JSONObject obj = new JSONObject() ;

		Connection con = null;
		Statement stmt = null;
		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			stmt.executeUpdate(sb.toString()) ;
			stmt.execute(sql);
			
			obj.put("result_code", "0");
			obj.put("desc", "Unit Deleted Successfully") ;
			return createResponse(obj);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				//if (con != null) con.close();
				if (stmt != null) stmt.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null ;
	}
	
	/**
	 * Internal helper method that wraps a given object within a response
	 * object.
	 */
	private RequestResponse createResponse( JSONObject o ) {
		return new RequestResponse(o.toJSONString());
	}

	public RequestResponse update() {
		StringBuilder sb = new StringBuilder();
		sb.append("update unit_master set name='"+name+"',description='"+desc+"',id="+id+" where name='"+name+"'");
		
		System.out.println("sdfsdfsd"+sb.toString());
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
}
