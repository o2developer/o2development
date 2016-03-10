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
public class UnitsList extends Report {

	/**
	 * @param s
	 */
	public UnitsList(SessionManager s) {
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
			obj.put("id",rs.getInt("id") );
			obj.put("name", rs.getString("name"));
			obj.put("description", rs.getString("description"));
			resjson.add(obj);
		}
	
		return resjson;
	}

	/* (non-Javadoc)
	 * Gives the list of units skipping the default 0 one.
	 */
	@Override
	protected String getSQL(RequestResponse req) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("select id,name,description from unit_master where id!=0");
		
		String chk="";
		try{
		chk=req.getParam("name");
		
		if(chk.equals("hover"))
		{
		 sb = new StringBuilder();
			
			sb.append("select id,name,description from unit_master where id="+req.getParam("id_no"));
			
			
		}
		}
		catch(Exception e)
		{
			
		}
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
