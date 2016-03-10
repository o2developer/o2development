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
 *  This will give  Item Details of  each DC number of Stock Transfers .
 */
public class StockTransDcDetails extends Report {

	public StockTransDcDetails(SessionManager s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#toJSON(java.sql.ResultSet)
	 */
	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {
		JSONArray resJsonSend = new JSONArray();
		while(rs.next()) {
		JSONObject obj = new JSONObject() ;
		obj.put("name", rs.getString("name") ) ;
		obj.put("qty",rs.getInt("aqty")) ;
		obj.put("amount", rs.getFloat("rate"));
		obj.put("category", rs.getString("cat_name")); 
		obj.put("note", rs.getString("note")) ;
		resJsonSend.add(obj) ;
		}
		return resJsonSend;
	}

	/*  This methods takes dc_no , from_branch and to_branch 
	 *  As Arguments and Gives back the item level details of that stock transfer.
	 */
	@Override
	protected String getSQL(RequestResponse req) {

		StringBuilder sb = new StringBuilder() ;
		sb.append(" select item.name , stxn.aqty , item.rate , cat.cat_name , stxn.note from   ");
		sb.append(" stock_txn_details stxn INNER JOIN item_master item ON  stxn.sitemid = item.id  ");
		sb.append(" INNER JOIN  category_master cat ON cat.category_id = item.category  where ");
		sb.append(" stxnid = ( select txn_id from stock_txn  where dc_no ="+req.getParam("dc_no") );
		
		if (req.getParam("from_branch")!=null  && req.getParam("to_branch")!=null )
			sb.append(" AND  from_branch="+req.getParam("from_branch") +" AND  to_branch="+req.getParam("to_branch")+")");
		
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
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	protected String getOrderbyString(RequestResponse req) {
		// TODO Auto-generated method stub
		return "";
	}

}
