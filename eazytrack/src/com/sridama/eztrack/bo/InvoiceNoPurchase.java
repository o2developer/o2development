/**
 * 
 */
package com.sridama.eztrack.bo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.eztrack.utils.Utils;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 * 
 */
public class InvoiceNoPurchase extends Report {
	private final static Logger LOGGER = Logger.getLogger(InvoiceNoPurchase.class.getName());
	public InvoiceNoPurchase(SessionManager s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sridama.eztrack.bo.Report#toJSON(java.sql.ResultSet)
	 */
	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {

		JSONObject o = new JSONObject();
		JSONArray arr = new JSONArray();

		rs.next();
		int invo = rs.getInt(1);
		if (invo == 0) {
			o.put("invoicenum", 1);
		} else {
			o.put("invoicenum", invo);
		}
	//	o.put("result", 0);

		arr.add(o);
		LOGGER.debug("Response new invoice number purchase "+o.toJSONString());
		return arr;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sridama.eztrack.bo.Report#getSQL(com.sridama.txngw.core.RequestResponse
	 * )
	 */
	@Override
	protected String getSQL(RequestResponse req) {
		// TODO should change the query for NULL pointer exception
		String sql = "select max(invoice_no)+1 from item_txn where branch_id="
				+ session.getBrCode() + " AND txn_code='p'";
		
	LOGGER.debug("SQL for getting next invoice number purchase "+sql);
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
