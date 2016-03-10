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
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.eztrack.utils.Utils;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 */
public class StockManager {
	private final static Logger LOGGER = Logger.getLogger(StockManager.class.getName());
	private RequestResponse req ;
	private SessionManager session ;
	
	private int dcno ;
	private int fbranch ;
	private int tbranch ;
	private JSONArray items ;
	private String esugamNo ;
	private String vehicleNo ;
	private float amount ;
	private int txnId ;
	
	//Connection con  = null ;
	
	/*
	 * Holds the session from the controller
	 */
	public StockManager ( SessionManager session) {
		this.session  = session ;
	}
	
	public RequestResponse transferStock(RequestResponse req) {
		JSONParser parser = new JSONParser();
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		LOGGER.debug("Inside the transfer stock items"+req.getParam("items"));
		
		try {
			if (req.getParam("items") != null)
				this.items = (JSONArray) parser.parse(req.getParam("items"));
			if (req.getParam("dcno") != null && !req.getParam("dcno").isEmpty())
				this.dcno = Integer.parseInt(req.getParam("dcno"));
			if (req.getParam("from_branch") != null
					&& !req.getParam("from_branch").isEmpty())
				this.fbranch = Integer.parseInt(req.getParam("from_branch"));
			if (req.getParam("to_branch") != null
					&& !req.getParam("to_branch").isEmpty())
				this.tbranch = Integer.parseInt(req.getParam("to_branch"));
			if (req.getParam("esugam") != null )
				this.esugamNo = req.getParam("esugam");
			if (req.getParam("vehicle_no") != null )
				this.vehicleNo = req.getParam("vehicle_no");
			if (req.getParam("amount") != null && !req.getParam("amount").isEmpty())
				this.amount = Float.parseFloat(req.getParam("amount"));

			StringBuilder sb = new StringBuilder();
			sb.append("insert into stock_txn (txn_id,txn_date,from_branch,");
			sb.append("to_branch , dc_no , note , done_by , status , esugam , vehicle_no , amount ) values (default ,'"
					+ Utils.getCurrentDateTime() + "',");
			sb.append(fbranch + "," + tbranch + "," + dcno
					+ ",'some note message','"+session.getName()+"','p'");
			sb.append(", '"+esugamNo+"','"+vehicleNo+"' ,"+amount+")");

			//System.out.println(sb.toString());
			con = JDBCHelper.getConnection();
			con.setAutoCommit(false);

			stmt = con.createStatement() ;
			stmt.execute(sb.toString());
			
			Iterator<String> iterator = items.iterator();
			while (iterator.hasNext()) {
				Object obj = iterator.next() ;
				updateStockDetais(obj);
				updateStock(obj);
			}

			lastTxnId(con);    // holding current transaction id
			// commiting transaction when no exception is thrown
			con.commit();
			
			
			// prints  generates the pdf of the transaction for the current transaction
			PDFStockTransfer pdf = new PDFStockTransfer();
			pdf.invoivepdf(""+dcno, req.getParam("contextPath"), session.getBrCode(), txnId) ;
			//System.out.println("Generation of pdf id successfull");

			JSONObject res = new JSONObject();
			res.put("result", 0);
			res.put("desc", "Transaction Successful !");

			return createResponse(res);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				if (con != null) con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (ParseException e) {
			try {
				if (con != null) con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {

			try {
//				if (con != null) con.close();
				if (rs != null) rs.close();
				if (stmt != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/*
	 * debits the stock from the sending branch , will not be credited to the receving branch till he accepts
	 */
	private void updateStock(Object json) throws SQLException {
		JSONParser parser = new JSONParser();
		Connection con = null;
		JSONObject obj = null ;
		Statement  stmt = null ;
		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			obj = (JSONObject) parser.parse(json.toString());
			StringBuilder sb = new StringBuilder();
			
			sb.append("update item_stock set stock=stock-"+obj.get("qty")+" where itemid="+obj.get("id"));
			sb.append(" and branchcode="+session.getBrCode());
			//System.out.println("dediting the stock sql \n"+sb.toString());
			stmt.execute(sb.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(stmt!=null) stmt.close();
		}
	
	}
	
	
	/*
	 * updates values for transaction details
	 */
	private  void  updateStockDetais(Object json) throws SQLException {
		JSONParser parser = new JSONParser();
		JSONObject obj = null ;
		Statement  stmt = null ;
		try {
			stmt = JDBCHelper.getConnection().createStatement();
			obj = (JSONObject) parser.parse(json.toString());
			StringBuilder sb = new StringBuilder();
			sb.append("insert into stock_txn_details (stxnid,sitemid,aqty,note) values");
			sb.append("(last_insert_id()," + obj.get("id") + ","
					+ obj.get("qty") + ", 'some note on this item updation')");
			stmt.execute(sb.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(stmt!=null) stmt.close();
		}
	}
	
	/*
	 * Gives the transaction id of current transaction
	 */
	private void lastTxnId(Connection con) throws SQLException {
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select last_insert_id()") ;
		if (rs.next())
			this.txnId = rs.getInt(1) ;
	}
	
	/**
	 * Internal helper method that wraps a given object within a response
	 * object.
	 */
	private RequestResponse createResponse( JSONObject o ) {
		return new RequestResponse(o.toJSONString());
	}
	
}
