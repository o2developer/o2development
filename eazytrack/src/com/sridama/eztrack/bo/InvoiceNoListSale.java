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
public class InvoiceNoListSale extends Report {

	/**
	 * @param s
	 */
	public InvoiceNoListSale(SessionManager s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#toJSON(java.sql.ResultSet)
	 */
	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {
		JSONArray resarr = new JSONArray();
		while (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("txn_id", rs.getInt("txn_id"));
			obj.put("inv_no", rs.getInt("invoice_no"));
			resarr.add(obj);
		}
		return resarr;
	}

	/* (non-Javadoc)
	 * Sale invoice numbers of the branches 
	 */
	@Override
	protected String getSQL(RequestResponse req) {
		StringBuilder sb = new StringBuilder();
		sb.append("select txn_id, invoice_no from item_txn where ");
		sb.append("txn_code='s' AND branch_id="+session.getBrCode());
		
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
