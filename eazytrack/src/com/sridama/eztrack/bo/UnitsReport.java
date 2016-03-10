/**
 * 
 */
package com.sridama.eztrack.bo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class UnitsReport extends Report {

	/**
	 * @param s
	 */
	public UnitsReport(SessionManager s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	/* 
	 * Returns the array of units object json
	 */
	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		JSONObject o = null ;
		JSONArray arr = new JSONArray();
		
		while (rs.next()) {
			o = new JSONObject();
			o.put("name", rs.getInt("name"));
			o.put("desc", rs.getString("cust_type"));
			arr.add(o);
		}
		
		return arr;
		
	}

	/* 
	 * Query will give all the units from unit master
	 */
	@Override
	protected String getSQL(RequestResponse req) {
		// TODO Auto-generated method stub
		
		String sql = "select * from unit_master";
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
