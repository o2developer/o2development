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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class Customer {

	private String name  = null ;
	private String phone = null ;   // equals phone1 in db
	private int custId =  -1 ;
	private String custType ;
	
	private String address ;
	private String phone2  ;
	private String email ;
	private String	tin ;
	private String cst_no  ;
	private String state  ; 
	
	public void setCustType(String custType) {
		this.custType = custType;
	}

	public Customer (String json) {
		JSONParser parse = new JSONParser() ;
		try {
			//System.out.println("json in customer class "+json);
			JSONObject obj = (JSONObject) parse.parse(json) ;
			this.name = obj.get("name").toString() ;
			this.phone = obj.get("phone").toString() ;
			this.custId = Integer.parseInt(obj.get("cust_id").toString()) ;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}  
	
	
	public Customer (RequestResponse req) {
	
	if (req.getParam("cust_id")!=null)
		this.custId = Integer.parseInt(req.getParam("cust_id"));
	if (req.getParam("name")!=null)
		this.name =  req.getParam("name") ;
	if (req.getParam("phone")!=null)
		this.phone = req.getParam("phone");
	if (req.getParam("cust_type")!=null)
		this.custType = req.getParam("cust_type") ;
	if (req.getParam("address")!=null)
		this.address = req.getParam("address") ;
	if (req.getParam("phone2")!=null)
		this.phone2 = req.getParam("phone2") ;
	if (req.getParam("email")!=null)
		this.email = req.getParam("email") ;
	if (req.getParam("tin")!=null)
		this.tin = req.getParam("tin") ;
	if (req.getParam("cst_no")!=null)
		this.cst_no = req.getParam("cst_no") ;
	if (req.getParam("state")!=null)
		this.state = req .getParam("state");
	}
	
	public String getPhone() {
		return phone;
	}


	public int getCustId() {
		return custId;
	}


	/*
	 * Saving to the database if customers phone number is present
	 */
	public void save(Connection con) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into client_master (name , phone1,cust_type) values ('");
		sb.append( name + "','" + phone + "','"+custType+"')");
		// TODO close stmt
		Statement stmt = null;
		ResultSet rs = null;

		stmt = con.createStatement();
		stmt.execute(sb.toString());

		rs = stmt.executeQuery("select max(cust_id) from client_master");
		while (rs.next()) {
			custId = rs.getInt(1);
		}
	}
	
	/*
	 * Saving to the database if customers phone number is present
	 */
	public RequestResponse save() throws IOException  {
		
		JSONObject obj = new JSONObject() ;
		if (checkIfCustomerExists()) {
			obj.put("result_code", "-1");
			obj.put("desc", "customer exists Already") ;
			
			return createResponse(obj) ;
			
		}
		StringBuilder sb = new StringBuilder();
		sb.append("insert into client_master (cust_id ,name ,address, phone1,phone2,email,cust_type,tin,cst_no,state) values (default,'");
		sb.append( name + "','" + address + "','"+phone+"','"+phone2+"','"+email+"','"+custType+"','"+tin+"','"+cst_no+"','"+state+"')");
		

		Connection con = null ;
		Statement stmt = null;
		try {
	   con = JDBCHelper.getConnection();
		

		stmt = con.createStatement();
		
		stmt.execute(sb.toString());
		obj.put("result_code", "0");
		obj.put("desc", "Customer created successfully ") ;
		return createResponse(obj) ;
		
		
		}catch(SQLException e) {
			e.printStackTrace() ;
		}catch(ClassNotFoundException e ) {
			e.printStackTrace() ;
		}catch (IOException e) {
			e.printStackTrace() ;
		} finally {
			
				try {
					//if (con!=null) con.close();
					if (stmt!=null) stmt.close() ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	    return null ;
	}
	
	
	private boolean checkIfCustomerExists() {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from client_master where name='" + name );
		sb.append("' and phone1='" + phone + "'");

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
//				if (con != null) con.close();
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}
	
	
	/*
	 * deletes the customer from the client_master
	 */
	public  RequestResponse delete() {

		String sql = "delete from client_master where cust_id="+custId;

		JSONObject obj = new JSONObject() ;

		Connection con = null;
		Statement stmt = null;
		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			stmt.execute(sql);
			
			obj.put("result_code", "0");
			obj.put("desc", "Entry Deleted Successfully") ;
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
				e.printStackTrace();
			}
		}
		return null ;
	}
	
	
	/*
	 * updates the customer in the database 
	 */
	
	public RequestResponse  update()  {
		
		StringBuilder sb = new StringBuilder();
		sb.append("update client_master set name='"+name+"' ,");
		sb.append("address='"+address+"', ");
		sb.append("phone1='"+phone+"', ");
		sb.append("phone2='"+phone2+"', ");
		sb.append("email='"+email+"', ");
		sb.append("cust_type='"+custType+"', ");
		sb.append("tin='"+tin+"', ");
		sb.append("cst_no='"+cst_no+"', ");
		sb.append("state='"+state+"' ");
		sb.append("where  cust_id="+custId );
		
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			stmt.executeUpdate(sb.toString());
			
			JSONObject obj = new JSONObject();
			obj.put("result_code", "o");
			obj.put("desc", "Unit created Successfully") ;
			
			return createResponse(obj) ;

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				//if (con != null) con.close();
				if (rs != null) rs.close();
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
	private RequestResponse createResponse(JSONObject o) {
		return new RequestResponse(o.toJSONString());
	}
}
