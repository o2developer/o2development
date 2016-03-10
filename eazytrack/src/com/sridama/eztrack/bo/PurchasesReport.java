/**
 * 
 */
package com.sridama.eztrack.bo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class PurchasesReport extends Report {
	HashMap<Integer, Integer> taxMap = new HashMap<Integer,Integer>() ;
	
	
	/*
	 *   Columns which needs to be filtered 
	 */
	private String[] colsArray = {

			"",
			"txn.txn_date" ,
			"txn.invoice_no" ,
			"cust.name" ,
			"",
			"",
			"" ,
			"",
			""
			};
	
	
	/**
	 * @param s
	 */
	public PurchasesReport(SessionManager s) {
		super(s);
	}

	/* 
	 *  Gives the response json of purchases report
	 */
	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {
		
		JSONArray resJsonSend = new JSONArray();
		while (rs.next()) {
			JSONObject resJson = new JSONObject();
		    resJson.put("invoice_no", rs.getInt("invoice_no"));
			resJson.put("pur_invoice_no", rs.getInt("pur_invoice_no"));
			
			try {
				 java.util.Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("txn_date"));
				 resJson.put("invoice_date", new String(new SimpleDateFormat("yyyy-MM-dd").format(date))); //"DD/MM/YYYY
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (rs.getInt("inter_state") == 0)
				resJson.put("inter_state", "n");
			resJson.put("inter_state", "y");
			if (rs.getInt("cform") == 0)
				resJson.put("cform", "n");
			resJson.put("cform", "y");
			resJson.put("payment_mode", rs.getInt("payment_type"));
			
			HashMap<String, Float> taxMap =null ;
			try {
				taxMap =	getVatPercentages(rs.getInt("txn_id"));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	
			// Updating the values of taxes 
			
			if (taxMap.containsKey("5.50")) 
				resJson.put("vat_five", taxMap.get("5.50"));
			else 
				resJson.put("vat_five",  "0.00");
			
			if (taxMap.containsKey("14.50"))
				resJson.put("vat_frtn", taxMap.get("14.50"));
			else 
				resJson.put("vat_frtn", "0.00");
			
			if (taxMap.containsKey("2.00"))
				resJson.put("cst", taxMap.get("2.00"));
			else
				resJson.put("cst", "0.00");
			
		//	resJson.put("cst" , rs.getDouble("cst"));
 		//	resJson.put("vat_five", rs.getDouble("totalvat_five"));
		//	resJson.put("vat_frtn", rs.getDouble("totalvat_fourteen"));
			resJson.put("sub_total", rs.getDouble("sub_total"));
			resJson.put("additional", rs.getDouble("transportation"));
			resJson.put("round_off_disc", rs.getDouble("discount"));
			resJson.put("final_total", rs.getDouble("grand_total"));

			JSONObject cust_info = new JSONObject();
			cust_info.put("cust_id", rs.getInt("cust_id"));
			cust_info.put("cust_name", rs.getString("name"));
			cust_info.put("phone1", rs.getString("phone1"));
			resJson.put("cust_info", cust_info);

			//resJson.put("items", getItemDetails(rs.getInt("txn_id")));

			resJsonSend.add( resJson );
		}
		return resJsonSend;
		
	}
	
	
	private HashMap<String, Float> getVatPercentages(int txn_id) throws ClassNotFoundException, IOException {
		HashMap<String, Float> taxMap = new HashMap<String, Float>() ;
		ResultSet rstax = null;
		Connection con = null;
		Statement stmt = null;
		try {
			con = JDBCHelper.getConnection();
			rstax = null;
			stmt = con.createStatement();
			StringBuilder sbtax = new StringBuilder();
			sbtax.append(" select vat_percent , sum(amount*(vat_percent/100)) from txn_details where txnid="
					+ txn_id + " group by vat_percent");
			rstax = stmt.executeQuery(sbtax.toString());
			//System.out.println("vat classification query " + sbtax.toString());

			while (rstax.next()) {
				//System.out.println(rstax.getInt(1) + "   " + rstax.getInt(2));
				taxMap.put(rstax.getString(1), rstax.getFloat(2));
			} // End of while
			
			
			return taxMap ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (rstax != null) rstax.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return null ;
	}
	
	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#getSQL(com.sridama.txngw.core.RequestResponse)
	 * Gives the sql for fetching  purchases report
	 */
	@Override
	protected String getSQL(RequestResponse req) {
		int branch = session.getBrCode() ; 
		/*
		 *  Gives the report of the selected branch to admin
		 */
		if (req.getParam("branch")!=null && !req.getParam("branch").isEmpty() && !req.getParam("branch").equals("-1"))
    		branch = Integer.parseInt(req.getParam("branch"));
		
		
		StringBuilder sb = new StringBuilder() ;
		sb.append("select txn.txn_id, txn.invoice_no,txn.pur_invoice_no, txn.txn_date,txn.inter_state,txn.cform,txn.payment_type,");
		sb.append("txn.sub_total, txn.discount , txn.transportation , txn.cst , txn.grand_total ,txn.totalvat_fourteen ,");
		sb.append(" txn.totalvat_five,cust.name,cust.cust_id,cust.phone1  from item_txn txn INNER JOIN client_master cust ");
		sb.append("ON  txn.party_id = cust.cust_id where txn.txn_code='p' AND txn.branch_id="+branch+" AND status=1 " );
		

		// If dates are specified, add them to the where clause filter...
		if (req.getParam("fdate") != null && !req.getParam("tdate").isEmpty()) {
			
			sb.append(" AND DATE(txn_date)>='" + req.getParam("fdate")+"' AND DATE(txn_date)<='" + req.getParam("tdate") + "'");
			
		}
		
		// Sales report based on invoice number range  
		if (req.getParam("inv_from")!=null && !req.getParam("inv_to").isEmpty()) {
			
			sb.append(" AND invoice_no>='" + req.getParam("inv_from") + "' AND invoice_no<='" + req.getParam("inv_to") + "'");
			
		}

		// similarly if customer id is specified, narrow the filter to include
		// this customer data only.
		if (req.getParam("cust_id")!=null && !req.getParam("cust_id").isEmpty() ){
			sb.append(" AND party_id=" + req.getParam("cust_id"));
		}

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
