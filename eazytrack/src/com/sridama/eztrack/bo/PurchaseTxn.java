/**
 * 
 */
package com.sridama.eztrack.bo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 * 
 */
public class PurchaseTxn {

	SessionManager ses = null ;
	public PurchaseTxn(SessionManager ses) {
		this.ses = ses ;
	}
	
	
	public RequestResponse purReq(RequestResponse req)  {

		Connection con = null;
		
		/*
		 * If this condition satisfies then it is a reject invoice operation 
		 */
		if ( req.getParam("action")!= null  && !req.getParam("action").isEmpty() && req.getParam("action").equals("reject") )  {
			try {
			con = JDBCHelper.getConnection() ;
			
			Invocie inv = new Invocie(req.getParam("invoice") );
			inv.setBrCode(ses.getBrCode()) ;
			inv.reject(con);
			inv.updateStockRejectUpdate( con );  // updates the item_stock  from txn_details
			
			JSONObject res = new JSONObject() ;
			res.put( "result", 0 );
			res.put( "desc", "Invoice Rejected Successfully ! " );
			
			return createResponse(res) ;
			
			} catch (Exception e) {
				e.printStackTrace() ;
			} 
/*			finally {
				try {
					if (con != null) con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}*/
				
		}
		
		
		try {

			con = JDBCHelper.getConnection() ;
			con.setAutoCommit(false);

			Customer cust = new Customer(req.getParam("customer"));

			/*
			 * Check if the customer is already registered or not
			 */

			if (cust.getCustId() == -1
					&& !(cust.getPhone() ==null || cust.getPhone().equals(""))) {
				cust.setCustType("seller");
				cust.save(con);
			}
			
			/*
			 * saving the purchase invoice 
			 */
			Object obj = req.getParam("invoice");
			Invocie inv = new Invocie(obj.toString());
			inv.setTxnType("p") ;
			inv.setBrCode( ses.getBrCode() );
			inv.setCustId( cust.getCustId() );
			
			
			/*
			 * based on user action 
			 */
			if (inv.getAction().equals("save") )
				inv.save(con);
			if (inv.getAction().equals("update")) {
				inv.getTransactionId(con);  //  Fetches the txn_id based on branch and invoice no
				inv.update(con);   //  updates the item_txn 
				inv.updateStockRejectUpdate(con); //updates the stock to item_stock from txn_details 
				inv.delete(con);  // deletes the the entry from txn_details
			}
			if (inv.getAction().equals("reject") ) {
				inv.getTransactionId(con);  // updating the stock  back when deleting
				inv.reject(con);     // set the status to reject in item_txn
				inv.updateStockRejectUpdate(con);  // updates the item_stock  from txn_details
			}
			
			
			/*
			 * saving the item details of purchase invoice
			 */
			JSONParser parser = new JSONParser();
			JSONArray items = (JSONArray) parser.parse(req.getParam("items"));
			Iterator<String> iterator = items.iterator();
			while (iterator.hasNext()) {
				// System.out.println(iterator.next());
				Object json = iterator.next();
				InvoiceItems itxn = new InvoiceItems(json.toString());
				itxn.setTxnId(inv.getTransId());
				itxn.setTxnType(inv.getTxnType());
				itxn.setBrCode(ses.getBrCode());
				if (!inv.getAction().equals("reject")) {    // if not reject operation perform this action
					 itxn.save(con);
					 itxn.updateStock(con);
					}
			}
			// commits when no exception is thrown
			con.commit();
			
	    	JSONObject res = new JSONObject();
				res.put("result", 0);
				res.put("desc", "Transaction Successful !");
				
				return createResponse(res);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch(ParseException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} 
		finally {
			if (con != null)
				try {
					 con.setAutoCommit(true);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		return null;
	}
	
	/**
	 * Internal helper method that wraps a given object within a response
	 * object.
	 */
	private RequestResponse createResponse(JSONObject o) {
		return new RequestResponse(o.toJSONString());
	}
}
