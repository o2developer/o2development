package com.sridama.eztrack.bo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.txngw.core.RequestResponse;

public class TaxSlabList extends Report {

	public TaxSlabList(SessionManager s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {
		JSONArray resjson = new JSONArray();
		
		while (rs.next()) {
			JSONObject obj = new JSONObject() ;
			obj.put("id",rs.getInt("id") );
			obj.put("rate", rs.getFloat("rate"));
			obj.put("description", rs.getString("description"));
			resjson.add(obj);
		}
	
		return resjson;
	}

	@Override
	protected String getSQL(RequestResponse req) {
		StringBuilder sb = new StringBuilder() ;
		sb.append("select id,rate, description from tax_master");
		
		String chk="";
		try{
		chk=req.getParam("name");
		
		if(chk.equals("hover"))
		{
		 sb = new StringBuilder();
			
			sb.append("select id, rate, description from tax_master where id="+req.getParam("id_no"));
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

	public static RequestResponse update() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getOrderbyString(RequestResponse req) {
		// TODO Auto-generated method stub
		return "";
	}

}
