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
public class StockTransTxn {

	SessionManager ses = null ;
	public StockTransTxn(SessionManager ses) {
		this.ses = ses ;
	}
	
	public RequestResponse purReq(RequestResponse req)  {
		Connection con = null;
		try {
			con = JDBCHelper.getConnection();
			con.setAutoCommit(false);
			/*
			 * saving the stock transfer invoice  
			 */
			Object obj = req.getParam("invoice");
			Invocie inv = new Invocie(obj.toString());
			inv.setTxnType("t") ;
			inv.setBrCode(ses.getBrCode());
			inv.save(con);
			
			/*
			 * saving the item details of purchase invoide
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
				itxn.setToBranch(inv.getToBranch());
				itxn.save(con);
				itxn.updateStock(con);
			}
			

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch(ParseException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				if (con != null) con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} 
//		finally {
//			if (con != null)
//				try {
//					con.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		}

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
