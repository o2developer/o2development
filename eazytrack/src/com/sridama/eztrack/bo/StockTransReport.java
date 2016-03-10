/**
 * 
 */
package com.sridama.eztrack.bo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.eztrack.utils.Utils;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class StockTransReport extends Report {
	
	
	/*
	 *   Columns which needs to be filtered 
	 */
	private String[] colsArray = {
			"",
			"txn_date" ,
			"dc_no" ,
			"" ,
			"" ,
			"status"
			};

	public StockTransReport(SessionManager s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#toJSON(java.sql.ResultSet)
	 * Date 	DC Number 	Description 	Amount 	Status 
	 */
	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {
		JSONArray resJsonSend = new JSONArray();
		while(rs.next()) {
		JSONObject obj = new JSONObject() ;
		try {
			 java.util.Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("txn_date"));
			obj.put("date", new String(new SimpleDateFormat("yyyy-MM-dd").format(date))); //"DD/MM/YYYY
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	
		obj.put("dc_no",rs.getInt("dc_no")) ;
		obj.put("amount",rs.getFloat("amount"));
	
		if ( rs.getString("status").equals("a") )  
			obj.put("status", "accepted");
		else if ( rs.getString("status").equals("p") )
			obj.put("status", "pending");
		else if ( rs.getString("status").equals("r") )
			obj.put("status", "rejected");
		
		
		
		if ( rs.getInt("from_branch") == session.getBrCode() ) {
			obj.put("action", "outword");
			obj.put("desc", " To branch "+Utils.getBranchName(rs.getInt("to_branch")));
		} else if (rs.getInt("to_branch") == session.getBrCode()) {
			obj.put("action", "inword");
			obj.put("desc", "From branch"+Utils.getBranchName(rs.getInt("from_branch")));
		}
		
		obj.put("from_branch", rs.getInt("from_branch"));
		obj.put("to_branch", rs.getInt("to_branch") );
		
		resJsonSend.add(obj) ;
		}
		return resJsonSend;
	}

	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#getSQL(com.sridama.txngw.core.RequestResponse)
	 */
	@Override
	protected String getSQL(RequestResponse req) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select txn_id , dc_no , txn_date, from_branch , to_branch , status , amount from stock_txn where ");
		
		
		/*
		 *  filtering the search when to_branch is mentioned
		 */
		if ( req.getParam("to_branch")!=null && !req.getParam("to_branch").isEmpty() )
			sb.append("   to_branch="+req.getParam("to_branch")+" AND from_branch = "+session.getBrCode() );
		
		else if ( req.getParam("from_branch")!=null && !req.getParam("from_branch").isEmpty() )
			sb.append("   to_branch="+session.getBrCode()+" AND from_branch="+req.getParam("from_branch") );
		
		else 
			sb.append("   to_branch="+session.getBrCode()+" OR from_branch = "+session.getBrCode()  );
		
		if ( req.getParam("fdate")!=null   && req.getParam("tdate")!=null )
			sb.append(" AND  txn_date >="+req.getParam("fdate")+"  AND txn_date <="+req.getParam("tdate") );
		
		sb.append(" ORDER BY txn_date ");
		
		//System.out.println(sb.toString());
		
		return sb.toString();
	}

	@Override
	protected String getReportSortField(RequestResponse req) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	protected String getReportFilter(RequestResponse req) {

		// find out if a search string was given .
		// if yes, loop through all columns that have bSearchable = true
		// and then create a search sql expression and return .
		String filterString = qmap.get("sSearch");
		System.out.println(filterString);
		if (filterString == null || filterString.isEmpty())
			return "";
		
		String sqlSearch = "";
		
		int numSearchCols = Integer.parseInt(qmap.get("iColumns"));
		boolean orFlag = false ;
		for (int i = 0; i < numSearchCols; i++) {
			String key = "bSearchable_" + i;
			boolean bSearch = qmap.get(key).equalsIgnoreCase("true")?true:false;
			if (bSearch) {
				if (!orFlag) {
					//sqlSearch += " OR ";
					orFlag = true;
				} else {
					sqlSearch += " OR ";
				}
				filterString = filterString.contains("undefined") ? "" : filterString ;
				filterString = filterString.contains("+") ?  filterString.replace("+", " ") : filterString ;
				     sqlSearch += " " + colsArray[i] + " LIKE '%" + filterString + "%'"; 
			}
		}
		return sqlSearch;
	}

	@Override
	protected String getOrderbyString(RequestResponse req) {
		// TODO Auto-generated method stub
		return "";
	}

}
