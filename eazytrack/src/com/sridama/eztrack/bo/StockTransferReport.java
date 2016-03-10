/**
 * 
 */
package com.sridama.eztrack.bo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class StockTransferReport extends Report {

	public StockTransferReport(SessionManager s) {
		super(s);
	}

	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#toJSON(java.sql.ResultSet)
	 */
	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {
		JSONArray resJsonSend = new JSONArray();
		while(rs.next()) {
		JSONObject obj = new JSONObject() ;
		obj.put("txn_number", rs.getInt("txn_id") ) ;
		obj.put("dc_no",rs.getInt("dc_no")) ;
		obj.put("date", rs.getString("txn_date"));
		obj.put("from_branch", rs.getInt("from_branch")); 
		obj.put("items", getItemDetails (rs.getInt("txn_id"))) ;
		resJsonSend.add(obj) ;
		}
		return resJsonSend;
	}
	
	/*
	 * gives the item details for particular txn_id
	 */
	public JSONArray getItemDetails (int txnId) {
		JSONArray itemList = new JSONArray() ;
		/*String sql = "select txn_details.quantity,txn_details.dscnt_percent,txn_details.vat_percent," +
				"    item_master.id, item_master.name, category_master.cat_name  from txn_details INNER JOIN item_master " +
				"    ON txn_details.item = item_master.id  INNER JOIN category_master ON " +
				"    category_master.category_id = item_master.category  where txnid="+txnId ;
		*/
		
		StringBuilder sb = new StringBuilder() ;
		sb.append("select stock_txn_details.sitemid , stock_txn_details.aqty , ");
		sb.append("item_master.code , item_master.name from stock_txn_details INNER JOIN  ");
		sb.append("item_master ON stock_txn_details.sitemid = item_master.id where stxnid="+txnId);

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sb.toString());
		
			while (rs.next()) {
				JSONObject obj = new JSONObject() ;
				obj.put("id", rs.getInt("sitemid")) ;
				obj.put("qty", rs.getInt("aqty")) ;
				//obj.put("category", rs.getString("cat_name")) ;
				obj.put("desc",rs.getString("name") );
				obj.put("code",rs.getInt("code") );

				itemList.add(obj);
			}
			
			return itemList ;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null ;
	}

	
	/* (non-Javadoc)
	 * gives the sql of incoming transfers to the particular branch
	 */
	@Override
	protected String getSQL(RequestResponse req) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select txn_id , dc_no , txn_date , from_branch from stock_txn ");
		sb.append("where to_branch="+session.getBrCode()+" and status='p'");
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
