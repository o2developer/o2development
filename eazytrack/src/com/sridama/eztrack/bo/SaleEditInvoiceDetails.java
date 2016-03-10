/**
 * 
 */
package com.sridama.eztrack.bo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class SaleEditInvoiceDetails extends Report
{

	RequestResponse req;

	/**
	 * @param s
	 */
	public SaleEditInvoiceDetails(final SessionManager s)
	{
		super(s);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#toJSON(java.sql.ResultSet)
	 */
	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException
	{

		final JSONArray resJsonSend = new JSONArray();
		while (rs.next())
		{
			final JSONObject resJson = new JSONObject();
			resJson.put("invoice_no", rs.getInt("invoice_no"));
			try
			{
				 java.util.Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("txn_date"));
				resJson.put("invoice_date", new String(new SimpleDateFormat("d/M/yyyy").format(date))); //"DD/MM/YYYY

				if (!rs.getString("pur_inv_date").startsWith("0000")) {
				 java.util.Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse( rs.getString("pur_inv_date") );
				 resJson.put("pur_invoice_date", new String(new SimpleDateFormat("d/M/yyyy").format(date1))); //"DD/MM/YYYY
				} else { 
					 resJson.put("pur_invoice_date" ," ");
				}
				
				if (!rs.getString("goods_receive_date").startsWith("0000")) {
			    java.util.Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("goods_receive_date"));
				resJson.put("receipt_date", new String(new SimpleDateFormat("d/M/yyyy").format(date2))); //"DD/MM/YYYY
				}else {
					resJson.put("receipt_date", " ");
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			
			if (rs.getInt("inter_state") == 0)
				resJson.put("inter_state", "n");
			else
			  resJson.put("inter_state", "y");
			if (rs.getInt("cform") == 0)
				resJson.put("cform", "n");
			else
			   resJson.put("cform", "y");
			resJson.put("payment_mode", rs.getInt("payment_type"));

			//			resJson.put("vat_five", rs.getDouble("totalvat_five"));
			resJson.put("vat_frtn", rs.getDouble("totalvat_fourteen"));
			resJson.put("sub_total", rs.getDouble("sub_total"));
			resJson.put("additional", rs.getDouble("transportation"));
			resJson.put("round_off_disc", rs.getDouble("discount"));
			resJson.put("final_total", rs.getDouble("grand_total"));

			resJson.put("pur_invoice_no", rs.getInt("pur_invoice_no"));

			final JSONObject cust_info = new JSONObject();
			cust_info.put("cust_id", rs.getInt("cust_id"));
			cust_info.put("cust_name", rs.getString("name"));
			cust_info.put("phone1", rs.getString("phone1"));
			resJson.put("cust_info", cust_info);
			try
			{
				resJson.put("items", getTxnDetails(rs.getInt("txn_id")));
			//	resJson.put("taxes", getVatPerccentages(rs.getInt("txn_id")));
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}

			resJsonSend.add(resJson);
		}

		return resJsonSend;

	}

	/*
	 * returns the array of txn details
	 */
	private JSONArray getTxnDetails(int txnId) throws ClassNotFoundException, IOException
	{

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		final StringBuilder sb = new StringBuilder();
		sb.append("select item.name , item.id , item.category, txn.quantity ,");
		sb.append(" txn.discount,txn.amount,txn.rate,txn.dscnt_percent,txn.vat_percent ,");
		sb.append(" cat.cat_name from txn_details txn INNER JOIN item_master item  ");
		sb.append("ON txn.item = item.id INNER JOIN category_master cat ");
		sb.append(" ON item.category = cat.category_id  where txn.txnid=" + txnId);

		/*
		 * "name" : "cricket bat", "id" : 45 , "qty" : 8 , "rate" : "8500.00",
		 * "discnt_percent" : "10.00", "cform" : "n", "vat" : "5.5" "category" :
		 * 1
		 */

		try
		{
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sb.toString());

			final JSONArray arr = new JSONArray(); // response
			final JSONArray jarray = new JSONArray();
			final JSONObject totals = new JSONObject();
			while (rs.next())
			{
				final JSONObject obj = new JSONObject();
				obj.put("id", rs.getInt("id"));
				obj.put("name", rs.getString("name"));
				obj.put("qty", rs.getInt("quantity"));
				obj.put("discnt", String.format("%.2f", rs.getFloat("discount")));
				obj.put("amount", rs.getFloat("amount"));
				obj.put("rate", rs.getFloat("rate"));
				obj.put("disc_percent", rs.getFloat("dscnt_percent"));
				obj.put("vat_percent", rs.getFloat("vat_percent"));
				obj.put("category", rs.getInt("category"));
				jarray.add(obj);
			} // End of while

			return jarray;
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
			}
			catch (final SQLException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	
	

	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#getSQL(com.sridama.txngw.core.RequestResponse)
	 */
	@Override
	protected String getSQL(final RequestResponse req)
	{
		//	{"inv_no":7,"branch":"1"  , "txn_type":"purchase"}
		// , txn.invoice_no,
		// ,  
		String txnCode = "";
		if (req.getParam("txn_type") != null && !req.getParam("txn_type").isEmpty())
			txnCode = req.getParam("txn_type");

		if (txnCode.equals("purchase"))
			txnCode = "p";
		else
			txnCode = "s";

		final StringBuilder sb = new StringBuilder();

		sb.append("select  txn.invoice_no, txn.txn_id, txn.txn_date,txn.inter_state , txn.cform , txn.payment_type , ");
		sb.append("txn.sub_total, txn.discount , txn.transportation ,txn.grand_total ,txn.totalvat_fourteen ,");
		sb.append("txn.pur_invoice_no , txn.pur_inv_date ,goods_receive_date ");
		sb.append(", txn.totalvat_five,cust.name,cust.cust_id,cust.phone1  from item_txn txn INNER JOIN client_master cust ");
		sb.append("ON txn.party_id = cust.cust_id where txn.txn_code='" + txnCode + "' AND txn.branch_id=" + session.getBrCode() + " AND ");
		sb.append(" txn.invoice_no =" + req.getParam("inv_no"));

		System.out.println(sb.toString());

		return sb.toString();
	}

	@Override
	protected String getReportSortField(final RequestResponse req)
	{
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	protected String getReportFilter(final RequestResponse req)
	{
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	protected String getOrderbyString(RequestResponse req) {
		// TODO Auto-generated method stub
		return "";
	}
}
