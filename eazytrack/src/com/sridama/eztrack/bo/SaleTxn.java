package com.sridama.eztrack.bo ;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sridama.eztrack.controller.EzTrackServlet;
import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.txngw.core.RequestResponse;

public class SaleTxn {
	
	private final static Logger LOGGER = Logger.getLogger(SaleTxn.class
			.getName());

	private SessionManager ses = null ;
	
	public SaleTxn(SessionManager ses ) {
		this.ses = ses ;
	}
	
	/*
	 * Making the sale transaction here, this involves multiple sql transactions
	 */
	public RequestResponse saleReq(RequestResponse req) {
		
		Connection con1 = null;
		Connection con = null ;

		/*
		 * If this condition satisfies then it is a reject invoice operation 
		 */
		if ( req.getParam("action")!= null  && !req.getParam("action").isEmpty() && req.getParam("action").equals("reject") )   {
			try {
			con1 = JDBCHelper.getConnection() ;
			LOGGER.debug("  JSON inside reject invoice method  "+req.getParam("txn_type")) ;
			Invocie inv = new Invocie(req.getParam("invoice") );
			inv.setBrCode(ses.getBrCode()) ;
			inv.setTxnType( req.getParam("txn_type") );
			inv.reject(con1);
			inv.updateStockRejectUpdate( con1 );  // updates the item_stock  from txn_details
			
			JSONObject res = new JSONObject() ;
			res.put( (Object)"result", (Object)0 );
			res.put( "desc", "Invoice Rejected Successfully!");
			
			return createResponse(res) ;
			
			} catch (Exception e) {
				LOGGER.debug("Sale Txn Exception "+e) ;
				e.printStackTrace() ;
			}
/*			finally {
				if ( con1!=null ) {
					try {
					con1.close();
					}catch(Exception e) {
						e.printStackTrace() ;
					}
				}
			}*/
		}
		
		
		
		try {
		con = JDBCHelper.getConnection();
		
			/*
			 * While doing multiple transactions to DB we may have to roll back
			 * so setting setAutoCommit to false
			 */
			con.setAutoCommit (false) ;
			
			
			
			
 			String custInfo = req.getParam("customer") ;
			
			Customer cust = new Customer(custInfo);
			/*
			 * if customer is new and with mobile number, saves to database
			 */
			if (cust.getCustId() == -1
					&& !((cust.getPhone().equals("") || cust.getPhone() ==null))) {
			    cust.setCustType("purchaser");
				cust.save(con);
			}

			
			/*
			 * saving invoice data to transaction table
			 */
			Invocie inv = new Invocie(req.getParam("invoice"));
			inv.setTxnType("s") ;
			inv.setCustId(cust.getCustId());
			inv.setBrCode(ses.getBrCode());  
			
			/*
			 * based on user action 
			 */
			if (inv.getAction().equals("save"))
				inv.save(con);
			if (inv.getAction().equals("update")) {
				inv.getTransactionId(con);  //  Fetches the txn_id based on branch and invoice no
				inv.update(con);   //  updates the item_txn 
				inv.updateStockRejectUpdate(con); //updates the stock to item_stock from txn_details 
				inv.delete(con);  // deletes the the entry from txn_details
			}
			if (inv.getAction().equals("reject")) {
				inv.getTransactionId(con);  // updating the stock  back when deleting
				inv.reject(con);     // set the status to reject in item_txn
				inv.updateStockRejectUpdate(con);  // updates the item_stock  from txn_details
			}

			
			/*
			 * Saves the items to txn details with reference to transaction id
			 * from invoice
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
			// prints  generates the pdf of the transaction for the current transaction
			FirstPdf pdf = new FirstPdf();
	    	pdf.invoivepdf(""+inv.getInvoiceNo() , req.getParam("contextPath"), ses.getBrCode());   //E:/SPORTSLINE_RV_SIR/eazytrack
			
	    	JSONObject res = new JSONObject();
			res.put("result", 0);
			res.put("desc", "Transaction Successful !");
			
			return createResponse(res);
			
		} catch (ParseException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		catch (SQLException e) {
			try {
				//System.out.println("Error occurred while executing sale txn: " + e.getMessage());
				LOGGER.debug("SQL Exception in sale txn  "+e);
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		catch (ClassNotFoundException e) {
			try {
				con.rollback();
				LOGGER.debug("Class not found Exception "+e);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.debug("IO Exception "+e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} 
		finally {
			try {
				//if (con != null) con.close();
				con.setAutoCommit(true);
			} catch (SQLException e1) {
				e1.printStackTrace();
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
