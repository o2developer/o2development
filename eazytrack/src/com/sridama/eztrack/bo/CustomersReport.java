/**
 * 
 */
package com.sridama.eztrack.bo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.eztrack.controller.EzTrackServlet;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author admin
 */
public class CustomersReport  extends Report {
	private final static Logger LOGGER = Logger.getLogger(CustomersReport.class
			.getName());

	public CustomersReport(SessionManager s) {
		super(s);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		JSONObject o = null ;
		JSONArray arr = new JSONArray();
		
		while (rs.next()) {
			o = new JSONObject();
			o.put("id", rs.getInt("cust_id"));
			o.put("cust_type", rs.getString("cust_type"));
			o.put("cstno", rs.getString("cst_no"));
			String phone1 = rs.getString("phone1");
			if (phone1!=null && !phone1.equals("")) {
			if (phone1.contains(":")) { // this will affect json
				phone1 = phone1.replace(":", "-");
			}
			if (phone1.contains("\"")) {
				phone1 = phone1.replace("\"", "");
			}

			}
			o.put("phone1", phone1 );
			o.put("phone2", rs.getString("phone2"));
			o.put("email", rs.getString("email"));
			o.put("address", rs.getString("address"));
			o.put("state", rs.getString("state"));
			o.put("name", rs.getString("name"));
			o.put("value", rs.getString("name"));
			
			JSONArray tokenArray =  new JSONArray() ;
			StringTokenizer st = new StringTokenizer(rs.getString("name"));
			while (st.hasMoreTokens())
			       tokenArray.add( ""+st.nextElement());
			
			tokenArray.add(""+rs.getString("phone2"));
			
			o.put("tokens", tokenArray );

			
			
			
			o.put("tin", rs.getString("TIN"));

			arr.add(o);
		}
		
		return arr;
		
	}


	/*
	 *  Gives the customer whose names starts with the type ahead values, this can be extended for  names as well
	 */
	@Override
	protected String getSQL(RequestResponse req) {
		// TODO Auto-generated method stub
		String sql = "select * from client_master  where ( name like '"+req.getParam("type_ahead")+"%' " +
				" OR  phone2 like '"+req.getParam("type_ahead")+"%' )";
	//	String sql = " select * from client_master ";
		
		LOGGER.debug("SQL Query of getting  Customer list"+sql);
		return sql;
	}


	@Override
	protected String getReportSortField(RequestResponse req) {
		// TODO Auto-generated method stub
		return "";
	}


	@Override
	protected String getReportFilter(RequestResponse req) {
		// TODO Auto-generated method stub
		return "";
	}


	@Override
	protected String getOrderbyString(RequestResponse req) {
		// TODO Auto-generated method stub
		return "";
	}
 
}
