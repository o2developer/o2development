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
 *
 */
public class TaxSlab {

	private SessionManager session;
	private float rate ;
	private int id ;
	private String desc ;
	
	/*
	 * Updates the local variables
	 */
	public TaxSlab(RequestResponse req, SessionManager sessionObj) {
		
		this.session = sessionObj ;
		if (req.getParam("id")!=null && !req.getParam("id").isEmpty())
             this.id = Integer.parseInt(req.getParam("id"));
		if (req.getParam("rate")!=null && !req.getParam("rate").isEmpty())
	         this.rate = Float.parseFloat(req.getParam("rate"));
		if (req.getParam("desc")!=null )
	         this.desc = req.getParam("desc");
	}

	
	
	public int getId() {
		return id;
	}



	/**
	 * Updates the local variables
	 */
	public TaxSlab(float rate) {
         this.rate = rate ;
	}
	
	/*
	 * saves the new tax slab to database
	 */
	public RequestResponse save() {
		Connection con = null ;
		Statement stmt = null ;
		
		JSONObject obj = new JSONObject() ;
	
 // checking if this entry exists already in database 
		String sql = "select * from tax_master where rate="+rate;
		if ( Utils.checkIfEntryExists(sql)) {
			obj.put("result_code", "-1");
			obj.put("desc", "rate exists Already") ;
			return Utils.createResponse(obj) ;
		}
		StringBuilder sb = new StringBuilder() ;
		sb.append("insert into tax_master (rate , description) values ");
		sb.append("('"+rate+"','vat@"+rate+"')");
		
		try {
			con = JDBCHelper.getConnection() ;
			stmt = con.createStatement() ;
			stmt.execute(sb.toString()) ;
			
		this.id = Utils.getRecentId(con) ;
			
			obj.put("result_code", "0");
			obj.put("desc", "tax rate created successfully") ;
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
		sb.append("update tax_master set rate="+rate+", description='"+desc+"' where id="+id);
		
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
					System.out.println("response here is"+res);
			return createResponse(res);
			
		} catch(Exception e){
			e.printStackTrace();
			System.out.println("------------------------------------------------------------------------------");
			return null;
		}
		
	}



	

}
