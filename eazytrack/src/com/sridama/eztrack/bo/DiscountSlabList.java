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
public class DiscountSlabList extends Report {

	public DiscountSlabList(SessionManager s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#toJSON(java.sql.ResultSet)
	 */
	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {
		JSONArray resjson = new JSONArray();
		while (rs.next()) {
			JSONObject obj = new JSONObject() ;
			obj.put("id", rs.getInt("id"));
			obj.put("disc", rs.getFloat("disc"));
			resjson.add(obj);
		}
		return resjson;
	}

	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#getSQL(com.sridama.txngw.core.RequestResponse)
	 */
	@Override
	protected String getSQL(RequestResponse req) {
		StringBuilder sb = new StringBuilder() ;
		sb.append("select id , disc from discount_master");
		return sb.toString();
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
