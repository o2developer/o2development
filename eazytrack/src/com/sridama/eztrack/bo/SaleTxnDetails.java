/**
 * 
 */
package com.sridama.eztrack.bo ;

import java.io.IOException ;
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
public class SaleTxnDetails extends Report {

	private RequestResponse req = null ;
	float transportation = 0.0f ;
	float roundOfDisc = 0.0f ;
	float grandTotal = 0.0f ;
	/**
	 * @param s
	 */
	public SaleTxnDetails(SessionManager s) {
		super(s);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#toJSON(java.sql.ResultSet)
	 */
	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {
		  int   qtyTotal = 0 ;
		  float disTotal = 0.0f ;
		  float vatTotal = 0.0f ;
		  float amountTotal = 0.0f ;
		  float vatAmt = 0.0f ;
			  
		  		JSONArray arr = new JSONArray() ; // response 
				JSONArray jarray = new JSONArray();
			    JSONObject objinv = new JSONObject();
			    JSONObject totals = new JSONObject();
				while (rs.next()) {
					JSONObject obj = new JSONObject();
					obj.put("name" , rs.getString("name"));
					obj.put("qty", rs.getInt("quantity"));
					     qtyTotal +=  rs.getInt("quantity") ; 
					obj.put("discnt", String.format("%.2f", rs.getFloat("discount")));
					     disTotal+= rs.getFloat("discount") ;
					obj.put("amount", rs.getFloat("amount"));
					  amountTotal += rs.getFloat("amount") ;
					obj.put("rate", rs.getFloat("rate") );
					obj.put("disc_percent" , rs.getFloat("dscnt_percent"));
					obj.put("vat_percent", rs.getFloat("vat_percent"));
					// vat_amt =  price - original value
					//vat_amount = (rate - discount) - ((rate - discount)/(1+(vatPercent/100)));
					vatAmt = (rs.getFloat("rate")-rs.getFloat("discount")) * (1-(1/(1+(rs.getFloat("vat_percent")/100)))) ;
					vatTotal += vatAmt ;
					obj.put("vat_amount", String.format("%.2f" ,vatAmt ) );
					obj.put("category", rs.getString("cat_name"));
					jarray.add(obj);
				}  // End of while
				try {
				objinv.put("taxes", getVatPerccentages(getTxnId()));
				}catch(Exception e) {
					e.printStackTrace();
				}
				objinv.put("items" , jarray ) ;
				objinv.put("qty_total" , qtyTotal ) ;
				objinv.put ("amt_total" , String.format("%.2f" ,amountTotal ) );
				objinv.put ("vat_total" , String.format("%.2f" ,vatTotal )  );
				objinv.put ("dis_total" ,  String.format("%.2f" ,disTotal )  );
				objinv.put ("additional" ,  String.format("%.2f" , transportation ));
				objinv.put ("round_disc" ,  String.format("%.2f" , roundOfDisc ));
				objinv.put ("grand_total" ,  String.format("%.2f" , grandTotal )  );
				
				arr.add(objinv);
		return arr;
	}

	
	
	/*
	 * Gives the total tax for the invoice
	 */
	private  JSONArray getVatPerccentages( int txn_id ) throws ClassNotFoundException, IOException  {
		Connection con = null ;
		Statement stmt = null ;
		ResultSet rs = null ;
		float totalVat = 0.0f ;
		StringBuilder sbtax = new StringBuilder();
		sbtax.append("select vat_percent , sum(amount*(vat_percent/100)) from txn_details where txnid="+txn_id+" group by vat_percent");
		
		try{
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sbtax.toString());

			final JSONArray arr = new JSONArray(); // response
			final JSONArray jarray = new JSONArray();
			final JSONObject totals = new JSONObject();
			while (rs.next())
			{
				jarray.add("vat/cst@" +rs.getDouble(1)+  " : "+rs.getDouble(2));
				totalVat+=rs.getDouble(2);
			} // End of while

			jarray.add( totalVat );
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
		
		return null ;
	}
	
	
	
	/*
	 * returns the array of txn id
	 */
	private int getTxnId() throws ClassNotFoundException, IOException
	{
		int txnId = 0 ;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		String txnType = "";
		String txnCode = "" ;
	   if ( req.getParam("txn_type")!=null && !req.getParam("txn_type").isEmpty() )
		      txnType =  req.getParam("txn_type") ;
		     
		if (txnType.equals("sale"))
			txnCode = "s";
		else
			txnCode="p";
	   

	    StringBuilder sb = new StringBuilder();
		sb.append("(select txn_id, transportation , discount , grand_total from item_txn where invoice_no="+req.getParam("inv_no") );
		sb.append(" and branch_id="+req.getParam("branch")+" and txn_code='"+txnCode+"')");
	

		try
		{
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sb.toString());
		
			if (rs.next()) {
				transportation = rs.getFloat("transportation");
				roundOfDisc = rs.getFloat("discount");
				grandTotal = rs.getFloat("grand_total") ;
			  return  rs.getInt("txn_id");
			}
		}
		catch (final SQLException e) {
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
		return  0 ;
	}

	
	
	/* (non-Javadoc)
	 * @see com.sridama.eztrack.bo.Report#getSQL(com.sridama.txngw.core.RequestResponse)
	 */
	@Override
	protected String getSQL(RequestResponse req) {
		
		this.req = req ;  // to make this available to entire class
		
		String txnType = "";
		String txnCode = "" ;
	   if ( req.getParam("txn_type")!=null && !req.getParam("txn_type").isEmpty() )
		      txnType =  req.getParam("txn_type") ;
		     
		if (txnType.equals("sale"))
			txnCode = "s";
		else
			txnCode="p";
	   
		StringBuilder sb = new StringBuilder();
		sb.append("select item.name , txn.quantity , txn.discount , txn.amount , txn.rate , txn.dscnt_percent,txn.vat_percent ,");
		sb.append(" cat.cat_name , itxn.discount, itxn.transportation from txn_details txn INNER JOIN item_master item  ");
		sb.append("ON txn.item = item.id INNER JOIN category_master cat ");
		sb.append(" ON item.category = cat.category_id INNER JOIN item_txn itxn ON txn.txnid = itxn.txn_id  where txn.txnid IN " );  //txnId
		sb.append("(select txn_id from item_txn where invoice_no="+req.getParam("inv_no") );
		sb.append(" and branch_id="+req.getParam("branch")+" and txn_code='"+txnCode+"')");
		
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
