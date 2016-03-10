/**
 * 
 */
package com.sridama.eztrack.bo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.eztrack.utils.Utils;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class UsersList extends Report {

	public UsersList(SessionManager s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	/* 
	 * Returns User's list json
	 */
	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			JSONObject o = new JSONObject();
			o.put("name", rs.getString("user_name"));
			o.put("password", rs.getString("password"));
			o.put("login_id", rs.getString("login_id"));
			o.put("branch", Utils.getBranchName(rs.getInt("branch_id")));
			o.put("modified_by", rs.getString("modified_by") );
			o.put("modified_on", rs.getString("modified_on"));

			arr.add(o);
		}
		
		return arr;
		
	}

	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#getSQL(com.sridama.txngw.core.RequestResponse)
	 */
	@Override
	protected String getSQL(RequestResponse req) {
		StringBuilder sb = new StringBuilder();
		sb.append("select login_id , password ,user_name , branch_id , modified_by , modified_on ");
		sb.append(" from user_master");
		String chk="";
		
		try{
		chk=req.getParam("name");
		
		if(chk.equals("hover"))
		{
		 sb = new StringBuilder();
		 sb.append("select user_name , modified_by , modified_on,login_id,branch_id,password from user_master where login_id='"+req.getParam("login_id")+"' and branch_id='"+req.getParam("branch")+"'");
		
			System.out.println("sdfsd"+sb.toString());
		}
		
		}
		catch(Exception e)
		{
			
		}
		System.out.println("vgv"+sb.toString());
		return sb.toString() ;
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
