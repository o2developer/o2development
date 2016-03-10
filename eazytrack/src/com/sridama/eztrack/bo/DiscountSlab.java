/**
 * 
 */
package com.sridama.eztrack.bo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;

import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.eztrack.utils.Utils;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 */
public class DiscountSlab {
	private SessionManager session;
	private int id ;
	private float discount ;
	
	/**
	 * initiates the variables
	 * @param sessionObj 
	 */
	public DiscountSlab(RequestResponse req, SessionManager sessionObj) {
		
		this.session = sessionObj ;
		
		if (req.getParam("disc")!=null && !req.getParam("disc").isEmpty())
			this.discount = Float.parseFloat(req.getParam("disc"));
		if (req.getParam("id")!=null && !req.getParam("id").isEmpty())
			this.id = Integer.parseInt(req.getParam("id"));
	}
	
	
	public DiscountSlab(Float discount) {
		this.discount = discount ;
	}

	public int getId() {
		return id;
	}
	
	
	public RequestResponse save()  {

		Connection con = null ;
		Statement stmt = null ;
		
		JSONObject obj = new JSONObject() ;
	
 // checking if this entry exists already in database 
		String sql = "select * from discount_master where disc="+discount;
		if ( Utils.checkIfEntryExists(sql)) {
			obj.put("result_code", "-1");
			obj.put("desc", "discount exists Already") ;
			return Utils.createResponse(obj) ;
		}
		StringBuilder sb = new StringBuilder() ;
		sb.append("insert into discount_master (disc) values ");
		sb.append("("+discount+")");
		try {
			con = JDBCHelper.getConnection() ;
			stmt = con.createStatement() ;
			stmt.execute(sb.toString()) ;
			
		this.id = Utils.getRecentId(con) ;
			
			obj.put("result_code", "0");
			obj.put("desc", "Discount created successfully") ;
			return Utils.createResponse(obj) ;
			
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

	private RequestResponse createResponse(JSONObject o) {
		// TODO Auto-generated method stub
		return new RequestResponse(o.toJSONString());
	}
	public RequestResponse update() {
		StringBuilder sb = new StringBuilder();
		sb.append("update discount_master set id="+id+", disc="+discount+" where id="+id);
		
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
