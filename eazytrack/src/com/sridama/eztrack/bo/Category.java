/**
 * 
 */
package com.sridama.eztrack.bo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;

import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class Category {

	private int id ;
	private String name ;
	private String desc ;
	private int parentId ;
	
	/**
	 *  Update the variables 
	 */
	public Category(RequestResponse req) {
		if (req.getParam("id")!=null && !req.getParam("id").isEmpty())
			this.id = Integer.parseInt(req.getParam("id"));
		if (req.getParam("name")!=null)
			this.name = req.getParam("name");
		if (req.getParam("desc")!=null)
			this.desc = req.getParam("desc");
		if (req.getParam("parent_id")!=null && !req.getParam("parent_id").isEmpty())
			this.parentId = Integer.parseInt(req.getParam("parent_id"));
	}
	
	/*
	 * creates the new category
	 */
	public RequestResponse save()  {
		Connection con = null ;
		Statement stmt = null ;
		
		JSONObject obj = new JSONObject() ;
		if (checkIfCategoryExists()) {
			obj.put("result_code", "-1");
			obj.put("desc", "category  exists Already") ;
			return createResponse(obj) ;
		}
		StringBuilder sb = new StringBuilder() ;
		sb.append("insert into category_master (cat_name , description , parent_id ) values ");
		sb.append("('"+name+"','"+desc+"',"+parentId+")");
		try {
			con = JDBCHelper.getConnection() ;
			stmt = con.createStatement() ;
			stmt.execute(sb.toString()) ;
			
			obj.put("result_code", "0");
			obj.put("desc", "category created successfully") ;
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
	 * checks if category already exists
	 */
	private boolean checkIfCategoryExists() {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from category_master where cat_name='"+name+"'" );

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
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	
	/*
	 * deletes the category, and will update all the items with 
	 * this category id to the root which is 0 in category master
	 */
	public  RequestResponse delete() {
 //TODO on delete of the  category 
		String sql = "delete from client_master where category_id="+id ;
		
		StringBuilder sb = new StringBuilder();
		sb.append("update item_master set category=0 where category="+id);

		JSONObject obj = new JSONObject() ;

		Connection con = null;
		Statement stmt = null;
		try {
			con = JDBCHelper.getConnection();
			con.setAutoCommit(false) ;
			stmt = con.createStatement();
			
			stmt.executeUpdate(sb.toString());
			
			stmt.execute(sql);
			
			con.commit() ;  // commits when no exception is observed 
			obj.put("result_code", "0");
			obj.put("desc", "Category Deleted Successfully") ;
			return createResponse(obj);
			
		} catch (SQLException e) {
			e.printStackTrace();
			try {
			if (con != null) con.rollback() ;
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (stmt != null) stmt.close();
				
			} catch (SQLException e) {
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
}
