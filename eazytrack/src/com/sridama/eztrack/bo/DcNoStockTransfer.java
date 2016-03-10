/**
 * 
 */
package com.sridama.eztrack.bo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class DcNoStockTransfer extends Report {

	private final static Logger LOGGER = Logger.getLogger(DcNoStockTransfer.class.getName());
	
	public DcNoStockTransfer(SessionManager s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#toJSON(java.sql.ResultSet)
	 */
	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {

		JSONObject o = new JSONObject();
		JSONArray arr = new JSONArray();

		rs.next();
		int invo = rs.getInt(1);
		if (invo == 0) {
			o.put("dc_no", 1);
		} else {
			o.put("dc_no", invo);
		}
	//	o.put("result", 0);

		arr.add(o);
		LOGGER.debug("Next Dc number for stock transfer is "+o.toJSONString());
		return arr;
	}

	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#getSQL(com.sridama.txngw.core.RequestResponse)
	 */
	@Override
	protected String getSQL(RequestResponse req) {
		
		String sql = "select (case count(dc_no) when 0 then 1 else max(dc_no)+1 end ) " +
				"from stock_txn where from_branch="+ session.getBrCode() ;
	LOGGER.debug("Sql for getting next dc no number for new Sale "+sql);
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
